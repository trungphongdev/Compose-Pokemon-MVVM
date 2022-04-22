package com.example.jetpackcomposepokemonmvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetpackcomposepokemonmvvm.pokemondetail.PokemonDetailScreen
import com.example.jetpackcomposepokemonmvvm.pokemonlist.PokemonListScreen
import com.example.jetpackcomposepokemonmvvm.ui.theme.JetpackComposePokemonMVVMTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposePokemonMVVMTheme {
                Navigation()
                }
            }
    }
}



@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "pokemon_list_screen") {
        composable("pokemon_list_screen") {
            PokemonListScreen(navController = navController)
        }
        composable(
            route = "pokemon_detail_screen/{dominantColor}/{pokemonName}",
            arguments = listOf(
                navArgument("dominantColor") { type = NavType.IntType },
                navArgument("pokemonName") { type = NavType.StringType},
            )
        ) {

            val dominantColor = remember {
                Color(it.arguments?.getInt("dominantColor")!!) ?: Color.White
            }


            val pokemonName = remember {
               it.arguments?.getString("pokemonName")
            }


            PokemonDetailScreen(dominantColor = dominantColor, pokemonName = pokemonName ?: "", navController = navController)




        }

    }
}