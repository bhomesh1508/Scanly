package com.docscanner.app.presentation.editor

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.docscanner.app.domain.model.Document
import com.docscanner.app.domain.model.FilterType
import com.docscanner.app.domain.model.Page
import com.docscanner.app.domain.repository.DocumentRepository
import com.docscanner.app.service.filter.ImageFilterService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val documentRepository: DocumentRepository,
    private val imageFilterService: ImageFilterService
) : ViewModel() {

    val documentId: String = checkNotNull(savedStateHandle["documentId"])

    private val _document = MutableStateFlow<Document?>(null)
    val document: StateFlow<Document?> = _document.asStateFlow()

    private val _pages = MutableStateFlow<List<Page>>(emptyList())
    val pages: StateFlow<List<Page>> = _pages.asStateFlow()

    private val _selectedPageIndex = MutableStateFlow(0)
    val selectedPageIndex: StateFlow<Int> = _selectedPageIndex.asStateFlow()

    private val _currentFilter = MutableStateFlow(FilterType.ORIGINAL)
    val currentFilter: StateFlow<FilterType> = _currentFilter.asStateFlow()

    private val _brightness = MutableStateFlow(0f)
    val brightness: StateFlow<Float> = _brightness.asStateFlow()

    private val _contrast = MutableStateFlow(0f)
    val contrast: StateFlow<Float> = _contrast.asStateFlow()

    private val _previewBitmap = MutableStateFlow<Bitmap?>(null)
    val previewBitmap: StateFlow<Bitmap?> = _previewBitmap.asStateFlow()

    init {
        // Mock load
    }

    fun selectPage(index: Int) {
        _selectedPageIndex.value = index
    }

    fun applyFilter(filterType: FilterType) {
        _currentFilter.value = filterType
        updatePreview()
    }

    fun adjustBrightness(value: Float) {
        _brightness.value = value
        updatePreview()
    }

    fun adjustContrast(value: Float) {
        _contrast.value = value
        updatePreview()
    }

    private fun updatePreview() {
        // Implementation for updating preview with filter and adjustments
    }

    fun rotatePage() {
        // Implementation
    }

    fun deletePage(pageId: String) {
        viewModelScope.launch {
            documentRepository.deletePage(pageId)
        }
    }

    fun duplicatePage(pageId: String) {
        viewModelScope.launch {
            documentRepository.duplicatePage(pageId)
        }
    }

    fun reorderPages(pageIds: List<String>) {
        viewModelScope.launch {
            documentRepository.reorderPages(documentId, pageIds)
        }
    }

    fun addMorePages(pageUris: List<Uri>) {
        viewModelScope.launch {
            documentRepository.addPages(documentId, pageUris.map { it.toString() })
        }
    }

    fun saveChanges() {
        viewModelScope.launch {
            documentRepository.updateDocument(_document.value!!)
        }
    }
}
