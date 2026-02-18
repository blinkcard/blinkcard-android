/**
 * Copyright (c) Microblink. All rights reserved. This code is provided for
 * use as-is and may not be copied, modified, or redistributed.
 */

package com.microblink.blinkcard.ux.scanning

import com.microblink.blinkcard.core.image.InputImage
import com.microblink.blinkcard.core.session.BlinkCardProcessResult
import com.microblink.blinkcard.core.settings.ScanningSettings

/**
 * An interface that represents the translation process from [BlinkCardProcessResult] to [BlinkCardScanningUxEvent].
 *
 * The translator converts scanning results into UX events based on the provided settings
 */
interface BlinkCardUxTranslator {
    suspend fun translate(
        processResult: BlinkCardProcessResult,
        inputImage: InputImage?,
        scanningSettings: ScanningSettings,
    ): List<BlinkCardScanningUxEvent>
}