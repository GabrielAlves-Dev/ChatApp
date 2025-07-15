package com.gabriel.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import com.gabriel.chatapp.model.Room
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RoomViewModel : ViewModel() {

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms

    private val roomsRef = FirebaseDatabase.getInstance().getReference("rooms")

    init {
        fetchRooms()
    }

    private fun fetchRooms() {
        roomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val roomList = mutableListOf<Room>()
                for (child in snapshot.children) {
                    if (child.hasChild("name")) {
                        val room = child.getValue(Room::class.java)
                        if (room != null) {
                            roomList.add(room.copy(id = child.key!!))
                        }
                    }
                }
                _rooms.value = roomList
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun createRoom(roomName: String) {
        val roomId = roomsRef.push().key ?: return
        val room = Room(id = roomId, name = roomName)
        roomsRef.child(roomId).setValue(room)
    }

    fun deleteRoom(roomId: String) {
        roomsRef.child(roomId).removeValue()
    }
}