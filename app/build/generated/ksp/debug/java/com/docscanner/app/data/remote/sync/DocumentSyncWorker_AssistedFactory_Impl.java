package com.docscanner.app.data.remote.sync;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class DocumentSyncWorker_AssistedFactory_Impl implements DocumentSyncWorker_AssistedFactory {
  private final DocumentSyncWorker_Factory delegateFactory;

  DocumentSyncWorker_AssistedFactory_Impl(DocumentSyncWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public DocumentSyncWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<DocumentSyncWorker_AssistedFactory> create(
      DocumentSyncWorker_Factory delegateFactory) {
    return InstanceFactory.create(new DocumentSyncWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<DocumentSyncWorker_AssistedFactory> createFactoryProvider(
      DocumentSyncWorker_Factory delegateFactory) {
    return InstanceFactory.create(new DocumentSyncWorker_AssistedFactory_Impl(delegateFactory));
  }
}
