package com.zeromeal.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zeromeal.app.navigation.Screen
import com.zeromeal.app.ui.components.*
import com.zeromeal.app.ui.theme.ZeroMealGreen

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavItem.HOME,
                onItemSelected = { item ->
                    when (item) {
                        BottomNavItem.HOME -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                        BottomNavItem.STOCK -> navController.navigate(Screen.Stock.route)
                        BottomNavItem.RECIPE -> navController.navigate(Screen.RecipeList.route)
                        BottomNavItem.PROFILE -> navController.navigate(Screen.Profile.route)
                        BottomNavItem.ADD -> navController.navigate(Screen.ManualInput.route)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // Header with green background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZeroMealGreen)
            ) {
                HomeHeader(
                    onNotificationClick = {
                        navController.navigate(Screen.Notification.route)
                    },
                    onProfileClick = {
                        navController.navigate(Screen.Profile.route)
                    },
                    hasUnreadNotifications = uiState.hasUnreadNotifications
                )
            }
            
            // Search bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                placeholder = "Cari bahan, resep, atau belanjaan..."
            )
            
            // Quick Actions
            QuickActionsSection(
                onInputManualClick = {
                    navController.navigate(Screen.ManualInput.route)
                }
            )
            
            // Expiring Soon
            ExpiringSoonSection(
                items = uiState.filteredExpiringItems,
                onSeeAllClick = {
                    navController.navigate(Screen.Stock.route)
                }
            )
            
            // Recommended Recipes
            RecipesSection(
                recipes = uiState.filteredRecipes,
                onSeeAllClick = {
                    navController.navigate(Screen.RecipeList.route)
                },
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                }
            )
            
            // Shopping List
            ShoppingListSection(
                items = uiState.filteredShoppingItems,
                onManageClick = {
                    navController.navigate(Screen.ShoppingList.route)
                },
                onItemCheckedChange = { id, checked ->
                    viewModel.onHomeShoppingItemChecked(id, checked)
                }
            )
            
            // Bottom padding
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

