package com.example.droidcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
            NotePadApp()
        }
    }
}

@Composable
fun NotePadApp() {
    var isLocked by remember { mutableStateOf(true) }
    var pin by remember { mutableStateOf("") }
    val notes = remember {
        mutableStateListOf(
            Note(1, "Work Task", "Finish project report", "Work"),
            Note(2, "Grocery", "Buy milk and bread", "Personal"),
            Note(3, "Idea", "Build a mobile app", "Work")
        )
    }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Work", "Personal")

    if (isLocked) {
        LockScreen(pin = pin, onPinChange = { pin = it }, onUnlock = { if (pin == "1234") isLocked = false })
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {}) { Icon(Icons.Default.Add, contentDescription = "Add") }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                Text("My Notes", style = MaterialTheme.typography.headlineMedium)
                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
                LazyColumn {
                    val filteredNotes = if (selectedCategory == "All") notes else notes.filter { it.category == selectedCategory }
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
    }
}

@Composable
fun LockScreen(pin: String, onPinChange: (String) -> Unit, onUnlock: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = pin,
            onValueChange = onPinChange,
            label = { Text("Enter PIN (1234)") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onUnlock) { Text("Unlock") }
    }
}