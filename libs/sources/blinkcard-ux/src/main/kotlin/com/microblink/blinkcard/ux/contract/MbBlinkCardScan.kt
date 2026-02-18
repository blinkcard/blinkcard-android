/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

package com.microblink.blinkcard.ux.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.BundleCompat
import com.microblink.blinkcard.core.BlinkCardSdkSettings
import com.microblink.blinkcard.core.session.BlinkCardScanningResult
import com.microblink.blinkcard.core.session.BlinkCardSessionSettings
import com.microblink.blinkcard.ux.DefaultShowHelpButton
import com.microblink.blinkcard.ux.DefaultShowOnboardingDialog
import com.microblink.blinkcard.ux.activity.BlinkCardScanActivity
import com.microblink.blinkcard.ux.camera.CameraSettings
import com.microblink.blinkcard.ux.contract.CancelReason
import com.microblink.blinkcard.ux.contract.ScanActivityColors
import com.microblink.blinkcard.ux.contract.ScanActivityResultStatus
import com.microblink.blinkcard.ux.contract.ScanActivitySettings
import com.microblink.blinkcard.ux.settings.BlinkCardUxSettings
import com.microblink.blinkcard.ux.theme.SdkStrings
import com.microblink.blinkcard.ux.utils.ParcelableUiTypography
import kotlinx.parcelize.Parcelize

class MbBlinkCardScan :
    ActivityResultContract<BlinkCardScanActivitySettings, BlinkCardScanActivityResult>() {
    override fun createIntent(context: Context, input: BlinkCardScanActivitySettings): Intent {
        return Intent(context, BlinkCardScanActivity::class.java).also {
            input.saveToIntent(it)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): BlinkCardScanActivityResult {
        if (resultCode == Activity.RESULT_OK) {
            BlinkCardScanningResultHolder.blinkCardScanningResult?.let { result ->
                return BlinkCardScanActivityResult(
                    status = ScanActivityResultStatus.Scanned,
                    result = result
                )
            }
            throw IllegalStateException("Activity was completed successfully, but the result is empty!")
        } else {
            val cancelReason = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getSerializableExtra(EXTRA_CANCEL_REASON, CancelReason::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent?.getSerializableExtra(EXTRA_CANCEL_REASON) as? CancelReason
            }
            return when (cancelReason) {
                CancelReason.ErrorSdkInit ->
                    BlinkCardScanActivityResult(
                        status = ScanActivityResultStatus.ErrorSdkInit,
                        result = null
                    )

                CancelReason.UserRequested -> BlinkCardScanActivityResult(
                    status = ScanActivityResultStatus.Canceled,
                    result = null
                )

                else -> BlinkCardScanActivityResult(
                    status = ScanActivityResultStatus.Canceled,
                    result = null
                )
            }
        }
    }

    companion object {
        const val EXTRA_CANCEL_REASON = "ExtraCancelReason"
    }

}

/**
 * Configuration settings for the [BlinkCardScanActivity].
 *
 * This data class encapsulates the various settings that control the behavior and appearance of
 * the [BlinkCardScanActivity], including the SDK settings, scanning session configurations,
 * UI customization options, and other miscellaneous preferences.
 *
 * @property sdkSettings The core SDK settings required for initializing and
 *           running the BlinkCard SDK. This is a mandatory parameter.
 * @property cameraSettings The [CameraSettings] used for card scanning. Defaults to [CameraSettings] with default values.
 * @property scanningSessionSettings Configuration options for the card scanning session. This
 *           allows you to customize aspects of the scanning process, such as certain visual check strictness
 *           and timeout duration. Defaults to [BlinkCardSessionSettings].
 * @property uxSettings The [BlinkCardUxSettings] used to customize the UX.
 * @property scanActivityUiColors Custom colors for the [BlinkCardScanActivity] user interface.
 *           If set to `null`, the default colors will be used. Defaults to `null`.
 * @property scanActivityUiStrings Custom strings for the [BlinkCardScanActivity] user
 *           interface. Defaults to [SdkStrings.Default].
 * @property scanActivityTypography Custom typography for the [BlinkCardScanActivity] user
 *           interface. Due to a limitation of [androidx.compose.material3.Typography] class, [ParcelableUiTypography] mimics
 *           [com.microblink.ux.theme.UiTypography] by allowing the customization of all the elements to a lesser extent.
 *           The most important [androidx.compose.ui.text.TextStyle] and [androidx.compose.ui.text.Font] customizations are still available through this class.
 *           Defaults to [ParcelableUiTypography.Default].
 * @property showOnboardingDialog Determines whether an onboarding dialog should be displayed to
 *           the user when the activity is first launched. Defaults to [DefaultShowOnboardingDialog].
 * @property showHelpButton Determines whether a help button should be displayed in the activity.
 *           The button allows the user to open help screens during scanning. Defaults to [DefaultShowHelpButton].
 * @property enableEdgeToEdge Enables edge-to-edge display for the activity. This is the default behavior for
 *           all Android 15 devices (and above) and cannot be changed by this setting. In order to have this behavior
 *           on older OS versions, this setting should be set to `true`. Padding and window insets are
 *           handled automatically. Defaults to `true`.
 * @property deleteCachedAssetsAfterUse Indicates whether cached SDK assets should be deleted after
 *           they are used. If the scanning session is used only once (e.g. for user onboarding), the setting
 *           should be set to `true`. Otherwise, the setting should be set to `false` to avoid assets being re-downloaded.
 *           This only applies if `DOWNLOAD_RESOURCES` variant of the SDK is used. Defaults to `false`.
 *
 */
@Parcelize
data class BlinkCardScanActivitySettings @JvmOverloads constructor(
    val sdkSettings: BlinkCardSdkSettings,
    val scanningSessionSettings: BlinkCardSessionSettings = BlinkCardSessionSettings(),
    val uxSettings: BlinkCardUxSettings = BlinkCardUxSettings(),
    override val cameraSettings: CameraSettings = CameraSettings(),
    override val scanActivityUiColors: ScanActivityColors? = null,
    override val scanActivityUiStrings: SdkStrings = SdkStrings.Default,
    override val scanActivityTypography: ParcelableUiTypography = ParcelableUiTypography.Default(
        null
    ),
    override val showOnboardingDialog: Boolean = DefaultShowOnboardingDialog,
    override val showHelpButton: Boolean = DefaultShowHelpButton,
    override val enableEdgeToEdge: Boolean = true,
    override val deleteCachedAssetsAfterUse: Boolean = false
) : ScanActivitySettings {
    internal fun saveToIntent(intent: Intent) {
        intent.putExtra(INTENT_EXTRAS_BLINKCARD_SETTINGS, this)
    }

    override fun describeContents(): Int {
        return 0
    }

    internal companion object {
        internal fun loadFromIntent(intent: Intent): BlinkCardScanActivitySettings {
            return intent.extras?.let {
                BundleCompat.getParcelable(
                    it, INTENT_EXTRAS_BLINKCARD_SETTINGS,
                    BlinkCardScanActivitySettings::class.java
                )
            }
                ?: throw java.lang.IllegalStateException("Intent does not contain expected BlinkCardScanActivitySettings!")
        }

        private const val INTENT_EXTRAS_BLINKCARD_SETTINGS = "MbBlinkCardSettings"
    }
}

/**
 * Class containing results of the BlinkCard Scanning activity.
 *
 * @property status Represents the status of the activity result and shows whether the activity completed
 *                  successfully or what caused it not to complete.
 * @property result Result of the card scanning session activity.
 *                  Result is present only if [status] is [ScanActivityResultStatus.Scanned], otherwise it is null.
 *
 */
public data class BlinkCardScanActivityResult(
    val status: ScanActivityResultStatus,
    val result: BlinkCardScanningResult?
)