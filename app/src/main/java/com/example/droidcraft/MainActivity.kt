package com.example.droidcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data Models
data class Note(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)

val Categories = listOf("All", "Work", "Personal", "Ideas", "Finance", "Urgent")

// Helper mapping for category color highlights
fun getCategoryColor(category: String): Color {
    return when (category) {
        "Work" -> Color(0xFFD1E8FF)
        "Personal" -> Color(0xFFE8F5E9)
        "Ideas" -> Color(0xFFFFF9C4)
        "Finance" -> Color(0xFFF3E5F5)
        "Urgent" -> Color(0xFFFFEBEE)
        else -> Color(0xFFF5F5F5)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF4A43EC),
                    secondary = Color(0xFF00C853),
                    background = Color(0xFFF8F9FC),
                    surface = Color.White
                )
            ) {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    // Pin & Security states
    var pinCode by remember { mutableStateOf("1234") } // Default lock PIN
    var isLocked by remember { mutableStateOf(true) }
    var tempPinSetup by remember { mutableStateOf("") }
    var setupMode by remember { mutableStateOf(false) }

    // Notes dataset
    val notes = remember {
        mutableStateListOf(
            Note(
                title = "Welcome to SafePad \uD83D\uDCDD",
                content = "This is a secure notepad where you can categorize notes and lock them with a custom passcode! Touch any note to edit it, or long press to delete. Use the category filters to quickly organize your workflow.",
                category = "Ideas"
            ),
            Note(
                title = "Shopping List",
                content = "- Milk & Eggs\n- Fresh blueberries\n- Whole wheat bread\n- Dark chocolate",
                category = "Personal"
            ),
            Note(
                title = "Project Launch Roadmap",
                content = "1. Finalize UI mocks\n2. Integrate database schemas\n3. Perform client presentation at 2 PM",
                category = "Work"
            ),
            Note(
                title = "Monthly Budget Allocations",
                content = "Rent: 40%\nGroceries: 15%\nSavings: 25%\nUtilities: 10%\nLeisure: 10%",
                category = "Finance"
            ),
            Note(
                title = "Urgent: Fix production crash!",
                content = "Hotfix memory leak related to rendering. Release patch v1.4.2 ASAP.",
                category = "Urgent"
            )
        )
    }

    // App Navigation & Dialog States
    var showEditor by remember { mutableStateOf(false) }
    var currentEditingNote by remember { mutableStateOf<Note?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showSettingsDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isLocked,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LockScreen(
                correctPin = pinCode,
                onUnlockSuccess = { isLocked = false },
                onSetupNewPin = {
                    setupMode = true
                }
            )
        }

        AnimatedVisibility(
            visible = !isLocked,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Safe Lock Icon",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "SafePad",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { showSettingsDialog = true }) {
                                Icon(Icons.Default.Settings, contentDescription = "Security Settings")
                            }
                            IconButton(onClick = { isLocked = true }) {
                                Icon(Icons.Default.Lock, contentDescription = "Lock App", tint = Color.Red)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            currentEditingNote = null
                            showEditor = true
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Note")
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // Search Bar
                    SearchBarComponent(query = searchQuery, onQueryChange = { searchQuery = it })

                    // Category Filter list
                    CategoryFilterRow(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )

                    // Notes Grid
                    val filteredNotes = notes.filter { note ->
                        val matchesCategory = selectedCategory == "All" || note.category == selectedCategory
                        val matchesSearch = note.title.contains(searchQuery, ignoreCase = true) ||
                                note.content.contains(searchQuery, ignoreCase = true)
                        matchesCategory && matchesSearch
                    }

                    if (filteredNotes.isEmpty()) {
                        EmptyNotesView()
                    } else {
                        NotesGrid(
                            notes = filteredNotes,
                            onNoteClick = { note ->
                                currentEditingNote = note
                                showEditor = true
                            },
                            onNoteDelete = { note ->
                                notes.remove(note)
                            }
                        )
                    }
                }
            }
        }

        // Note Editor Dialog
        if (showEditor) {
            NoteEditorDialog(
                note = currentEditingNote,
                onDismiss = { showEditor = false },
                onSave = { updatedNote ->
                    val index = notes.indexOfFirst { it.id == updatedNote.id }
                    if (index != -1) {
                        notes[index] = updatedNote
                    } else {
                        notes.add(0, updatedNote)
                    }
                    showEditor = false
                }
            )
        }

        // Settings Dialog (Change PIN)
        if (showSettingsDialog) {
            SettingsDialog(
                currentPin = pinCode,
                onDismiss = { showSettingsDialog = false },
                onSavePin = { newPin ->
                    pinCode = newPin
                    showSettingsDialog = false
                }
            )
        }

        // Dedicated Fullscreen Setup mode for convenience
        if (setupMode) {
            SetupPinScreen(
                onPinSet = { newPin ->
                    pinCode = newPin
                    setupMode = false
                    isLocked = false
                },
                onCancel = {
                    setupMode = false
                }
            )
        }
    }
}

@Composable
fun SearchBarComponent(query: String, onQueryChange = (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search notes or category details...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear Search")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun CategoryFilterRow(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(Categories) { category ->
            val isSelected = category == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(20.dp),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (isSelected) Color.Transparent else Color.LightGray,
                    selectedBorderColor = Color.Transparent,
                    borderWidth = 1.dp
                )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesGrid(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onNoteDelete: (Note) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp
    ) {
        items(notes, key = { it.id }) { note ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { onNoteClick(note) },
                        onLongClick = { onNoteDelete(note) }
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Category Chip Tag
                        Box(
                            modifier = Modifier
                                .background(
                                    color = getCategoryColor(note.category),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = note.category,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                        }

                        // Long Press indicator (Delete icon indicator)
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete Prompt",
                            tint = Color.LightGray,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { onNoteDelete(note) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = note.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        maxLines = 6,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Formatted Date
                    val sdf = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
                    val dateString = sdf.format(Date(note.timestamp))

                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyNotesView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "No Notes",
            tint = Color.LightGray,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No matching notes found",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Try clearing search filters or create a brand new secure note using the button below.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )
    }
}

// Security / Lock Screen UI
@Composable
fun LockScreen(
    correctPin: String,
    onUnlockSuccess: () -> Unit,
    onSetupNewPin: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2C))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFF32324D), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked Logo",
                tint = Color(0xFF4A43EC),
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SafePad Secured",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter passcode to decrypt and unlock notes",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Dots indicating input length
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(4) { index ->
                val isFilled = index < enteredPin.length
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = if (isFilled) Color(0xFF4A43EC) else Color(0xFF32324D),
                            shape = CircleShape
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            // Helper text so developers/testers don't get permanently stuck
            Text(
                text = "Default PIN is 1234",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Keypad Grid UI
        KeypadGrid(
            onDigitClick = { digit ->
                if (enteredPin.length < 4) {
                    enteredPin += digit
                    errorMessage = ""
                }
                if (enteredPin.length == 4) {
                    if (enteredPin == correctPin) {
                        onUnlockSuccess()
                        enteredPin = ""
                    } else {
                        errorMessage = "Invalid passcode. Please try again."
                        enteredPin = ""
                    }
                }
            },
            onDelete = {
                if (enteredPin.isNotEmpty()) {
                    enteredPin = enteredPin.dropLast(1)
                    errorMessage = ""
                }
            },
            onClear = {
                enteredPin = ""
                errorMessage = ""
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onSetupNewPin) {
            Text("Set / Change Master Passcode", color = Color(0xFF8C8A9B))
        }
    }
}

@Composable
fun KeypadGrid(
    onDigitClick: (String) -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit
) {
    val buttons = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("C", "0", "⌫")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in buttons) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                for (item in row) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.2f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF27273A))
                            .clickable {
                                when (item) {
                                    "C" -> onClear()
                                    "⌫" -> onDelete()
                                    else -> onDigitClick(item)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Dialog for Note Editor (Create or Update)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorDialog(
    note: Note?,
    onDismiss: () -> Unit,
    onSave: (Note) -> Unit
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var selectedCategory by remember { mutableStateOf(note?.category ?: "Personal") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = if (note == null) "Create Secure Note" else "Edit Secure Note",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("E.g., Ideas for Project") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Selector for note category
                Text(
                    text = "Select Category Tag",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Exclude "All" filter from actual note categories
                    val editableCategories = Categories.filter { it != "All" }
                    items(editableCategories) { category ->
                        val isSelected = category == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else getCategoryColor(category)
                                )
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color.White else Color.Black,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    placeholder = { Text("Start typing your note securely...") },
                    minLines = 4,
                    maxLines = 10,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() || content.isNotBlank()) {
                                onSave(
                                    Note(
                                        id = note?.id ?: java.util.UUID.randomUUID().toString(),
                                        title = if (title.isBlank()) "Untitled Note" else title,
                                        content = content,
                                        category = selectedCategory,
                                        timestamp = System.currentTimeMillis()
                                    )
                                )
                            } else {
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Save Note", color = Color.White)
                    }
                }
            }
        }
    }
}

// Dialog to Change Pin Code Settings
@Composable
fun SettingsDialog(
    currentPin: String,
    onDismiss: () -> Unit,
    onSavePin: (String) -> Unit
) {
    var oldPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Security Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Modify your 4-digit master security passcode",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = oldPinInput,
                    onValueChange = {
                        if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                            oldPinInput = it
                            errorMsg = ""
                        }
                    },
                    label = { Text("Verify Current PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = newPinInput,
                    onValueChange = {
                        if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                            newPinInput = it
                            errorMsg = ""
                        }
                    },
                    label = { Text("New 4-Digit PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMsg.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMsg,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (oldPinInput != currentPin) {
                                errorMsg = "Verify current PIN is incorrect."
                            } else if (newPinInput.length != 4) {
                                errorMsg = "New PIN must be exactly 4 digits."
                            } else {
                                onSavePin(newPinInput)
                            }
                        }
                    ) {
                        Text("Update PIN", color = Color.White)
                    }
                }
            }
        }
    }
}

// Full screen dedicated Setup Pin screen (Invoked when clicked master override)
@Composable
fun SetupPinScreen(
    onPinSet: (String) -> Unit,
    onCancel: () -> Unit
) {
    var setupInput by remember { mutableStateOf("") }
    var confirmInput by remember { mutableStateOf("") }
    var isConfirming by remember { mutableStateOf(false) }
    var errorLabel by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2C))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFF2C324E), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Config Icon",
                tint = Color(0xFF00C853),
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (!isConfirming) "Set Custom PIN" else "Confirm PIN",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (!isConfirming) "Input a new 4-digit PIN to lock your notepad" else "Re-enter the 4-digit PIN to confirm",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Dots indicating input status
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val activeString = if (!isConfirming) setupInput else confirmInput
            repeat(4) { index ->
                val isFilled = index < activeString.length
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = if (isFilled) Color(0xFF00C853) else Color(0xFF32324D),
                            shape = CircleShape
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorLabel.isNotEmpty()) {
            Text(
                text = errorLabel,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        KeypadGrid(
            onDigitClick = { digit ->
                if (!isConfirming) {
                    if (setupInput.length < 4) {
                        setupInput += digit
                    }
                    if (setupInput.length == 4) {
                        isConfirming = true
                        errorLabel = ""
                    }
                } else {
                    if (confirmInput.length < 4) {
                        confirmInput += digit
                    }
                    if (confirmInput.length == 4) {
                        if (setupInput == confirmInput) {
                            onPinSet(confirmInput)
                        } else {
                            errorLabel = "PINs do not match! Restarting PIN setup."
                            setupInput = ""
                            confirmInput = ""
                            isConfirming = false
                        }
                    }
                }
            },
            onDelete = {
                if (!isConfirming) {
                    if (setupInput.isNotEmpty()) setupInput = setupInput.dropLast(1)
                } else {
                    if (confirmInput.isNotEmpty()) confirmInput = confirmInput.dropLast(1)
                }
            },
            onClear = {
                setupInput = ""
                confirmInput = ""
                isConfirming = false
                errorLabel = ""
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onCancel) {
            Text("Cancel Setup", color = Color.White)
        }
    }
}