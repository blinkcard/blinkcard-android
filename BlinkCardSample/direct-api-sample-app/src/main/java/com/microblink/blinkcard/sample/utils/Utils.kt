package com.microblink.blinkcard.sample.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun getBitmapFromAsset(context: Context, filePath: String): Bitmap? {
    return try {
        context.assets.open(filePath).use { BitmapFactory.decodeStream(it) }
    } catch (_: Exception) {
        null
    }
}
