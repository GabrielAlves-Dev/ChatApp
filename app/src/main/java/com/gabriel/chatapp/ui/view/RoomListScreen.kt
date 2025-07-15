// gabrielalves-dev/chatapp/ChatApp-5fd9569fb937b7ae25136c9a32a7c114bea97d62/app/src/main/java/com/gabriel/chatapp/ui/view/RoomListScreen.kt
package com.gabriel.chatapp.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabriel.chatapp.model.Room

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RoomListScreen(
    rooms: List<Room>,
    onRoomSelected: (String) -> Unit,
    onCreateRoom: (String) -> Unit,
    onDeleteRoom: (String) -> Unit
) {
    var showCreateRoomDialog by remember { mutableStateOf(false) }
    var newRoomName by remember { mutableStateOf("") }
    var selectedRoom by remember { mutableStateOf<Room?>(null) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Salas de Bate-papo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (selectedRoom != null) {
                        IconButton(onClick = {
                            onDeleteRoom(selectedRoom!!.id)
                            selectedRoom = null
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Excluir Sala",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        var showMenu by remember { mutableStateOf(false) }

                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Pesquisar") },
                                onClick = {
                                    showMenu = false
                                    showSearchBar = !showSearchBar
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Criar nova sala") },
                                onClick = {
                                    showMenu = false
                                    showCreateRoomDialog = true
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (showSearchBar) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Pesquisar sala") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val filteredRooms = rooms.filter { it.name.contains(searchQuery, ignoreCase = true) }
                items(filteredRooms) { room ->
                    RoomListItem(
                        roomName = room.name,
                        isSelected = room == selectedRoom,
                        onRoomClicked = {
                            if (selectedRoom == null) {
                                onRoomSelected(it)
                            } else {
                                selectedRoom = if (selectedRoom == room) null else room
                            }
                        },
                        onRoomLongClicked = {
                            selectedRoom = if (selectedRoom == room) null else room
                        }
                    )
                }
            }
        }
    }

    if (showCreateRoomDialog) {
        AlertDialog(
            onDismissRequest = { showCreateRoomDialog = false },
            title = { Text("Criar nova sala") },
            text = {
                OutlinedTextField(
                    value = newRoomName,
                    onValueChange = { newRoomName = it },
                    label = { Text("Nome da sala") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newRoomName.isNotBlank()) {
                            onCreateRoom(newRoomName)
                            newRoomName = ""
                            showCreateRoomDialog = false
                        }
                    }
                ) {
                    Text("Criar")
                }
            },
            dismissButton = {
                Button(onClick = { showCreateRoomDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoomListItem(
    roomName: String,
    isSelected: Boolean,
    onRoomClicked: (String) -> Unit,
    onRoomLongClicked: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .combinedClickable(
                onClick = { onRoomClicked(roomName) },
                onLongClick = { onRoomLongClicked(roomName) }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = roomName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}