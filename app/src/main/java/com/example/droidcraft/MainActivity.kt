package com.example.droidcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainAppScreen()
                }
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    var isLocked by remember { mutableStateOf(true) }
    
    if (isLocked) {
        LockScreen(onUnlock = { if (it == "1234") isLocked = false })
    } else {
        NoteAppContent(onLock = { isLocked = true })
    }
}

@Composable
fun LockScreen(onUnlock: (String) -> Unit) {
    var input by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Lock, contentDescription = "Lock", modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("Enter PIN (1234)") })
        Button(onClick = { onUnlock(input) }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Unlock")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteAppContent(onLock: () -> Unit) {
    val notes = remember { mutableStateListOf(
        Note(1, "Grocery", "Buy milk", "Personal"),
        Note(2, "Work", "Meeting at 10", "Work"),
        Note(3, "Gym", "Leg day", "Personal")
    ) }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Personal", "Work")
    
    val filteredNotes = if (selectedCategory == "All") notes else notes.filter { it.category == selectedCategory }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("My Notes", style = MaterialTheme.typography.headlineMedium)
            IconButton(onClick = onLock) { Icon(Icons.Default.Lock, contentDescription = "Lock") }
        }
        
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredNotes) { note ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(note.title, fontWeight = FontWeight.Bold)
                        Text(note.content, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}