package com.example.lookers.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.lookers.BuildConfig
import com.example.lookers.databinding.ActivityLoginBinding
import com.example.lookers.util.getFirebaseToken
import com.example.lookers.util.goToActivity
import com.example.lookers.util.handleState
import com.example.lookers.util.toast
import com.example.lookers.view.base.BaseActivity
import com.example.lookers.viewmodel.AuthViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        credentialManager = CredentialManager.create(this)

        observeAuthResponse()
        setListeners()
    }

    private fun setListeners() {
        binding.btnGoogleSignIn.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        lifecycleScope.launch {
            try {
                val googleIdOption = buildGoogleIdOption()
                val request = buildRequest(googleIdOption)

                val result =
                    credentialManager.getCredential(
                        request = request,
                        context = this@LoginActivity,
                    )

                when (val credential = result.credential) {
                    is CustomCredential -> {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)

                        Timber.d("idToken : $googleIdTokenCredential.idToken")
                        authViewModel.googleLogin(googleIdTokenCredential.idToken)
                    }

                    else -> {
                        Timber.e("Unexpected credential type")
                    }
                }
            } catch (e: GetCredentialException) {
                handleCredentialException(e)
            }
        }
    }

    private fun buildGoogleIdOption(): GetGoogleIdOption =
        GetGoogleIdOption
            .Builder()
            .setServerClientId(BuildConfig.SERVER_CLIENT_ID)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(true)
            .setNonce(null)
            .build()

    private fun buildRequest(googleIdOption: GetGoogleIdOption) =
        GetCredentialRequest
            .Builder()
            .addCredentialOption(googleIdOption)
            .build()

    private fun observeAuthResponse() {
        authViewModel.authResponse.observe(this@LoginActivity) { state ->
            handleState(
                state,
                onLoading = {},
                onSuccess = { authResponse ->
                    lifecycleScope.launch {
                        authViewModel.sendFirebaseToken(getFirebaseToken())
                        goToActivity(MainActivity::class.java, clearStack = true)
                        finish()
                    }
                },
                onError = { errorMessage -> Timber.e(errorMessage) },
            )
        }
    }

    private fun handleCredentialException(e: GetCredentialException) {
        val message =
            when (e) {
                is NoCredentialException -> "No credentials available"
                is GetCredentialCancellationException -> "Sign in cancelled"
                is GetCredentialInterruptedException -> "Sign in interrupted"
                else -> "Sign in failed: ${e.message}"
            }
        toast(message)
    }
}
