package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BloodStock
import com.example.data.UserProfile
import com.example.ui.BloodViewModel
import com.example.ui.components.AnalyticsLineChart
import com.example.ui.components.PremiumButton
import com.example.ui.components.PremiumGlassCard
import com.example.ui.components.SectionHeading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: BloodViewModel) {
    val scrollState = rememberScrollState()

    val profile by viewModel.userProfile.collectAsState()
    val stocks by viewModel.allStocks.collectAsState()

    // Mock logs
    val logs = remember {
        listOf(
            "04:50 AM - Database synced successfully with Cloud Firestore (0 conflicts resolved)",
            "04:41 AM - Backup completed (Local Room DB exported to encrypted backup)",
            "04:32 AM - Emergency SOS broadcasted by user 'Rony Mia' within 5km radius",
            "04:10 AM - Stock replenishment: 5 bags of O- added by Square Hospital",
            "03:45 AM - Fake account detector scanned: 0 flags raised",
            "03:15 AM - Automated rate-limiting: Active (0 spam attempts blocked)"
        )
    }

    val forecastData = listOf(0.2f, 0.35f, 0.45f, 0.85f, 0.95f, 0.7f, 0.6f)
    val forecastLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(bottom = 90.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeading(title = "Clinical Administrator Console")

        // --- SECTION 1: ROLE CONFIGURATION SWITCHER ---
        PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ManageAccounts, "Roles Icon", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Account Privilege Roles Manager", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Change account context representation inside memory. This will grant access to hospital stocks and system analytics.",
                fontSize = 11.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Donor", "Hospital", "SuperAdmin").forEach { role ->
                    val isActive = profile?.userType == role
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isActive) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable {
                                profile?.let {
                                    viewModel.updateProfile(it.copy(userType = role))
                                }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            role,
                            color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- SECTION 2: AI BLOOD DEMAND FORECASTING ---
        SectionHeading(title = "Predictive Blood Demand (AI Model)")
        PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Weekly Blood Demand Analysis (Units)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            AnalyticsLineChart(dataPoints = forecastData, labels = forecastLabels)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.AutoAwesome, "AI icon", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                Text(
                    "AI Prediction: Peak critical demand expected on Thursday-Friday (Monsoon season cases rise). Maintain reserves of O- and B-.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- SECTION 3: STOCK INVENTORY CONTROL PANEL ---
        if (profile?.userType == "Hospital" || profile?.userType == "SuperAdmin") {
            SectionHeading(title = "Stock Inventory Level Adjustments")
            PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Clinical Blood Stock Bags Controller",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                stocks.forEach { stock ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stock.bloodType,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "${stock.unitsAvailable} Bags",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        // Adjust buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (stock.unitsAvailable > 0) {
                                        viewModel.updateStock(stock.id, stock.unitsAvailable - 1)
                                    }
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(Icons.Default.Remove, "Decrease", modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = {
                                    viewModel.updateStock(stock.id, stock.unitsAvailable + 1)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            ) {
                                Icon(Icons.Default.Add, "Increase", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- SECTION 4: SECURITY SYSTEM LOGS ---
        SectionHeading(title = "Clinical Audit Trail & Logs")
        PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                "System Activity Records (SQLite)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            logs.forEach { log ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("•", color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(
                        log,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
