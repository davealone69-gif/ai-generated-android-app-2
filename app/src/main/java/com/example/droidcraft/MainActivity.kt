package com.example.droidcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Note(val id: Int, val title: String, val content: String, val category: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    var isLocked by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }
    var noteInput by remember { mutableStateOf("") }
    val notes = remember { mutableStateListOf(
        Note(1, "Work", "Finish report", "Work"),
        Note(2, "Grocery", "Buy milk", "Personal")
    ) }

    val categories = listOf("All", "Work", "Personal", "Other")

    if (isLocked) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Lock, contentDescription = "Locked", modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { isLocked = false }) { Text("Unlock App") }
        }
    } else {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text("Note Pad") },
                    actions = {
                        IconButton(onClick = { isLocked = true }) {
                            Icon(Icons.Default.LockOpen, "Lock")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("New Note Content") },
                    trailingIcon = {
                        IconButton(onClick = {
                            if (noteInput.isNotBlank()) {
                                notes.add(Note(notes.size + 1, "New Note", noteInput, "Other"))
                                noteInput = ""
                            }
                        }) {
                            Icon(Icons.Default.Add, "Add")
                        }
                    }
                )

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(notes.filter { selectedCategory == "All" || it.category == selectedCategory }) { note ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(note.title, fontWeight = FontWeight.Bold)
                                Text(note.content)
                                Text("Category: ${note.category}", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}