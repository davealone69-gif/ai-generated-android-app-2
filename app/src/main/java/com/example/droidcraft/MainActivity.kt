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
import androidx.compose.material.icons.filled.LockOpen
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
    var isLocked by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }
    val notes = remember {
        mutableStateListOf(
            Note(1, "Work Task", "Finish report", "Work"),
            Note(2, "Grocery", "Buy milk and bread", "Personal"),
            Note(3, "Idea", "Write a new novel", "Creative")
        )
    }

    val categories = listOf("All", "Work", "Personal", "Creative")
    val filteredNotes = if (selectedCategory == "All") notes else notes.filter { it.category == selectedCategory }

    if (isLocked) {
        LockScreen(onUnlock = { isLocked = false })
    } else {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text("DroidCraft Notes") },
                    actions = {
                        IconButton(onClick = { isLocked = true }) {
                            Icon(Icons.Default.Lock, contentDescription = "Lock App")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                // Category Filter
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    categories.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                // Notes List
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredNotes) { note ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(note.title, fontWeight = FontWeight.Bold)
                                Text(note.content, style = MaterialTheme.typography.bodyMedium)
                                Text("Category: ${note.category}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LockScreen(onUnlock: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("App is locked", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onUnlock) {
                Text("Unlock to access notes")
            }
        }
    }
}