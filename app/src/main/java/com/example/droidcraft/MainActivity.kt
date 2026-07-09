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
    var isLocked by remember { mutableStateOf(true) }
    var pin by remember { mutableStateOf("") }
    
    if (isLocked) {
        LockScreen(onUnlock = { if (pin == "1234") isLocked = false }) { pin = it }
    } else {
        NotesScreen(onLock = { isLocked = true })
    }
}

@Composable
fun LockScreen(onUnlock: () -> Unit, onPinChange: (String) -> Unit) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Enter PIN (1234)", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = "", onValueChange = onPinChange, label = { Text("PIN") })
        Button(onClick = onUnlock) { Text("Unlock") }
    }
}

@Composable
fun NotesScreen(onLock: () -> Unit) {
    val notes = remember { mutableStateListOf(Note(1, "Work Task", "Finish report", "Work"), Note(2, "Grocery", "Buy milk", "Personal")) }
    val categories = listOf("All", "Work", "Personal")
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredNotes = if (selectedCategory == "All") notes else notes.filter { it.category == selectedCategory }

    Scaffold(
        topBar = {
            SmallTopAppBar(title = { Text("My Notes") }, actions = {
                IconButton(onClick = onLock) { Icon(Icons.Default.Lock, "Lock") }
            })
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            Row(Modifier.padding(8.dp)) {
                categories.forEach { cat ->
                    FilterChip(selected = selectedCategory == cat, onClick = { selectedCategory = cat }, label = { Text(cat) })
                    Spacer(Modifier.width(8.dp))
                }
            }
            LazyColumn {
                items(filteredNotes) { note ->
                    Card(Modifier.fillMaxWidth().padding(8.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text(note.title, fontWeight = FontWeight.Bold)
                            Text(note.content)
                        }
                    }
                }
            }
        }
    }
}