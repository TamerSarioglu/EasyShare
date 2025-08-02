package com.tamersarioglu.easyshare.di

import android.content.Context
import androidx.room.Room
import com.tamersarioglu.easyshare.data.local.dao.DownloadHistoryDao
import com.tamersarioglu.easyshare.data.local.database.EasyShareDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideEasyShareDatabase(
        @ApplicationContext context: Context
    ): EasyShareDatabase {
        return Room.databaseBuilder(
            context,
            EasyShareDatabase::class.java,
            EasyShareDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideDownloadHistoryDao(database: EasyShareDatabase): DownloadHistoryDao {
        return database.downloadHistoryDao()
    }
}