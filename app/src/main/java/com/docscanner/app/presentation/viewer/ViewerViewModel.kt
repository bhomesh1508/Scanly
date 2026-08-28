package com.docscanner.app.presentation.viewer

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.docscanner.app.domain.model.Document
import com.docscanner.app.domain.model.Page
import com.docscanner.app.domain.model.PdfExportOptions
import com.docscanner.app.domain.repository.DocumentRepository
import com.docscanner.app.service.pdf.PdfGeneratorService
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ViewerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val documentRepository: DocumentRepository,
    private val pdfGeneratorService: PdfGeneratorService
) : ViewModel() {

    val documentId: String = checkNotNull(savedStateHandle["documentId"])

    private val _document = MutableStateFlow<Document?>(null)
    val document: StateFlow<Document?> = _document.asStateFlow()

    private val _pages = MutableStateFlow<List<Page>>(emptyList())
    val pages: StateFlow<List<Page>> = _pages.asStateFlow()

    private val _currentPageIndex = MutableStateFlow(0)
    val currentPageIndex: StateFlow<Int> = _currentPageIndex.asStateFlow()

    private val _ocrText = MutableStateFlow<String?>(null)
    val ocrText: StateFlow<String?> = _ocrText.asStateFlow()

    private val _ocrLoading = MutableStateFlow(false)
    val ocrLoading: StateFlow<Boolean> = _ocrLoading.asStateFlow()

    init {
        viewModelScope.launch {
            documentRepository.getDocumentById(documentId).collect {
                _document.value = it
            }
        }
        viewModelScope.launch {
            documentRepository.getPages(documentId).collect {
                _pages.value = it
            }
        }
    }

    fun setPage(index: Int) {
        _currentPageIndex.value = index
    }

    fun runOcr(context: Context) {
        _ocrLoading.value = true
        _ocrText.value = null
        val page = _pages.value.getOrNull(_currentPageIndex.value)
        if (page != null) {
            try {
                // Dummy uri loading
                val uri = android.net.Uri.parse(page.processedImagePath)
                val image = InputImage.fromFilePath(context, uri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        _ocrText.value = visionText.text
                        _ocrLoading.value = false
                    }
                    .addOnFailureListener {
                        _ocrText.value = "Error extracting text."
                        _ocrLoading.value = false
                    }
            } catch (e: Exception) {
                _ocrLoading.value = false
            }
        } else {
            _ocrLoading.value = false
        }
    }

    fun copyOcrText(context: Context) {
        val text = _ocrText.value ?: return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("OCR Text", text)
        clipboard.setPrimaryClip(clip)
    }

    fun exportPdf(options: PdfExportOptions): Result<File> {
        val file = File("dummy.pdf")
        pdfGeneratorService.generatePdf(_pages.value, options, file)
        return Result.success(file)
    }

    fun sharePdf(context: Context, file: File) {
        pdfGeneratorService.sharePdf(context, file)
    }

    fun printDocument(context: Context, file: File) {
        pdfGeneratorService.printPdf(context, file)
    }

    fun renameDocument(newTitle: String) {
        viewModelScope.launch {
            documentRepository.renameDocument(documentId, newTitle)
        }
    }

    fun deleteDocument() {
        viewModelScope.launch {
            documentRepository.moveToTrash(documentId)
        }
    }
}
