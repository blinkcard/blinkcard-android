/**
 * Copyright (c) Microblink. All rights reserved. This code is provided for
 * use as-is and may not be copied, modified, or redistributed.
 */

package com.microblink.blinkcard.ux.scanning

import com.microblink.blinkcard.core.session.BlinkCardScanningResult
import com.microblink.blinkcard.ux.utils.ErrorReason

interface BlinkCardScanningDoneHandler {
    fun onScanningFinished(result: BlinkCardScanningResult)

    fun onScanningCanceled()

    fun onError(error: ErrorReason)
}