package com.docscanner.app.presentation.scanner;

import com.docscanner.app.domain.repository.DocumentRepository;
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
public final class ScannerViewModel_Factory implements Factory<ScannerViewModel> {
  private final Provider<DocumentRepository> documentRepositoryProvider;

  private ScannerViewModel_Factory(Provider<DocumentRepository> documentRepositoryProvider) {
    this.documentRepositoryProvider = documentRepositoryProvider;
  }

  @Override
  public ScannerViewModel get() {
    return newInstance(documentRepositoryProvider.get());
  }

  public static ScannerViewModel_Factory create(
      Provider<DocumentRepository> documentRepositoryProvider) {
    return new ScannerViewModel_Factory(documentRepositoryProvider);
  }

  public static ScannerViewModel newInstance(DocumentRepository documentRepository) {
    return new ScannerViewModel(documentRepository);
  }
}
