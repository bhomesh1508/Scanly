package com.docscanner.app.service.encryption;

import android.app.Application;
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
public final class EncryptionService_Factory implements Factory<EncryptionService> {
  private final Provider<Application> contextProvider;

  private EncryptionService_Factory(Provider<Application> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public EncryptionService get() {
    return newInstance(contextProvider.get());
  }

  public static EncryptionService_Factory create(Provider<Application> contextProvider) {
    return new EncryptionService_Factory(contextProvider);
  }

  public static EncryptionService newInstance(Application context) {
    return new EncryptionService(context);
  }
}
