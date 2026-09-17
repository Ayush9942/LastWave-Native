package com.lastwave.app.ui.sync

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomSyncBottomSheet(
    onDismiss: () -> Unit,
    viewModel: RoomSyncViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (state.isConnected) "Group Session Active" else "Join or Host Session",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (state.isConnected) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Room Code", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = state.roomId,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(state.roomId))
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Code")
                        }
                    }
                }

                Button(
                    onClick = { viewModel.leaveRoom() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Leave Session")
                }
            } else {
                OutlinedTextField(
                    value = state.roomId,
                    onValueChange = { viewModel.onRoomIdChange(it) },
                    label = { Text("Room Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.createRoom() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Create Room")
                    }

                    Button(
                        onClick = { viewModel.joinRoom() },
                        enabled = state.roomId.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Join Room")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}