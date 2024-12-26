package com.example.lookers.view.activity.example

import android.Manifest
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.lookers.databinding.ActivityExampleBinding
import com.example.lookers.util.goToActivity
import com.example.lookers.util.handleState
import com.example.lookers.util.toast
import com.example.lookers.view.base.BaseActivity
import com.example.lookers.viewmodel.DataStoreViewModel
import com.example.lookers.viewmodel.ExampleViewModel
import com.gun0912.tedpermission.coroutine.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExampleActivity : BaseActivity<ActivityExampleBinding>(ActivityExampleBinding::inflate) {
    private val exampleViewModel: ExampleViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpExampleData()
        setUpCount()
        increaseCount()
        moveToSub()
        moveToSub2()
        setUserInfo()
        setTokens()
    }

    private fun setUpExampleData() {
        exampleViewModel.getExampleData()
        exampleViewModel.exampleEntity.observe(this) { state ->
            handleState(
                state,
                onLoading = {
                    binding.mainText.text = "Loading data, please wait..."
                },
                onSuccess = { example ->
                    val displayText =
                        buildString {
                            append("X-Cloud-Trace-Context: ${example.xCloudTraceContext ?: "null"}\n")
                            append("Traceparent: ${example.traceparent ?: "null"}\n")
                            append("User-Agent: ${example.userAgent ?: "null"}\n")
                            append("Host: ${example.host ?: "null"}\n")
                        }
                    binding.mainText.text = displayText
                },
                onError = { errorMessage ->
                    binding.mainText.text = "Failed to load data: $errorMessage"
                },
            )
        }
    }

    private fun setUpCount() {
        lifecycleScope.launch {
            val initialCount = dataStoreViewModel.getExampleData()
            binding.textSetting.text = initialCount.toString()
        }
    }

    private fun increaseCount() {
        dataStoreViewModel.getExampleData()

        dataStoreViewModel.exampleData.observe(this) { currentCount ->
            binding.textSetting.text = currentCount.toString()
        }

        binding.btn.setOnClickListener {
            val currentCount = dataStoreViewModel.exampleData.value ?: 0
            val newCount = currentCount + 1
            dataStoreViewModel.setExampleData(newCount)
        }
    }

    private fun moveToSub() {
        binding.btnMove.setOnClickListener {
            toast("move to sub")
            goToActivity(SubActivity::class.java, clearStack = true)
        }
    }

    private fun moveToSub2() {
        binding.btnMoveQr.setOnClickListener {
            lifecycleScope.launch {
                getPermission()
            }
        }
    }

    private suspend fun getPermission() {
        try {
            val permissionResult =
                TedPermission
                    .create()
                    .setPermissions(Manifest.permission.CAMERA)
                    .setDeniedMessage("권한을 허용하지 않으면 QR 스캔을 할 수 없습니다.\n\n[설정] > [권한]")
                    .check()

            if (permissionResult.isGranted) {
                goToActivity(Sub2Activity::class.java)
            } else {
                toast("Permission Denied")
            }
        } catch (e: Exception) {
            toast("error: $e")
        }
    }

    private fun setUserInfo() {
        lifecycleScope.launch {
            dataStoreViewModel.getUserInfo()
        }

        dataStoreViewModel.userInfo.observe(this) { userInfo ->
            binding.textUserInfo.text = userInfo.toString()
        }
    }

    private fun setTokens() {
        lifecycleScope.launch {
            dataStoreViewModel.getAccessToken()
        }

        dataStoreViewModel.accessToken.observe(this) { tokens ->
            binding.textToken.text = tokens.toString()
        }
    }
}
