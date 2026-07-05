package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BloodViewModel
import com.example.ui.components.PremiumGlassCard
import com.example.ui.components.SectionHeading

// Leaderboard User Data Class
data class LeaderboardUser(
    val name: String,
    val points: Int,
    val donations: Int,
    val rank: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(viewModel: BloodViewModel) {
    val profile by viewModel.userProfile.collectAsState()

    val leaderboard = remember {
        listOf(
            LeaderboardUser("Ahmed Alom", 650, 12, 1),
            LeaderboardUser("Maria Rahman", 520, 9, 2),
            LeaderboardUser("Rony Mia (You)", 350, 6, 3),
            LeaderboardUser("Zubair Islam", 280, 5, 4),
            LeaderboardUser("Sumaiya Khan", 210, 4, 5),
            LeaderboardUser("Sajid Hasan", 180, 3, 6)
        )
    }

    // Points progress animation
    val progressAnim = animateFloatAsState(
        targetValue = ((profile?.points ?: 350) / 1000f).coerceIn(0f, 1f),
        animationSpec = tween(1200),
        label = "progress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 80.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeading(title = "Rewards, Badges & Rankings")

            // --- SECTION 1: CIRCULAR PROGRESS CANVAS ---
            PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Circle Canvas
                    Box(
                        modifier = Modifier.size(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Background ring
                            drawCircle(
                                color = Color.LightGray.copy(alpha = 0.2f),
                                radius = size.minDimension / 2 - 8.dp.toPx(),
                                style = Stroke(width = 8.dp.toPx())
                            )
                            // Progress arc
                            drawArc(
                                brush = Brush.sweepGradient(
                                    colors = listOf(Color(0xFFE53935), Color(0xFFFF8A80))
                                ),
                                startAngle = -90f,
                                sweepAngle = 360f * progressAnim.value,
                                useCenter = false,
                                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${profile?.points ?: 350}",
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("Total Pts", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    // Progress info details
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Next Badge Level: Gold Saver",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "You are ${(1000 - (profile?.points ?: 350)).coerceAtLeast(0)} points away from unlocking the Super Donor certificate! Keep answering medical diagnostics or responding to urgent SOS broadcasts.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- SECTION 2: BADGES GRID ---
            SectionHeading(title = "Earned Honor Badges")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BadgeWidget(
                    name = "Life Saver",
                    description = "Verify medical eligibility",
                    icon = Icons.Default.Verified,
                    unlocked = (profile?.points ?: 0) >= 120,
                    modifier = Modifier.weight(1f)
                )
                BadgeWidget(
                    name = "First Blood",
                    description = "Completed 1st donation",
                    icon = Icons.Default.WaterDrop,
                    unlocked = (profile?.donationCount ?: 0) >= 1,
                    modifier = Modifier.weight(1f)
                )
                BadgeWidget(
                    name = "AI Learner",
                    description = "Consult AI Assistant",
                    icon = Icons.Default.AutoAwesome,
                    unlocked = true, // Consult seeded welcoming message
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BadgeWidget(
                    name = "Emergency Hero",
                    description = "Trigger SOS distress signal",
                    icon = Icons.Default.Campaign,
                    unlocked = (profile?.points ?: 0) >= 300,
                    modifier = Modifier.weight(1f)
                )
                BadgeWidget(
                    name = "Century Donor",
                    description = "Donated 10+ times",
                    icon = Icons.Default.WorkspacePremium,
                    unlocked = (profile?.donationCount ?: 0) >= 10,
                    modifier = Modifier.weight(1f)
                )
                BadgeWidget(
                    name = "Streak Champion",
                    description = "3 Months active status",
                    icon = Icons.Default.ElectricBolt,
                    unlocked = false, // Not yet achieved
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- SECTION 3: TOP DONORS LEADERBOARD ---
            SectionHeading(title = "Regional Top Donors Leaderboard")
            PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
                leaderboard.forEachIndexed { idx, user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Rank indicator
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (user.rank) {
                                            1 -> Color(0xFFFFA000) // Gold
                                            2 -> Color(0xFF78909C) // Silver
                                            3 -> Color(0xFF8D6E63) // Bronze
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${user.rank}",
                                    color = if (user.rank <= 3) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            // User Profile Avatar Circle
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    user.name.take(1),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column {
                                Text(
                                    user.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "${user.donations} donations saving lives",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Text(
                            "${user.points} pts",
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }

                    if (idx < leaderboard.size - 1) {
                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeWidget(
    name: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    unlocked: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(115.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (unlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else Color.LightGray.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (unlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else Color.Gray.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Badge: $name icon",
                    tint = if (unlocked) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                name,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = if (unlocked) MaterialTheme.colorScheme.onSurface else Color.Gray
            )
            Text(
                description,
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                lineHeight = 10.sp,
                maxLines = 2
            )
        }
    }
}
