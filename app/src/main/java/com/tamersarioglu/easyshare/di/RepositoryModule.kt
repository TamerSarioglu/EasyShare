package com.tamersarioglu.easyshare.di

import com.tamersarioglu.easyshare.data.repository.PermissionRepositoryImpl
import com.tamersarioglu.easyshare.data.repository.VideoDownloadRepositoryImpl
import com.tamersarioglu.easyshare.domain.repository.PermissionRepository
import com.tamersarioglu.easyshare.domain.repository.VideoDownloadRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVideoDownloadRepository(
        videoDownloadRepositoryImpl: VideoDownloadRepositoryImpl
    ): VideoDownloadRepository

    @Binds
    @Singleton
    abstract fun bindPermissionRepository(
        permissionRepositoryImpl: PermissionRepositoryImpl
    ): PermissionRepository
}