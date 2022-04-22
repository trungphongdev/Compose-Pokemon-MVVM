package com.example.jetpackcomposepokemonmvvm.pokemonlist

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusOrder
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.jetpackcomposepokemonmvvm.R
import com.example.jetpackcomposepokemonmvvm.data.model.PokedexListEntry
import timber.log.Timber

@Composable
fun PokemonListScreen(
    navController: NavController,
    viewmodel: PokemonListViewModel = hiltViewModel()
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()

    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(20.dp))
            // Logo
            Image(
                painter = painterResource(id = R.drawable.pokemon_logo),
                contentDescription = "Pokemo    n",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(width = 300.dp, height = 100.dp)
                    .align(CenterHorizontally)

            )
            // Search Bar
            SearchBar(
                hint = "Search...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                viewmodel.searchPokemonByName(it)
            }

            // Item List
            PokemonList(navController = navController)




        }

    }

}



@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)

            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    when {
                        it.isFocused -> isHintDisplayed = false && text.isNotEmpty()
                    }
                }
        )


        if (isHintDisplayed) {
            Text(
                text = hint ,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 20.dp,vertical = 12.dp)
            )
        }

    }
}

@Composable
fun PokedexEntry(
    entry: PokedexListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltViewModel()
) {

    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(vertical = 10.dp, horizontal = 8.dp)
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    colors = listOf(dominantColor, defaultDominantColor)
                )
            )
            .clickable {
                navController.navigate(
                                    route = "pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName.lowercase()}"
                )
            }


    ) {
        Column(verticalArrangement = Arrangement.Center,horizontalAlignment = Alignment.CenterHorizontally) {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entry.imgUrl)
                    .build()

            )

            viewModel.calcDominantColor(LocalContext.current.getDrawable(R.drawable.pokemon_logo)!!) { color ->
            dominantColor = color}

            if (painter.state is AsyncImagePainter.State.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(CenterHorizontally))
            }

            Image(
                painter = painter, contentDescription = entry.pokemonName,
                modifier = Modifier.size(100.dp)
            )


            Text(
                text = entry.pokemonName,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )

        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PokedexRow(
    rowIndex: Int,
    entries: List<PokedexListEntry>,
    navController: NavController
) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(rowIndex),
        contentPadding = PaddingValues(
            20.dp
        ),
        modifier = Modifier.fillMaxSize()) {
        items(entries.size) {item ->
            PokedexEntry(
                entry = entries[item],
                navController = navController )

        }
    }

}

@Composable
fun PokemonList(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val pokemonList =  remember{ viewModel.pokemonList }
    PokedexRow(rowIndex = 2, entries = pokemonList.value, navController = navController)
}