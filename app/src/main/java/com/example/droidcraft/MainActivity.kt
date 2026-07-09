package com.example.droidcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Note(val id: Int, val title: String, val content: String, val category: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NotePadApp()
                }
            }
        }
    }
}

@Composable
fun NotePadApp() {
    var isLocked by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }
    
    val notes = remember {
        mutableStateListOf(
            Note(1, "Shopping List", "Milk, Eggs, Bread", "Personal"),
            Note(2, "Project Ideas", "Build AI assistant", "Work"),
            Note(3, "Workout", "5 sets of pushups", "Health")
        )
    }

    val categories = listOf("All", "Personal", "Work", "Health")
    val filteredNotes = if (selectedCategory == "All") notes else notes.filter { it.category == selectedCategory }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("DroidNotes", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { isLocked = !isLocked }) {
                Icon(if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen, contentDescription = "Lock App")
            }
        }

        if (isLocked) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("App is locked. Unlock to view notes.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            ScrollableTabRow(selectedTabIndex = categories.indexOf(selectedCategory)) {
                categories.forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        text = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredNotes) { note ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = note.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Category: ${note.category}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}