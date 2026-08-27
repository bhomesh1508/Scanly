package com.docscanner.app.presentation.storage;

import com.docscanner.app.domain.repository.AuthRepository;
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
public final class StorageViewModel_Factory implements Factory<StorageViewModel> {
  private final Provider<SyncRepository> syncRepositoryProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private StorageViewModel_Factory(Provider<SyncRepository> syncRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider) {
    this.syncRepositoryProvider = syncRepositoryProvider;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public StorageViewModel get() {
    return newInstance(syncRepositoryProvider.get(), authRepositoryProvider.get());
  }

  public static StorageViewModel_Factory create(Provider<SyncRepository> syncRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider) {
    return new StorageViewModel_Factory(syncRepositoryProvider, authRepositoryProvider);
  }

  public static StorageViewModel newInstance(SyncRepository syncRepository,
      AuthRepository authRepository) {
    return new StorageViewModel(syncRepository, authRepository);
  }
}
