/**
 * Copyright (c) Microblink. All rights reserved. This code is provided for
 * use as-is and may not be copied, modified, or redistributed.
 */

package com.microblink.blinkcard.ux.scanning

import com.microblink.blinkcard.core.geometry.Quadrilateral
import com.microblink.blinkcard.core.image.InputImage
import com.microblink.blinkcard.core.session.BlinkCardProcessResult
import com.microblink.blinkcard.ux.state.UiScanningSide

sealed interface BlinkCardScanningUxEvent {

    /**
     * Request to scan a specific side of the card.
     *
     * @property side Specified the [UiScanningSide] that is to be scanned.
     */
    data class RequestSide(val side: UiScanningSide) : BlinkCardScanningUxEvent

    /**
     * Camera image is too blurry for accurate card capture.
     */
    object BlurDetected : BlinkCardScanningUxEvent

    /**
     * No card has been located by the camera.
     */
    object CardNotFound : BlinkCardScanningUxEvent

    /**
     * Indicates the wrong side of the card is being presented.
     */
    object ScanningWrongSide : BlinkCardScanningUxEvent

    /**
     * The card has been located
     */
    object CardLocated : BlinkCardScanningUxEvent

    /**
     * The card has been located by the recognizer.
     *
     * @property location Specified the exact coordinates of the card.
     * @property inputImage Image of the located card.
     */
    data class CardLocatedLocation(
        val location: Quadrilateral,
        val inputImage: InputImage
    ) : BlinkCardScanningUxEvent

    /**
     * Card is too far from the camera.
     */
    object CardTooFar : BlinkCardScanningUxEvent

    /**
     * Card is too close to the camera.
     */
    object CardTooClose : BlinkCardScanningUxEvent

    /**
     * Part of card is occluded or partially outside of the camera.
     */
    object CardNotFullyVisible : BlinkCardScanningUxEvent

    /**
     * Card is not parallel to the camera plane.
     */
    object CardTooTilted : BlinkCardScanningUxEvent

    /**
     * Scanning has been successfully completed.
     */
    object ScanningDone : BlinkCardScanningUxEvent

    /**
     * Event that holds all the information about the currently scan.
     *
     * @property processResult Represents the result of card processing.
     */
    data class CardProcessResult(
        val processResult: BlinkCardProcessResult
    ) : BlinkCardScanningUxEvent
}