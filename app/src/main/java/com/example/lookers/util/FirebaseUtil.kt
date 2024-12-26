package com.example.lookers.util

import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun getFirebaseToken(): String =
    suspendCoroutine { continuation ->
        FirebaseMessaging.getInstance()
            .token
            .addOnSuccessListener { token ->
                Timber.d("firebaseToken: $token")
                continuation.resume(token)
            }.addOnFailureListener { exception ->
                Timber.e(exception)
                continuation.resumeWithException(exception)
            }
    }