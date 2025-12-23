package com.zeromeal.app.ui.stock

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.compose.ui.res.painterResource
import com.zeromeal.app.R
import com.zeromeal.app.domain.model.ExpiryStatus
import com.zeromeal.app.domain.model.FoodItem
import com.zeromeal.app.navigation.Screen
import com.zeromeal.app.ui.components.BottomNavigationBar
import com.zeromeal.app.ui.components.BottomNavItem
import com.zeromeal.app.ui.components.HomeHeader
import com.zeromeal.app.ui.theme.ZeroMealGreen
import com.zeromeal.app.ui.theme.ZeroMealOrange
import com.zeromeal.app.ui.theme.ZeroMealRed

enum class StockFilter(val label: String) {
    URGENT("Urgent"),
    WARNING("Warning"),
    NORMAL("Normal"),
    ALL("Semua")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(
    navController: NavController,
    viewModel: StockViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedItem by remember { mutableStateOf<FoodItem?>(null) }
    var itemToDelete by remember { mutableStateOf<FoodItem?>(null) }
    var itemToBuy by remember { mutableStateOf<FoodItem?>(null) }

    Scaffold(
        topBar = {
            // Header hijau dengan logo, notifikasi, dan profil
            // Header hijau dengan logo, notifikasi, dan profil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZeroMealGreen)
            ) {
                HomeHeader(
                    onNotificationClick = { navController.navigate(Screen.Notification.route) },
                    onProfileClick = { navController.navigate(Screen.Profile.route) },
                    hasUnreadNotifications = uiState.hasUnreadNotifications
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavItem.STOCK,
                onItemSelected = { item ->
                    when (item) {
                        BottomNavItem.HOME -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                        BottomNavItem.STOCK -> navController.navigate(Screen.Stock.route) {
                            popUpTo(Screen.Stock.route) { inclusive = true }
                        }
                        BottomNavItem.ADD -> navController.navigate(Screen.ManualInput.route)
                        BottomNavItem.RECIPE -> navController.navigate(Screen.RecipeList.route)
                        BottomNavItem.PROFILE -> navController.navigate(Screen.Profile.route)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding() + 8.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = paddingValues.calculateBottomPadding()
                )
        ) {
            // Search bar sederhana
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Search Stock") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Scrollable Chips
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState())
                        .padding(end = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StockFilter.values().forEach { filter ->
                        val chipColor = when (filter) {
                            StockFilter.URGENT -> ZeroMealRed
                            StockFilter.WARNING -> ZeroMealOrange
                            StockFilter.NORMAL -> ZeroMealGreen
                            StockFilter.ALL -> ZeroMealGreen
                        }
                        FilterChip(
                            selected = uiState.selectedFilter == filter,
                            onClick = { viewModel.onFilterSelected(filter) },
                            label = {
                                Text(
                                    filter.label,
                                    color = if (uiState.selectedFilter == filter) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = chipColor,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Tombol sort dengan menu pilihan
                var sortMenuExpanded by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { sortMenuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.FilterList,
                            contentDescription = "Urutkan"
                        )
                    }
                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Nama A-Z") },
                            onClick = {
                                viewModel.onSortOptionSelected(StockSortOption.NAME_ASC)
                                sortMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Nama Z-A") },
                            onClick = {
                                viewModel.onSortOptionSelected(StockSortOption.NAME_DESC)
                                sortMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Tanggal pembelian (terbaru)") },
                            onClick = {
                                viewModel.onSortOptionSelected(StockSortOption.PURCHASE_DATE_DESC)
                                sortMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // Daftar stok
            if (uiState.filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada stok tersimpan",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.filteredItems) { item ->
                        StockItemCard(
                            item = item,
                            onItemClick = { selectedItem = item },
                            onEditClick = {
                                navController.navigate(Screen.ManualInputEdit.createRoute(item.id))
                            },
                            onDeleteClick = { itemToDelete = item },
                            onBuyClick = { itemToBuy = item }
                        )
                    }
                }
            }

            // Detail dialog saat item diklik
            selectedItem?.let { foodItem ->
                AlertDialog(
                    onDismissRequest = { selectedItem = null },
                    confirmButton = {
                        TextButton(onClick = { selectedItem = null }) {
                            Text("Tutup")
                        }
                    },
                    title = { Text(foodItem.name) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Kategori: ${foodItem.category}")
                            Text("Tanggal beli: ${foodItem.purchaseDate}")
                            Text("Kedaluwarsa: ${foodItem.expirationDate}")
                            Text("Jumlah: ${foodItem.quantity} ${foodItem.unit}")
                            Text("Lokasi: ${foodItem.storageLocation}")
                            foodItem.notes?.let { Text("Catatan: $it") }
                        }
                    }
                )
            }
            // Dialog konfirmasi hapus
            itemToDelete?.let { foodItem ->
                AlertDialog(
                    onDismissRequest = { itemToDelete = null },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteItem(foodItem.id)
                            itemToDelete = null
                        }) {
                            Text("Ya, hapus")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { itemToDelete = null }) {
                            Text("Batal")
                        }
                    },
                    title = { Text("Hapus item") },
                    text = { Text("Yakin mau menghapus \"${foodItem.name}\"?") }
                )
            }
            // Dialog konfirmasi beli
            itemToBuy?.let { foodItem ->
                AlertDialog(
                    onDismissRequest = { itemToBuy = null },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.addItemToShoppingList(foodItem)
                            itemToBuy = null
                        }) {
                            Text("Iya")
                        }
                    },
                    dismissButton = {
                        Row {
                            TextButton(onClick = { itemToBuy = null }) {
                                Text("Tidak")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = {
                                viewModel.addItemToShoppingList(foodItem)
                                itemToBuy = null
                                navController.navigate(Screen.ShoppingList.route)
                            }) {
                                Text("Masuk ke daftar belanja")
                            }
                        }
                    },
                    title = { Text("Ditambahkan ke daftar belanja?") },
                    text = { Text("Apakah kamu ingin menambahkan \"${foodItem.name}\" ke daftar belanja?") }
                )
            }
        }
    }
}

@Composable
private fun StockItemCard(
    item: FoodItem,
    onItemClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onBuyClick: () -> Unit
) {
    val context = LocalContext.current

    val (badgeColor, badgeText) = when (item.expiryStatus) {
        ExpiryStatus.URGENT -> ZeroMealRed to "Urgent"
        ExpiryStatus.WARNING -> ZeroMealOrange to "Warning"
        ExpiryStatus.NORMAL -> ZeroMealGreen.copy(alpha = 0.2f) to "Normal"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Gambar produk
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        item.imageUrl?.let { url ->
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(context)
                                        .data(url)
                                        .crossfade(true)
                                        .build()
                                ),
                                contentDescription = item.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = item.category,
                            fontSize = 12.sp,
                            color = ZeroMealGreen
                        )
                        Text(
                            text = "Tgl Beli: ${item.purchaseDate}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Kedaluwarsa: ${item.expirationDate}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        // Hari menuju kadaluarsa
                        val daysText = when {
                            item.daysUntilExpiration < 0 -> "Sudah kadaluarsa ${-item.daysUntilExpiration} hari lalu"
                            item.daysUntilExpiration == 0L -> "Kadaluarsa hari ini!"
                            else -> "Kadaluarsa dalam ${item.daysUntilExpiration} hari"
                        }
                        val daysColor = when (item.expiryStatus) {
                            ExpiryStatus.URGENT -> ZeroMealRed
                            ExpiryStatus.WARNING -> ZeroMealOrange
                            ExpiryStatus.NORMAL -> ZeroMealGreen
                        }
                        Text(
                            text = daysText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = daysColor
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onEditClick, shape = RoundedCornerShape(12.dp)) {
                        Text("Edit", fontSize = 13.sp)
                    }
                    OutlinedButton(onClick = onDeleteClick, shape = RoundedCornerShape(12.dp)) {
                        Text("Hapus", fontSize = 13.sp, color = ZeroMealRed)
                    }
                }

                    Button(
                    onClick = onBuyClick,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Beli", fontSize = 13.sp)
                }
            }
        }
    }
}

