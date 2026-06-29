package com.example.mad

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mad.ui.theme.MediRemindTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(onBack: () -> Unit = {}, onDone: (Medicine) -> Unit = {}) {
    val colorScheme = MaterialTheme.colorScheme
    val bgColor = if (isSystemInDarkTheme()) colorScheme.background else Color(0xFFE1F5FE)
    val inputBg = if (isSystemInDarkTheme()) colorScheme.surfaceVariant else Color(0xFFD1E9F6)
    val buttonBlue = colorScheme.primary

    var medicineName by remember { mutableStateOf("") }
    var pillsQty by remember { mutableStateOf("01") }
    var selectedType by remember { mutableStateOf("Tablet") }
    var selectedUnit by remember { mutableStateOf("Tablet") }
    var expanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }
    var frequencyExpanded by remember { mutableStateOf(false) }
    var selectedFrequency by remember { mutableStateOf("1 time,Daily") }
    
    val medicineTypes = listOf(
        "Tablet", "Capsule", "Spray", "Gel", "Cream", "Injection",
        "Powder", "Inhaler", "Gummy", "Herb", "Ampoule", "Softgel",
        "Chewy bite", "Drops", "Lotion", "Liquid"
    )
    val dosageUnits = listOf(
        "ML", "mg", "g", "Drop", "Tablet", "Teaspoon", "Tablespoon", "Spray", "LB", "Ounce", "Gummy"
    )

    var showStartDatePicker by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf("Start Date") }
    val startDatePickerState = rememberDatePickerState()

    var showEndDatePicker by remember { mutableStateOf(false) }
    var endDate by remember { mutableStateOf("End Date") }
    val endDatePickerState = rememberDatePickerState()

    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf("08:20 PM") }
    val timePickerState = rememberTimePickerState(initialHour = 20, initialMinute = 20)

    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    // Map types to icons (using standard available icons)
    val typeIcons = mapOf(
        "Tablet" to Icons.Default.Circle,
        "Capsule" to Icons.Default.Circle,
        "Spray" to Icons.Default.Cyclone,
        "Gel" to Icons.Default.Opacity,
        "Cream" to Icons.Default.Opacity,
        "Injection" to Icons.Default.Edit,
        "Powder" to Icons.Default.Grain,
        "Inhaler" to Icons.Default.Air,
        "Gummy" to Icons.Default.Favorite,
        "Herb" to Icons.Default.Spa,
        "Ampoule" to Icons.Default.Info,
        "Softgel" to Icons.Default.Lens,
        "Chewy bite" to Icons.Default.Restaurant,
        "Drops" to Icons.Default.WaterDrop,
        "Lotion" to Icons.Default.CleanHands,
        "Liquid" to Icons.Default.LocalPharmacy
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Medicine",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(bgColor)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Medicine Name Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(0.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Medicine Name", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = medicineName,
                        onValueChange = { medicineName = it },
                        placeholder = { Text(text = "Medicine name", color = colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = inputBg,
                            unfocusedContainerColor = inputBg,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface
                        ),
                        singleLine = true
                    )
                }
            }

            // Type Dropdown (Tablet)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = typeIcons[selectedType] ?: Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = buttonBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = selectedType, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colorScheme.onSurface)
                        }
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                }

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(colorScheme.surface)
                ) {
                    medicineTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { 
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = typeIcons[type] ?: Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        tint = colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = type, color = colorScheme.onSurface) 
                                }
                            },
                            onClick = {
                                selectedType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Start/End Dates
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showStartDatePicker = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = startDate,
                            fontWeight = FontWeight.Medium,
                            color = if (startDate == "Start Date") colorScheme.onSurface else buttonBlue
                        )
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = colorScheme.onSurface)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = colorScheme.outlineVariant)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showEndDatePicker = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = endDate,
                            fontWeight = FontWeight.Medium,
                            color = if (endDate == "End Date") colorScheme.onSurface else buttonBlue
                        )
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = colorScheme.onSurface)
                    }
                }
            }

            if (showStartDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showStartDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            startDatePickerState.selectedDateMillis?.let {
                                startDate = dateFormatter.format(Date(it))
                            }
                            showStartDatePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = startDatePickerState)
                }
            }

            if (showEndDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showEndDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            endDatePickerState.selectedDateMillis?.let {
                                endDate = dateFormatter.format(Date(it))
                            }
                            showEndDatePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = endDatePickerState)
                }
            }

            // Pills Qty and Does
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Pills Qty   ", fontWeight = FontWeight.Medium, color = colorScheme.onSurface)
                        BasicTextField(
                            value = pillsQty,
                            onValueChange = { if (it.length <= 3) pillsQty = it },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = colorScheme.onSurfaceVariant,
                                fontSize = 16.sp
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.width(IntrinsicSize.Min)
                        )
                    }
                }
                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = !unitExpanded },
                    modifier = Modifier.weight(1.2f)
                ) {
                    Card(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Does           ", fontWeight = FontWeight.Medium, color = colorScheme.onSurface)
                            Text(text = selectedUnit, color = colorScheme.onSurfaceVariant)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = colorScheme.onSurface)
                        }
                    }

                    ExposedDropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false },
                        modifier = Modifier.background(colorScheme.surface)
                    ) {
                        dosageUnits.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(text = unit, color = colorScheme.onSurface) },
                                onClick = {
                                    selectedUnit = unit
                                    unitExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Frequency
            ExposedDropdownMenuBox(
                expanded = frequencyExpanded,
                onExpandedChange = { frequencyExpanded = !frequencyExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Set Frequency", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colorScheme.onSurface)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = selectedFrequency, color = colorScheme.onSurfaceVariant, fontSize = 14.sp)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = colorScheme.onSurface)
                        }
                    }
                }

                ExposedDropdownMenu(
                    expanded = frequencyExpanded,
                    onDismissRequest = { frequencyExpanded = false },
                    modifier = Modifier.background(colorScheme.surface)
                ) {
                    val frequencies = listOf(
                        "1 time,Daily", "2 time,Daily", "3 time,Daily",
                        "Every X Days", "Every X Weeks", "Every X Months"
                    )
                    frequencies.forEach { freq ->
                        DropdownMenuItem(
                            text = { Text(text = freq, color = colorScheme.onSurface) },
                            onClick = {
                                selectedFrequency = freq
                                frequencyExpanded = false
                            }
                        )
                    }
                }
            }

            // Schedule Time
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker = true },
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Schedule Time", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colorScheme.onSurface)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = selectedTime, color = colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = colorScheme.onSurface)
                    }
                }
            }

            if (showTimePicker) {
                TimePickerDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val cal = Calendar.getInstance()
                            cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            cal.set(Calendar.MINUTE, timePickerState.minute)
                            val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                            selectedTime = timeFormatter.format(cal.time)
                            showTimePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                    }
                ) {
                    TimePicker(state = timePickerState)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val newMedicine = Medicine(
                        name = medicineName,
                        quantity = pillsQty,
                        type = selectedType,
                        unit = selectedUnit,
                        frequency = selectedFrequency,
                        time = selectedTime,
                        startDate = startDate,
                        endDate = endDate,
                        typeIconName = selectedType
                    )
                    onDone(newMedicine)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonBlue)
            ) {
                Text(text = "Done", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = { content() }
    )
}

@Preview(showBackground = true)
@Composable
fun AddMedicineScreenPreview() {
    MediRemindTheme {
        AddMedicineScreen()
    }
}
