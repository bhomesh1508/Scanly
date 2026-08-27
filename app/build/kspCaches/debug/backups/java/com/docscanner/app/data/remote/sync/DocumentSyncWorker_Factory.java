package com.docscanner.app.data.remote.sync;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.docscanner.app.data.remote.auth.FirebaseAuthService;
import dagger.internal.DaggerGenerated;
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
public final class DocumentSyncWorker_Factory {
  private final Provider<SyncManager> syncManagerProvider;

  private final Provider<FirebaseAuthService> authServiceProvider;

  private DocumentSyncWorker_Factory(Provider<SyncManager> syncManagerProvider,
      Provider<FirebaseAuthService> authServiceProvider) {
    this.syncManagerProvider = syncManagerProvider;
    this.authServiceProvider = authServiceProvider;
  }

  public DocumentSyncWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, syncManagerProvider.get(), authServiceProvider.get());
  }

  public static DocumentSyncWorker_Factory create(Provider<SyncManager> syncManagerProvider,
      Provider<FirebaseAuthService> authServiceProvider) {
    return new DocumentSyncWorker_Factory(syncManagerProvider, authServiceProvider);
  }

  public static DocumentSyncWorker newInstance(Context context, WorkerParameters params,
      SyncManager syncManager, FirebaseAuthService authService) {
    return new DocumentSyncWorker(context, params, syncManager, authService);
  }
}
