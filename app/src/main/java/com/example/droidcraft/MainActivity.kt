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
import androidx.compose.ui.unit.dp

data class Note(val id: Int, val title: String, val content: String, val category: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                NoteApp()
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
            Note(1, "Meeting", "Discuss project Q4", "Work"),
            Note(2, "Grocery", "Milk, Eggs, Bread", "Personal"),
            Note(3, "App Idea", "Build a note app", "Ideas")
        )
    }

    if (isLocked) {
        LockScreen(onUnlock = { isLocked = false })
    } else {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text("DroidCraft Notes") },
                    actions = {
                        IconButton(onClick = { isLocked = true }) {
                            Icon(Icons.Default.Lock, "Lock App")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                ScrollableTabRow(selectedTabIndex = categories.indexOf(selectedCategory)) {
                    categories.forEach { cat ->
                        Tab(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            text = { Text(cat) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val filteredNotes = if (selectedCategory == "All") notes 
                                        else notes.filter { it.category == selectedCategory }
                    
                    items(filteredNotes) { note ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(note.title, style = MaterialTheme.typography.titleMedium)
                                Text(note.content, style = MaterialTheme.typography.bodyMedium)
                                Text(note.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
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
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("App is locked", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onUnlock) {
            Icon(Icons.Default.LockOpen, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Unlock")
        }
    }
}