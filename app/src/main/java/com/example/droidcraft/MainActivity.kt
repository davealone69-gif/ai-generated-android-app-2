package com.example.droidcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

@Composable
fun NoteApp() {
    var isLocked by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Work", "Personal", "Ideas")
    
    val notes = remember {
        mutableStateListOf(
            Note(1, "Meeting", "Discuss project Q4", "Work"),
            Note(2, "Grocery", "Buy milk and eggs", "Personal"),
            Note(3, "App Idea", "Build a note app", "Ideas")
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("My Notes", style = MaterialTheme.typography.headlineMedium)
            IconButton(onClick = { isLocked = !isLocked }) {
                Icon(if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen, contentDescription = "Lock")
            }
        }

        if (!isLocked) {
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

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(notes.filter { selectedCategory == "All" || it.category == selectedCategory }) { note ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = note.title, fontWeight = FontWeight.Bold)
                            Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Category: ${note.category}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("App Locked. Tap the lock icon to unlock.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}