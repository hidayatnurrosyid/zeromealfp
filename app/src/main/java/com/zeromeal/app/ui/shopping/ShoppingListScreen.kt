package com.zeromeal.app.ui.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zeromeal.app.domain.model.ShoppingItem
import com.zeromeal.app.navigation.Screen
import com.zeromeal.app.ui.components.BottomNavigationBar
import com.zeromeal.app.ui.components.BottomNavItem
import com.zeromeal.app.ui.theme.ZeroMealGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    navController: NavController,
    viewModel: ShoppingListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Belanja", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.ShoppingAdd.route) },
                containerColor = ZeroMealGreen,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Item",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding(),
                start = 16.dp,
                end = 16.dp,
                bottom = paddingValues.calculateBottomPadding()
            )
        ) {
            ShoppingStatsHeader(uiState)

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
        ) {
                    Text("Belum ada item dalam daftar belanja", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.items) { item ->
                        ShoppingListRow(
                            item = item,
                            onCheckedChange = { checked ->
                                viewModel.onItemCheckedChange(item.id, checked)
                            },
                            onDeleteClick = {
                                viewModel.deleteItem(item.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShoppingStatsHeader(uiState: ShoppingListUiState) {
    val stats = uiState.stats
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Total Item", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = if (stats != null) "${stats.totalItems} item dalam daftar" else "-",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Sudah Dibeli", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = if (stats != null) "${stats.purchasedItems} item" else "-",
                fontSize = 14.sp,
                color = ZeroMealGreen,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ShoppingListRow(
    item: ShoppingItem,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = onCheckedChange
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(text = item.name, fontWeight = FontWeight.Medium)
                item.quantity?.let {
                    if (it.isNotBlank()) {
                        Text(text = it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = Color.White,
                    modifier = Modifier
                        .background(Color(0xFFFF6B6B), shape = CircleShape)
                        .padding(8.dp)
                )
            }
        }
    }
}

