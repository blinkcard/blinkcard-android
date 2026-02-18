/**
 * Copyright (c) Microblink. All rights reserved. This code is provided for
 * use as-is and may not be copied, modified, or redistributed.
 */

package com.microblink.blinkcard.ux.scanning

import com.microblink.blinkcard.core.result.ImageAnalysisDetectionStatus
import com.microblink.blinkcard.core.image.InputImage
import com.microblink.blinkcard.core.result.ScanningSide
import com.microblink.blinkcard.core.result.ScanningStatus
import com.microblink.blinkcard.core.result.enum.ProcessingStatus
import com.microblink.blinkcard.core.session.BlinkCardProcessResult
import com.microblink.blinkcard.core.session.DetectionStatus
import com.microblink.blinkcard.core.settings.ScanningSettings
import com.microblink.blinkcard.ux.state.UiScanningSide

/**
 * Translates [BlinkCardProcessResult] and other scanning session information into a
 * list of [BlinkCardScanningUxEvent] objects.
 *
 * This class is responsible for interpreting the results of the card
 * scanning process and generating user experience-related events that can be
 * used to update the UI or provide feedback to the user. It handles logic
 * related to card sides, timeouts, and various detection statuses.
 *
 */
class BlinkCardScanningUxTranslator : BlinkCardUxTranslator {

    private var currentSide = UiScanningSide.First

    /**
     * Translates the given [BlinkCardProcessResult] and [InputImage]
     * into a list of [BlinkCardScanningUxEvent] objects.
     *
     * This function analyzes the current state of the scanning session and the
     * results of the last image processing step to determine which UX events
     * should be generated.
     *
     * @param processResult The [BlinkCardProcessResult] from the scanning session.
     * @param inputImage The [InputImage] used for the process. Can be `null`.
     * @param scanningSettings The [ScanningSettings] used to configure scanning behavior.
     * @return A list of [BlinkCardScanningUxEvent] objects representing the user
     *         experience events that should be dispatched.
     */
    override suspend fun translate(
        processResult: BlinkCardProcessResult,
        inputImage: InputImage?,
        scanningSettings: ScanningSettings,
    ): List<BlinkCardScanningUxEvent> {
        val events = mutableListOf<BlinkCardScanningUxEvent>()

        val imageAnalysisResult = processResult.inputImageAnalysisResult

        if (processResult.resultCompleteness.isComplete()) {
            events.add(BlinkCardScanningUxEvent.ScanningDone)
            return events
        }

        when (currentSide) {
            UiScanningSide.First -> {
                if (processResult.resultCompleteness.scanningStatus == ScanningStatus.SideScanned) {
                    currentSide = UiScanningSide.Second
                    events.add(BlinkCardScanningUxEvent.RequestSide(UiScanningSide.Second))
                }
            }

            UiScanningSide.Second -> {
                if (processResult.inputImageAnalysisResult.scanningSide != ScanningSide.Second) {
                    currentSide = UiScanningSide.First
                }
            }

            else -> {}
        }

        if (events.isNotEmpty()) return events

        if (imageAnalysisResult.cardLocation != null) {
            events.add(
                if (inputImage != null) {
                    BlinkCardScanningUxEvent.CardLocatedLocation(
                        location = imageAnalysisResult.cardLocation!!,
                        inputImage = inputImage
                    )
                } else {
                    BlinkCardScanningUxEvent.CardLocated
                }
            )
        } else {
            events.add(BlinkCardScanningUxEvent.CardNotFound)
        }

        // below just one event can be generated, by following priorities
        var hasEvents = false

        when (imageAnalysisResult.processingStatus) {
            ProcessingStatus.FieldIdentificationFailed,
            ProcessingStatus.ImageReturnFailed -> {
                events.add(BlinkCardScanningUxEvent.CardNotFullyVisible)
                hasEvents = true
            }

            ProcessingStatus.AwaitingOtherSide -> {
                events.add(BlinkCardScanningUxEvent.RequestSide(currentSide))
                hasEvents = true
            }

            ProcessingStatus.ScanningWrongSide -> {
                events.add(BlinkCardScanningUxEvent.ScanningWrongSide)
                hasEvents = true
            }

            else -> {}
        }

        if (hasEvents) {
            events.add(
                BlinkCardScanningUxEvent.CardProcessResult(processResult)
            )
            return events
        }

        hasEvents = true

        when (imageAnalysisResult.detectionStatus) {
            DetectionStatus.CameraTooFar -> events.add(BlinkCardScanningUxEvent.CardTooFar)
            DetectionStatus.CameraTooClose,
            DetectionStatus.DocumentTooCloseToCameraEdge -> events.add(BlinkCardScanningUxEvent.CardTooClose)
            DetectionStatus.DocumentPartiallyVisible -> events.add(BlinkCardScanningUxEvent.CardNotFullyVisible)
            DetectionStatus.CameraAngleTooSteep -> events.add(BlinkCardScanningUxEvent.CardTooTilted)
            else -> {
                hasEvents = false
            }
        }

        if (hasEvents) {
            events.add(BlinkCardScanningUxEvent.CardProcessResult(processResult))
            return events
        }

        hasEvents = true

        if (scanningSettings.skipImagesWithBlur &&
            imageAnalysisResult.processingStatus == ProcessingStatus.ImagePreprocessingFailed &&
            imageAnalysisResult.blurDetectionStatus == ImageAnalysisDetectionStatus.Detected
        ) {
            events.add(BlinkCardScanningUxEvent.BlurDetected)
        } else {
            hasEvents = false
        }

        if (hasEvents) {
            events.add(BlinkCardScanningUxEvent.CardProcessResult(processResult))
            return events
        }

        events.add(BlinkCardScanningUxEvent.CardNotFound)
        events.add(BlinkCardScanningUxEvent.RequestSide(currentSide))
        events.add(BlinkCardScanningUxEvent.CardProcessResult(processResult))
        return events
    }

}