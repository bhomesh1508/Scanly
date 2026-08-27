package com.docscanner.app.presentation.editor;

import androidx.lifecycle.SavedStateHandle;
import com.docscanner.app.domain.repository.DocumentRepository;
import com.docscanner.app.service.filter.ImageFilterService;
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
public final class EditorViewModel_Factory implements Factory<EditorViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<DocumentRepository> documentRepositoryProvider;

  private final Provider<ImageFilterService> imageFilterServiceProvider;

  private EditorViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DocumentRepository> documentRepositoryProvider,
      Provider<ImageFilterService> imageFilterServiceProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.documentRepositoryProvider = documentRepositoryProvider;
    this.imageFilterServiceProvider = imageFilterServiceProvider;
  }

  @Override
  public EditorViewModel get() {
    return newInstance(savedStateHandleProvider.get(), documentRepositoryProvider.get(), imageFilterServiceProvider.get());
  }

  public static EditorViewModel_Factory create(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DocumentRepository> documentRepositoryProvider,
      Provider<ImageFilterService> imageFilterServiceProvider) {
    return new EditorViewModel_Factory(savedStateHandleProvider, documentRepositoryProvider, imageFilterServiceProvider);
  }

  public static EditorViewModel newInstance(SavedStateHandle savedStateHandle,
      DocumentRepository documentRepository, ImageFilterService imageFilterService) {
    return new EditorViewModel(savedStateHandle, documentRepository, imageFilterService);
  }
}
