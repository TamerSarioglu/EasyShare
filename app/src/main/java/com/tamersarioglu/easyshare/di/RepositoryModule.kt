package com.tamersarioglu.easyshare.di

import com.tamersarioglu.easyshare.data.repository.DownloadHistoryRepositoryImpl
import com.tamersarioglu.easyshare.data.repository.VideoDownloadRepositoryImpl
import com.tamersarioglu.easyshare.domain.repository.DownloadHistoryRepository
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
    abstract fun bindDownloadHistoryRepository(
        downloadHistoryRepositoryImpl: DownloadHistoryRepositoryImpl
    ): DownloadHistoryRepository
}