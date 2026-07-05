package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.BloodViewModel
import com.example.ui.components.PremiumButton
import com.example.ui.components.PremiumGlassCard
import com.example.ui.screens.*
import com.example.ui.theme.BloodConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BloodConnectTheme {
                val viewModel: BloodViewModel = viewModel()
                val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                val selectedScreen by viewModel.selectedScreen.collectAsState()

                if (!isLoggedIn) {
                    LoginScreen(viewModel = viewModel)
                } else {
                    MainAppScaffold(viewModel = viewModel, currentScreen = selectedScreen)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(viewModel: BloodViewModel, currentScreen: String) {
    val unreadCount by viewModel.unreadNotificationsCount.collectAsState()
    val notifications by viewModel.allNotifications.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    // Editable profile states
    var editPhone by remember { mutableStateOf("") }
    var editLocation by remember { mutableStateOf("") }
    var editWeight by remember { mutableStateOf("") }
    var editHeight by remember { mutableStateOf("") }
    var editDiseases by remember { mutableStateOf("") }
    var editAvailability by remember { mutableStateOf(true) }

    // Sync editable fields when profile is fetched
    LaunchedEffect(profile) {
        profile?.let {
            editPhone = it.phone
            editLocation = it.location
            editWeight = it.weight.toString()
            editHeight = it.height.toString()
            editDiseases = it.diseases
            editAvailability = it.isAvailable
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorite Heart Icon",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "BloodConnect",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    // Admin Icon
                    IconButton(onClick = { viewModel.navigateTo("admin") }) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin panel privilege settings",
                            tint = if (currentScreen == "admin") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Alerts Bell Icon
                    Box {
                        IconButton(onClick = { showNotificationsDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notification center alerts",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 4.dp, end = 4.dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$unreadCount",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Profile Icon
                    IconButton(onClick = { showProfileDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "View user profile properties",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = currentScreen == "dashboard",
                    onClick = { viewModel.navigateTo("dashboard") },
                    icon = { Icon(Icons.Default.Home, "Dashboard Home") },
                    label = { Text("Home", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == "search",
                    onClick = { viewModel.navigateTo("search") },
                    icon = { Icon(Icons.Default.LocationOn, "Find Donors") },
                    label = { Text("Search", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == "maps",
                    onClick = { viewModel.navigateTo("maps") },
                    icon = { Icon(Icons.Default.Map, "Interactive GPS radar map") },
                    label = { Text("Radar", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == "chat",
                    onClick = { viewModel.navigateTo("chat") },
                    icon = { Icon(Icons.Default.SupportAgent, "AI matching assistant chatbot") },
                    label = { Text("AI Chat", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == "health",
                    onClick = { viewModel.navigateTo("health") },
                    icon = { Icon(Icons.Default.Scale, "BMI Health indices") },
                    label = { Text("Health", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == "rewards",
                    onClick = { viewModel.navigateTo("rewards") },
                    icon = { Icon(Icons.Default.Stars, "Badges rewards points") },
                    label = { Text("Rewards", fontSize = 11.sp) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen switching animations
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "screen_trans"
            ) { screen ->
                when (screen) {
                    "dashboard" -> DashboardScreen(viewModel = viewModel)
                    "search" -> SearchScreen(viewModel = viewModel)
                    "maps" -> MapViewScreen(viewModel = viewModel)
                    "chat" -> ChatScreen(viewModel = viewModel)
                    "health" -> HealthScreen(viewModel = viewModel)
                    "rewards" -> RewardsScreen(viewModel = viewModel)
                    "admin" -> AdminScreen(viewModel = viewModel)
                    "request" -> RequestScreen(viewModel = viewModel)
                    else -> DashboardScreen(viewModel = viewModel)
                }
            }
        }

        // --- ACTIONABLE NOTIFICATIONS POPUP ---
        if (showNotificationsDialog) {
            AlertDialog(
                onDismissRequest = { showNotificationsDialog = false },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Clinical Alert Inbox", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
                            Text("Mark All Read", fontSize = 12.sp)
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (notifications.isEmpty()) {
                            Text(
                                "No active alerts. You will be notified instantly of emergency matching requirements nearby.",
                                color = Color.Gray,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            notifications.forEach { item ->
                                val typeColor = when (item.type) {
                                    "Emergency" -> Color(0xFFE53935)
                                    "Reward" -> Color(0xFF00897B)
                                    else -> Color(0xFF37474F)
                                }
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.markNotificationAsRead(item.id) },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (item.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(typeColor)
                                                .align(Alignment.CenterVertically)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                item.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = typeColor
                                            )
                                            Text(
                                                item.message,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        IconButton(
                                            onClick = { viewModel.deleteNotification(item.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Delete notification item",
                                                modifier = Modifier.size(14.dp),
                                                tint = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showNotificationsDialog = false }) {
                        Text("Close Alert Center")
                    }
                }
            )
        }

        // --- EDIT PROFILE SETTINGS DIALOG ---
        if (showProfileDialog) {
            AlertDialog(
                onDismissRequest = { showProfileDialog = false },
                title = { Text("Edit Donor Profile & Health Metrics", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Basic Info", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text("Contact Phone") },
                            leadingIcon = { Icon(Icons.Default.Phone, "Phone") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editLocation,
                            onValueChange = { editLocation = it },
                            label = { Text("Current Location Area") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, "Location") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Text("Medical Metrics", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = editWeight,
                                onValueChange = { editWeight = it },
                                label = { Text("Weight (kg)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = editHeight,
                                onValueChange = { editHeight = it },
                                label = { Text("Height (cm)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = editDiseases,
                            onValueChange = { editDiseases = it },
                            label = { Text("Chronic Conditions / Diseases") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Set Status Available to Donate", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Switch(checked = editAvailability, onCheckedChange = { editAvailability = it })
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    showProfileDialog = false
                                    viewModel.logout()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Icon(Icons.Default.Logout, "Log out icon", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Log out", fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    showProfileDialog = false
                                    viewModel.deleteAccount()
                                },
                                modifier = Modifier.weight(1.2f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                            ) {
                                Icon(Icons.Default.Delete, "Delete account icon", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete Account", fontSize = 12.sp, maxLines = 1)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showProfileDialog = false
                            profile?.let {
                                val updated = it.copy(
                                    phone = editPhone,
                                    location = editLocation,
                                    weight = editWeight.toDoubleOrNull() ?: it.weight,
                                    height = editHeight.toDoubleOrNull() ?: it.height,
                                    diseases = editDiseases,
                                    isAvailable = editAvailability
                                )
                                viewModel.updateProfile(updated)
                            }
                        }
                    ) {
                        Text("Save Changes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showProfileDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
