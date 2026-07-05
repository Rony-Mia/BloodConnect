package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BloodViewModel
import com.example.ui.components.PremiumButton
import com.example.ui.components.PremiumGlassCard
import com.example.ui.components.SectionHeading

// Map Marker Data Class
data class MapMarker(
    val id: Int,
    val name: String,
    val type: String, // "Donor", "Hospital", "BloodBank"
    val bloodType: String,
    val distance: Double,
    val etaMin: Int,
    val phone: String,
    val xOffsetRatio: Float, // Normalized X position on canvas (0.1 to 0.9)
    val yOffsetRatio: Float  // Normalized Y position on canvas (0.1 to 0.9)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapViewScreen(viewModel: BloodViewModel) {
    val context = LocalContext.current

    // Seed mock map markers
    val markers = remember {
        listOf(
            MapMarker(1, "Central Blood Bank", "BloodBank", "O+, A+, B+", 0.6, 4, "+880 1800-111222", 0.5f, 0.45f),
            MapMarker(2, "Square Hospital Trauma Center", "Hospital", "All Types", 2.1, 12, "+880 1711-222333", 0.3f, 0.25f),
            MapMarker(3, "Maria Rahman (Donor)", "Donor", "O+", 0.8, 6, "+880 1655-666777", 0.7f, 0.55f),
            MapMarker(4, "Ahmed Alom (Donor)", "Donor", "A+", 1.2, 8, "+880 1811-222333", 0.6f, 0.3f),
            MapMarker(5, "Dhaka Medical College Clinic", "Hospital", "All Types", 3.4, 18, "+880 1799-888777", 0.2f, 0.75f),
            MapMarker(6, "Sumaiya Khan (Donor)", "Donor", "B-", 2.4, 14, "+880 1922-333444", 0.8f, 0.2f)
        )
    }

    var selectedMarker by remember { mutableStateOf<MapMarker?>(markers.first()) }
    var filterType by remember { mutableStateOf("All") } // "All", "Donor", "Hospital", "BloodBank"

    // Pulse animation for radar scanning
    val infiniteTransition = rememberInfiniteTransition(label = "mapRadar")
    val radarRadiusRatio by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar"
    )
    val radarAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeading(title = "Local GPS Live Tracking Map")

            // Filters selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Donor", "Hospital", "BloodBank").forEach { type ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (filterType == type) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { filterType = type }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (type == "BloodBank") "Banks" else type,
                            color = if (filterType == type) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // INTERACTIVE MAP CANVAS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .background(Color(0xFF1E272C)) // Radar / Dark theme grid map
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            // Look for clicked markers based on proximity
                            val canvasWidth = size.width
                            val canvasHeight = size.height

                            val clicked = markers
                                .filter { filterType == "All" || it.type == filterType }
                                .minByOrNull { marker ->
                                    val mx = marker.xOffsetRatio * canvasWidth
                                    val my = marker.yOffsetRatio * canvasHeight
                                    val dx = mx - offset.x
                                    val dy = my - offset.y
                                    dx * dx + dy * dy
                                }

                            clicked?.let { marker ->
                                val mx = marker.xOffsetRatio * canvasWidth
                                val my = marker.yOffsetRatio * canvasHeight
                                val dx = mx - offset.x
                                val dy = my - offset.y
                                // Limit tap sensitivity to 35dp radius
                                if (dx * dx + dy * dy <= 1200f) {
                                    selectedMarker = marker
                                }
                            }
                        }
                    }
            ) {
                val width = size.width
                val height = size.height

                // Draw Grid lines
                val gridSpacing = 60f
                var x = 0f
                while (x < width) {
                    drawLine(
                        color = Color(0xFF37474F).copy(alpha = 0.4f),
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = 1f
                    )
                    x += gridSpacing
                }
                var y = 0f
                while (y < height) {
                    drawLine(
                        color = Color(0xFF37474F).copy(alpha = 0.4f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                    y += gridSpacing
                }

                // Draw radar scan ring from Center
                val centerX = width * 0.5f
                val centerY = height * 0.5f
                val maxRadius = (if (width < height) width else height) * 0.5f
                drawCircle(
                    color = Color(0xFFE53935).copy(alpha = radarAlpha),
                    radius = maxRadius * radarRadiusRatio,
                    center = Offset(centerX, centerY),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )

                // Draw concentric ranges rings
                drawCircle(color = Color(0xFF37474F).copy(alpha = 0.5f), radius = maxRadius * 0.3f, center = Offset(centerX, centerY), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f))
                drawCircle(color = Color(0xFF37474F).copy(alpha = 0.5f), radius = maxRadius * 0.6f, center = Offset(centerX, centerY), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f))
                drawCircle(color = Color(0xFF37474F).copy(alpha = 0.5f), radius = maxRadius * 0.9f, center = Offset(centerX, centerY), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f))

                // Draw User current position pulsing Blue dot
                drawCircle(
                    color = Color(0xFF29B6F6).copy(alpha = 0.2f),
                    radius = 24f * (radarRadiusRatio + 0.5f),
                    center = Offset(centerX, centerY)
                )
                drawCircle(
                    color = Color(0xFF29B6F6),
                    radius = 8f,
                    center = Offset(centerX, centerY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 3f,
                    center = Offset(centerX, centerY)
                )

                // Draw Marker Pins
                markers
                    .filter { filterType == "All" || it.type == filterType }
                    .forEach { marker ->
                        val mx = marker.xOffsetRatio * width
                        val my = marker.yOffsetRatio * height

                        val pinColor = when (marker.type) {
                            "Donor" -> Color(0xFFE53935)     // Red droplet
                            "Hospital" -> Color(0xFF00897B)  // Green Cross
                            else -> Color(0xFFFFA000)        // Orange Bloodbank
                        }

                        // Selected highlighted ring
                        if (selectedMarker?.id == marker.id) {
                            drawCircle(
                                color = pinColor.copy(alpha = 0.25f),
                                radius = 24f * (radarRadiusRatio + 0.3f),
                                center = Offset(mx, my)
                            )
                        }

                        // Draw Marker drop/cross base shape
                        drawCircle(
                            color = pinColor,
                            radius = 12f,
                            center = Offset(mx, my)
                        )
                        // White inner core
                        drawCircle(
                            color = Color.White,
                            radius = 5f,
                            center = Offset(mx, my)
                        )
                    }
            }

            // HUD Overlay Map Labels key
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFE53935)))
                        Text("Matched Donors", color = Color.LightGray, fontSize = 9.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00897B)))
                        Text("Hospitals", color = Color.LightGray, fontSize = 9.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFA000)))
                        Text("Blood Banks", color = Color.LightGray, fontSize = 9.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF29B6F6)))
                        Text("You (Mirpur 10)", color = Color.LightGray, fontSize = 9.sp)
                    }
                }
            }

            // Tapping tooltip
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text("Tap pins to select", color = Color.White, fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // SELECTED PIN HUD OVERLAY DETAILS (Sliding style bottom layout)
        selectedMarker?.let { marker ->
            PremiumGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 90.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                imageVector = when (marker.type) {
                                    "Donor" -> Icons.Default.Person
                                    "Hospital" -> Icons.Default.LocalHospital
                                    else -> Icons.Default.Bloodtype
                                },
                                contentDescription = "Marker Type",
                                tint = when (marker.type) {
                                    "Donor" -> Color(0xFFE53935)
                                    "Hospital" -> Color(0xFF00897B)
                                    else -> Color(0xFFFFA000)
                                }
                            )
                            Text(
                                marker.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = when (marker.type) {
                                "Donor" -> "Blood Group: ${marker.bloodType} (Available)"
                                "Hospital" -> "Emergency ICU & Trauma Services"
                                else -> "Storage capacity: ${marker.bloodType}"
                            },
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // GPS Route details
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("DISTANCE", fontSize = 10.sp, color = Color.Gray)
                        Text("${marker.distance} km", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    VerticalDivider(modifier = Modifier.height(30.dp))
                    Column {
                        Text("ESTIMATED ETA", fontSize = 10.sp, color = Color.Gray)
                        Text("${marker.etaMin} mins", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF00897B))
                    }
                    VerticalDivider(modifier = Modifier.height(30.dp))
                    Column {
                        Text("MATCH STATUS", fontSize = 10.sp, color = Color.Gray)
                        Text("Optimal Route", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${marker.phone}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Call, "Call")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Call Facility", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            // Launch Map intent representation
                            val mapUri = Uri.parse("geo:23.8041,90.3625?q=${Uri.encode(marker.name)}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                            context.startActivity(mapIntent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Navigation, "Navigate")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Directions", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
