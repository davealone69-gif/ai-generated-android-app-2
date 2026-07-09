package com.example.droidcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
            NotePadApp()
        }
    }
}

@Composable
fun NotePadApp() {
    var isLocked by remember { mutableStateOf(true) }
    var pin by remember { mutableStateOf("") }
    val correctPin = "1234"

    if (isLocked) {
        LockScreen(pin, onPinChange = { pin = it }, onUnlock = { if (pin == correctPin) isLocked = false })
    } else {
        NotesScreen(onLock = { isLocked = true; pin = "" })
    }
}

@Composable
fun LockScreen(pin: String, onPinChange: (String) -> Unit, onUnlock: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp))
        Text("Enter PIN (1234)", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = pin, onValueChange = onPinChange, singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onUnlock) { Text("Unlock") }
    }
}

@Composable
fun NotesScreen(onLock: () -> Unit) {
    var notes = remember { mutableStateListOf(Note(1, "Buy Milk", "Need 2L", "Personal"), Note(2, "Compose", "Learn State", "Work")) }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Personal", "Work")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("My Notes", style = MaterialTheme.typography.headlineMedium)
            IconButton(onClick = onLock) { Icon(Icons.Default.Lock, contentDescription = "Lock") }
        }
        
        Row {
            categories.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            val filtered = if (selectedCategory == "All") notes else notes.filter { it.category == selectedCategory }
            items(filtered) { note ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(note.title, fontWeight = FontWeight.Bold)
                        Text(note.content, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}