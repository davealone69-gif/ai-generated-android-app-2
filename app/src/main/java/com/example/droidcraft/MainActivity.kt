package com.example.droidcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF81C784),
                    secondary = Color(0xFF4DB6AC),
                    background = Color(0xFF121212),
                    surface = Color(0xFF1E1E1E),
                    onPrimary = Color(0xFF1B5E20),
                    onSecondary = Color(0xFF004D40)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppScreen()
                }
            }
        }
    }
}

// Data models
data class Note(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var content: String,
    var category: String,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    // Application States
    val notes = remember {
        mutableStateListOf(
            Note(
                title = "Welcome to SafePad 📝",
                content = "This is a secure notepad app. Keep your personal thoughts and credentials safe under PIN protection! Use custom categories to organize everything.",
                category = "General"
            ),
            Note(
                title = "Shopping List 🛒",
                content = "- Fresh milk\n- Whole wheat bread\n- Organic avocados\n- Dark chocolate 85%",
                category = "Personal"
            ),
            Note(
                title = "App Launch Tasks 🚀",
                content = "1. Finish jetpack compose UI\n2. Integrate biometric lock simulator\n3. Package and release update",
                category = "Work"
            ),
            Note(
                title = "Investment Ideas 💡",
                content = "Look into green hydrogen startups and automation systems. Diversify index funds further next month.",
                category = "Finance"
            )
        )
    }

    val categories = remember {
        mutableStateListOf("All", "General", "Personal", "Work", "Finance", "Secret")
    }

    // Security states
    var isLocked by remember { mutableStateOf(true) }
    var securityPin by remember { mutableStateOf("1234") } // Default starting PIN
    var isSettingUpPin by remember { mutableStateOf(false) }

    // Navigation and UI States
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var noteToEdit by remember { mutableStateOf<Note?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showPinChangeDialog by remember { mutableStateOf(false) }
    var showCreateNoteScreen by remember { mutableStateOf(false) }

    // Filter logic
    val filteredNotes = notes.filter { note ->
        val matchesCategory = (selectedCategory == "All" || note.category == selectedCategory)
        val matchesSearch = note.title.contains(searchQuery, ignoreCase = true) || 
                            note.content.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }.sortedByDescending { it.timestamp }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLocked) {
            // Screen Overlay for security lock code entry
            LockScreen(
                correctPin = securityPin,
                onUnlockSuccess = { isLocked = false },
                onResetSetup = {
                    isSettingUpPin = true
                }
            )
        } else {
            // Main App Workspace Content
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Safe Lock Icon",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "SafePad",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { showPinChangeDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Security Settings",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = { isLocked = true }) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Lock Device Now",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { showCreateNoteScreen = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add New Note")
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // Search Bar Section
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Search your locked notes...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    // Categories Dynamic Filter List
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(categories) { category ->
                                val isSelected = category == selectedCategory
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategory = category },
                                    label = { Text(category) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                        
                        // Add Category Shortcut
                        IconButton(
                            onClick = { showAddCategoryDialog = true },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = "Create Category",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Notes Core Listing
                    if (filteredNotes.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Empty safe illustration",
                                    modifier = Modifier.size(72.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (searchQuery.isNotEmpty()) "No matches found" else "This safe is empty",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "Tap + to add your secure secrets",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(filteredNotes, key = { it.id }) { note ->
                                NoteCard(
                                    note = note,
                                    onClick = { noteToEdit = note },
                                    onDelete = { notes.remove(note) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Custom Category Dialog
        if (showAddCategoryDialog) {
            var newCategoryName by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddCategoryDialog = false },
                title = { Text("Add New Category") },
                text = {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Category Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val sanitized = newCategoryName.trim()
                            if (sanitized.isNotEmpty() && !categories.contains(sanitized)) {
                                categories.add(sanitized)
                                selectedCategory = sanitized
                            }
                            showAddCategoryDialog = false
                            newCategoryName = ""
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddCategoryDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Pin Change Configuration Dialog
        if (showPinChangeDialog) {
            var inputOldPin by remember { mutableStateOf("") }
            var inputNewPin by remember { mutableStateOf("") }
            var validationError by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showPinChangeDialog = false },
                title = { Text("Change security lock PIN") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = inputOldPin,
                            onValueChange = { if (it.length <= 4) inputOldPin = it },
                            label = { Text("Current PIN") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = inputNewPin,
                            onValueChange = { if (it.length <= 4) inputNewPin = it },
                            label = { Text("New 4-Digit PIN") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (validationError.isNotEmpty()) {
                            Text(
                                text = validationError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (inputOldPin != securityPin) {
                                validationError = "Current PIN is incorrect"
                            } else if (inputNewPin.length != 4 || inputNewPin.any { !it.isDigit() }) {
                                validationError = "New PIN must be exactly 4 digits"
                            } else {
                                securityPin = inputNewPin
                                showPinChangeDialog = false
                            }
                        }
                    ) {
                        Text("Save PIN")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPinChangeDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Create / Write Note dialog or Full view overlay
        if (showCreateNoteScreen) {
            NoteEditorOverlay(
                initialNote = null,
                categoriesList = categories.filter { it != "All" },
                onSave = { title, content, cat ->
                    notes.add(Note(title = title, content = content, category = cat))
                    showCreateNoteScreen = false
                },
                onClose = { showCreateNoteScreen = false }
            )
        }

        // Edit Existing Note overlay
        if (noteToEdit != null) {
            NoteEditorOverlay(
                initialNote = noteToEdit,
                categoriesList = categories.filter { it != "All" },
                onSave = { title, content, cat ->
                    val index = notes.indexOfFirst { it.id == noteToEdit?.id }
                    if (index != -1) {
                        notes[index] = notes[index].copy(title = title, content = content, category = cat, timestamp = System.currentTimeMillis())
                    }
                    noteToEdit = null
                },
                onClose = { noteToEdit = null }
            )
        }
    }
}

// Security keypad and Lock screen protection overlay
@Composable
fun LockScreen(
    correctPin: String,
    onUnlockSuccess: () -> Unit,
    onResetSetup: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var isPinIncorrect by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F14))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "App Locked Secure",
            tint = if (isPinIncorrect) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "SafePad Encrypted Storage",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isPinIncorrect) "Incorrect PIN, try again!" else "Enter 4-Digit Security PIN to Unlock",
            color = if (isPinIncorrect) MaterialTheme.colorScheme.error else Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Dots Display indicator representing the password entry status
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 4) {
                val filled = i < enteredPin.length
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            if (filled) MaterialTheme.colorScheme.primary else Color.DarkGray
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Secure Keypad Layout
        val buttons = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("C", "0", "⌫")
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in buttons) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.wrapContentSize()
                ) {
                    for (digit in row) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E1E24))
                                .clickable {
                                    isPinIncorrect = false
                                    when (digit) {
                                        "⌫" -> {
                                            if (enteredPin.isNotEmpty()) {
                                                enteredPin = enteredPin.dropLast(1)
                                            }
                                        }
                                        "C" -> {
                                            enteredPin = ""
                                        }
                                        else -> {
                                            if (enteredPin.length < 4) {
                                                enteredPin += digit
                                                if (enteredPin.length == 4) {
                                                    if (enteredPin == correctPin) {
                                                        onUnlockSuccess()
                                                    } else {
                                                        isPinIncorrect = true
                                                        enteredPin = ""
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = digit,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (digit == "C" || digit == "⌫") MaterialTheme.colorScheme.secondary else Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Demo default PIN: 1234",
            color = Color.DarkGray,
            fontSize = 12.sp
        )
    }
}

// Gorgeous note representation card component
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val categoryColor = when (note.category) {
        "Personal" -> Color(0xFF81C784)
        "Work" -> Color(0xFF64B5F6)
        "Finance" -> Color(0xFFFFD54F)
        "Secret" -> Color(0xFFE57373)
        else -> Color(0xFFBA68C8)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Tag/Label with dynamic matching colors
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(categoryColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = note.category,
                        color = categoryColor,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    val dateFormatted = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(note.timestamp))
                    Text(
                        text = dateFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Note Button",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Note writing editor composition layout overlay
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorOverlay(
    initialNote: Note?,
    categoriesList: List<String>,
    onSave: (title: String, content: String, category: String) -> Unit,
    onClose: () -> Unit
) {
    var titleText by remember { mutableStateOf(initialNote?.title ?: "") }
    var contentText by remember { mutableStateOf(initialNote?.content ?: "") }
    var selectedCategory by remember { mutableStateOf(initialNote?.category ?: if (categoriesList.isNotEmpty()) categoriesList.first() else "General") }
    var expandedDropdown by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (initialNote == null) "New Safe Note" else "Edit Safe Note",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Title field input
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Secure Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selection dropdown select menu trigger
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        label = { Text("Category") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expandedDropdown = !expandedDropdown }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Trigger")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        categoriesList.forEach { categoryOption ->
                            DropdownMenuItem(
                                text = { Text(categoryOption) },
                                onClick = {
                                    selectedCategory = categoryOption
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                // Note description context area
                OutlinedTextField(
                    value = contentText,
                    onValueChange = { contentText = it },
                    label = { Text("Write your thoughts securely...") },
                    minLines = 6,
                    maxLines = 10,
                    modifier = Modifier.fillMaxWidth()
                )

                // Actions Layout triggers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onClose) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (titleText.isNotBlank() && contentText.isNotBlank()) {
                                onSave(titleText, contentText, selectedCategory)
                            }
                        },
                        enabled = titleText.isNotBlank() && contentText.isNotBlank()
                    ) {
                        Text("Save Secret")
                    }
                }
            }
        }
    }
}