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
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NotePadApp()
                }
            }
        }
    }
}

@Composable
fun NotePadApp() {
    var isLocked by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }
    
    if (isLocked) {
        LockScreen(onUnlock = { if (password == "1234") isLocked = false }) { password = it }
    } else {
        NoteScreen(onLock = { isLocked = true })
    }
}

@Composable
fun LockScreen(onUnlock: () -> Unit, onPasswordChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("App Locked", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = "", onValueChange = onPasswordChange, label = { Text("Enter Password (1234)") })
        Button(onClick = onUnlock) { Text("Unlock") }
    }
}

@Composable
fun NoteScreen(onLock: () -> Unit) {
    val categories = listOf("All", "Work", "Personal", "Ideas")
    var selectedCategory by remember { mutableStateOf("All") }
    val notes = remember { mutableStateListOf(
        Note(1, "Meeting", "Discuss project", "Work"),
        Note(2, "Buy milk", "Shopping list", "Personal")
    )}

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("My Notes", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = onLock) { Icon(Icons.Default.Lock, contentDescription = "Lock") }
        }
        
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

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(notes.filter { selectedCategory == "All" || it.category == selectedCategory }) { note ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(note.title, fontWeight = FontWeight.Bold)
                        Text(note.content, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        FloatingActionButton(onClick = {}, modifier = Modifier.align(Alignment.End)) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}