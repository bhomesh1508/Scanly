package com.docscanner.app.service.pdf;

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
public final class PdfGeneratorService_Factory implements Factory<PdfGeneratorService> {
  @Override
  public PdfGeneratorService get() {
    return newInstance();
  }

  public static PdfGeneratorService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PdfGeneratorService newInstance() {
    return new PdfGeneratorService();
  }

  private static final class InstanceHolder {
    static final PdfGeneratorService_Factory INSTANCE = new PdfGeneratorService_Factory();
  }
}
