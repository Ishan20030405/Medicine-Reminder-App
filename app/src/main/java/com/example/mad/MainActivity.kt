package com.example.mad

import android.Manifest
import android.app.AlarmManager
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.mad.ui.theme.MediRemindTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private var alarmMedicineName by mutableStateOf<String?>(null)
    private var alarmMedicineDetails by mutableStateOf<String?>(null)
    private var alarmMedicineQuantity by mutableStateOf<String?>(null)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.getBooleanExtra("STOP_ALARM", false)) {
            AlarmSoundManager.stop()
            alarmMedicineName = null
        }
        
        val medName = intent.getStringExtra("MEDICINE_NAME")
        if (medName != null) {
            alarmMedicineName = medName
            alarmMedicineQuantity = intent.getStringExtra("MEDICINE_QUANTITY") ?: "1"
            alarmMedicineDetails = intent.getStringExtra("MEDICINE_DETAILS") ?: "Take your medicine"
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setShowWhenLocked(true)
                setTurnScreenOn(true)
                val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                keyguardManager.requestDismissKeyguard(this, null)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            var hasNotificationPermission by remember {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                } else {
                    mutableStateOf(true)
                }
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    hasNotificationPermission = isGranted
                }
            )

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                
                // Check and ask for Exact Alarm permission on Android 12+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    if (!alarmManager.canScheduleExactAlarms()) {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                }
            }

            var themeMode by remember { mutableStateOf("Light") }
            val darkTheme = when (themeMode) {
                "Light" -> false
                "Dark" -> true
                else -> isSystemInDarkTheme()
            }

            MediRemindTheme(darkTheme = darkTheme) {
                val auth = FirebaseAuth.getInstance()
                val database = FirebaseDatabase.getInstance("https://medireminder-67c71-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
                var currentUser by remember { mutableStateOf(auth.currentUser) }
                var currentScreen by remember { mutableStateOf(if (currentUser != null) "home" else "login") }
                val medicineList = remember { mutableStateListOf<Medicine>() }

                // Listen for Auth State Changes
                DisposableEffect(auth) {
                    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                        currentUser = firebaseAuth.currentUser
                    }
                    auth.addAuthStateListener(listener)
                    onDispose {
                        auth.removeAuthStateListener(listener)
                    }
                }

                LaunchedEffect(currentUser) {
                    val userId = currentUser?.uid
                    if (userId != null) {
                        val medicinesRef = database.child("Users").child(userId).child("Medicines")
                        medicinesRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                medicineList.clear()
                                for (medSnapshot in snapshot.children) {
                                    val medicine = medSnapshot.getValue(Medicine::class.java)
                                    if (medicine != null) {
                                        medicineList.add(medicine)
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    } else {
                        medicineList.clear()
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        when (currentScreen) {
                            "login" -> {
                                LoginScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    onCreateAccountClick = { currentScreen = "signup" },
                                    onLoginClick = { currentScreen = "home" }
                                )
                            }
                            "signup" -> {
                                SignupScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    onLoginClick = { currentScreen = "login" },
                                    onSignupSuccess = { currentScreen = "home" }
                                )
                            }
                            "home" -> {
                                HomeScreen(
                                    medicines = medicineList,
                                    onAddClick = { currentScreen = "add_medicine" },
                                    onStatusUpdate = { medicine: Medicine, taken: Boolean, skipped: Boolean ->
                                        val userId = currentUser?.uid
                                        if (userId != null && medicine.id.isNotEmpty()) {
                                            val updatedMed = medicine.copy(isTaken = taken, isSkipped = skipped)
                                            database.child("Users").child(userId).child("Medicines")
                                                .child(medicine.id).setValue(updatedMed)
                                        }
                                        AlarmSoundManager.stop()
                                    },
                                    onBottomNavClick = { currentScreen = it }
                                )
                            }
                            "medicine_list" -> {
                                MedicineListScreen(
                                    medicines = medicineList,
                                    onBottomNavClick = { currentScreen = it },
                                    onDeleteMedicine = { medicine ->
                                        val userId = currentUser?.uid
                                        if (userId != null && medicine.id.isNotEmpty()) {
                                            database.child("Users").child(userId).child("Medicines")
                                                .child(medicine.id).removeValue()
                                        }
                                    }
                                )
                            }
                            "settings" -> {
                                SettingsScreen(
                                    onBottomNavClick = { currentScreen = it },
                                    onProfileClick = { currentScreen = "profile" },
                                    currentTheme = themeMode,
                                    onThemeChange = { themeMode = it }
                                )
                            }
                            "profile" -> {
                                ProfileScreen(
                                    onBack = { currentScreen = "settings" },
                                    onSignOut = {
                                        auth.signOut()
                                        medicineList.clear()
                                        currentScreen = "login"
                                    }
                                )
                            }
                            "add_medicine" -> {
                                val context = LocalContext.current
                                AddMedicineScreen(
                                    onBack = { currentScreen = "home" },
                                    onDone = { newMedicine ->
                                        val userId = currentUser?.uid
                                        if (userId != null) {
                                            val medRef = database.child("Users").child(userId).child("Medicines").push()
                                            val medWithId = newMedicine.copy(id = medRef.key ?: "")
                                            medRef.setValue(medWithId)
                                            AlarmScheduler.scheduleAlarm(context, medWithId)
                                        }
                                        currentScreen = "home"
                                    }
                                )
                            }
                        }
                    }

                    // Alarm Popup Overlay
                    alarmMedicineName?.let { name ->
                        AlarmScreen(
                            name = name,
                            details = alarmMedicineDetails ?: "",
                            quantity = alarmMedicineQuantity ?: "1",
                            onTakeNow = {
                                AlarmSoundManager.stop()
                                alarmMedicineName = null
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                                    setShowWhenLocked(false)
                                }
                            },
                            onSkipped = {
                                AlarmSoundManager.stop()
                                alarmMedicineName = null
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                                    setShowWhenLocked(false)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmScreen(
    name: String,
    details: String,
    quantity: String,
    onTakeNow: () -> Unit,
    onSkipped: () -> Unit
) {
    val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FE) 
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(text = name, fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = details, fontSize = 18.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(60.dp))

            Box(contentAlignment = Alignment.Center) {
                Surface(modifier = Modifier.size(180.dp), shape = CircleShape, color = Color(0xFFE0E0E0)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(90.dp))
                    }
                }
                Surface(
                    modifier = Modifier.size(44.dp).align(Alignment.TopEnd).offset(x = (-4).dp, y = 4.dp),
                    shape = CircleShape,
                    color = Color(0xFF4285F4),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Alarm, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(text = currentTime, fontSize = 54.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5F6368))
            Spacer(modifier = Modifier.height(40.dp))
            Text(text = "Take $quantity pill", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onTakeNow,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
            ) {
                Text("Take now", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSkipped,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F3F4))
            ) {
                Text("Skipped", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5F6368))
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreviewMain() {
    MediRemindTheme {
        LoginScreen()
    }
}
