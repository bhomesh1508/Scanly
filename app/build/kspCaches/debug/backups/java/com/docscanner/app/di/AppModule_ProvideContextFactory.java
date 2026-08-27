package com.docscanner.app.di;

import android.app.Application;
import android.content.Context;
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
public final class AppModule_ProvideContextFactory implements Factory<Context> {
  private final Provider<Application> appProvider;

  private AppModule_ProvideContextFactory(Provider<Application> appProvider) {
    this.appProvider = appProvider;
  }

  @Override
  public Context get() {
    return provideContext(appProvider.get());
  }

  public static AppModule_ProvideContextFactory create(Provider<Application> appProvider) {
    return new AppModule_ProvideContextFactory(appProvider);
  }

  public static Context provideContext(Application app) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideContext(app));
  }
}
