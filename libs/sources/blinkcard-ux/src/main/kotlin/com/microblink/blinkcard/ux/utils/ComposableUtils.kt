/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

package com.microblink.blinkcard.ux.utils

import androidx.compose.runtime.Composable
import com.microblink.blinkcard.ux.R
import com.microblink.blinkcard.ux.components.ErrorDialog
import com.microblink.blinkcard.ux.components.HelpScreenPage
import com.microblink.blinkcard.ux.components.HelpScreens
import com.microblink.blinkcard.ux.state.ErrorState
import com.microblink.blinkcard.ux.theme.BlinkCardTheme

@Composable
fun fillHelpScreensBlinkCard(): HelpScreens {
    return HelpScreens(
        onboardingDialogPage = HelpScreenPage(
            pageImage = R.drawable.mb_blinkcard_onboarding,
            pageTitle = BlinkCardTheme.sdkStrings.helpDialogsStrings.onboardingTitle,
            pageMessage = BlinkCardTheme.sdkStrings.helpDialogsStrings.onboardingMessage,
        ),
        helpDialogPages = listOf(
            HelpScreenPage(
                pageImage = R.drawable.mb_blinkcard_help_page_one,
                pageTitle = BlinkCardTheme.sdkStrings.helpDialogsStrings.helpTitles[0],
                pageMessage = BlinkCardTheme.sdkStrings.helpDialogsStrings.helpMessages[0]
            ), HelpScreenPage(
                pageImage = R.drawable.mb_blinkcard_help_page_two,
                pageTitle = BlinkCardTheme.sdkStrings.helpDialogsStrings.helpTitles[1],
                pageMessage = BlinkCardTheme.sdkStrings.helpDialogsStrings.helpMessages[1]
            ), HelpScreenPage(
                pageImage = R.drawable.mb_blinkcard_help_page_three,
                pageTitle = BlinkCardTheme.sdkStrings.helpDialogsStrings.helpTitles[2],
                pageMessage = BlinkCardTheme.sdkStrings.helpDialogsStrings.helpMessages[2]
            ), HelpScreenPage(
                pageImage = R.drawable.mb_blinkcard_help_page_four,
                pageTitle = BlinkCardTheme.sdkStrings.helpDialogsStrings.helpTitles[3],
                pageMessage = BlinkCardTheme.sdkStrings.helpDialogsStrings.helpMessages[3]
            )
        )
    )
}

@Composable
fun fillErrorDialogsBlinkCard(
    onRetry: () -> Unit,
    onDoneError: () -> Unit
): Map<ErrorState, @Composable () -> Unit> {
    return mapOf(
        ErrorState.NoError to {},
        ErrorState.ErrorInvalidLicense to
                {
                    ErrorDialog(
                        com.microblink.blinkcard.ux.R.string.mb_blinkcard_license_locked,
                        null,
                        com.microblink.blinkcard.ux.R.string.mb_blinkcard_close,
                        onButtonClick = onDoneError
                    )
                },

        ErrorState.ErrorNetworkError to
                {
                    ErrorDialog(
                        com.microblink.blinkcard.ux.R.string.mb_blinkcard_license_locked,
                        null,
                        com.microblink.blinkcard.ux.R.string.mb_blinkcard_close,
                        onButtonClick = onDoneError
                    )
                },

        ErrorState.ErrorTimeoutExpired to
                {
                    ErrorDialog(
                        com.microblink.blinkcard.ux.R.string.mb_blinkcard_recognition_timeout_dialog_title,
                        com.microblink.blinkcard.ux.R.string.mb_blinkcard_recognition_timeout_dialog_message,
                        com.microblink.blinkcard.ux.R.string.mb_blinkcard_recognition_timeout_dialog_retry_button,
                        onButtonClick = onRetry
                    )
                },

        ErrorState.ErrorDocumentClassFiltered to
                {
                }
    )
}