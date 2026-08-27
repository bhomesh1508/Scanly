package com.docscanner.app.service.notification;

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
public final class NotificationService_Factory implements Factory<NotificationService> {
  private final Provider<Application> contextProvider;

  private NotificationService_Factory(Provider<Application> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public NotificationService get() {
    return newInstance(contextProvider.get());
  }

  public static NotificationService_Factory create(Provider<Application> contextProvider) {
    return new NotificationService_Factory(contextProvider);
  }

  public static NotificationService newInstance(Application context) {
    return new NotificationService(context);
  }
}
