package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BloodViewModel
import com.example.ui.components.PremiumButton
import com.example.ui.components.PremiumGlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: BloodViewModel) {
    var isSignUpTab by remember { mutableStateOf(false) }
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPhone by remember { mutableStateOf("") }
    var useOtpMode by remember { mutableStateOf(false) }
    var otpSent by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }

    // Biometric Modal Dialog
    var showBiometricModal by remember { mutableStateOf(false) }

    // Signup form states
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regBloodType by remember { mutableStateOf("O+") }
    var regLocation by remember { mutableStateOf("") }
    var regAge by remember { mutableStateOf("") }
    var regGender by remember { mutableStateOf("Male") }

    val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    val genders = listOf("Male", "Female", "Other")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "BloodConnect Logo Icon",
                    tint = Color.White,
                    modifier = Modifier.size(45.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "BloodConnect",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Every Drop Counts • Saving Lives Together",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Premium Tab Switcher
            TabRow(
                selectedTabIndex = if (isSignUpTab) 1 else 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                indicator = { Box(Modifier.fillMaxSize()) } // Overridden below
            ) {
                Tab(
                    selected = !isSignUpTab,
                    onClick = { isSignUpTab = false },
                    modifier = Modifier
                        .height(45.dp)
                        .background(if (!isSignUpTab) MaterialTheme.colorScheme.primary else Color.Transparent)
                ) {
                    Text(
                        "Sign In",
                        color = if (!isSignUpTab) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
                Tab(
                    selected = isSignUpTab,
                    onClick = { isSignUpTab = true },
                    modifier = Modifier
                        .height(45.dp)
                        .background(if (isSignUpTab) MaterialTheme.colorScheme.primary else Color.Transparent)
                ) {
                    Text(
                        "Register",
                        color = if (isSignUpTab) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
                if (!isSignUpTab) {
                    // LOGIN INTERFACE
                    if (!useOtpMode) {
                        // Email Sign-in
                        OutlinedTextField(
                            value = loginEmail,
                            onValueChange = { loginEmail = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Default.Email, "Email Icon") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, "Lock Icon") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        PremiumButton(
                            text = "Login with Email",
                            onClick = {
                                viewModel.login(
                                    email = loginEmail.ifEmpty { "ronymia2021@gmail.com" },
                                    password = loginPassword.ifEmpty { "password123" },
                                    phone = "",
                                    method = "Email Credentials"
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "email_login_btn"
                        )
                    } else {
                        // Phone OTP Sign-in
                        OutlinedTextField(
                            value = loginPhone,
                            onValueChange = { loginPhone = it },
                            label = { Text("Phone Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, "Phone Icon") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !otpSent
                        )
                        AnimatedVisibility(visible = otpSent) {
                            Column {
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = otpCode,
                                    onValueChange = { otpCode = it },
                                    label = { Text("6-Digit OTP Code") },
                                    leadingIcon = { Icon(Icons.Default.Pin, "OTP Icon") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        PremiumButton(
                            text = if (!otpSent) "Send OTP" else "Verify & Login",
                            onClick = {
                                if (!otpSent) {
                                    otpSent = true
                                } else {
                                    viewModel.login(
                                        email = "ronymia2021@gmail.com",
                                        password = "",
                                        phone = loginPhone.ifEmpty { "+880 1712-345678" },
                                        method = "Phone OTP verification"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Modes Toggle / Biometric login
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { useOtpMode = !useOtpMode }) {
                            Text(if (useOtpMode) "Use Email Login" else "Use Phone OTP")
                        }
                        IconButton(onClick = { showBiometricModal = true }) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometric Auth Fingerprint Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Demo Bypass: Feel free to leave fields empty and click login to instantly use full app features",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                } else {
                    // REGISTER INTERFACE
                    OutlinedTextField(
                        value = regName,
                        onValueChange = { regName = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, "User Icon") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, "Email Icon") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = { regPassword = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, "Lock Icon") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = regPhone,
                        onValueChange = { regPhone = it },
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, "Phone Icon") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Location & Age Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = regLocation,
                            onValueChange = { regLocation = it },
                            label = { Text("Location") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, "Location Icon") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = regAge,
                            onValueChange = { regAge = it },
                            label = { Text("Age") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.6f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Blood Type & Gender selectors
                    Text(
                        "Blood Group Selection",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        bloodTypes.take(4).forEach { type ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (regBloodType == type) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { regBloodType = type }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    type,
                                    color = if (regBloodType == type) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        bloodTypes.drop(4).forEach { type ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (regBloodType == type) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { regBloodType = type }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    type,
                                    color = if (regBloodType == type) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    PremiumButton(
                        text = "Complete Registration",
                        onClick = {
                            viewModel.register(
                                name = regName.ifEmpty { "Rony Mia" },
                                email = regEmail.ifEmpty { "ronymia2021@gmail.com" },
                                password = regPassword.ifEmpty { "password123" },
                                phone = regPhone.ifEmpty { "+880 1712-345678" },
                                bloodType = regBloodType,
                                location = regLocation.ifEmpty { "Mirpur, Dhaka" },
                                age = regAge.toIntOrNull() ?: 24,
                                gender = regGender
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // BIOMETRIC OVERLAY DIALOG MOCK
        if (showBiometricModal) {
            AlertDialog(
                onDismissRequest = { showBiometricModal = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Fingerprint scanning animation indicator",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(50.dp)
                    )
                },
                title = { Text("Biometric Sign In") },
                text = {
                    Text(
                        "Touch the fingerprint sensor or look at the front camera to authenticating with Face Unlock.",
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showBiometricModal = false
                            viewModel.login(
                                email = "ronymia2021@gmail.com",
                                password = "",
                                phone = "",
                                method = "Biometric validation (Fingerprint/Face)"
                            )
                        }
                    ) {
                        Text("Simulate Auth Success")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBiometricModal = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
