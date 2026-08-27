package com.docscanner.app.presentation.scanner

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.docscanner.app.domain.repository.DocumentRepository
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ScanState { IDLE, SCANNING, PROCESSING, COMPLETE, ERROR }

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val documentRepository: DocumentRepository
) : ViewModel() {

    private val _scanState = MutableStateFlow(ScanState.IDLE)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _scannedPages = MutableStateFlow<List<Uri>>(emptyList())
    val scannedPages: StateFlow<List<Uri>> = _scannedPages.asStateFlow()

    private val _pdfUri = MutableStateFlow<Uri?>(null)
    val pdfUri: StateFlow<Uri?> = _pdfUri.asStateFlow()

    fun processScanResult(result: GmsDocumentScanningResult) {
        _scanState.value = ScanState.PROCESSING
        try {
            _scannedPages.value = result.pages?.map { it.imageUri } ?: emptyList()
            _pdfUri.value = result.pdf?.uri
            _scanState.value = ScanState.COMPLETE
        } catch (e: Exception) {
            _scanState.value = ScanState.ERROR
        }
    }

    fun createDocument(title: String, onDocumentCreated: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val pagePaths = _scannedPages.value.map { it.toString() }
                val pdfPath = _pdfUri.value?.toString()
                val document = documentRepository.createDocument(title, pagePaths, pdfPath)
                onDocumentCreated(document.id)
            } catch (e: Exception) {
                _scanState.value = ScanState.ERROR
            }
        }
    }

    fun clearScanResult() {
        _scannedPages.value = emptyList()
        _pdfUri.value = null
        _scanState.value = ScanState.IDLE
    }
}
