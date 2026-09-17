package com.lastwave.app.ui.sync

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lastwave.app.data.websocket.WebSocketManager
import com.lastwave.app.playback.MusicPlaybackService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class RoomUiState(
    val roomId: String = "",
    val isConnected: Boolean = false,
    val connectedUsersCount: Int = 1,
    val error: String? = null
)

@HiltViewModel
class RoomSyncViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomUiState())
    val uiState: StateFlow<RoomUiState> = _uiState.asStateFlow()

    fun onRoomIdChange(newId: String) {
        _uiState.value = _uiState.value.copy(roomId = newId.trim())
    }

    fun createRoom() {
        val randomRoomId = UUID.randomUUID().toString().substring(0, 6).uppercase()
        _uiState.value = _uiState.value.copy(roomId = randomRoomId)
        joinRoom(randomRoomId)
    }

    fun joinRoom(roomId: String = _uiState.value.roomId) {
        if (roomId.isBlank()) return
        
        // Send command to MusicPlaybackService via Intent
        val intent = Intent(context, MusicPlaybackService::class.java).apply {
            action = "ACTION_JOIN_ROOM"
            putExtra("EXTRA_ROOM_ID", roomId)
        }
        context.startService(intent)
        
        _uiState.value = _uiState.value.copy(isConnected = true, roomId = roomId)
    }

    fun leaveRoom() {
        val intent = Intent(context, MusicPlaybackService::class.java).apply {
            action = "ACTION_LEAVE_ROOM"
        }
        context.startService(intent)
        
        _uiState.value = _uiState.value.copy(isConnected = false, roomId = "")
    }
}