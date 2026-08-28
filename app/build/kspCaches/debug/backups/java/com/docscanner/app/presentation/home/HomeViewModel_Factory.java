package com.docscanner.app.presentation.home;

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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<DocumentRepository> documentRepositoryProvider;

  private HomeViewModel_Factory(Provider<DocumentRepository> documentRepositoryProvider) {
    this.documentRepositoryProvider = documentRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(documentRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<DocumentRepository> documentRepositoryProvider) {
    return new HomeViewModel_Factory(documentRepositoryProvider);
  }

  public static HomeViewModel newInstance(DocumentRepository documentRepository) {
    return new HomeViewModel(documentRepository);
  }
}
