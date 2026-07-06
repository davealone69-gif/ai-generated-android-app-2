package com.example.droidcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

// Data classes
data class Note(
    val id: Long,
    var title: String,
    var content: String,
    var category: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainAppScreen()
        }
    }
}

@Composable
fun MainAppScreen() {
    // This is the main entry point controlling the lock screen state
    var isLocked by remember { mutableStateOf(true) } // Start locked
    val appPin = "1234" // The secret PIN

    MaterialTheme { // Apply MaterialTheme to the whole app
        if (isLocked) {
            LockScreen(
                appPin = appPin,
                onUnlock = { isLocked = false }
            )
        } else {
            NotePadApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockScreen(appPin: String, onUnlock: () -> Unit) {
    var pinAttempt by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to DroidCraft Notes",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Enter PIN to access your notes",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = pinAttempt,
            onValueChange = {
                if (it.length <= appPin.length) { // Limit input length to PIN length
                    pinAttempt = it
                    pinError = false // Reset error on new input
                }
            },
            label = { Text("PIN") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            isError = pinError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.6f)
        )
        if (pinError) {
            Text(
                text = "Incorrect PIN. Try again.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (pinAttempt == appPin) {
                    onUnlock()
                } else {
                    pinError = true
                    pinAttempt = "" // Clear input on error
                }
            },
            enabled = pinAttempt.length == appPin.length, // Enable only when full PIN is entered
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Unlock")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotePadApp() {
    val initialNotes = remember {
        mutableStateListOf(
            Note(1, "Shopping List", "Milk, Eggs, Bread, Butter", "Personal"),
            Note(2, "Project Alpha Meeting", "Discuss Q3 goals, assign tasks.", "Work"),
            Note(3, "Idea for new app", "A social network for pet owners.", "Ideas"),
            Note(4, "Weekend Plans", "Visit the park, read a book.", "Personal"),
            Note(5, "Bug Report", "App crashes on login screen for Android 12 devices.", "Work")
        )
    }
    var notes by remember { mutableStateOf(initialNotes.toList()) } // Convert to immutable list for state
    val categories = remember { listOf("All", "Personal", "Work", "Ideas", "Other") }
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    var showNoteEditor by remember { mutableStateOf(false) }
    var currentEditingNoteId by remember { mutableStateOf<Long?>(null) } // null for new note

    val filteredNotes = remember(notes, selectedCategory) {
        if (selectedCategory == "All") {
            notes
        } else {
            notes.filter { it.category == selectedCategory }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DroidCraft Notes") },
                actions = {
                    CategoryFilterDropdown(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                currentEditingNoteId = null // For a new note
                showNoteEditor = true
            }) {
                Icon(Icons.Filled.Add, "Add new note")
            }
        }
    ) { paddingValues ->
        if (filteredNotes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (selectedCategory == "All") "No notes yet!" else "No notes found in '$selectedCategory' category.",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (selectedCategory == "All") "Tap the '+' button to create your first note." else "Try selecting 'All' or adding a new note.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredNotes, key = { it.id }) { note ->
                    NoteCard(note = note) {
                        currentEditingNoteId = note.id
                        showNoteEditor = true
                    }
                }
            }
        }


        if (showNoteEditor) {
            val noteToEdit = currentEditingNoteId?.let { id ->
                notes.find { it.id == id }
            }
            NoteEditorDialog(
                note = noteToEdit,
                categories = categories.drop(1), // Don't include "All" for editing
                onSave = { newOrUpdatedNote ->
                    val existingNoteIndex = notes.indexOfFirst { it.id == newOrUpdatedNote.id }
                    val updatedNotes = notes.toMutableList() // Create a mutable copy

                    if (existingNoteIndex != -1) {
                        updatedNotes[existingNoteIndex] = newOrUpdatedNote
                    } else {
                        // Assign a new unique ID for new notes
                        val newId = (notes.maxOfOrNull { it.id } ?: 0L) + 1
                        updatedNotes.add(newOrUpdatedNote.copy(id = newId))
                    }
                    notes = updatedNotes.toList() // Update the state with the new list
                    showNoteEditor = false
                },
                onCancel = {
                    showNoteEditor = false
                }
            )
        }
    }
}

@Composable
fun CategoryFilterDropdown(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        TextButton(onClick = { expanded = true }) {
            Text(selectedCategory)
            Icon(Icons.Filled.MoreVert, contentDescription = "Select category")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(note: Note, onClick: (Note) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(note) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.category,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorDialog(
    note: Note?, // Null for a new note
    categories: List<String>,
    onSave: (Note) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember(note) { mutableStateOf(note?.title ?: "") }
    var content by remember(note) { mutableStateOf(note?.content ?: "") }
    var selectedCategory by remember(note) { mutableStateOf(note?.category ?: categories.firstOrNull() ?: "Other") }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onCancel) {
        Card(modifier = Modifier.fillMaxWidth(0.95f)) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = if (note == null) "New Note" else "Edit Note",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Category selection dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryMenuExpanded,
                    onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = categoryMenuExpanded,
                        onDismissRequest = { categoryMenuExpanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    categoryMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = onCancel) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onSave(
                                    Note(
                                        id = note?.id ?: 0L, // ID will be assigned by NotePadApp for new notes
                                        title = title,
                                        content = content,
                                        category = selectedCategory
                                    )
                                )
                            }
                        },
                        enabled = title.isNotBlank() && content.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}