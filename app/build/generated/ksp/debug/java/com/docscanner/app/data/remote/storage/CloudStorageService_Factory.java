package com.docscanner.app.data.remote.storage;

import com.google.firebase.storage.FirebaseStorage;
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
public final class CloudStorageService_Factory implements Factory<CloudStorageService> {
  private final Provider<FirebaseStorage> storageProvider;

  private CloudStorageService_Factory(Provider<FirebaseStorage> storageProvider) {
    this.storageProvider = storageProvider;
  }

  @Override
  public CloudStorageService get() {
    return newInstance(storageProvider.get());
  }

  public static CloudStorageService_Factory create(Provider<FirebaseStorage> storageProvider) {
    return new CloudStorageService_Factory(storageProvider);
  }

  public static CloudStorageService newInstance(FirebaseStorage storage) {
    return new CloudStorageService(storage);
  }
}
