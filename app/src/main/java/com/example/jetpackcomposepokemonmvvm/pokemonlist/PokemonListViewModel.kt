package com.example.jetpackcomposepokemonmvvm.pokemonlist

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import coil.compose.AsyncImagePainter
import com.example.jetpackcomposepokemonmvvm.data.model.PokedexListEntry
import com.example.jetpackcomposepokemonmvvm.data.remote.responses.PokemonList
import com.example.jetpackcomposepokemonmvvm.repository.PokemonRepository
import com.example.jetpackcomposepokemonmvvm.util.Constants
import com.example.jetpackcomposepokemonmvvm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class  PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    var loadError  = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var cachedPokemonList = listOf<PokedexListEntry>()

    var isSearchStarting = true
    init {
        loadPokemonpaginated()
    }



    fun searchPokemonByName(name: String) {
        val listToSearch = if (isSearchStarting) {
            pokemonList.value
        } else {
            cachedPokemonList
        }
        viewModelScope.launch(Dispatchers.Default) {

            if (name.isEmpty()) {
                pokemonList.value = cachedPokemonList
                isSearchStarting = true
                return@launch

            }
            val results = listToSearch.filter {
                it.pokemonName.contains(name.trim(), ignoreCase = true)
            }
            if (isSearchStarting) {
                cachedPokemonList = pokemonList.value
                isSearchStarting = false
            }
                pokemonList.value = results

        }

    }

    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
            val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate {palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }

    fun loadPokemonpaginated(){
        viewModelScope.launch {
            val result = repository.getPokemonList(Constants.PAGE_SIZE)
            when(result) {
                is Resource.Success -> {
                    val pokedexEntries = result.data!!.results.mapIndexed {index, entry ->
                        val number = if (entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }

                        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"

                        PokedexListEntry(entry.name.capitalize(Locale.ROOT),url,number.toInt())
                    }
                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokedexEntries
                }
                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false

                }
            }
        }
    }
}