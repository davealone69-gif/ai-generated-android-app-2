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
    
    if (isLocked) {
        LockScreen(onUnlock = { if (it == "1234") isLocked = false })
    } else {
        NotesScreen(onLock = { isLocked = true })
    }
}

@Composable
fun LockScreen(onUnlock: (String) -> Unit) {
    var input by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Enter PIN (1234)") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onUnlock(input) }) { Text("Unlock") }
    }
}

@Composable
fun NotesScreen(onLock: () -> Unit) {
    val categories = listOf("All", "Work", "Personal", "Ideas")
    var selectedCategory by remember { mutableStateOf("All") }
    val notes = remember {
        mutableStateListOf(
            Note(1, "Task 1", "Finish project", "Work"),
            Note(2, "Grocery", "Buy milk", "Personal"),
            Note(3, "App Idea", "Build a note app", "Ideas")
        )
    }

    val filteredNotes = if (selectedCategory == "All") notes else notes.filter { it.category == selectedCategory }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { }) { Icon(Icons.Default.Add, null) }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("My Notes", style = MaterialTheme.typography.headlineMedium)
                TextButton(onClick = onLock) { Text("Lock") }
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

            LazyColumn {
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