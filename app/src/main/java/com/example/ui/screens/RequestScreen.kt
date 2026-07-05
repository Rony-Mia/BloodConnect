package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BloodRequest
import com.example.ui.BloodViewModel
import com.example.ui.components.PremiumButton
import com.example.ui.components.PremiumGlassCard
import com.example.ui.components.SectionHeading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestScreen(viewModel: BloodViewModel) {
    val requests by viewModel.allRequests.collectAsState()

    var showForm by remember { mutableStateOf(false) }

    // Form Fields
    var patientName by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("O+") }
    var units by remember { mutableStateOf("1") }
    var hospitalName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var requiredDate by remember { mutableStateOf("2026-07-10") }
    var requiredTime by remember { mutableStateOf("11:00 AM") }
    var phone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isUrgent by remember { mutableStateOf(false) }

    val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showForm = !showForm },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = if (showForm) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "Create Request toggle"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeading(title = "Manage Blood Requests")

                if (showForm) {
                    // CREATE REQUEST FORM
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = patientName,
                            onValueChange = { patientName = it },
                            label = { Text("Patient Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, "User") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Text("Select Blood Group Required", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            bloodTypes.take(4).forEach { type ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (bloodType == type) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { bloodType = type }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        type,
                                        color = if (bloodType == type) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            bloodTypes.drop(4).forEach { type ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (bloodType == type) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { bloodType = type }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        type,
                                        color = if (bloodType == type) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = units,
                                onValueChange = { units = it },
                                label = { Text("Units (Bags)") },
                                leadingIcon = { Icon(Icons.Default.WaterDrop, "Bags") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Contact Phone") },
                                leadingIcon = { Icon(Icons.Default.Phone, "Phone") },
                                modifier = Modifier.weight(1.5f),
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = hospitalName,
                            onValueChange = { hospitalName = it },
                            label = { Text("Hospital Name") },
                            leadingIcon = { Icon(Icons.Default.LocalHospital, "Hospital") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Hospital Address / Location") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, "Address") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = requiredDate,
                                onValueChange = { requiredDate = it },
                                label = { Text("Required Date") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = requiredTime,
                                onValueChange = { requiredTime = it },
                                label = { Text("Required Time") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Medical Case Notes / Description") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = if (isUrgent) Color(0xFFE53935) else Color.LightGray,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = "Distress indicator warning",
                                    tint = if (isUrgent) Color(0xFFE53935) else Color.Gray
                                )
                                Column {
                                    Text(
                                        "Mark as Urgent SOS",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUrgent) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text("Broadcast immediate distress alerts", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            Switch(
                                checked = isUrgent,
                                onCheckedChange = { isUrgent = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE53935))
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        PremiumButton(
                            text = "Publish Blood Request",
                            onClick = {
                                viewModel.createBloodRequest(
                                    patientName = patientName.ifEmpty { "Anonymous Patient" },
                                    bloodType = bloodType,
                                    units = units.toIntOrNull() ?: 1,
                                    hospitalName = hospitalName.ifEmpty { "Central Clinic" },
                                    address = address.ifEmpty { "Mirpur 10, Dhaka" },
                                    date = requiredDate,
                                    time = requiredTime,
                                    phone = phone.ifEmpty { "+880 1700-000000" },
                                    notes = notes.ifEmpty { "Urgent replacement blood required. Standard matches." },
                                    isUrgent = isUrgent
                                )
                                showForm = false
                                // Reset form
                                patientName = ""
                                hospitalName = ""
                                address = ""
                                phone = ""
                                notes = ""
                                isUrgent = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    // REQUEST FEED SCROLL LIST
                    if (requests.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No current blood requests. Create one below!", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(start = 0.dp, end = 0.dp, top = 8.dp, bottom = 80.dp)
                        ) {
                            items(requests) { request ->
                                RequestCardItem(request = request, viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RequestCardItem(request: BloodRequest, viewModel: BloodViewModel) {
    PremiumGlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (request.isUrgent) Color(0xFFE53935).copy(alpha = 0.4f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (request.isUrgent) Color(0xFFE53935) else Color(0xFF37474F)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        request.bloodType,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(request.patientName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = "Status: ${request.status}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (request.status) {
                            "Completed" -> Color(0xFF00897B)
                            "Active" -> Color(0xFFFFA000)
                            else -> Color(0xFFE53935)
                        }
                    )
                }
            }

            if (request.isUrgent && request.status != "Completed") {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFE53935).copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("SOS URGENT", color = Color(0xFFE53935), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocalHospital, "Hospital", tint = Color.Gray, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("${request.hospitalName} (${request.hospitalAddress})", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccessTime, "Date-Time", tint = Color.Gray, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Required on ${request.requiredDate} at ${request.requiredTime}", fontSize = 13.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Call, "Phone", tint = Color.Gray, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Contact: ${request.contactPhone}", fontSize = 13.sp, color = Color.Gray)
        }

        if (request.notes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Notes: ${request.notes}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Complete/Accept buttons inside Request Feed
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (request.status == "Pending") {
                TextButton(onClick = { viewModel.respondToRequest(request.id, accept = false) }) {
                    Text("Reject", color = Color.Gray)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { viewModel.respondToRequest(request.id, accept = true) }) {
                    Text("Commit to Donate")
                }
            } else if (request.status == "Active") {
                Button(
                    onClick = { viewModel.completeRequest(request) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B))
                ) {
                    Icon(Icons.Default.Done, "Complete Icon", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mark as Completed (+150 pts)")
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, "Completed status", tint = Color(0xFF00897B), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Solved & Completed", color = Color(0xFF00897B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
