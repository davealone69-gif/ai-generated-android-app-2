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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteApp() {
    var isLocked by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Work", "Personal", "Ideas")
    val notes = remember {
        mutableStateListOf(
            Note(1, "Meeting", "Discuss project updates", "Work"),
            Note(2, "Grocery", "Buy milk and eggs", "Personal"),
            Note(3, "Idea", "Build an Android app", "Ideas")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DroidCraft Notepad") },
                actions = {
                    IconButton(onClick = { isLocked = !isLocked }) {
                        Icon(if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen, "Lock")
                    }
                }
            )
        }
    ) { padding ->
        if (isLocked) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("App Locked. Unlock to view notes.", fontWeight = FontWeight.Bold)
            }
        } else {
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
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
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val filteredNotes = if (selectedCategory == "All") notes else notes.filter { it.category == selectedCategory }
                    items(filteredNotes) { note ->
                        Card(modifier = Modifier.fillMaxWidth()) {
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
}