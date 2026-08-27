package com.docscanner.app.presentation.trash;

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
public final class TrashViewModel_Factory implements Factory<TrashViewModel> {
  private final Provider<DocumentRepository> documentRepositoryProvider;

  private TrashViewModel_Factory(Provider<DocumentRepository> documentRepositoryProvider) {
    this.documentRepositoryProvider = documentRepositoryProvider;
  }

  @Override
  public TrashViewModel get() {
    return newInstance(documentRepositoryProvider.get());
  }

  public static TrashViewModel_Factory create(
      Provider<DocumentRepository> documentRepositoryProvider) {
    return new TrashViewModel_Factory(documentRepositoryProvider);
  }

  public static TrashViewModel newInstance(DocumentRepository documentRepository) {
    return new TrashViewModel(documentRepository);
  }
}
