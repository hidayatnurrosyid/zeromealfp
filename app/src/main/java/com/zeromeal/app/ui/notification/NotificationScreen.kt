package com.zeromeal.app.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.zeromeal.app.domain.model.Notification
import com.zeromeal.app.domain.model.NotificationType
import com.zeromeal.app.navigation.Screen
import com.zeromeal.app.ui.theme.ZeroMealGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifikasi", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.markAllAsRead() }) {
                        Text(
                            text = "Baca Semua",
                            color = ZeroMealGreen,
                            fontSize = 14.sp
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            NotificationFilterChips(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = viewModel::onFilterSelected
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (uiState.notifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada notifikasi", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.notifications) { notif ->
                        NotificationItem(
                            notification = notif,
                            onClick = {
                                viewModel.markAsRead(notif.id)
                                navController.navigate(Screen.Stock.route)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationFilterChips(
    selectedFilter: NotificationFilter,
    onFilterSelected: (NotificationFilter) -> Unit
) {
    val filters = listOf(
        NotificationFilter.ALL to "Semua",
        NotificationFilter.INFO to "Info",
        NotificationFilter.RECIPE to "Resep",
        NotificationFilter.URGENT to "Urgent",
        NotificationFilter.WARNING to "Warning",
        NotificationFilter.REMINDER to "Reminder"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { pair ->
            val filter = pair.first
            val label = pair.second
            val isSelected = selectedFilter == filter

            val bgSelected = when (filter) {
                NotificationFilter.ALL -> Color(0xFFE5F8EA)
                NotificationFilter.INFO -> Color(0xFFE5F8EA)
                NotificationFilter.RECIPE -> Color(0xFFE3F2FD)
                NotificationFilter.WARNING -> Color(0xFFFFF3E0)
                NotificationFilter.URGENT -> Color(0xFFFFEBEE)
                NotificationFilter.REMINDER -> Color(0xFFF3E5F5)
            }

            val textSelected = when (filter) {
                NotificationFilter.ALL -> ZeroMealGreen
                NotificationFilter.INFO -> ZeroMealGreen
                NotificationFilter.RECIPE -> Color(0xFF1976D2)
                NotificationFilter.WARNING -> Color(0xFFF57C00)
                NotificationFilter.URGENT -> Color(0xFFD32F2F)
                NotificationFilter.REMINDER -> Color(0xFF6A1B9A)
            }

            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = label,
                        maxLines = 1,
                        softWrap = false
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedContainerColor = bgSelected,
                    selectedLabelColor = textSelected
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = when (notification.type) {
                                NotificationType.URGENT -> Color(0xFFFFE5E5)
                                NotificationType.WARNING -> Color(0xFFFFF3CD)
                                NotificationType.INFO -> Color(0xFFE5F0FF)
                                NotificationType.REMINDER -> Color(0xFFEDE7F6)
                            },
                            shape = CircleShape
                        )
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = notification.message,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(ZeroMealGreen, CircleShape)
                    )
                }
            }

            notification.actionText?.let { actionText ->
                TextButton(
                    onClick = onClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = actionText,
                        color = ZeroMealGreen,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
