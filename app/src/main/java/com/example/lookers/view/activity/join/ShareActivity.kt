package com.example.lookers.view.activity.join

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.example.lookers.R
import com.example.lookers.databinding.ActivityShareBinding
import com.example.lookers.model.entity.drawer.DrawerEntity
import com.example.lookers.model.entity.qr.QrShareRequest
import com.example.lookers.util.handleState
import com.example.lookers.util.toast
import com.example.lookers.view.base.BaseActivity
import com.example.lookers.viewmodel.DrawerViewModel
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ShareActivity : BaseActivity<ActivityShareBinding>(ActivityShareBinding::inflate) {
    private val drawerViewModel: DrawerViewModel by viewModels()
    private lateinit var drawerList: List<DrawerEntity>
    private lateinit var selectedDrawer: DrawerEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setListeners()
        observeDrawerList()

        drawerViewModel.getAllMyDrawers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    private fun generateQRCode(qrShareRequest: QrShareRequest) {
        val qrEncoder = BarcodeEncoder()
        val content = generateContent(qrShareRequest)
        val bitmap = qrEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 400, 400)
        binding.qrImage.setImageBitmap(bitmap)
    }

    private fun generateContent(qrShareRequest: QrShareRequest): String =
        StringBuilder()
            .apply {
                append("lookers://main_activity?")
                append("drawerId=${qrShareRequest.drawerId}&")
                append("time=${qrShareRequest.time}")
            }.toString()

    private fun setSpinner() {
        val deviceDropdown = binding.deviceDropdown
        val adapter = ArrayAdapter(this, R.layout.item_dropdown, drawerList.map { it.drawerName })
        deviceDropdown.setAdapter(adapter)

        Timber.d("drawerList: $drawerList")
        deviceDropdown.setText(
            if (drawerList.isNotEmpty()) {
                drawerList[0].drawerName
            } else {
                "기기 없음"
            },
            false,
        )
    }

    private fun setListeners() {
        binding.btnRefreshQr.setOnClickListener {
            if (::selectedDrawer.isInitialized) {
                generateQRCode(QrShareRequest(selectedDrawer.drawerId, System.currentTimeMillis()))
            } else {
                toast("No drawer selected.")
            }
        }
    }

    private fun observeDrawerList() {
        drawerViewModel.myDrawerList.observe(this) { resultState ->
            handleState(
                resultState,
                onLoading = {},
                onSuccess = { drawers ->
                    drawerList = drawers

                    if (drawerList.isNotEmpty()) {
                        selectedDrawer = drawerList[0]
                        generateQRCode(
                            QrShareRequest(
                                drawerList[0].drawerId,
                                System.currentTimeMillis(),
                            ),
                        )
                    } else {
                        toast("No drawers available.")
                    }
                    setSpinner()
                },
                onError = { errorMessage ->
                    toast("Failed: $errorMessage")
                },
            )
        }

        binding.deviceDropdown.setOnItemClickListener { _, _, position, _ ->
            selectedDrawer = drawerList[position]
            generateQRCode(QrShareRequest(selectedDrawer.drawerId, System.currentTimeMillis()))
        }
    }
}
