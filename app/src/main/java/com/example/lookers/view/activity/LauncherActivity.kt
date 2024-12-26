package com.example.lookers.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.lookers.databinding.ActivityLauncherBinding
import com.example.lookers.util.goToActivity
import com.example.lookers.view.base.BaseActivity
import com.example.lookers.viewmodel.DataStoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LauncherActivity : BaseActivity<ActivityLauncherBinding>(ActivityLauncherBinding::inflate) {
    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        observeAccessToken()
        dataStoreViewModel.getAccessToken()
    }

    private fun observeAccessToken() {
        dataStoreViewModel.accessToken.observe(this) { accessToken ->
            goToActivity(
                if (accessToken.isNotEmpty()) {
                    MainActivity::class.java
                } else {
                    LoginActivity::class.java
                },
                clearStack = true,
            )
            finish()
        }
    }
}
