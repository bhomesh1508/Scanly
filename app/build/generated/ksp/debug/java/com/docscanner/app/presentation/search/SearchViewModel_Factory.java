package com.docscanner.app.presentation.search;

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
public final class SearchViewModel_Factory implements Factory<SearchViewModel> {
  private final Provider<DocumentRepository> documentRepositoryProvider;

  private SearchViewModel_Factory(Provider<DocumentRepository> documentRepositoryProvider) {
    this.documentRepositoryProvider = documentRepositoryProvider;
  }

  @Override
  public SearchViewModel get() {
    return newInstance(documentRepositoryProvider.get());
  }

  public static SearchViewModel_Factory create(
      Provider<DocumentRepository> documentRepositoryProvider) {
    return new SearchViewModel_Factory(documentRepositoryProvider);
  }

  public static SearchViewModel newInstance(DocumentRepository documentRepository) {
    return new SearchViewModel(documentRepository);
  }
}
