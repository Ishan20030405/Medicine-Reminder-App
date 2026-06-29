package com.example.mad

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineListScreen(
    medicines: List<Medicine>,
    onBottomNavClick: (String) -> Unit,
    onDeleteMedicine: (Medicine) -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val bgColor = if (isSystemInDarkTheme()) colorScheme.background else Color(0xFFE1F5FE)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Medicine",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.surface)
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
                        isSelected = false,
                        onClick = { onBottomNavClick("home") }
                    )
                    BottomNavItem(
                        iconRes = R.drawable.medicine,
                        label = "Medicine",
                        isSelected = true,
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(bgColor)
                .padding(16.dp)
        ) {
            Text(
                text = "All Medicines",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.secondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(medicines) { medicine ->
                    SimpleMedicineItem(
                        medicine = medicine,
                        onDeleteClick = { onDeleteMedicine(medicine) }
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleMedicineItem(medicine: Medicine, onDeleteClick: () -> Unit = {}) {
    val colorScheme = MaterialTheme.colorScheme
    val buttonBlue = colorScheme.primary
    var showDelete by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { showDelete = !showDelete }
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
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
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicine.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${medicine.frequency} | ${medicine.quantity} ${medicine.unit}",
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }

            AnimatedVisibility(
                visible = showDelete,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}
