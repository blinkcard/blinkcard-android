/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

package com.microblink.blinkcard.ux.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.microblink.blinkcard.core.ping.util.PingletTracker
import com.microblink.blinkcard.core.utils.MbLog
import com.microblink.blinkcard.ux.BlinkCardCameraScanningScreen
import com.microblink.blinkcard.ux.components.LoadingScreen
import com.microblink.blinkcard.ux.contract.BlinkCardScanActivitySettings
import com.microblink.blinkcard.ux.contract.BlinkCardScanningResultHolder
import com.microblink.blinkcard.ux.contract.CancelReason
import com.microblink.blinkcard.ux.contract.MbBlinkCardScan
import com.microblink.blinkcard.ux.createUiSettings
import com.microblink.blinkcard.ux.theme.BlinkCardSdkTheme
import kotlinx.coroutines.launch

private const val TAG = "BlinkCardScanActivity"

class BlinkCardScanActivity : AppCompatActivity() {

    private val activityViewModel: BlinkCardScanActivityViewModel by viewModels()
    private lateinit var blinkCardScanActivitySettings: BlinkCardScanActivitySettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        blinkCardScanActivitySettings = BlinkCardScanActivitySettings.loadFromIntent(intent)
        if (blinkCardScanActivitySettings.enableEdgeToEdge) enableEdgeToEdge()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onCancel(CancelReason.UserRequested)
            }
        })

        activityViewModel.viewModelScope.launch {
            activityViewModel.initializeLocalSdk(
                context = this@BlinkCardScanActivity,
                blinkCardSdkSettings = blinkCardScanActivitySettings.sdkSettings,
                onInitFailed = { exception ->
                    MbLog.e(TAG, exception) { "SDK initialization failed" }
                    onCancel(CancelReason.ErrorSdkInit)
                }
            )
        }

        PingletTracker.Log.trackInfo(
            context = this,
            logMessage = "Using $TAG"
        )

        setContent {

            val uiSettings = createUiSettings(blinkCardScanActivitySettings)

            BlinkCardSdkTheme(uiSettings) {
                val displayLoading = activityViewModel.displayLoading.collectAsStateWithLifecycle()
                if (displayLoading.value) {
                    LoadingScreen()
                } else {
                    activityViewModel.localSdk?.let {
                        BlinkCardCameraScanningScreen(
                            blinkCardSdk = it,
                            uxSettings = blinkCardScanActivitySettings.uxSettings,
                            uiSettings = uiSettings,
                            cameraSettings = blinkCardScanActivitySettings.cameraSettings,
                            sessionSettings = blinkCardScanActivitySettings.scanningSessionSettings,
                            onScanningSuccess = { result ->
                                BlinkCardScanningResultHolder.blinkCardScanningResult = result
                                this.setResult(RESULT_OK)
                                this.finish()
                            },
                            onScanningCanceled = {
                                onCancel(CancelReason.UserRequested)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        if (blinkCardScanActivitySettings.deleteCachedAssetsAfterUse) {
            activityViewModel.unloadSdkAndDeleteCachedAssets()
        } else {
            activityViewModel.unloadSdk()
        }
        super.onDestroy()
    }

    fun onCancel(cancelReason: CancelReason) {
        val extras = Intent()
        extras.putExtra(MbBlinkCardScan.EXTRA_CANCEL_REASON, cancelReason)
        this.setResult(RESULT_CANCELED, extras)
        this.finish()

    }

}