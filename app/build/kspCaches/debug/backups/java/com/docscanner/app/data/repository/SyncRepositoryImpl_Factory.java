package com.docscanner.app.data.repository;

import android.app.Application;
import com.docscanner.app.data.remote.sync.SyncManager;
import com.docscanner.app.domain.repository.AuthRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SyncRepositoryImpl_Factory implements Factory<SyncRepositoryImpl> {
  private final Provider<SyncManager> syncManagerProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<Application> contextProvider;

  private SyncRepositoryImpl_Factory(Provider<SyncManager> syncManagerProvider,
      Provider<AuthRepository> authRepositoryProvider, Provider<Application> contextProvider) {
    this.syncManagerProvider = syncManagerProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public SyncRepositoryImpl get() {
    return newInstance(syncManagerProvider.get(), authRepositoryProvider.get(), contextProvider.get());
  }

  public static SyncRepositoryImpl_Factory create(Provider<SyncManager> syncManagerProvider,
      Provider<AuthRepository> authRepositoryProvider, Provider<Application> contextProvider) {
    return new SyncRepositoryImpl_Factory(syncManagerProvider, authRepositoryProvider, contextProvider);
  }

  public static SyncRepositoryImpl newInstance(SyncManager syncManager,
      AuthRepository authRepository, Application context) {
    return new SyncRepositoryImpl(syncManager, authRepository, context);
  }
}
