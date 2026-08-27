package com.docscanner.app;

import com.docscanner.app.service.notification.NotificationService;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class DocScannerApp_MembersInjector implements MembersInjector<DocScannerApp> {
  private final Provider<NotificationService> notificationServiceProvider;

  private DocScannerApp_MembersInjector(Provider<NotificationService> notificationServiceProvider) {
    this.notificationServiceProvider = notificationServiceProvider;
  }

  @Override
  public void injectMembers(DocScannerApp instance) {
    injectNotificationService(instance, notificationServiceProvider.get());
  }

  public static MembersInjector<DocScannerApp> create(
      Provider<NotificationService> notificationServiceProvider) {
    return new DocScannerApp_MembersInjector(notificationServiceProvider);
  }

  @InjectedFieldSignature("com.docscanner.app.DocScannerApp.notificationService")
  public static void injectNotificationService(DocScannerApp instance,
      NotificationService notificationService) {
    instance.notificationService = notificationService;
  }
}
