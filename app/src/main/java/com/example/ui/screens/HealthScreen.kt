package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BloodViewModel
import com.example.ui.components.PremiumButton
import com.example.ui.components.PremiumGlassCard
import com.example.ui.components.SectionHeading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen(viewModel: BloodViewModel) {
    val scrollState = rememberScrollState()

    // BMI States
    var weightInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    val bmiResult by viewModel.currentBmi.collectAsState()
    val bmiCategory by viewModel.bmiResultText.collectAsState()

    // Eligibility Checker Questionnaire States
    var userAge by remember { mutableStateOf("") }
    var userWeight by remember { mutableStateOf("") }
    var bpSystolic by remember { mutableStateOf("120") }
    var pulseRate by remember { mutableStateOf("72") }
    var isHealthyChecked by remember { mutableStateOf(true) }
    var hasTattooLast6Months by remember { mutableStateOf(false) }
    var onHeavyMedication by remember { mutableStateOf(false) }

    val eligibilityCheckResult by viewModel.isEligibleToDonate.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(bottom = 90.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeading(title = "Health Diagnostics & Eligibility")

        // --- SECTION 1: ELIGIBILITY CHECKER ---
        PremiumGlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MedicalServices, "Eligibility Logo", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clinical Donation Eligibility", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (eligibilityCheckResult == null) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Answer these quick clinical safety parameters to check your current eligibility to donate blood safely.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = userAge,
                            onValueChange = { userAge = it },
                            label = { Text("Your Age") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = userWeight,
                            onValueChange = { userWeight = it },
                            label = { Text("Weight (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = bpSystolic,
                            onValueChange = { bpSystolic = it },
                            label = { Text("Systolic BP (mmHg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = pulseRate,
                            onValueChange = { pulseRate = it },
                            label = { Text("Pulse (bpm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Checklist Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("I feel generally fit, healthy, and fully rested today", fontSize = 12.sp)
                        Checkbox(checked = isHealthyChecked, onCheckedChange = { isHealthyChecked = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Got a tattoo or body piercing in last 6 months?", fontSize = 12.sp)
                        Checkbox(checked = hasTattooLast6Months, onCheckedChange = { hasTattooLast6Months = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Am on antibiotics or active heavy medication?", fontSize = 12.sp)
                        Checkbox(checked = onHeavyMedication, onCheckedChange = { onHeavyMedication = it })
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    PremiumButton(
                        text = "Verify Medical Eligibility",
                        onClick = {
                            val ageVal = userAge.toIntOrNull() ?: 24
                            val weightVal = userWeight.toDoubleOrNull() ?: 70.0
                            val bpVal = bpSystolic.toIntOrNull() ?: 120
                            val pulseVal = pulseRate.toIntOrNull() ?: 72
                            
                            val combinedHealthStatus = isHealthyChecked && !hasTattooLast6Months && !onHeavyMedication
                            viewModel.checkEligibility(
                                age = ageVal,
                                weight = weightVal,
                                pulse = pulseVal,
                                bpSystolic = bpVal,
                                isHealthy = combinedHealthStatus
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                // ELIGIBILITY RESULT SCREEN
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val statusColor = if (eligibilityCheckResult == true) Color(0xFF00897B) else Color(0xFFE53935)
                    Icon(
                        imageVector = if (eligibilityCheckResult == true) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = "Eligibility Status Result icon",
                        tint = statusColor,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (eligibilityCheckResult == true) "ELIGIBILITY VERIFIED! 🎉" else "MEDICALLY INELIGIBLE",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = statusColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (eligibilityCheckResult == true) {
                            "Congratulations! You satisfy all standard healthcare regulations for blood donation. Your status is active and verified, granting you +20 reward points."
                        } else {
                            "Based on the clinical parameters provided, you do not satisfy standard donor eligibility constraints (Age 18-65, weight >= 50kg, BP 90-180, Pulse 50-100, no active tattoos/medication). Please consult a doctor."
                        },
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    TextButton(onClick = { viewModel.resetEligibility() }) {
                        Text("Re-test Eligibility parameters")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- SECTION 2: BMI CALCULATOR ---
        PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Scale, "BMI Logo", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("BMI Calculator (Body Mass Index)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Calculates index from weight and height. Donor minimum weight requires a healthy BMI index greater than 18.5.",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = heightInput,
                    onValueChange = { heightInput = it },
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PremiumButton(
                text = "Calculate Body Mass Index",
                onClick = {
                    val w = weightInput.toDoubleOrNull() ?: 72.0
                    val h = heightInput.toDoubleOrNull() ?: 175.0
                    viewModel.calculateBmi(w, h)
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Display BMI results
            bmiResult?.let { bmi ->
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("YOUR BMI SCORE", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(
                        String.format("%.1f", bmi),
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        bmiCategory ?: "Healthy status",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- SECTION 3: DONATION PREPARATION TIPS ---
        PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TipsAndUpdates, "Preparation Logo", tint = Color(0xFFFFA000), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Donation Preparation & Care", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BulletPoint(text = "Drink plenty of water (at least 500ml) directly before donating.")
                BulletPoint(text = "Eat a healthy, iron-rich, non-fatty meal 2-3 hours beforehand.")
                BulletPoint(text = "Ensure you get 7-8 hours of high-quality sleep the night before.")
                BulletPoint(text = "Avoid smoking and alcohol consumption 24 hours prior to blood retrieval.")
                BulletPoint(text = "Sit comfortably, breathe regularly, and rest for 15 minutes post donation.")
            }
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("•", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
