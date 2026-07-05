package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Single logged-in user profile
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val bloodType: String = "",
    val location: String = "",
    val age: Int = 0,
    val gender: String = "",
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val diseases: String = "",
    val lastDonationDate: String = "",
    val donationCount: Int = 0,
    val isAvailable: Boolean = false,
    val points: Int = 0,
    val userType: String = "Donor" // "Donor", "Seeker", "Hospital", "BloodBank", "Admin", "SuperAdmin"
)

@Entity(tableName = "blood_donors")
data class BloodDonor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val bloodType: String = "",
    val location: String = "",
    val phone: String = "",
    val isAvailable: Boolean = false,
    val donationCount: Int = 0,
    val distanceKm: Double = 0.0,
    val gender: String = "",
    val age: Int = 0
)

@Entity(tableName = "blood_requests")
data class BloodRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientName: String = "",
    val bloodType: String = "",
    val unitsRequired: Int = 0,
    val hospitalName: String = "",
    val hospitalAddress: String = "",
    val requiredDate: String = "",
    val requiredTime: String = "",
    val contactPhone: String = "",
    val notes: String = "",
    val isUrgent: Boolean = false,
    val status: String = "Pending", // "Pending", "Active", "Completed", "Cancelled"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "blood_stock")
data class BloodStock(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val facilityName: String = "",
    val bloodType: String = "",
    val unitsAvailable: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "campaign_events")
data class CampaignEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String = "",
    val organizer: String = "",
    val location: String = "",
    val date: String = "",
    val time: String = "",
    val description: String = "",
    val registeredCount: Int = 0,
    val isRegistered: Boolean = false
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderId: String = "", // "user", "assistant", "system"
    val senderName: String = "",
    val messageText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String = "",
    val message: String = "",
    val type: String = "", // "Emergency", "System", "DonationReminder", "Reward"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
