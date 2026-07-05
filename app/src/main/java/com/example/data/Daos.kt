package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Query("UPDATE user_profiles SET points = points + :points WHERE id = 1")
    suspend fun addPoints(points: Int)
}

@Dao
interface DonorDao {
    @Query("SELECT * FROM blood_donors ORDER BY distanceKm ASC")
    fun getAllDonors(): Flow<List<BloodDonor>>

    @Query("SELECT * FROM blood_donors WHERE bloodType = :bloodType ORDER BY distanceKm ASC")
    fun getDonorsByBloodType(bloodType: String): Flow<List<BloodDonor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonor(donor: BloodDonor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDonors(donors: List<BloodDonor>)

    @Query("DELETE FROM blood_donors")
    suspend fun deleteAllDonors()
}

@Dao
interface RequestDao {
    @Query("SELECT * FROM blood_requests ORDER BY isUrgent DESC, createdAt DESC")
    fun getAllRequests(): Flow<List<BloodRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: BloodRequest)

    @Update
    suspend fun updateRequest(request: BloodRequest)

    @Query("UPDATE blood_requests SET status = :status WHERE id = :id")
    suspend fun updateRequestStatus(id: Int, status: String)

    @Query("DELETE FROM blood_requests")
    suspend fun deleteAllRequests()

    @Delete
    suspend fun deleteRequest(request: BloodRequest)
}

@Dao
interface StockDao {
    @Query("SELECT * FROM blood_stock ORDER BY bloodType ASC")
    fun getAllStocks(): Flow<List<BloodStock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: BloodStock)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStocks(stocks: List<BloodStock>)

    @Query("DELETE FROM blood_stock")
    suspend fun deleteAllStocks()

    @Query("UPDATE blood_stock SET unitsAvailable = :units, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun updateStockUnits(id: Int, units: Int, lastUpdated: Long = System.currentTimeMillis())
}

@Dao
interface EventDao {
    @Query("SELECT * FROM campaign_events ORDER BY date ASC")
    fun getAllEvents(): Flow<List<CampaignEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CampaignEvent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEvents(events: List<CampaignEvent>)

    @Query("DELETE FROM campaign_events")
    suspend fun deleteAllEvents()

    @Query("UPDATE campaign_events SET isRegistered = :registered, registeredCount = registeredCount + :diff WHERE id = :id")
    suspend fun setEventRegistration(id: Int, registered: Boolean, diff: Int)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getChatMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications")
    suspend fun deleteAllNotifications()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Int)
}
