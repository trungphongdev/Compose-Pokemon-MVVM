package com.example.jetpackcomposepokemonmvvm.repository

import android.util.Log
import com.example.jetpackcomposepokemonmvvm.data.remote.PokeApi
import com.example.jetpackcomposepokemonmvvm.data.remote.responses.Pokemon
import com.example.jetpackcomposepokemonmvvm.data.remote.responses.PokemonList
import com.example.jetpackcomposepokemonmvvm.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(
        private val api: PokeApi,
){
    suspend fun getPokemonList(limit: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit)
        } catch (e: Exception) {
            return Resource.Error(message = "An unknown error occured")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfo(pokemonName)
        }catch (e: Exception) {
            Log.e("response",e.message!!)
            return Resource.Error("An unknown error occured")
        }
        Log.e("response","name response "+ response.name)
        return  Resource.Success(response)
    }
}