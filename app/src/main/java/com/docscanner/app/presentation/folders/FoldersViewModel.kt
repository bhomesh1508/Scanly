package com.docscanner.app.presentation.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.docscanner.app.domain.model.Folder
import com.docscanner.app.domain.repository.FolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoldersViewModel @Inject constructor(
    private val folderRepository: FolderRepository
) : ViewModel() {

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders.asStateFlow()

    init {
        viewModelScope.launch {
            folderRepository.getAllFolders().collect {
                _folders.value = it
            }
        }
    }

    fun createFolder(name: String, color: Long) {
        viewModelScope.launch {
            folderRepository.createFolder(name, color)
        }
    }

    fun renameFolder(folderId: String, newName: String) {
        viewModelScope.launch {
            folderRepository.renameFolder(folderId, newName)
        }
    }

    fun deleteFolder(folderId: String) {
        viewModelScope.launch {
            folderRepository.deleteFolder(folderId)
        }
    }

    fun changeColor(folderId: String, color: Long) {
        viewModelScope.launch {
            folderRepository.changeFolderColor(folderId, color)
        }
    }
}
