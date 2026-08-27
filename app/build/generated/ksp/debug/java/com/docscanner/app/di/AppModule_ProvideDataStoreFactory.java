package com.docscanner.app.di;

import android.app.Application;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideDataStoreFactory implements Factory<DataStore<Preferences>> {
  private final Provider<Application> appProvider;

  private AppModule_ProvideDataStoreFactory(Provider<Application> appProvider) {
    this.appProvider = appProvider;
  }

  @Override
  public DataStore<Preferences> get() {
    return provideDataStore(appProvider.get());
  }

  public static AppModule_ProvideDataStoreFactory create(Provider<Application> appProvider) {
    return new AppModule_ProvideDataStoreFactory(appProvider);
  }

  public static DataStore<Preferences> provideDataStore(Application app) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDataStore(app));
  }
}
