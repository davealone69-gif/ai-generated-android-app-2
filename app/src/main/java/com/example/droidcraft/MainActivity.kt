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
    
    if (isLocked) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("Enter PIN (1234)") },
                singleLine = true
            )
            Button(onClick = { if (pin == "1234") isLocked = false }, modifier = Modifier.padding(top = 16.dp)) {
                Text("Unlock")
            }
        }
    } else {
        NoteScreen()
    }
}

@Composable
fun NoteScreen() {
    val notes = remember { mutableStateListOf(
        Note(1, "Grocery List", "Milk, Eggs", "Personal"),
        Note(2, "Meeting", "Discuss Project", "Work")
    ) }
    val categories = listOf("All", "Personal", "Work")
    var selectedCategory by remember { mutableStateOf("All") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("My Notes", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(notes.filter { selectedCategory == "All" || it.category == selectedCategory }) { note ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(note.title, fontWeight = FontWeight.Bold)
                        Text(note.content, style = MaterialTheme.typography.bodyMedium)
                        Text("Category: ${note.category}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
        
        FloatingActionButton(onClick = { }, modifier = Modifier.align(Alignment.End)) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}