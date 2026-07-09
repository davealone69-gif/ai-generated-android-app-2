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
                    NotePadApp()
                }
            }
        }
    }
}

@Composable
fun NotePadApp() {
    var isLocked by remember { mutableStateOf(true) }
    var pin by remember { mutableStateOf("") }
    val notes = remember { mutableStateListOf(
        Note(1, "Grocery", "Milk, Eggs", "Personal"),
        Note(2, "Meeting", "Discuss Project", "Work"),
        Note(3, "Idea", "Build a robot", "Personal")
    ) }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Work", "Personal")

    if (isLocked) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Lock, contentDescription = "Locked", modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = pin, onValueChange = { pin = it }, label = { Text("Enter PIN (1234)") })
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { if (pin == "1234") isLocked = false }) { Text("Unlock") }
        }
    } else {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("My Notes", style = MaterialTheme.typography.headlineMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        categories.forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) }
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { /* Add logic */ }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { padding ->
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                items(notes.filter { selectedCategory == "All" || it.category == selectedCategory }) { note ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(note.title, fontWeight = FontWeight.Bold)
                            Text(note.content, style = MaterialTheme.typography.bodyMedium)
                            Text(note.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}