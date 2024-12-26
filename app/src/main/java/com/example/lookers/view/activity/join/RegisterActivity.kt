package com.example.lookers.view.activity.join

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.example.lookers.BuildConfig
import com.example.lookers.R
import com.example.lookers.databinding.ActivityRegisterBinding
import com.example.lookers.model.entity.embedded.WifiRequest
import com.example.lookers.model.entity.user.UserEntity
import com.example.lookers.model.network.EmbeddedService
import com.example.lookers.util.toast
import com.example.lookers.view.base.BaseActivity
import com.example.lookers.viewmodel.DataStoreViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding>(ActivityRegisterBinding::inflate) {
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private var userInfo: UserEntity = UserEntity(0, "", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setListeners()
        dataStoreViewModel.getUserInfo()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    private fun setListeners() {
        binding.btnSendWifiInfo.setOnClickListener {
            val wifiInfo = getWifiDetails()
            val ssid = wifiInfo.first
            val ipAddress = wifiInfo.second

            if (ssid.isEmpty() || ipAddress.isEmpty()) {
                toast("Wifi 정보를 가져오는데 실패했습니다.")
                return@setOnClickListener
            }

            if (ssid == "Unknown" || ipAddress == "Unknown") {
                toast("Wifi 정보를 가져오는데 실패했습니다.")
                return@setOnClickListener
            }

            if (ssid != BuildConfig.EMBEDDED_WIFI_SSID) {
                toast("기기와 연결된 Wifi가 아닙니다.\n${BuildConfig.EMBEDDED_WIFI_SSID}에 연결해주세요.")
                return@setOnClickListener
            }

            showWifiSendDialog()
        }

        dataStoreViewModel.userInfo.observe(this@RegisterActivity) {
            userInfo = it
        }
    }

    @SuppressLint("DefaultLocale")
    private fun getWifiDetails(): Pair<String, String> {
        var ssid = "Unknown"
        var ipAddress = "Unknown"

        try {
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

            if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                ssid = wifiInfo.ssid.removeSurrounding("\"")

                val ipInt = wifiInfo.ipAddress

                ipAddress =
                    String.format(
                        "%d.%d.%d.%d",
                        (ipInt and 0xff),
                        (ipInt shr 8 and 0xff),
                        (ipInt shr 16 and 0xff),
                        (ipInt shr 24 and 0xff),
                    )
            } else {
                toast("Wifi에 연결되어 있지 않습니다.")
            }
        } catch (e: SecurityException) {
            Timber.e(e)
        }

        return Pair(ssid, ipAddress)
    }

    private fun showWifiSendDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_wifi_send, null)

        val floorDropdown =
            dialogView.findViewById<MaterialAutoCompleteTextView>(R.id.floor_dropdown)
        val floorAdapter =
            ArrayAdapter(this, R.layout.item_dropdown, arrayOf(1, 2, 3, 4, 5))
        floorDropdown.setAdapter(floorAdapter)
        floorDropdown.setText(floorAdapter.getItem(0).toString(), false)

        val ssidDropdown = dialogView.findViewById<MaterialAutoCompleteTextView>(R.id.ssidDropdown)
        val ssids = getWifiLists().map { it.SSID }

        val adapter = ArrayAdapter(this, R.layout.item_dropdown, ssids)
        ssidDropdown.setAdapter(adapter)
        ssidDropdown.setText(ssids[0], false)

        val passwordEditText = dialogView.findViewById<TextInputEditText>(R.id.passwordEditText)

        val dialog =
            AlertDialog
                .Builder(this)
                .setView(dialogView)
                .create()

        dialogView.findViewById<MaterialButton>(R.id.btnConnect).setOnClickListener {
            val selectedFloor = floorDropdown.text.toString().toInt()
            val selectedSSID = ssidDropdown.text.toString()
            val password = passwordEditText.text.toString()
            sendWifiInfo(selectedFloor, selectedSSID, password)
            dialog.dismiss()
        }

        dialogView.findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes =
            dialog.window?.attributes?.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }

        dialog.show()
    }

    @SuppressLint("MissingPermission")
    private fun getWifiLists(): List<ScanResult> {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (!wifiManager.isWifiEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
                startActivity(panelIntent)
                return emptyList()
            } else {
                @Suppress("DEPRECATION")
                wifiManager.isWifiEnabled = true
            }
        }

        wifiManager.startScan()

        return wifiManager.scanResults
            .distinctBy { it.SSID }
            .filter { it.SSID.isNotEmpty() && it.SSID != "LookersAP" }
            .sortedByDescending { it.level }
    }

    private fun sendWifiInfo(
        level: Int,
        ssid: String,
        password: String,
    ) {
        dataStoreViewModel.getUserInfo()

        val progressDialog =
            ProgressDialog(this).apply {
                setMessage("Wifi 정보를 전송 중입니다...")
                setCancelable(false)
                show()
            }

        val retrofit =
            Retrofit
                .Builder()
                .baseUrl(BuildConfig.EMBEDDED_WIFI_IP_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient
                        .Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build(),
                ).build()

        val embeddedService = retrofit.create(EmbeddedService::class.java)
        val wifiRequest = WifiRequest(level, ssid, password, userInfo.email)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = embeddedService.sendWifiInfo(wifiRequest)

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()

                    if (response.isSuccessful) {
                        toast("Wifi 정보가 성공적으로 전송되었습니다.")
                    } else {
                        toast("Wifi 정보 전송에 실패했습니다. (${response.code()})")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    when (e) {
                        is ConnectException -> toast("서버에 연결할 수 없습니다.")
                        is SocketTimeoutException -> toast("연결 시간이 초과되었습니다.")
                        else -> toast("오류가 발생했습니다: ${e.message}")
                    }
                    Timber.e("WifiSend", "Error sending wifi info", e)
                }
            }
        }
    }
}
