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
        Note(1, "Shopping", "Buy milk and bread", "Personal"),
        Note(2, "Work", "Finish project report", "Work"),
        Note(3, "Ideas", "Compose a new song", "Creative")
    ) }
    var selectedCategory by remember { mutableStateOf("All") }

    if (isLocked) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Locked", style = MaterialTheme.typography.headlineMedium)
            OutlinedTextField(value = pin, onValueChange = { pin = it }, label = { Text("Enter PIN (1234)") }, singleLine = true)
            Button(onClick = { if (pin == "1234") isLocked = false }) { Text("Unlock") }
        }
    } else {
        val categories = listOf("All", "Personal", "Work", "Creative")
        val filteredNotes = if (selectedCategory == "All") notes else notes.filter { it.category == selectedCategory }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("My Notes", style = MaterialTheme.typography.headlineSmall)
                IconButton(onClick = { isLocked = true; pin = "" }) { Icon(Icons.Default.Lock, "Lock") }
            }
            
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
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
                            Text(text = note.title, fontWeight = FontWeight.Bold)
                            Text(text = note.content)
                            Text(text = "Category: ${note.category}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            
            FloatingActionButton(onClick = { notes.add(Note(notes.size + 1, "New Note", "Content...", "Personal")) }) {
                Icon(Icons.Default.Add, "Add")
            }
        }
    }
}