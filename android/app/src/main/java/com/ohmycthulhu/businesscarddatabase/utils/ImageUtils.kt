package com.ohmycthulhu.businesscarddatabase.utils

import android.graphics.Bitmap
import android.util.Log
import kotlinx.android.synthetic.main.activity_new_card_photo.*

class ImageUtils {
    companion object {
        fun limitImageSize (image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
            Log.d("image_sizes", "Image size ${image.width} x ${image.height}")
            if (image.width > 2000 && image.width >= image.height) {
                return Bitmap.createBitmap(image, 0, 0, 2000, (2000 * (image.height / image.width.toFloat())).toInt())
            }
            if (image.height > 2000 && image.height >= image.width) {
                return Bitmap.createBitmap(image, 0, 0, (2000 * (image.width / image.height.toFloat())).toInt(), 2000)
            }
            return image
        }
    }
}