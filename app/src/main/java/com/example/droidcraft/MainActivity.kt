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
import androidx.compose.ui.unit.dp

data class Note(val id: Int, val title: String, val content: String, val category: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NoteApp()
                }
            }
        }
    }
}

@Composable
fun NoteApp() {
    var isLocked by remember { mutableStateOf(true) }
    var pin by remember { mutableStateOf("") }
    val correctPin = "1234"

    if (isLocked) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 4) pin = it },
                label = { Text("Enter PIN (1234)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { if (pin == correctPin) isLocked = false }) {
                Text("Unlock")
            }
        }
    } else {
        NoteListScreen(onLock = { isLocked = true })
    }
}

@Composable
fun NoteListScreen(onLock: () -> Unit) {
    val categories = listOf("All", "Work", "Personal", "Ideas")
    var selectedCategory by remember { mutableStateOf("All") }
    val notes = remember {
        mutableStateListOf(
            Note(1, "Meeting", "Discuss project", "Work"),
            Note(2, "Buy milk", "Shopping list", "Personal"),
            Note(3, "App idea", "Build a note app", "Ideas")
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("My Notes", style = MaterialTheme.typography.headlineMedium)
            IconButton(onClick = onLock) { Icon(Icons.Default.Lock, contentDescription = "Lock") }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            categories.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(notes.filter { selectedCategory == "All" || it.category == selectedCategory }) { note ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(note.title, style = MaterialTheme.typography.titleMedium)
                        Text(note.content, style = MaterialTheme.typography.bodyMedium)
                        Text(note.category, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}