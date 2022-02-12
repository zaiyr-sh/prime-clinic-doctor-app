package kg.iaau.diploma.primeclinicdoctor.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kg.iaau.diploma.local_storage.prefs.StoragePreferences
import kg.iaau.diploma.network.api.ApiAuth
import kg.iaau.diploma.primeclinicdoctor.repository.AuthRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesStoragePreferences(@ApplicationContext context: Context) = StoragePreferences(context)

    @Singleton
    @Provides
    fun providesAuthRepository(storagePreferences: StoragePreferences, apiAuth: ApiAuth) = AuthRepository(storagePreferences, apiAuth)

}