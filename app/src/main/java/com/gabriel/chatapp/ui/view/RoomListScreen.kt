package com.gabriel.chatapp.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val defaultRooms = listOf("Geral", "Jogos", "Estudos", "Filmes e Séries")

@OptIn(ExperimentalMaterial3Api::class) // <- ANOTAÇÃO ADICIONADA AQUI
@Composable
fun RoomListScreen(onRoomSelected: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Salas de Bate-papo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(defaultRooms) { roomName ->
                RoomListItem(roomName = roomName, onRoomClicked = { onRoomSelected(it) })
            }
        }
    }
}

@Composable
fun RoomListItem(roomName: String, onRoomClicked: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onRoomClicked(roomName) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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