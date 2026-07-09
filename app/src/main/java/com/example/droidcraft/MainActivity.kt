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
        NotesApp(onLock = { isLocked = true; pin = "" })
    }
}

@Composable
fun LockScreen(pin: String, onPinChange: (String) -> Unit, onUnlock: () -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Lock, contentDescription = null, Modifier.size(64.dp))
        Text("Enter PIN (1234)", style = MaterialTheme.typography.headlineSmall)
        TextField(value = pin, onValueChange = onPinChange, singleLine = true)
        Button(onClick = onUnlock, Modifier.padding(top = 16.dp)) { Text("Unlock") }
    }
}

@Composable
fun NotesApp(onLock: () -> Unit) {
    var notes by remember { mutableStateOf(listOf(Note(1, "Shopping", "Milk, Bread", "Personal"), Note(2, "Work", "Meeting at 5", "Work"))) }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Personal", "Work")

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("My Notes", style = MaterialTheme.typography.headlineMedium)
            IconButton(onClick = onLock) { Icon(Icons.Default.Lock, null) }
        }
        
        Row(Modifier.padding(vertical = 8.dp)) {
            categories.forEach { cat ->
                FilterChip(selected = selectedCategory == cat, onClick = { selectedCategory = cat }, label = { Text(cat) })
                Spacer(Modifier.width(8.dp))
            }
        }

        LazyColumn {
            val filtered = if (selectedCategory == "All") notes else notes.filter { it.category == selectedCategory }
            items(filtered) { note ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(note.title, fontWeight = FontWeight.Bold)
                        Text(note.content)
                    }
                }
            }
        }
    }
}