package com.lastwave.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ActionType {
    @SerialName("PLAY") PLAY,
    @SerialName("PAUSE") PAUSE,
    @SerialName("SEEK") SEEK,
    @SerialName("SYNC") SYNC,
    @SerialName("TRACK_CHANGE") TRACK_CHANGE
}

@Serializable
data class RoomMessage(
    val type: ActionType,
    val trackId: String? = null,
    val position: Long? = null,
    val isPlaying: Boolean = false,
    val sentAt: Long = System.currentTimeMillis()
)