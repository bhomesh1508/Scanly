package com.docscanner.app.presentation.folders;

import androidx.lifecycle.SavedStateHandle;
import com.docscanner.app.domain.repository.DocumentRepository;
import com.docscanner.app.domain.repository.FolderRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class FolderDetailViewModel_Factory implements Factory<FolderDetailViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<DocumentRepository> documentRepositoryProvider;

  private final Provider<FolderRepository> folderRepositoryProvider;

  private FolderDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DocumentRepository> documentRepositoryProvider,
      Provider<FolderRepository> folderRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.documentRepositoryProvider = documentRepositoryProvider;
    this.folderRepositoryProvider = folderRepositoryProvider;
  }

  @Override
  public FolderDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), documentRepositoryProvider.get(), folderRepositoryProvider.get());
  }

  public static FolderDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DocumentRepository> documentRepositoryProvider,
      Provider<FolderRepository> folderRepositoryProvider) {
    return new FolderDetailViewModel_Factory(savedStateHandleProvider, documentRepositoryProvider, folderRepositoryProvider);
  }

  public static FolderDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      DocumentRepository documentRepository, FolderRepository folderRepository) {
    return new FolderDetailViewModel(savedStateHandle, documentRepository, folderRepository);
  }
}
