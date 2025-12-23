package com.zeromeal.app.ui.recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.res.painterResource
import com.zeromeal.app.R
import com.zeromeal.app.domain.model.Recipe
import com.zeromeal.app.navigation.Screen
import com.zeromeal.app.ui.components.BottomNavigationBar
import com.zeromeal.app.ui.components.BottomNavItem
import com.zeromeal.app.ui.theme.ZeroMealGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    navController: NavController,
    viewModel: RecipeListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            // Header hijau dengan logo, notifikasi, dan profil
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZeroMealGreen)
                    .padding(top = 32.dp, bottom = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo
                    Image(
                        painter = painterResource(id = R.drawable.logo_zeromeal),
                        contentDescription = "ZeroMeal Logo",
                        modifier = Modifier
                            .height(72.dp)
                            .width(140.dp)
                    )
                    // Ikon notif dan profil
                    Row {
                        IconButton(onClick = { navController.navigate(Screen.Notification.route) }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifikasi",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profil",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavItem.RECIPE,
                onItemSelected = { item ->
                    when (item) {
                        BottomNavItem.HOME -> navController.navigate(Screen.Home.route)
                        BottomNavItem.STOCK -> navController.navigate(Screen.Stock.route)
                        BottomNavItem.RECIPE -> {}
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
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Section: Resep Bahan Tersedia
                item {
                    SectionHeader(
                        title = "Resep Bahan Tersedia",
                        subtitle = "${uiState.filteredAvailable.size} resep"
                    )
                }
                items(uiState.filteredAvailable.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.getOrNull(0)?.let { recipe ->
                            RecipeGridCard(
                                recipe = recipe,
                                inventoryIngredientNames = uiState.inventoryItems.map { it.name },
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    navController.navigate(Screen.RecipeDetail.createRoute(recipe.id))
                                }
                            )
                        }
                        rowItems.getOrNull(1)?.let { recipe ->
                            RecipeGridCard(
                                recipe = recipe,
                                inventoryIngredientNames = uiState.inventoryItems.map { it.name },
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    navController.navigate(Screen.RecipeDetail.createRoute(recipe.id))
                                }
                            )
                        } ?: Spacer(modifier = Modifier.weight(1f))
                    }
                }

                // Section: Rekomendasi Resep
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionHeader(
                        title = "Resep Rekomendasi",
                        subtitle = "${uiState.filteredRecommended.size} resep"
                    )
                }
                items(uiState.filteredRecommended.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.getOrNull(0)?.let { recipe ->
                            RecipeGridCard(
                                recipe = recipe,
                                inventoryIngredientNames = uiState.inventoryItems.map { it.name },
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    navController.navigate(Screen.RecipeDetail.createRoute(recipe.id))
                                }
                            )
                        }
                        rowItems.getOrNull(1)?.let { recipe ->
                            RecipeGridCard(
                                recipe = recipe,
                                inventoryIngredientNames = uiState.inventoryItems.map { it.name },
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    navController.navigate(Screen.RecipeDetail.createRoute(recipe.id))
                                }
                            )
                        } ?: Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun RecipeGridCard(
    recipe: Recipe,
    inventoryIngredientNames: List<String>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Image di atas
            recipe.imageUrl?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = recipe.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    contentScale = ContentScale.Crop
                )
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = recipe.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 2
                )
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "⭐",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${recipe.rating} (${recipe.reviewCount})",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = "${recipe.cookingTime} menit • ${recipe.difficulty}",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

