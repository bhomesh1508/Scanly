package com.docscanner.app.di

import com.docscanner.app.data.service.auth.AuthServiceImpl
import com.docscanner.app.data.service.cloud.CloudStorageServiceImpl
import com.docscanner.app.domain.service.auth.AuthService
import com.docscanner.app.domain.service.cloud.CloudStorageService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindCloudStorageService(
        impl: CloudStorageServiceImpl
    ): CloudStorageService

    @Binds
    @Singleton
    abstract fun bindAuthService(
        impl: AuthServiceImpl
    ): AuthService
}
