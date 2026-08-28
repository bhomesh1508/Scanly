package com.docscanner.app.service.pdf

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.print.PrintManager
import androidx.core.content.FileProvider
import com.docscanner.app.domain.model.MarginPreset
import com.docscanner.app.domain.model.Page
import com.docscanner.app.domain.model.PageSize
import com.docscanner.app.domain.model.PdfExportOptions
import com.docscanner.app.domain.model.QualityLevel
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfGeneratorService @Inject constructor() {

    fun generatePdf(pages: List<Page>, options: PdfExportOptions, outputFile: File): Result<File> {
        val document = PdfDocument()
        return try {
            pages.forEachIndexed { index, page ->
                val sampleSize = when (options.quality) {
                    QualityLevel.COMPRESSED -> 2
                    else -> 1
                }
                val decodeOptions = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                    inPreferredConfig = android.graphics.Bitmap.Config.RGB_565
                }
                val imagePath = page.processedImagePath.ifBlank { page.originalImagePath }
                val bitmap = BitmapFactory.decodeFile(imagePath, decodeOptions) ?: return@forEachIndexed
                
                try {
                    val (docWidth, docHeight) = getDimensions(options.pageSize, bitmap.width, bitmap.height)
                    val margin = getMarginPoints(options.margin)

                    val pageInfo = PdfDocument.PageInfo.Builder(docWidth, docHeight, index + 1).create()
                    val pdfPage = document.startPage(pageInfo)
                    val canvas = pdfPage.canvas

                    val availableWidth = docWidth - 2 * margin
                    val availableHeight = docHeight - 2 * margin

                    val scale = minOf(
                        availableWidth.toFloat() / bitmap.width,
                        availableHeight.toFloat() / bitmap.height
                    )

                    val scaledWidth = bitmap.width * scale
                    val scaledHeight = bitmap.height * scale

                    val left = margin + (availableWidth - scaledWidth) / 2
                    val top = margin + (availableHeight - scaledHeight) / 2

                    canvas.drawBitmap(bitmap, null, android.graphics.RectF(left, top, left + scaledWidth, top + scaledHeight), null)
                    document.finishPage(pdfPage)
                } finally {
                    bitmap.recycle()
                }
            }

            FileOutputStream(outputFile).use { fos ->
                document.writeTo(fos)
            }

            Result.success(outputFile)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            try {
                document.close()
            } catch (_: Exception) {}
        }
    }

    fun sharePdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = android.content.ClipData.newRawUri("", uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Share PDF")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }


    fun printPdf(context: Context, file: File) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        // A complete implementation requires a custom PrintDocumentAdapter.
    }

    private fun getDimensions(pageSize: PageSize, imgWidth: Int, imgHeight: Int): Pair<Int, Int> {
        return when (pageSize) {
            PageSize.A4 -> Pair(595, 842)
            PageSize.LETTER -> Pair(612, 792)
            PageSize.LEGAL -> Pair(612, 1008)
            PageSize.A3 -> Pair(842, 1191)
            PageSize.A5 -> Pair(420, 595)
            PageSize.AUTO -> {
                val scale = 595f / imgWidth
                Pair(595, (imgHeight * scale).toInt())
            }
        }
    }

    private fun getMarginPoints(marginPreset: MarginPreset): Int {
        return when (marginPreset) {
            MarginPreset.NONE -> 0
            MarginPreset.SMALL -> 36
            MarginPreset.NORMAL -> 72
            MarginPreset.LARGE -> 108
        }
    }
}
