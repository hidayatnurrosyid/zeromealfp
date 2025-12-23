package com.zeromeal.app.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Stock : Screen("stock")
    object RecipeList : Screen("recipe_list")
    object RecipeDetail : Screen("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_detail/$recipeId"
    }
    object ShoppingList : Screen("shopping_list")
    object ShoppingAdd : Screen("shopping_add")
    object Notification : Screen("notification")
    object Profile : Screen("profile")
    object ManualInput : Screen("manual_input")
    object ManualInputEdit : Screen("manual_input_edit/{itemId}") {
        fun createRoute(itemId: String) = "manual_input_edit/$itemId"
    }
    object FavoriteRecipes : Screen("favorite_recipes")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object VerifyCode : Screen("verify_code/{email}") {
        fun createRoute(email: String) = "verify_code/$email"
    }
    object ResetPassword : Screen("reset_password/{email}/{code}") {
        fun createRoute(email: String, code: String) = "reset_password/$email/$code"
    }
}

