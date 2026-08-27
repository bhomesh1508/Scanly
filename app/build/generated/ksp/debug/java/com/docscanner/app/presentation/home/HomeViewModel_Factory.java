package com.docscanner.app.presentation.home;

import com.docscanner.app.domain.repository.DocumentRepository;
import com.docscanner.app.domain.repository.SyncRepository;
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

  private final Provider<SyncRepository> syncRepositoryProvider;

  private HomeViewModel_Factory(Provider<DocumentRepository> documentRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider) {
    this.documentRepositoryProvider = documentRepositoryProvider;
    this.syncRepositoryProvider = syncRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(documentRepositoryProvider.get(), syncRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<DocumentRepository> documentRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider) {
    return new HomeViewModel_Factory(documentRepositoryProvider, syncRepositoryProvider);
  }

  public static HomeViewModel newInstance(DocumentRepository documentRepository,
      SyncRepository syncRepository) {
    return new HomeViewModel(documentRepository, syncRepository);
  }
}
