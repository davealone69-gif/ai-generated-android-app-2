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
    var pin by remember { mutableStateOf("") }
    val categories = listOf("All", "Work", "Personal", "Ideas")
    var selectedCategory by remember { mutableStateOf("All") }
    val notes = remember {
        mutableStateListOf(
            Note(1, "Meeting", "Discuss project roadmap", "Work"),
            Note(2, "Grocery", "Milk, Bread, Eggs", "Personal"),
            Note(3, "App Idea", "Build a productivity app", "Ideas")
        )
    }

    if (isLocked) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp))
            Text("App Locked", style = MaterialTheme.typography.headlineMedium)
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("Enter PIN (1234)") },
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Button(onClick = { if (pin == "1234") isLocked = false }) {
                Text("Unlock")
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("DroidCraft Notes") })
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Row(modifier = Modifier.padding(bottom = 8.dp)) {
                    categories.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
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
                                Text(note.title, fontWeight = FontWeight.Bold)
                                Text(note.content, style = MaterialTheme.typography.bodyMedium)
                                Text(note.category, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}