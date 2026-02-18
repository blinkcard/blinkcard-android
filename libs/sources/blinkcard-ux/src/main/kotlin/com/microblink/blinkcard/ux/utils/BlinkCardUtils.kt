/**
 * Copyright (c) Microblink. All rights reserved. This code is provided for
 * use as-is and may not be copied, modified, or redistributed.
 */

package com.microblink.blinkcard.ux.utils

import com.microblink.blinkcard.core.BlinkCardSdk
import com.microblink.blinkcard.core.utils.sendPingletsIfAllowed
import com.microblink.blinkcard.core.ping.config.PingSendTriggerPoint

internal const val pingletOrientationDelayMs = 1000L

fun onCameraPermissionCheck(sessionNumber: Int) {
    UxPingletTracker.CameraPermission.trackCameraPermissionCheck(sessionNumber)
    BlinkCardSdk.sendPingletsIfAllowed(PingSendTriggerPoint.CameraPermissionCheck)
}

fun onCameraPermissionRequest(sessionNumber: Int) {
    UxPingletTracker.CameraPermission.trackCameraPermissionRequest(sessionNumber)
}

fun onCameraPermissionUserResponse(sessionNumber: Int, cameraPermissionGranted: Boolean) {
    UxPingletTracker.CameraPermission.trackCameraPermissionUserResponse(
        cameraPermissionGranted,
        sessionNumber
    )
}

fun onCameraPreviewStarted(sessionNumber: Int) {
    UxPingletTracker.UxEvent.trackSimpleEvent(
        UxPingletTracker.UxEvent.SimpleUxEventType.CameraStarted,
        sessionNumber
    )
    BlinkCardSdk.sendPingletsIfAllowed(PingSendTriggerPoint.CameraStarted)
}

fun onCameraPreviewStopped(sessionNumber: Int) {
    UxPingletTracker.UxEvent.trackSimpleEvent(
        UxPingletTracker.UxEvent.SimpleUxEventType.CameraClosed,
        sessionNumber
    )
}

fun onCloseButtonClicked(sessionNumber: Int) {
    UxPingletTracker.UxEvent.trackSimpleEvent(
        UxPingletTracker.UxEvent.SimpleUxEventType.CloseButtonClicked,
        sessionNumber
    )
}

fun onAppMovedToBackground(sessionNumber: Int) {
    UxPingletTracker.UxEvent.trackSimpleEvent(
        UxPingletTracker.UxEvent.SimpleUxEventType.AppMovedToBackground,
        sessionNumber
    )
}