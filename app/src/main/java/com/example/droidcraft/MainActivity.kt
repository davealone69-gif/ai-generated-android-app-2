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
    var pinInput by remember { mutableStateOf("") }
    val correctPin = "1234"

    if (isLocked) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp))
            Text("Enter PIN (1234)", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = pinInput,
                onValueChange = { pinInput = it },
                label = { Text("PIN") },
                singleLine = true
            )
            Button(onClick = { if (pinInput == correctPin) isLocked = false }) {
                Text("Unlock")
            }
        }
    } else {
        NoteListScreen()
    }
}

@Composable
fun NoteListScreen() {
    val categories = listOf("All", "Work", "Personal", "Ideas")
    var selectedCategory by remember { mutableStateOf("All") }
    val notes = remember {
        mutableStateListOf(
            Note(1, "Meeting", "Discuss project scope", "Work"),
            Note(2, "Groceries", "Buy milk and eggs", "Personal"),
            Note(3, "Idea", "Build an Android app", "Ideas")
        )
    }

    val filteredNotes = if (selectedCategory == "All") notes else notes.filter { it.category == selectedCategory }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("My Notes", style = MaterialTheme.typography.headlineMedium)
        
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            categories.forEach { cat ->
                FilterChip(
                    selected = (selectedCategory == cat),
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredNotes) { note ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(note.title, fontWeight = FontWeight.Bold)
                        Text(note.content, style = MaterialTheme.typography.bodyMedium)
                        Text("Category: ${note.category}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}