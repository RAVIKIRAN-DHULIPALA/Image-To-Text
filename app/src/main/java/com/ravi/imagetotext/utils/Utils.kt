package com.ravi.imagetotext.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream


object Utils {
    fun bitmapToByteArray(bitmap: Bitmap):ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun byteArrayToBitmap(byteArray: ByteArray):Bitmap{
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}