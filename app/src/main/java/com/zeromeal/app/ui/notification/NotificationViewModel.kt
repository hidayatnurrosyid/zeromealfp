package com.zeromeal.app.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeromeal.app.domain.model.Notification
import com.zeromeal.app.domain.model.NotificationType
import com.zeromeal.app.domain.usecase.notification.GetAllNotificationsUseCase
import com.zeromeal.app.domain.usecase.notification.MarkNotificationAsReadUseCase
import com.zeromeal.app.domain.usecase.notification.MarkAllNotificationsAsReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiState(
    val notifications: List<Notification> = emptyList(),
    val selectedFilter: NotificationFilter = NotificationFilter.ALL,
    val isLoading: Boolean = false
)

enum class NotificationFilter {
    ALL, INFO, RECIPE, URGENT, WARNING, REMINDER
}

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getAllNotificationsUseCase: GetAllNotificationsUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
    private val markAllNotificationsAsReadUseCase: MarkAllNotificationsAsReadUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        observeNotifications()
    }

    private fun observeNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getAllNotificationsUseCase().collectLatest { list ->
                _uiState.update { state ->
                    state.copy(
                        notifications = applyFilterToList(list, state.selectedFilter),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun applyFilterToList(
        all: List<Notification>,
        filter: NotificationFilter
    ): List<Notification> {
        val base = when (filter) {
            NotificationFilter.ALL -> all
            NotificationFilter.INFO -> all.filter { it.type == NotificationType.INFO }
            NotificationFilter.URGENT -> all.filter { it.type == NotificationType.URGENT }
            NotificationFilter.WARNING -> all.filter { it.type == NotificationType.WARNING }
            NotificationFilter.REMINDER -> all.filter { it.type == NotificationType.REMINDER }
            NotificationFilter.RECIPE -> all.filter { it.type == NotificationType.INFO && it.actionText == "Lihat Resep" }
        }
        return base.sortedByDescending { it.timestamp }
    }

    fun onFilterSelected(filter: NotificationFilter) {
        _uiState.update { state ->
            val currentAll = state.notifications + emptyList<Notification>() // snapshot not stored separately, so just refetch via flow
            state.copy(
                selectedFilter = filter,
                notifications = state.notifications // will be updated next emission
            )
        }
        // Force refresh by re-observing
        observeNotifications()
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            markNotificationAsReadUseCase(id)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            markAllNotificationsAsReadUseCase()
        }
    }
}




