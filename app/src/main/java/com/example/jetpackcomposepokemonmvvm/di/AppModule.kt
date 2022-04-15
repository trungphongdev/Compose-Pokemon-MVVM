package com.example.jetpackcomposepokemonmvvm.di

import com.example.jetpackcomposepokemonmvvm.data.remote.PokeApi
import com.example.jetpackcomposepokemonmvvm.repository.PokemonRepository
import com.example.jetpackcomposepokemonmvvm.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun providePokemonRepository(
        api: PokeApi
    ): PokemonRepository {
        return PokemonRepository(api)
    }


    @Singleton
    @Provides
    fun providePokeApi() : PokeApi{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PokeApi::class.java)

    }

}