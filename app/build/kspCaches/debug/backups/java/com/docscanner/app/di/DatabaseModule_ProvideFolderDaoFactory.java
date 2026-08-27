package com.docscanner.app.di;

import com.docscanner.app.data.local.dao.FolderDao;
import com.docscanner.app.data.local.db.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class DatabaseModule_ProvideFolderDaoFactory implements Factory<FolderDao> {
  private final Provider<AppDatabase> dbProvider;

  private DatabaseModule_ProvideFolderDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FolderDao get() {
    return provideFolderDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideFolderDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideFolderDaoFactory(dbProvider);
  }

  public static FolderDao provideFolderDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFolderDao(db));
  }
}
