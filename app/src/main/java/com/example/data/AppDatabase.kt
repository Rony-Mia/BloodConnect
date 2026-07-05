package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        BloodDonor::class,
        BloodRequest::class,
        BloodStock::class,
        CampaignEvent::class,
        ChatMessage::class,
        NotificationItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun donorDao(): DonorDao
    abstract fun requestDao(): RequestDao
    abstract fun stockDao(): StockDao
    abstract fun eventDao(): EventDao
    abstract fun chatDao(): ChatDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "blood_connect_db"
                )
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database)
                }
            }
        }

        private suspend fun populateDatabase(db: AppDatabase) {
            // 1. Seed User Profile
            db.userDao().insertOrUpdateProfile(
                UserProfile(
                    id = 1,
                    name = "Rony Mia",
                    email = "ronymia2021@gmail.com",
                    phone = "+880 1712-345678",
                    bloodType = "O+",
                    location = "Mirpur 10, Dhaka",
                    age = 24,
                    gender = "Male",
                    weight = 72.5,
                    height = 175.0,
                    diseases = "None",
                    lastDonationDate = "2026-04-10",
                    donationCount = 6,
                    isAvailable = true,
                    points = 350,
                    userType = "Donor"
                )
            )

            // 2. Seed Nearby Donors
            db.donorDao().insertAllDonors(
                listOf(
                    BloodDonor(name = "Ahmed Alom", bloodType = "A+", location = "Dhanmondi, Dhaka", phone = "+880 1811-222333", isAvailable = true, donationCount = 5, distanceKm = 1.2, gender = "Male", age = 28),
                    BloodDonor(name = "Sumaiya Khan", bloodType = "B-", location = "Banani, Dhaka", phone = "+880 1922-333444", isAvailable = true, donationCount = 2, distanceKm = 2.4, gender = "Female", age = 23),
                    BloodDonor(name = "Taskin Ahmed", bloodType = "O-", location = "Uttara, Dhaka", phone = "+880 1733-444555", isAvailable = false, donationCount = 8, distanceKm = 5.1, gender = "Male", age = 31),
                    BloodDonor(name = "Zubair Islam", bloodType = "AB+", location = "Mohakhali, Dhaka", phone = "+880 1544-555666", isAvailable = true, donationCount = 11, distanceKm = 3.8, gender = "Male", age = 35),
                    BloodDonor(name = "Maria Rahman", bloodType = "O+", location = "Tejgaon, Dhaka", phone = "+880 1655-666777", isAvailable = true, donationCount = 4, distanceKm = 0.8, gender = "Female", age = 25),
                    BloodDonor(name = "Sajid Hasan", bloodType = "B+", location = "Gulsan, Dhaka", phone = "+880 1766-777888", isAvailable = true, donationCount = 3, distanceKm = 4.2, gender = "Male", age = 26),
                    BloodDonor(name = "Kazi Fahim", bloodType = "A-", location = "Badda, Dhaka", phone = "+880 1877-888999", isAvailable = true, donationCount = 1, distanceKm = 6.0, gender = "Male", age = 22)
                )
            )

            // 3. Seed Blood Requests
            db.requestDao().insertRequest(
                BloodRequest(
                    patientName = "Karim Uddin",
                    bloodType = "O-",
                    unitsRequired = 2,
                    hospitalName = "Dhaka Medical College Hospital",
                    hospitalAddress = "Ramna, Dhaka",
                    requiredDate = "2026-07-06",
                    requiredTime = "10:00 AM",
                    contactPhone = "+880 1799-888777",
                    notes = "Emergency bypass surgery. Need urgent donors. Blood matched preferred.",
                    isUrgent = true,
                    status = "Pending"
                )
            )
            db.requestDao().insertRequest(
                BloodRequest(
                    patientName = "Fatema Begum",
                    bloodType = "AB+",
                    unitsRequired = 1,
                    hospitalName = "Square Hospital",
                    hospitalAddress = "Panthapath, Dhaka",
                    requiredDate = "2026-07-05",
                    requiredTime = "04:30 PM",
                    contactPhone = "+880 1688-777666",
                    notes = "Thalassemia patient regular transfusion.",
                    isUrgent = false,
                    status = "Active"
                )
            )
            db.requestDao().insertRequest(
                BloodRequest(
                    patientName = "Rafiqul Islam",
                    bloodType = "A+",
                    unitsRequired = 3,
                    hospitalName = "Ibn Sina Hospital",
                    hospitalAddress = "Kalyanpur, Dhaka",
                    requiredDate = "2026-06-28",
                    requiredTime = "09:00 AM",
                    contactPhone = "+880 1511-222333",
                    notes = "Road accident emergency. Solved successfully by BloodConnect volunteers.",
                    isUrgent = true,
                    status = "Completed"
                )
            )

            // 4. Seed Blood Stock Inventory
            db.stockDao().insertAllStocks(
                listOf(
                    BloodStock(facilityName = "Central Blood Bank", bloodType = "A+", unitsAvailable = 25),
                    BloodStock(facilityName = "Central Blood Bank", bloodType = "A-", unitsAvailable = 4),
                    BloodStock(facilityName = "Central Blood Bank", bloodType = "B+", unitsAvailable = 18),
                    BloodStock(facilityName = "Central Blood Bank", bloodType = "B-", unitsAvailable = 3),
                    BloodStock(facilityName = "Central Blood Bank", bloodType = "AB+", unitsAvailable = 12),
                    BloodStock(facilityName = "Central Blood Bank", bloodType = "AB-", unitsAvailable = 2),
                    BloodStock(facilityName = "Central Blood Bank", bloodType = "O+", unitsAvailable = 32),
                    BloodStock(facilityName = "Central Blood Bank", bloodType = "O-", unitsAvailable = 5)
                )
            )

            // 5. Seed Events / Campaigns
            db.eventDao().insertAllEvents(
                listOf(
                    CampaignEvent(
                        title = "Youth Blood Donation Festival 2026",
                        organizer = "Red Crescent Society BD",
                        location = "TSC, Dhaka University",
                        date = "2026-07-15",
                        time = "09:00 AM - 05:00 PM",
                        description = "Join Dhaka's biggest university blood donation drive. Get free certificates, snacks, and medical reports on eligibility and blood count analysis.",
                        registeredCount = 45,
                        isRegistered = false
                    ),
                    CampaignEvent(
                        title = "Emergency Disaster Blood Reserve Camp",
                        organizer = "BloodConnect Foundation",
                        location = "Mirpur Stadium Entrance 1",
                        date = "2026-07-22",
                        time = "10:00 AM - 04:00 PM",
                        description = "Aiming to build a solid backup reserve for monsoon emergencies and critical road traffic incident casualties. All blood groups needed.",
                        registeredCount = 89,
                        isRegistered = true
                    )
                )
            )

            // 6. Seed Welcome Notification and Emergency Alert
            db.notificationDao().insertNotification(
                NotificationItem(
                    title = "Welcome to BloodConnect!",
                    message = "Thank you Rony for joining hands to save lives. Complete your profile and eligibility check to earn your first donation badge.",
                    type = "System"
                )
            )
            db.notificationDao().insertNotification(
                NotificationItem(
                    title = "URGENT O- BLOOD NEEDED",
                    message = "Karim Uddin needs 2 units of O- blood at Dhaka Medical College Hospital. Tap to view and respond.",
                    type = "Emergency"
                )
            )

            // 7. Seed Assistant Chat Messages
            db.chatDao().insertMessage(
                ChatMessage(
                    senderId = "assistant",
                    senderName = "BloodConnect AI",
                    messageText = "Hello Rony! I am your AI Health & Matching Assistant. You can ask me questions about blood donation requirements, eligibility rules, BMI, or request smart donor matching nearby."
                )
            )
        }
    }
}
