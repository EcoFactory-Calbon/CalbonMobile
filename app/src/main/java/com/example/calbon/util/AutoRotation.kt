package com.example.calbon.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import com.cloudinary.android.preprocess.Preprocess
import java.io.InputStream

class AutoRotation(private val context: Context, private val imageUri: Uri) : Preprocess<Bitmap> {
    override fun execute(context: Context, resource: Bitmap): Bitmap {
        val rotationAngle = getRotationAngleFromExif(imageUri)
        if (rotationAngle == 0) return resource

        val matrix = Matrix()
        matrix.postRotate(rotationAngle.toFloat())
        return Bitmap.createBitmap(resource, 0, 0, resource.width, resource.height, matrix, true)
    }

    private fun getRotationAngleFromExif(uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    ExifInterface(inputStream)
                else
                    ExifInterface(uri.path ?: return 0)

                when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            } ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
}
