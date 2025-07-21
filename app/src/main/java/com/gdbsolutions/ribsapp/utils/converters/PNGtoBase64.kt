package com.gdbsolutions.ribsapp.utils.converters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream

fun Context.drawableToBase64(drawableResId: Int): String {
    val drawable = ContextCompat.getDrawable(this, drawableResId) ?: return ""
    val bitmap = (drawable as BitmapDrawable).bitmap
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val byteArray = stream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}