package com.docscanner.app.data.remote.sync;

import com.docscanner.app.data.local.dao.DocumentDao;
import com.docscanner.app.data.local.dao.PageDao;
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
public final class SyncManager_Factory implements Factory<SyncManager> {
  private final Provider<DocumentDao> documentDaoProvider;

  private final Provider<PageDao> pageDaoProvider;

  private SyncManager_Factory(Provider<DocumentDao> documentDaoProvider,
      Provider<PageDao> pageDaoProvider) {
    this.documentDaoProvider = documentDaoProvider;
    this.pageDaoProvider = pageDaoProvider;
  }

  @Override
  public SyncManager get() {
    return newInstance(documentDaoProvider.get(), pageDaoProvider.get());
  }

  public static SyncManager_Factory create(Provider<DocumentDao> documentDaoProvider,
      Provider<PageDao> pageDaoProvider) {
    return new SyncManager_Factory(documentDaoProvider, pageDaoProvider);
  }

  public static SyncManager newInstance(DocumentDao documentDao, PageDao pageDao) {
    return new SyncManager(documentDao, pageDao);
  }
}
