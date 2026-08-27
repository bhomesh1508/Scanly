package com.docscanner.app.service.filter;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class ImageFilterService_Factory implements Factory<ImageFilterService> {
  @Override
  public ImageFilterService get() {
    return newInstance();
  }

  public static ImageFilterService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ImageFilterService newInstance() {
    return new ImageFilterService();
  }

  private static final class InstanceHolder {
    static final ImageFilterService_Factory INSTANCE = new ImageFilterService_Factory();
  }
}
