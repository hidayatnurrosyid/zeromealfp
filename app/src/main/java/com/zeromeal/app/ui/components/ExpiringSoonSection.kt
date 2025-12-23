package com.zeromeal.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zeromeal.app.domain.model.ExpiryStatus
import com.zeromeal.app.domain.model.FoodItem
import com.zeromeal.app.ui.theme.ZeroMealGreen
import com.zeromeal.app.ui.theme.ZeroMealRed
import com.zeromeal.app.ui.theme.ZeroMealYellow

@Composable
fun ExpiringSoonSection(
    items: List<FoodItem>,
    onSeeAllClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Kedaluwarsa Segera",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Lihat Semua",
                fontSize = 14.sp,
                color = ZeroMealGreen,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable(onClick = onSeeAllClick)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (items.isEmpty()) {
            Text(
                text = "Tidak ada barang yang akan kedaluwarsa segera",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items.take(5).forEach { item ->
                    ExpiringItemCard(item = item)
                }
            }
        }
    }
}

@Composable
fun ExpiringItemCard(item: FoodItem) {
    val expiryColor = when (item.expiryStatus) {
        ExpiryStatus.URGENT -> ZeroMealRed
        ExpiryStatus.WARNING -> ZeroMealYellow
        ExpiryStatus.NORMAL -> ZeroMealGreen
    }
    
    val daysText = if (item.daysUntilExpiration < 0) {
        "Kedaluwarsa"
    } else if (item.daysUntilExpiration.toInt() == 0) {
        "Hari ini"
    } else {
        "${item.daysUntilExpiration} hari"
    }
    
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Image Section (Left)
            if (item.imageUrl != null) {
                coil.compose.AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📦", fontSize = 32.sp)
                }
            }
            
            // Content Section (Right)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    
                    Box(
                        modifier = Modifier
                            .background(expiryColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = daysText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = expiryColor
                        )
                    }
                }
                
                Column {
                    Text(
                        text = "${item.quantity} ${item.unit}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    Text(
                        text = "di ${item.storageLocation}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
