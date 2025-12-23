package com.zeromeal.app.ui.recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.zeromeal.app.domain.model.FunFact
import com.zeromeal.app.domain.model.Ingredient
import com.zeromeal.app.domain.model.Recipe
import com.zeromeal.app.ui.theme.ZeroMealGreen
import com.zeromeal.app.ui.theme.ZeroMealOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    navController: NavController,
    recipeId: String,
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Load recipe saat pertama kali
    androidx.compose.runtime.LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Resep") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Favorite button
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (uiState.isFavorite) Color.Red else Color.Gray
                        )
                    }
                    // Share button
                    IconButton(onClick = {
                        uiState.recipe?.let { recipe ->
                            val shareText = "Coba resep ${recipe.name} di ZeroMeal!\n\n" +
                                "⏱ ${recipe.cookingTime} menit | ⭐ ${recipe.rating}\n\n" +
                                "Bahan: ${recipe.ingredients.take(3).joinToString { it.name }}...\n\n" +
                                "Download ZeroMeal sekarang!"
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Bagikan Resep"))
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ZeroMealGreen)
            }
        } else {
            uiState.recipe?.let { recipe ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Header Image
                    item {
                        recipe.imageUrl?.let { url ->
                            Image(
                                painter = rememberAsyncImagePainter(url),
                                contentDescription = recipe.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                                contentScale = ContentScale.Crop
                            )
                        } ?: Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(Color.LightGray.copy(alpha = 0.3f))
                        )
                    }

                    // Recipe Info
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = recipe.name,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Rating & time
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⭐", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "${recipe.rating} (${recipe.reviewCount})",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🕐", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "${recipe.cookingTime} menit",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                                // Badge difficulty
                                Surface(
                                    color = ZeroMealGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = recipe.difficulty,
                                        color = ZeroMealGreen,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            // Description
                            recipe.description?.let {
                                Text(
                                    text = it,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    // Bahan-bahan
                    item {
                        SectionTitle("Bahan-bahan")
                    }
                    items(recipe.ingredients) { ingredient ->
                        IngredientRow(ingredient)
                    }

                    // Cara Membuat
                    item {
                        SectionTitle("Cara Membuat")
                    }
                    itemsIndexed(recipe.instructions) { index, instruction ->
                        InstructionRow(index + 1, instruction)
                    }

                    // Kandungan Gizi
                    item {
                        SectionTitle("Kandungan Gizi")
                        NutritionSection(recipe)
                    }

                    // Fun Facts
                    recipe.funFacts?.let { facts ->
                        if (facts.isNotEmpty()) {
                            item {
                                SectionTitle("Fun Fact")
                            }
                            items(facts) { fact ->
                                FunFactCard(fact)
                            }
                        }
                    }

                    // Chef Info
                    recipe.chefName?.let { chefName ->
                        item {
                            ChefSection(
                                chefName = chefName,
                                chefImageUrl = recipe.chefImageUrl,
                                uploadDate = recipe.uploadDate
                            )
                        }
                    }

                    // Rating Section
                    item {
                        RatingSection(
                            userRating = uiState.userRating,
                            hasSubmittedRating = uiState.hasSubmittedRating,
                            onRatingChange = viewModel::onUserRatingChange,
                            onSubmitRating = viewModel::submitRating
                        )
                    }

                    // Bagikan Resep Button
                    item {
                        Button(
                            onClick = {
                                uiState.recipe?.let { rec ->
                                    val shareText = "Coba resep ${rec.name} di ZeroMeal!\n\n" +
                                        "⏱ ${rec.cookingTime} menit | ⭐ ${rec.rating}\n\n" +
                                        "Bahan: ${rec.ingredients.take(3).joinToString { it.name }}...\n\n" +
                                        "Download ZeroMeal sekarang!"
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Bagikan Resep"))
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ZeroMealOrange),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("Bagikan Resep", color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }

                    // Resep Rekomendasi
                    if (uiState.recommendedRecipes.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Resep Rekomendasi",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    "${uiState.recommendedRecipes.size} resep",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                items(uiState.recommendedRecipes) { rec ->
                                    RecommendedRecipeCard(rec) {
                                        navController.navigate("recipe_detail/${rec.id}")
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Resep tidak ditemukan", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun IngredientRow(ingredient: Ingredient) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(ingredient.name, fontSize = 14.sp)
        Text(ingredient.quantity, fontSize = 14.sp, color = Color.Gray)
    }
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = Color.LightGray.copy(alpha = 0.5f)
    )
}

@Composable
private fun InstructionRow(stepNumber: Int, instruction: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Step number circle
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(ZeroMealGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber.toString(),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = instruction,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun NutritionSection(recipe: Recipe) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Calories center
        recipe.calories?.let { cal ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = cal.toString(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "kalori per porsi",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        // Nutrition rows
        recipe.carbs?.let { NutritionRow("Karbohidrat", "${it.toInt()}g") }
        recipe.protein?.let { NutritionRow("Protein", "${it.toInt()}g") }
        recipe.fat?.let { NutritionRow("Lemak", "${it.toInt()}g") }
        recipe.fiber?.let { NutritionRow("Serat", "${it.toInt()}g") }
        recipe.sodium?.let { NutritionRow("Natrium", "${it.toInt()}mg") }
        recipe.sugar?.let { NutritionRow("Gula", "${it.toInt()}g") }
    }
}

@Composable
private fun NutritionRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp)
        Text(value, fontSize = 14.sp, color = Color.Gray)
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
}

@Composable
private fun FunFactCard(fact: FunFact) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val emoji = when (fact.iconType) {
                "history" -> "📜"
                "tips" -> "💡"
                "variation" -> "🌍"
                else -> "ℹ️"
            }
            Text(emoji, fontSize = 20.sp)
            Column {
                Text(
                    text = fact.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = ZeroMealGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = fact.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ChefSection(chefName: String, chefImageUrl: String?, uploadDate: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Chef avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            chefImageUrl?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = chefName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } ?: Text("👨‍🍳", fontSize = 24.sp)
        }
        Column {
            Text(chefName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            uploadDate?.let {
                Text("Diupload $it", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun RecommendedRecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column {
            recipe.imageUrl?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = recipe.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentScale = ContentScale.Crop
                )
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f))
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    recipe.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐", fontSize = 11.sp)
                    Text(
                        " ${recipe.rating}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingSection(
    userRating: Int,
    hasSubmittedRating: Boolean,
    onRatingChange: (Int) -> Unit,
    onSubmitRating: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (hasSubmittedRating) "Terima kasih atas rating Anda!" else "Beri Rating Resep Ini",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Star Rating
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..5).forEach { star ->
                    Icon(
                        imageVector = if (star <= userRating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star $star",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(enabled = !hasSubmittedRating) {
                                onRatingChange(star)
                            }
                            .padding(4.dp),
                        tint = if (star <= userRating) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (userRating > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (userRating) {
                        1 -> "Kurang"
                        2 -> "Cukup"
                        3 -> "Baik"
                        4 -> "Sangat Baik"
                        5 -> "Luar Biasa!"
                        else -> ""
                    },
                    fontSize = 14.sp,
                    color = Color(0xFFFFB300),
                    fontWeight = FontWeight.Medium
                )
            }
            
            if (!hasSubmittedRating && userRating > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onSubmitRating,
                    colors = ButtonDefaults.buttonColors(containerColor = ZeroMealGreen),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Kirim Rating", color = Color.White)
                }
            }
        }
    }
}
