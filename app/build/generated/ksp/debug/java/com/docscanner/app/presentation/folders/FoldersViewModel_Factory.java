package com.docscanner.app.presentation.folders;

import com.docscanner.app.domain.repository.FolderRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class FoldersViewModel_Factory implements Factory<FoldersViewModel> {
  private final Provider<FolderRepository> folderRepositoryProvider;

  private FoldersViewModel_Factory(Provider<FolderRepository> folderRepositoryProvider) {
    this.folderRepositoryProvider = folderRepositoryProvider;
  }

  @Override
  public FoldersViewModel get() {
    return newInstance(folderRepositoryProvider.get());
  }

  public static FoldersViewModel_Factory create(
      Provider<FolderRepository> folderRepositoryProvider) {
    return new FoldersViewModel_Factory(folderRepositoryProvider);
  }

  public static FoldersViewModel newInstance(FolderRepository folderRepository) {
    return new FoldersViewModel(folderRepository);
  }
}
