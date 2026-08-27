package com.docscanner.app.service.filter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.docscanner.app.domain.model.FilterType
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class ImageFilterService @Inject constructor() {

    fun applyFilter(bitmap: Bitmap, filterType: FilterType): Bitmap {
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()

        when (filterType) {
            FilterType.ORIGINAL -> return bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
            FilterType.AUTO_ENHANCE -> {
                val cm = ColorMatrix().apply {
                    val scale = 1.1f
                    val translate = 10f
                    set(floatArrayOf(
                        scale, 0f, 0f, 0f, translate,
                        0f, scale, 0f, 0f, translate,
                        0f, 0f, scale, 0f, translate,
                        0f, 0f, 0f, 1f, 0f
                    ))
                }
                paint.colorFilter = ColorMatrixColorFilter(cm)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
            }
            FilterType.GRAYSCALE -> {
                val cm = ColorMatrix().apply { setSaturation(0f) }
                paint.colorFilter = ColorMatrixColorFilter(cm)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
            }
            FilterType.BLACK_WHITE -> {
                val cm = ColorMatrix().apply {
                    setSaturation(0f)
                    val scale = 128f
                    val translate = -128f * scale
                    postConcat(ColorMatrix(floatArrayOf(
                        scale, 0f, 0f, 0f, translate,
                        0f, scale, 0f, 0f, translate,
                        0f, 0f, scale, 0f, translate,
                        0f, 0f, 0f, 1f, 0f
                    )))
                }
                paint.colorFilter = ColorMatrixColorFilter(cm)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
            }
            FilterType.HIGH_CONTRAST -> {
                val cm = ColorMatrix().apply {
                    val scale = 1.5f
                    val translate = (-0.5f * scale + 0.5f) * 255f
                    set(floatArrayOf(
                        scale, 0f, 0f, 0f, translate,
                        0f, scale, 0f, 0f, translate,
                        0f, 0f, scale, 0f, translate,
                        0f, 0f, 0f, 1f, 0f
                    ))
                }
                paint.colorFilter = ColorMatrixColorFilter(cm)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
            }
            FilterType.COLOR_BOOST -> {
                val cm = ColorMatrix().apply { setSaturation(1.5f) }
                paint.colorFilter = ColorMatrixColorFilter(cm)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
            }
            FilterType.SHARPEN -> {
                val pixels = IntArray(bitmap.width * bitmap.height)
                bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                val width = bitmap.width
                val height = bitmap.height
                val newPixels = IntArray(width * height)
                
                val kernel = floatArrayOf(
                    0f, -1f, 0f,
                    -1f, 5f, -1f,
                    0f, -1f, 0f
                )
                
                for (y in 1 until height - 1) {
                    for (x in 1 until width - 1) {
                        var r = 0f; var g = 0f; var b = 0f
                        var k = 0
                        for (ky in -1..1) {
                            for (kx in -1..1) {
                                val pixel = pixels[(y + ky) * width + (x + kx)]
                                val weight = kernel[k++]
                                r += ((pixel shr 16) and 0xFF) * weight
                                g += ((pixel shr 8) and 0xFF) * weight
                                b += (pixel and 0xFF) * weight
                            }
                        }
                        val nr = r.toInt().coerceIn(0, 255)
                        val ng = g.toInt().coerceIn(0, 255)
                        val nb = b.toInt().coerceIn(0, 255)
                        newPixels[y * width + x] = (0xFF shl 24) or (nr shl 16) or (ng shl 8) or nb
                    }
                }
                result.setPixels(newPixels, 0, width, 0, 0, width, height)
            }
            FilterType.LIGHTEN -> {
                val cm = ColorMatrix().apply {
                    val translate = 30f
                    set(floatArrayOf(
                        1f, 0f, 0f, 0f, translate,
                        0f, 1f, 0f, 0f, translate,
                        0f, 0f, 1f, 0f, translate,
                        0f, 0f, 0f, 1f, 0f
                    ))
                }
                paint.colorFilter = ColorMatrixColorFilter(cm)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
            }
            FilterType.DARKEN -> {
                val cm = ColorMatrix().apply {
                    val translate = -30f
                    set(floatArrayOf(
                        1f, 0f, 0f, 0f, translate,
                        0f, 1f, 0f, 0f, translate,
                        0f, 0f, 1f, 0f, translate,
                        0f, 0f, 0f, 1f, 0f
                    ))
                }
                paint.colorFilter = ColorMatrixColorFilter(cm)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
            }
        }
        return result
    }

    fun applyAdjustments(bitmap: Bitmap, brightness: Float, contrast: Float): Bitmap {
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        
        val cm = ColorMatrix().apply {
            val scale = contrast
            val translate = brightness + (-0.5f * scale + 0.5f) * 255f
            set(floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            ))
        }
        
        val paint = Paint().apply { colorFilter = ColorMatrixColorFilter(cm) }
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }

    fun generateThumbnail(bitmap: Bitmap, maxSize: Int = 256): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val ratio = maxSize.toFloat() / max(width, height)
        if (ratio >= 1f) return bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
        
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
