/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

package com.microblink.blinkcard.ux.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microblink.blinkcard.core.BlinkCardSdk
import com.microblink.blinkcard.core.BlinkCardSdkSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BlinkCardScanActivityViewModel : ViewModel() {

    private val _displayLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var displayLoading = _displayLoading.asStateFlow()

    var localSdk: BlinkCardSdk? = null
        private set

    suspend fun initializeLocalSdk(
        context: Context,
        blinkCardSdkSettings: BlinkCardSdkSettings,
        onInitFailed: (exception: Throwable?) -> Unit
    ) {
        _displayLoading.update {
            true
        }

        val maybeInstance = BlinkCardSdk.initializeSdk(
            context,
            blinkCardSdkSettings
        )
        when {
            maybeInstance.isSuccess -> {
                localSdk = maybeInstance.getOrNull()
                _displayLoading.update {
                    false
                }
            }

            maybeInstance.isFailure -> {
                onInitFailed(maybeInstance.exceptionOrNull())
            }
        }
    }

    fun unloadSdk() {
        val sdkToClose = localSdk
        localSdk = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sdkToClose?.close()
            } catch (_: Exception) {
            }
        }
    }

    fun unloadSdkAndDeleteCachedAssets() {
        val sdkToClose = localSdk
        localSdk = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sdkToClose?.closeAndDeleteCachedAssets()
            } catch (_: Exception) {
            }
        }
    }

}