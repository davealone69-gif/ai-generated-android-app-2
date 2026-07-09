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
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    var isLocked by remember { mutableStateOf(true) }
    var pin by remember { mutableStateOf("") }
    val notes = remember {
        mutableStateListOf(
            Note(1, "Shopping", "Buy milk and eggs", "Personal"),
            Note(2, "Meeting", "Discuss project roadmap", "Work"),
            Note(3, "Idea", "Build a Compose app", "Personal")
        )
    }
    val categories = listOf("All", "Personal", "Work")
    var selectedCategory by remember { mutableStateOf("All") }

    if (isLocked) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Lock, contentDescription = "Locked", modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("Enter PIN (1234)") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { if (pin == "1234") isLocked = false }) {
                Text("Unlock")
            }
        }
    } else {
        Scaffold(
            topBar = {
                SmallTopAppBar(title = { Text("My Notes") }, actions = {
                    IconButton(onClick = { isLocked = true; pin = "" }) {
                        Icon(Icons.Default.Lock, "Lock App")
                    }
                })
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val filtered = if (selectedCategory == "All") notes 
                                   else notes.filter { it.category == selectedCategory }
                    items(filtered) { note ->
                        Card(modifier = Modifier.fillMaxWidth()) {
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
    }
}