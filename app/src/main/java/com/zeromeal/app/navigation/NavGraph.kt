package com.zeromeal.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zeromeal.app.ui.home.HomeScreen
import com.zeromeal.app.ui.stock.StockScreen
import com.zeromeal.app.ui.recipe.RecipeListScreen
import com.zeromeal.app.ui.recipe.RecipeDetailScreen
import com.zeromeal.app.ui.shopping.ShoppingListScreen
import com.zeromeal.app.ui.shopping.ShoppingAddScreen
import com.zeromeal.app.ui.notification.NotificationScreen
import com.zeromeal.app.ui.profile.ProfileScreen
import com.zeromeal.app.ui.manualinput.ManualInputScreen

import com.zeromeal.app.ui.splash.SplashScreen

import com.zeromeal.app.ui.login.LoginScreen
import com.zeromeal.app.ui.register.RegisterScreen
import com.zeromeal.app.ui.forgotpassword.ForgotPasswordScreen
import com.zeromeal.app.ui.forgotpassword.VerifyCodeScreen
import com.zeromeal.app.ui.forgotpassword.ResetPasswordScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Stock.route) {
            StockScreen(navController = navController)
        }
        composable(Screen.RecipeList.route) {
            RecipeListScreen(navController = navController)
        }
        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            RecipeDetailScreen(navController = navController, recipeId = recipeId)
        }
        composable(Screen.ShoppingList.route) {
            ShoppingListScreen(navController = navController)
        }
        composable(Screen.ShoppingAdd.route) {
            ShoppingAddScreen(navController = navController)
        }
        composable(Screen.Notification.route) {
            NotificationScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Screen.ManualInput.route) {
            ManualInputScreen(navController = navController)
        }
        composable(
            route = Screen.ManualInputEdit.route,
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            ManualInputScreen(navController = navController, itemId = itemId)
        }
        composable(Screen.FavoriteRecipes.route) {
            com.zeromeal.app.ui.recipe.FavoriteRecipesScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(
            route = Screen.VerifyCode.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                }
            )
        ) {
            VerifyCodeScreen(navController = navController)
        }
        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                },
                navArgument("code") {
                    type = NavType.StringType
                }
            )
        ) {
            ResetPasswordScreen(navController = navController)
        }
    }
}

