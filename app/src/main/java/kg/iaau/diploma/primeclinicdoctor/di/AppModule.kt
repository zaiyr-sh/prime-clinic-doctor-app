package kg.iaau.diploma.primeclinicdoctor.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kg.iaau.diploma.core.constants.DATABASE_NAME
import kg.iaau.diploma.local_storage.db.AppDatabase
import kg.iaau.diploma.local_storage.db.ScheduleDao
import kg.iaau.diploma.local_storage.prefs.StoragePreferences
import kg.iaau.diploma.network.api.ApiAuth
import kg.iaau.diploma.network.api.ApiMedCard
import kg.iaau.diploma.network.api.ApiSchedule
import kg.iaau.diploma.primeclinicdoctor.repository.AuthRepository
import kg.iaau.diploma.primeclinicdoctor.repository.MedCardsRepository
import kg.iaau.diploma.primeclinicdoctor.repository.ScheduleRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideScheduleDao(db: AppDatabase) = db.scheduleDao()

    @Singleton
    @Provides
    fun providesStoragePreferences(@ApplicationContext context: Context) = StoragePreferences(context)

    @Singleton
    @Provides
    fun providesAuthRepository(storagePreferences: StoragePreferences, apiAuth: ApiAuth) = AuthRepository(storagePreferences, apiAuth)

    @Singleton
    @Provides
    fun providesScheduleRepository(storagePreferences: StoragePreferences, apiSchedule: ApiSchedule, scheduleDao: ScheduleDao) = ScheduleRepository(storagePreferences, apiSchedule, scheduleDao)

    @Singleton
    @Provides
    fun providesMedCardsRepository(apiMedCard: ApiMedCard) = MedCardsRepository(apiMedCard)

}