package com.lastwave.app.data.network

import com.lastwave.app.data.model.RoomMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class WebSocketManager(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()
) {
    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private val json = Json { ignoreUnknownKeys = true }

    private val _events = MutableSharedFlow<RoomMessage>(extraBufferCapacity = 64)
    val events: SharedFlow<RoomMessage> = _events.asSharedFlow()

    fun connect(roomId: String, token: String? = null) {
        val baseUrl = "wss://lastwave-room-backend.ayushranjan14972008.workers.dev/room/$roomId"
        val url = if (!token.isNullOrEmpty()) "$baseUrl?token=$token" else baseUrl

        val request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val message = json.decodeFromString<RoomMessage>(text)
                    scope.launch { _events.emit(message) }
                } catch (_: Exception) {
                    // Ignored malformed messages
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // Connection error handling
            }
        })
    }

    fun sendEvent(event: RoomMessage): Boolean {
        val text = json.encodeToString(event)
        return webSocket?.send(text) ?: false
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }
}
