package com.AA.androidcodingchallenge

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


@Composable
fun Navigation(
    viewModel: ImageViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route) {
            MainScreen(navController = navController, viewModel = viewModel)
        }

        composable(
            route = Screen.DetailsScreen.route + "/{item}",
            arguments = listOf(
                navArgument("item") {
                    type = NavType.StringType
                    defaultValue = "0"
                })
        ){ entry ->
            DetailsScreen(id = entry.arguments?.getString("item"))
        }
    }
}