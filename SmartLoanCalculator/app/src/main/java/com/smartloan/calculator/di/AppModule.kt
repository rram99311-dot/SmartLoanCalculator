package com.smartloan.calculator.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.smartloan.calculator.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.settingsDataStore by preferencesDataStore("settings")
@Module @InstallIn(SingletonComponent::class) object AppModule {
    @Provides @Singleton fun database(@ApplicationContext context: Context): LoanDatabase = Room.databaseBuilder(context, LoanDatabase::class.java, "smart_loan.db").fallbackToDestructiveMigration().build()
    @Provides fun historyDao(database: LoanDatabase): HistoryDao = database.historyDao()
}
