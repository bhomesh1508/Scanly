package com.docscanner.app.presentation.viewer;

import androidx.lifecycle.SavedStateHandle;
import com.docscanner.app.domain.repository.DocumentRepository;
import com.docscanner.app.service.pdf.PdfGeneratorService;
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
public final class ViewerViewModel_Factory implements Factory<ViewerViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<DocumentRepository> documentRepositoryProvider;

  private final Provider<PdfGeneratorService> pdfGeneratorServiceProvider;

  private ViewerViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DocumentRepository> documentRepositoryProvider,
      Provider<PdfGeneratorService> pdfGeneratorServiceProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.documentRepositoryProvider = documentRepositoryProvider;
    this.pdfGeneratorServiceProvider = pdfGeneratorServiceProvider;
  }

  @Override
  public ViewerViewModel get() {
    return newInstance(savedStateHandleProvider.get(), documentRepositoryProvider.get(), pdfGeneratorServiceProvider.get());
  }

  public static ViewerViewModel_Factory create(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DocumentRepository> documentRepositoryProvider,
      Provider<PdfGeneratorService> pdfGeneratorServiceProvider) {
    return new ViewerViewModel_Factory(savedStateHandleProvider, documentRepositoryProvider, pdfGeneratorServiceProvider);
  }

  public static ViewerViewModel newInstance(SavedStateHandle savedStateHandle,
      DocumentRepository documentRepository, PdfGeneratorService pdfGeneratorService) {
    return new ViewerViewModel(savedStateHandle, documentRepository, pdfGeneratorService);
  }
}
