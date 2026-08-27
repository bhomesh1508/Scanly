package com.docscanner.app.di;

import com.docscanner.app.data.local.dao.PageDao;
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
public final class DatabaseModule_ProvidePageDaoFactory implements Factory<PageDao> {
  private final Provider<AppDatabase> dbProvider;

  private DatabaseModule_ProvidePageDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public PageDao get() {
    return providePageDao(dbProvider.get());
  }

  public static DatabaseModule_ProvidePageDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvidePageDaoFactory(dbProvider);
  }

  public static PageDao providePageDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providePageDao(db));
  }
}
