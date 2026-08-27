package com.docscanner.app.data.remote.sync;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = DocumentSyncWorker.class
)
public interface DocumentSyncWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.docscanner.app.data.remote.sync.DocumentSyncWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(
      DocumentSyncWorker_AssistedFactory factory);
}
