package com.joshgm3z.triplerocktv.core.util

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

fun String.getQrCode(
    width: Int = 512,
    height: Int = 512,
): Bitmap? = try {
    Logger.debug("textToEncode = [$this]")
    BarcodeEncoder().encodeBitmap(
        this,
        BarcodeFormat.QR_CODE,
        width, height
    )
} catch (e: Exception) {
    Logger.error(e.message.toString())
    e.printStackTrace()
    null
}

