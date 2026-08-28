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

    private val _rotation = MutableStateFlow(0)
    val rotation: StateFlow<Int> = _rotation.asStateFlow()

    private val _previewBitmap = MutableStateFlow<Bitmap?>(null)
    val previewBitmap: StateFlow<Bitmap?> = _previewBitmap.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        viewModelScope.launch {
            documentRepository.getDocumentById(documentId).collect {
                _document.value = it
            }
        }
        viewModelScope.launch {
            documentRepository.getPages(documentId).collect { pageList ->
                _pages.value = pageList
                if (pageList.isNotEmpty()) {
                    val safeIndex = _selectedPageIndex.value.coerceIn(0, pageList.size - 1)
                    _selectedPageIndex.value = safeIndex
                    val currentPage = pageList[safeIndex]
                    _currentFilter.value = currentPage.filter
                    _brightness.value = currentPage.brightness
                    _contrast.value = currentPage.contrast
                    _rotation.value = currentPage.rotation
                }
            }
        }
    }

    fun selectPage(index: Int) {
        val pageList = _pages.value
        if (index in pageList.indices) {
            _selectedPageIndex.value = index
            val page = pageList[index]
            _currentFilter.value = page.filter
            _brightness.value = page.brightness
            _contrast.value = page.contrast
            _rotation.value = page.rotation
        }
    }

    fun applyFilter(filterType: FilterType) {
        _currentFilter.value = filterType
    }

    fun adjustBrightness(value: Float) {
        _brightness.value = value.coerceIn(-1f, 1f)
    }

    fun adjustContrast(value: Float) {
        _contrast.value = value.coerceIn(-1f, 1f)
    }

    fun resetBrightness() {
        _brightness.value = 0f
    }

    fun resetContrast() {
        _contrast.value = 0f
    }

    fun resetAdjustments() {
        _brightness.value = 0f
        _contrast.value = 0f
    }

    fun rotatePage() {
        _rotation.value = (_rotation.value + 90) % 360
    }

    fun deletePage(pageId: String) {
        viewModelScope.launch {
            val currentIndex = _selectedPageIndex.value
            documentRepository.deletePage(pageId)
            val newSize = (_pages.value.size - 1).coerceAtLeast(0)
            if (newSize > 0) {
                _selectedPageIndex.value = currentIndex.coerceIn(0, newSize - 1)
            } else {
                _selectedPageIndex.value = 0
            }
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

    fun saveChanges(onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            _isSaving.value = true
            val pageList = _pages.value
            val currentPage = pageList.getOrNull(_selectedPageIndex.value)
            if (currentPage != null) {
                val updatedPage = currentPage.copy(
                    filter = _currentFilter.value,
                    brightness = _brightness.value,
                    contrast = _contrast.value,
                    rotation = _rotation.value
                )
                documentRepository.updatePage(updatedPage)
            }
            _document.value?.let { doc ->
                documentRepository.updateDocument(doc.copy(updatedAt = System.currentTimeMillis()))
            }
            _isSaving.value = false
            onSaved()
        }
    }
}
