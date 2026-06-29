package com.example.mad

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mad.ui.theme.MediRemindTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    medicines: List<Medicine> = emptyList(),
    onAddClick: () -> Unit = {},
    onStatusUpdate: (Medicine, Boolean, Boolean) -> Unit = { _, _, _ -> },
    onBottomNavClick: (String) -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val bgColor = if (androidx.compose.foundation.isSystemInDarkTheme()) colorScheme.background else Color(0xFFE1F5FE)
    
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    
    var selectedMedicine by remember { mutableStateOf<Medicine?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Today",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    },
                    actions = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Image(
                                painter = painterResource(id = R.drawable.today),
                                contentDescription = "Calendar",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.surface
                    )
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BottomNavItem(
                            iconRes = R.drawable.today,
                            label = "Today",
                            isSelected = true,
                            onClick = { onBottomNavClick("home") }
                        )
                        BottomNavItem(
                            iconRes = R.drawable.medicine,
                            label = "Medicine",
                            isSelected = false,
                            onClick = { onBottomNavClick("medicine_list") }
                        )
                        BottomNavItem(
                            iconRes = R.drawable.setting,
                            label = "Settings",
                            isSelected = false,
                            onClick = { onBottomNavClick("settings") }
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = colorScheme.primaryContainer,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(bgColor)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(medicines) { medicine ->
                    MedicineItem(
                        medicine = medicine,
                        onClick = {
                            selectedMedicine = medicine
                            showSheet = true
                        }
                    )
                }
            }
        }

        // Update States Bottom Sheet
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = colorScheme.surface,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                UpdateStatusContent(
                    onTaken = { 
                        selectedMedicine?.let { onStatusUpdate(it, true, false) }
                        showSheet = false 
                    },
                    onSkip = { 
                        selectedMedicine?.let { onStatusUpdate(it, false, true) }
                        showSheet = false 
                    }
                )
            }
        }

        // System Date Picker
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun CalendarDropdown(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Previous Month */ }) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Prev")
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Sep", fontWeight = FontWeight.Medium)
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "2025", fontWeight = FontWeight.Medium)
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                }

                IconButton(onClick = { /* Next Month */ }) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Days Label
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
                    Text(text = day, fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Grid (Static representation for Sep 2025)
            val days = (1..30).toList()
            val startOffset = 1 // Monday start for Sep 2025 if 1st is Monday
            
            Column {
                (0..4).forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        (0..6).forEach { dayIndex ->
                            val dayNum = week * 7 + dayIndex - startOffset + 1
                            if (dayNum in 1..30) {
                                val isSelected = dayNum == 9 || dayNum == 13
                                val isToday = dayNum == 11
                                
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            color = if (isSelected) colorScheme.primary else if (isToday) colorScheme.primaryContainer else Color.Transparent,
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayNum.toString(),
                                        color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurface,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            } else {
                                Box(modifier = Modifier.size(36.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedicineItem(medicine: Medicine, onClick: () -> Unit = {}) {
    val colorScheme = MaterialTheme.colorScheme
    val buttonBlue = colorScheme.primary
    
    Column(modifier = Modifier.clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = buttonBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = medicine.time,
                color = buttonBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon or Taken/Skipped Status
                if (medicine.isTaken) {
                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF4CAF50) // Green
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Taken",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                } else if (medicine.isSkipped) {
                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Red // Red
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Skipped",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = IconMapper.getIconForName(medicine.typeIconName),
                            contentDescription = null,
                            tint = buttonBlue,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = medicine.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${medicine.frequency} | ${medicine.quantity} ${medicine.unit}",
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateStatusContent(onTaken: () -> Unit, onSkip: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Update States",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(thickness = 1.dp, color = colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatusActionItem(
                icon = Icons.Default.Check,
                label = "Taken",
                onClick = onTaken
            )
            StatusActionItem(
                icon = Icons.Default.Close,
                label = "Skip",
                onClick = onSkip
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StatusActionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = RoundedCornerShape(16.dp),
            color = colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(48.dp),
                    tint = colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 16.sp, color = colorScheme.onSurface)
    }
}

@Composable
fun BottomNavItem(iconRes: Int, label: String, isSelected: Boolean, onClick: () -> Unit = {}) {
    val colorScheme = MaterialTheme.colorScheme
    val opacity = if (isSelected) 1f else 0.6f
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
            .graphicsLayer(alpha = opacity)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MediRemindTheme {
        HomeScreen()
    }
}
