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
    var isLocked by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Work", "Personal", "Ideas")
    
    val notes = remember {
        mutableStateListOf(
            Note(1, "Meeting", "Discuss project scope", "Work"),
            Note(2, "Grocery", "Buy milk and eggs", "Personal"),
            Note(3, "App Idea", "Build a note app", "Ideas")
        )
    }

    if (isLocked) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Lock, contentDescription = "Locked", modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { isLocked = false }) { Text("Unlock App") }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("My Notes", style = MaterialTheme.typography.headlineMedium)
                IconButton(onClick = { isLocked = true }) {
                    Icon(Icons.Default.LockOpen, "Lock App")
                }
            }
            
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
                            Text(note.title, fontWeight = FontWeight.Bold)
                            Text(note.content, style = MaterialTheme.typography.bodyMedium)
                            Text("Category: ${note.category}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { /* Add logic */ },
                modifier = Modifier.align(Alignment.End).padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}