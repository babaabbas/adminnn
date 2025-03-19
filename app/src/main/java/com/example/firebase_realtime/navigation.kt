package com.example.firebase_realtime

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun myapp(){
    val navController= rememberNavController()
    NavHost(navController, startDestination ="Home" ){
        composable("Home"){
            Real_Home(navController=navController)
        }
        composable(
            "food/{catName}/{id}/{photo}",
            arguments = listOf(
                navArgument("catName") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType },
                navArgument("photo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val catName = backStackEntry.arguments?.getString("catName") ?: ""
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val photo = backStackEntry.arguments?.getString("photo") ?: ""

            val category = Category(cat_name = catName, id_ = id, photo_ = photo)

            scaff2(navController, category)
        }


    }

}