package com.example.droidcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
                    primary = Color(0xFF80DEEA),
                    onPrimary = Color(0xFF00363A),
                    primaryContainer = Color(0xFF004D40),
                    secondary = Color(0xFFFFB74D),
                    background = Color(0xFF121212),
                    surface = Color(0xFF1E1E1E),
                    onBackground = Color(0xFFE0E0E0),
                    onSurface = Color(0xFFE0E0E0)
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

// Data Classes
data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val color: Color = Color(0xFF2D2D2D)
)

// Main State & Navigation Controller
@Composable
fun MainAppScreen() {
    // Note Management
    var notesList by rememberSaveable {
        mutableStateOf(
            listOf(
                Note(
                    title = "Meeting Brainstorming",
                    content = "1. Design new interactive dashboard\n2. Setup automated security scanning\n3. Coordinate launch date with product team.",
                    category = "Work",
                    color = Color(0xFF2C3E50),
                    isPinned = true
                ),
                Note(
                    title = "Grocery List",
                    content = "Organic milk, avocados, whole wheat bread, cold brew coffee, almonds.",
                    category = "Personal",
                    color = Color(0xFF1E3D59)
                ),
                Note(
                    title = "App Architecture Idea",
                    content = "Use clean architecture with single MVI container pattern. Use ProtoDataStore for local cache.",
                    category = "Ideas",
                    color = Color(0xFF1A3636)
                ),
                Note(
                    title = "Monthly Budget Goals",
                    content = "Track discretionary spending. Put 30% into savings. Cut down on daily coffee runs.",
                    category = "Finance",
                    color = Color(0xFF4A2E35)
                )
            )
        )
    }

    // Security Settings
    var appPin by rememberSaveable { mutableStateOf("1234") }
    var isPinEnabled by rememberSaveable { mutableStateOf(true) }
    var isUnlocked by remember { mutableStateOf(!isPinEnabled) }

    // Screen States
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var currentEditingNote by remember { mutableStateOf<Note?>(null) }
    var isCreatingNewNote by remember { mutableStateOf(false) }

    // Categories available
    val categories = listOf("All", "Work", "Personal", "Ideas", "Finance", "Urgent")

    // Force unlock view if PIN is enabled and app is locked
    if (isPinEnabled && !isUnlocked) {
        LockScreen(
            correctPin = appPin,
            onUnlockSuccess = { isUnlocked = true }
        )
    } else {
        // Main Notepad Dashboard
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Security Status",
                                tint = if (isPinEnabled) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier.size(20.dp)
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
                        // Quick Lock Button (only if PIN is enabled)
                        if (isPinEnabled) {
                            IconButton(onClick = { isUnlocked = false }) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Lock App",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        IconButton(onClick = { showSettingsDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Security Settings"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { isCreatingNewNote = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Search Bar Row
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search notes...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )

                // Category Filter Slider
                CategoryTabsRow(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )

                // Notes Grid View
                val filteredNotes = notesList.filter { note ->
                    val matchesCategory = (selectedCategory == "All" || note.category == selectedCategory)
                    val matchesSearch = note.title.contains(searchQuery, ignoreCase = true) ||
                            note.content.contains(searchQuery, ignoreCase = true)
                    matchesCategory && matchesSearch
                }.sortedWith(compareByDescending<Note> { it.isPinned }.thenByDescending { it.timestamp })

                if (filteredNotes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "No Notes",
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No notes found",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                } else {
                    NotesGrid(
                        notes = filteredNotes,
                        onNoteClick = { currentEditingNote = it },
                        onDeleteClick = { noteToDelete ->
                            notesList = notesList.filter { it.id != noteToDelete.id }
                        },
                        onPinClick = { noteToPin ->
                            notesList = notesList.map {
                                if (it.id == noteToPin.id) it.copy(isPinned = !it.isPinned) else it
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    )
                }
            }
        }

        // Add Note Dialog/Overlay Screen
        if (isCreatingNewNote) {
            AddEditNoteDialog(
                categories = categories.filter { it != "All" },
                onDismiss = { isCreatingNewNote = false },
                onSave = { title, content, category, color ->
                    val newNote = Note(
                        title = title.ifBlank { "Untitled Note" },
                        content = content,
                        category = category,
                        color = color
                    )
                    notesList = notesList + newNote
                    isCreatingNewNote = false
                }
            )
        }

        // Edit Note Dialog/Overlay Screen
        currentEditingNote?.let { note ->
            AddEditNoteDialog(
                noteToEdit = note,
                categories = categories.filter { it != "All" },
                onDismiss = { currentEditingNote = null },
                onSave = { title, content, category, color ->
                    notesList = notesList.map {
                        if (it.id == note.id) {
                            it.copy(
                                title = title.ifBlank { "Untitled Note" },
                                content = content,
                                category = category,
                                color = color,
                                timestamp = System.currentTimeMillis()
                            )
                        } else it
                    }
                    currentEditingNote = null
                }
            )
        }

        // Settings Dialog
        if (showSettingsDialog) {
            SettingsDialog(
                isPinEnabled = isPinEnabled,
                currentPin = appPin,
                onDismiss = { showSettingsDialog = false },
                onToggleLock = { isPinEnabled = it },
                onUpdatePin = { newPin -> appPin = newPin }
            )
        }
    }
}

// Category Slider Component
@Composable
fun CategoryTabsRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(containerColor)
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category,
                    color = contentColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Grid of Note Cards
@Composable
fun NotesGrid(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onDeleteClick: (Note) -> Unit,
    onPinClick: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(horizontal = 12.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(notes, key = { it.id }) { note ->
            NoteCard(
                note = note,
                onClick = { onNoteClick(note) },
                onDelete = { onDeleteClick(note) },
                onPinToggle = { onPinClick(note) }
            )
        }
    }
}

// Individual Note Card Design
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onPinToggle: () -> Unit
) {
    val formattedDate = remember(note.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        sdf.format(Date(note.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onPinToggle
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = note.color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.3f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = note.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row {
                        if (note.isPinned) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Pinned Note",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onPinToggle() }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Note",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onDelete() }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = note.content,
                    fontSize = 13.sp,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
            }
            Text(
                text = formattedDate,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

// Add/Edit Note Modal
@Composable
fun AddEditNoteDialog(
    noteToEdit: Note? = null,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, category: String, color: Color) -> Unit
) {
    var title by remember { mutableStateOf(noteToEdit?.title ?: "") }
    var content by remember { mutableStateOf(noteToEdit?.content ?: "") }
    var selectedCategory by remember { mutableStateOf(noteToEdit?.category ?: categories.first()) }

    // Palette colors for notes
    val colors = listOf(
        Color(0xFF2C3E50), // Navy
        Color(0xFF1E3D59), // Steel Blue
        Color(0xFF1A3636), // Deep Pine
        Color(0xFF4A2E35), // Dark Rose
        Color(0xFF3B1E40), // Dark Purple
        Color(0xFF3E2723)  // Dark Espresso
    )
    var selectedCardColor by remember { mutableStateOf(noteToEdit?.color ?: colors.first()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (noteToEdit == null) "Create Note" else "Edit Note",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Category selection dropdown-like list
                Column {
                    Text("Select Category", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(categories) { category ->
                            val isSelected = category == selectedCategory
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = 2.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .clickable { selectedCategory = category }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    category,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Color Picker Row
                Column {
                    Text("Choose Card Color", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        colors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (selectedCardColor == color) 3.dp else 0.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedCardColor = color }
                            )
                        }
                    }
                }

                // Content Input
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Write something...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp, max = 240.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                // Dialog Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(title, content, selectedCategory, selectedCardColor) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Save", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

// Settings Screen Dialog (PIN Management)
@Composable
fun SettingsDialog(
    isPinEnabled: Boolean,
    currentPin: String,
    onDismiss: () -> Unit,
    onToggleLock: (Boolean) -> Unit,
    onUpdatePin: (String) -> Unit
) {
    var newPinInput by remember { mutableStateOf("") }
    var changePinError by remember { mutableStateOf("") }
    var toggleEnabledState by remember { mutableStateOf(isPinEnabled) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Security Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Toggle Security Option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("PIN Code Protection", fontWeight = FontWeight.Bold)
                        Text(
                            "Require PIN on launch",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = toggleEnabledState,
                        onCheckedChange = {
                            toggleEnabledState = it
                            onToggleLock(it)
                        }
                    )
                }

                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))

                // Configure PIN view
                if (toggleEnabledState) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Modify PIN", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Enter a new 4-digit security PIN:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )

                        OutlinedTextField(
                            value = newPinInput,
                            onValueChange = { input ->
                                if (input.length <= 4 && input.all { it.isDigit() }) {
                                    newPinInput = input
                                    changePinError = ""
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("New PIN (e.g. 1234)") }
                        )

                        if (changePinError.isNotEmpty()) {
                            Text(changePinError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }

                        Button(
                            onClick = {
                                if (newPinInput.length == 4) {
                                    onUpdatePin(newPinInput)
                                    newPinInput = ""
                                    onDismiss()
                                } else {
                                    changePinError = "PIN must be exactly 4 digits long!"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Update PIN")
                        }
                    }
                }

                // Close Button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

// Security Pad Lock Screen Interface
@Composable
fun LockScreen(
    correctPin: String,
    onUnlockSuccess: () -> Unit
) {
    var enteredDigits by remember { mutableStateOf("") }
    var isErrorState by remember { mutableStateOf(false) }

    // Intercept Back Press during lock screen
    BackHandler {
        // Prevent back press to bypass security screen
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Screen Lock Header Icon & Title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "App Locked",
                    tint = if (isErrorState) Color.Red else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "SafePad Secure Lock",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isErrorState) "Incorrect PIN, please try again." else "Enter PIN to access your notes (Default: 1234)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isErrorState) Color.Red else Color.LightGray,
                    textAlign = TextAlign.Center
                )
            }

            // PIN Indicator Code Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 32.dp)
            ) {
                for (i in 0 until 4) {
                    val isActive = i < enteredDigits.length
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) MaterialTheme.colorScheme.primary
                                else Color.White.copy(alpha = 0.2f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isErrorState) Color.Red else Color.Transparent,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Lock Screen Keyboard Layout (Grid of Numbers)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("Clear", "0", "Del")
                )

                for (row in keys) {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (key in row) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .clickable {
                                        isErrorState = false
                                        when (key) {
                                            "Clear" -> enteredDigits = ""
                                            "Del" -> if (enteredDigits.isNotEmpty()) {
                                                enteredDigits = enteredDigits.dropLast(1)
                                            }
                                            else -> {
                                                if (enteredDigits.length < 4) {
                                                    enteredDigits += key
                                                    // Trigger validation check once PIN reaches correct length limit
                                                    if (enteredDigits.length == 4) {
                                                        if (enteredDigits == correctPin) {
                                                            onUnlockSuccess()
                                                        } else {
                                                            isErrorState = true
                                                            enteredDigits = ""
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                when (key) {
                                    "Del" -> Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Backspace",
                                        tint = Color.White
                                    )
                                    "Clear" -> Text(
                                        text = "C",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    else -> Text(
                                        text = key,
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}