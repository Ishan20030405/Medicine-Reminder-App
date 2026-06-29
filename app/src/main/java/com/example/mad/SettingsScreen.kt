package com.example.mad

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBottomNavClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    currentTheme: String = "Light",
    onThemeChange: (String) -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    // Background color that adapts slightly to light/dark
    val bgColor = if (isSystemInDarkTheme()) colorScheme.background else Color(0xFFE1F5FE)
    
    var showThemeSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showThemeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showThemeSheet = false },
            sheetState = sheetState,
            containerColor = colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Select Theme",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = colorScheme.onSurface
                )
                ThemeOption("Light Mode", currentTheme == "Light") {
                    onThemeChange("Light")
                    showThemeSheet = false
                }
                ThemeOption("Dark Mode", currentTheme == "Dark") {
                    onThemeChange("Dark")
                    showThemeSheet = false
                }
                ThemeOption("System Mode", currentTheme == "System") {
                    onThemeChange("System")
                    showThemeSheet = false
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontSize = 28.sp,
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
                        isSelected = false,
                        onClick = { onBottomNavClick("medicine_list") }
                    )
                    BottomNavItem(
                        iconRes = R.drawable.setting,
                        label = "Settings",
                        isSelected = true,
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
            SettingsSection(title = "General") {
                SettingsItem(
                    icon = Icons.Outlined.Person, 
                    label = "Profile",
                    onClick = onProfileClick
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsItem(
                    icon = Icons.Outlined.Contrast,
                    label = "Theme",
                    onClick = { showThemeSheet = true }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SettingsSection(title = "Communicate") {
                SettingsItem(icon = Icons.Outlined.Feedback, label = "Feedback")
                Spacer(modifier = Modifier.height(12.dp))
                SettingsItem(icon = Icons.Outlined.PrivacyTip, label = "Privacy Policy")
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "App Version 1.0",
                color = colorScheme.onBackground.copy(alpha = 0.6f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
fun ThemeOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
fun SettingsItem(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )
        }
    }
}
