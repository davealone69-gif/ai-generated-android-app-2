package com.example.droidcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.unit.dp

data class Note(val id: Int, val title: String, val content: String, val category: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NoteApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteApp() {
    var isLocked by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }
    val notes = remember { mutableStateListOf(
        Note(1, "Grocery", "Buy milk and eggs", "Personal"),
        Note(2, "Meeting", "Discuss project roadmap", "Work")
    ) }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Personal", "Work", "Ideas")

    if (isLocked) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Enter Password (try '123')") },
                singleLine = true
            )
            Button(onClick = { if (password == "123") isLocked = false }) {
                Text("Unlock App")
            }
        }
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text("Categories", style = MaterialTheme.typography.titleMedium)
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
                LazyColumn {
                    val filteredNotes = if (selectedCategory == "All") notes 
                                       else notes.filter { it.category == selectedCategory }
                    items(filteredNotes) { note ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(note.title, style = MaterialTheme.typography.titleLarge)
                                Text(note.content, style = MaterialTheme.typography.bodyMedium)
                                Text("Category: ${note.category}", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}