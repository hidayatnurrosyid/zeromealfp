package com.zeromeal.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zeromeal.app.ui.theme.ZeroMealGreen

enum class BottomNavItem(val label: String, val icon: ImageVector) {
    HOME("Beranda", Icons.Default.Home),
    STOCK("Stok", Icons.Default.ShoppingCart),
    ADD("Tambah", Icons.Default.Add),
    RECIPE("Resep", Icons.Default.Restaurant),
    PROFILE("Profil", Icons.Default.Person)
}

@Composable
fun BottomNavigationBar(
    selectedItem: BottomNavItem = BottomNavItem.HOME,
    onItemSelected: (BottomNavItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem.values().forEach { item ->
            if (item == BottomNavItem.ADD) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = ZeroMealGreen,
                            shape = CircleShape
                        )
                        .clickable { onItemSelected(item) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp)
                        .clickable { onItemSelected(item) }
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (item == selectedItem) ZeroMealGreen else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        color = if (item == selectedItem) ZeroMealGreen else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (item == selectedItem) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

