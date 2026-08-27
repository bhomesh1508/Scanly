package com.docscanner.app.data.remote.auth;

import com.google.firebase.auth.FirebaseAuth;
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
public final class FirebaseAuthService_Factory implements Factory<FirebaseAuthService> {
  private final Provider<FirebaseAuth> authProvider;

  private FirebaseAuthService_Factory(Provider<FirebaseAuth> authProvider) {
    this.authProvider = authProvider;
  }

  @Override
  public FirebaseAuthService get() {
    return newInstance(authProvider.get());
  }

  public static FirebaseAuthService_Factory create(Provider<FirebaseAuth> authProvider) {
    return new FirebaseAuthService_Factory(authProvider);
  }

  public static FirebaseAuthService newInstance(FirebaseAuth auth) {
    return new FirebaseAuthService(auth);
  }
}
