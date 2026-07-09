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
    var notes by remember { mutableStateOf(listOf(
        Note(1, "Shopping", "Milk and Eggs", "Personal"),
        Note(2, "Work", "Submit report", "Work"),
        Note(3, "Idea", "Build a Compose app", "Personal")
    )) }
    
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Personal", "Work")

    if (isLocked) {
        LockScreen(onUnlock = { isLocked = false })
    } else {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text("DroidCraft Notes") },
                    actions = {
                        IconButton(onClick = { isLocked = true }) {
                            Icon(Icons.Default.Lock, contentDescription = "Lock")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
                
                LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                    val filteredNotes = if (selectedCategory == "All") notes 
                                        else notes.filter { it.category == selectedCategory }
                    
                    items(filteredNotes) { note ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(note.title, style = MaterialTheme.typography.titleMedium)
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

@Composable
fun LockScreen(onUnlock: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("App Locked", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onUnlock) {
            Icon(Icons.Default.LockOpen, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Unlock App")
        }
    }
}