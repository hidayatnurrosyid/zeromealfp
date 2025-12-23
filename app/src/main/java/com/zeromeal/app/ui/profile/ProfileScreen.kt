package com.zeromeal.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zeromeal.app.navigation.Screen
import com.zeromeal.app.ui.components.BottomNavigationBar
import com.zeromeal.app.ui.components.BottomNavItem

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.zeromeal.app.ui.profile.ProfileViewModel
import com.zeromeal.app.domain.repository.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }

    // Theme Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Pilih Tema", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    ThemeMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = themeMode == mode,
                                onClick = {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (mode) {
                                    ThemeMode.SYSTEM -> "Ikuti Sistem"
                                    ThemeMode.LIGHT -> "Light Mode"
                                    ThemeMode.DARK -> "Dark Mode"
                                },
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profil",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavItem.PROFILE,
                onItemSelected = { item ->
                    when (item) {
                        BottomNavItem.HOME -> navController.navigate(Screen.Home.route)
                        BottomNavItem.STOCK -> navController.navigate(Screen.Stock.route)
                        BottomNavItem.RECIPE -> navController.navigate(Screen.RecipeList.route)
                        BottomNavItem.PROFILE -> {}
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            // User Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(40.dp),
                            tint = Color.Gray
                        )
                    }
                    
                    Column {
                        Text(
                            text = if (currentUser != null) "Halo, ${currentUser!!.nama}!" else "Halo, Tamu!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (currentUser != null) currentUser!!.email else "Silakan login",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            // Profile Menu List
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column {
                    ProfileMenuItem(
                        title = "Edit Profil",
                        icon = Icons.Default.Person,
                        onClick = { /* Navigate to Edit Profile */ }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    
                    ProfileMenuItem(
                        title = "Preferensi Makanan",
                        icon = Icons.Default.FavoriteBorder,
                        onClick = { navController.navigate(Screen.FavoriteRecipes.route) }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    
                    ProfileMenuItem(
                        title = "Riwayat Aktivitas",
                        icon = Icons.Default.Refresh,
                        onClick = { /* Navigate to Activity History */ }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    
                    ProfileMenuItem(
                        title = "Notifikasi",
                        icon = Icons.Default.Notifications,
                        onClick = { navController.navigate(Screen.Notification.route) }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    
                    ProfileMenuItem(
                        title = "Bahasa & Tema",
                        icon = Icons.Default.Settings,
                        onClick = { showThemeDialog = true }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    
                    ProfileMenuItem(
                        title = "Pusat Bantuan",
                        icon = Icons.Default.Info,
                        onClick = { /* Navigate to Help Center */ }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    
                    ProfileMenuItem(
                        title = "Keluar",
                        icon = Icons.Default.Close,
                        iconTint = Color.Red,
                        onClick = {
                            viewModel.logout {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) // Clear backstack fully
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    title: String,
    icon: ImageVector,
    iconTint: Color = Color.Gray,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                fontSize = 16.sp,
                color = if (iconTint == Color.Red) Color.Red else MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Arrow",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}
