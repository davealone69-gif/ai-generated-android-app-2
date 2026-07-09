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
    val correctPin = "1234"

    if (isLocked) {
        LockScreen(pin = pin, onPinChange = { pin = it }, onUnlock = { if (pin == correctPin) isLocked = false })
    } else {
        NoteScreen(onLock = { isLocked = true })
    }
}

@Composable
fun LockScreen(pin: String, onPinChange: (String) -> Unit, onUnlock: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("App Locked", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = pin, onValueChange = onPinChange, label = { Text("Enter PIN (1234)") })
        Button(onClick = onUnlock) { Text("Unlock") }
    }
}

@Composable
fun NoteScreen(onLock: () -> Unit) {
    val notes = remember { mutableStateListOf(
        Note(1, "Buy Milk", "Groceries", "Personal"),
        Note(2, "Meeting", "Discuss project", "Work")
    )}
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Personal", "Work")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("My Notes", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = onLock) { Icon(Icons.Default.Lock, contentDescription = "Lock") }
        }

        Row {
            categories.forEach { cat ->
                FilterChip(selected = selectedCategory == cat, onClick = { selectedCategory = cat }, label = { Text(cat) })
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        LazyColumn {
            items(notes.filter { selectedCategory == "All" || it.category == selectedCategory }) { note ->
                Card(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(note.title, fontWeight = FontWeight.Bold)
                        Text(note.content, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}