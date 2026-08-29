package com.docscanner.app.presentation.editor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
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

    private var originalPreviewBitmap: Bitmap? = null
    private var lastLoadedPageIndex: Int = -1

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
                    if (_selectedPageIndex.value != safeIndex) {
                        _selectedPageIndex.value = safeIndex
                    }
                    val currentPage = pageList[safeIndex]
                    // Only update these if we are loading for the very first time
                    if (lastLoadedPageIndex == -1) {
                        _currentFilter.value = currentPage.filter
                        _brightness.value = currentPage.brightness
                        _contrast.value = currentPage.contrast
                        _rotation.value = currentPage.rotation
                    }
                }
            }
        }

        viewModelScope.launch {
            combine(
                _pages,
                _selectedPageIndex,
                _currentFilter,
                _brightness,
                _contrast
            ) { pagesList, index, filter, brightness, contrast ->
                listOf(pagesList, index, filter, brightness, contrast)
            }.collectLatest { args ->
                val pagesList = args[0] as List<Page>
                val index = args[1] as Int
                val filter = args[2] as FilterType
                val brightness = args[3] as Float
                val contrast = args[4] as Float

                val page = pagesList.getOrNull(index) ?: return@collectLatest

                withContext(Dispatchers.Default) {
                    try {
                        if (originalPreviewBitmap == null || lastLoadedPageIndex != index) {
                            val options = BitmapFactory.Options().apply {
                                inSampleSize = 4
                                inPreferredConfig = Bitmap.Config.ARGB_8888
                                inMutable = true
                            }
                            originalPreviewBitmap = BitmapFactory.decodeFile(page.originalImagePath, options)
                            lastLoadedPageIndex = index
                        }
                        originalPreviewBitmap?.let { orig ->
                            var bmp = imageFilterService.applyFilter(orig, filter)
                            if (brightness != 0f || contrast != 0f) {
                                val temp = bmp
                                bmp = imageFilterService.applyAdjustments(bmp, brightness, contrast)
                                if (temp != orig) temp.recycle()
                            }
                            _previewBitmap.value = bmp
                        } ?: run {
                            _previewBitmap.value = null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _previewBitmap.value = null
                    }
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

    fun deletePage(pageId: String, onDocumentEmpty: () -> Unit) {
        viewModelScope.launch {
            val currentIndex = _selectedPageIndex.value
            documentRepository.deletePage(pageId)
            val newSize = (_pages.value.size - 1).coerceAtLeast(0)
            if (newSize > 0) {
                _selectedPageIndex.value = currentIndex.coerceIn(0, newSize - 1)
            } else {
                _selectedPageIndex.value = 0
                documentRepository.moveToTrash(documentId)
                withContext(Dispatchers.Main) {
                    onDocumentEmpty()
                }
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
        viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            val pageList = _pages.value
            val currentPage = pageList.getOrNull(_selectedPageIndex.value)
            if (currentPage != null) {
                var processedPath = currentPage.processedImagePath

                // Apply to full-resolution image if filters or adjustments exist
                val hasChanges = _currentFilter.value != FilterType.ORIGINAL || _brightness.value != 0f || _contrast.value != 0f
                if (hasChanges) {
                    val options = BitmapFactory.Options().apply {
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                        inMutable = true
                    }
                    val origBitmap = BitmapFactory.decodeFile(currentPage.originalImagePath, options)
                    if (origBitmap != null) {
                        var result = imageFilterService.applyFilter(origBitmap, _currentFilter.value)
                        if (_brightness.value != 0f || _contrast.value != 0f) {
                            result = imageFilterService.applyAdjustments(result, _brightness.value, _contrast.value)
                        }

                        val origFile = File(currentPage.originalImagePath)
                        val newFile = File(origFile.parentFile, "${currentPage.id}_processed_${System.currentTimeMillis()}.jpg")
                        FileOutputStream(newFile).use { out ->
                            result.compress(Bitmap.CompressFormat.JPEG, 95, out)
                        }
                        processedPath = newFile.absolutePath
                        if (result != origBitmap) {
                            result.recycle()
                        }
                        origBitmap.recycle()
                    }
                } else {
                    processedPath = currentPage.originalImagePath
                }

                val updatedPage = currentPage.copy(
                    filter = _currentFilter.value,
                    brightness = _brightness.value,
                    contrast = _contrast.value,
                    rotation = _rotation.value,
                    processedImagePath = processedPath,
                    thumbnailPath = processedPath // Also update thumbnail path for UI consistency
                )
                documentRepository.updatePage(updatedPage)
            }
            _document.value?.let { doc ->
                documentRepository.updateDocument(doc.copy(updatedAt = System.currentTimeMillis()))
            }
            _isSaving.value = false
            withContext(Dispatchers.Main) {
                onSaved()
            }
        }
    }
}
