/**
 * Copyright (c) Microblink. All rights reserved. This code is provided for
 * use as-is and may not be copied, modified, or redistributed.
 */

package com.microblink.blinkcard.ux.scanning

/**
 * Handler for BlinkCard scanning UX events.
 *
 * Implement this interface to receive UX events during the scanning process.
 * Events are dispatched by the [BlinkCardAnalyzer] after translating process results.
 */
interface BlinkCardScanningUxEventHandler {
    /**
     * Called when UX events are generated during scanning.
     *
     * @param events The list of [BlinkCardScanningUxEvent] objects to handle.
     */
    fun onUxEvents(events: List<BlinkCardScanningUxEvent>)
}

