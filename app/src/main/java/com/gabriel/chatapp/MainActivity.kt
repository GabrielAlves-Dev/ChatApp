package com.gabriel.chatapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gabriel.chatapp.ui.theme.MsgAppTheme
import com.gabriel.chatapp.ui.view.ChatScreen
import com.gabriel.chatapp.ui.view.RoomListScreen
import com.gabriel.chatapp.ui.view.notifyNewMessage
import com.gabriel.chatapp.viewmodel.MsgViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            MsgAppTheme {
                MsgAppRoot()
            }
        }
    }
}

@Composable
fun MsgAppRoot(vm: MsgViewModel = viewModel()) {
    val context = LocalContext.current
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val user by produceState(initialValue = firebaseAuth.currentUser) {
        if (value == null) {
            firebaseAuth.signInAnonymously()
                .addOnCompleteListener { value = firebaseAuth.currentUser }
        }
    }
    val userId = user?.uid ?: "user_anonimo"
    val userName by remember(user) { mutableStateOf("Usuário-${userId.takeLast(4)}") }

    // Esta variável agora controla qual tela é exibida
    var currentRoom by remember { mutableStateOf<String?>(null) }
    var lastNotifiedId by remember { mutableStateOf<String?>(null) }

    // Lida com a permissão de notificação
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
        }
    )

    LaunchedEffect(key1 = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Se currentRoom não for nulo, conecta ao ViewModel
    LaunchedEffect(currentRoom) {
        currentRoom?.let {
            vm.switchRoom(it)
        }
    }

    // Lógica para decidir qual tela mostrar
    if (currentRoom == null) {
        RoomListScreen(onRoomSelected = { roomName ->
            currentRoom = roomName
        })
    } else {
        ChatScreen(
            userId = userId,
            messages = vm.messages.collectAsState().value,
            onSend = { text -> vm.sendMessage(userId, userName, text) },
            currentRoom = currentRoom!!,
            lastNotifiedId = lastNotifiedId,
            onNotify = { msg ->
                if (hasNotificationPermission) {
                    notifyNewMessage(context, msg)
                    lastNotifiedId = msg.id
                }
            },
            onLeaveRoom = {
                currentRoom = null // Voltar para a lista de salas
            }
        )
    }
}