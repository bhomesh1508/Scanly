package com.docscanner.app.data.repository;

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
public final class SettingsRepositoryImpl_Factory implements Factory<SettingsRepositoryImpl> {
  private final Provider<Application> contextProvider;

  private SettingsRepositoryImpl_Factory(Provider<Application> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SettingsRepositoryImpl get() {
    return newInstance(contextProvider.get());
  }

  public static SettingsRepositoryImpl_Factory create(Provider<Application> contextProvider) {
    return new SettingsRepositoryImpl_Factory(contextProvider);
  }

  public static SettingsRepositoryImpl newInstance(Application context) {
    return new SettingsRepositoryImpl(context);
  }
}
