package com.example.data

import kotlinx.coroutines.flow.Flow

class BloodRepository(private val db: AppDatabase) {

    // --- User Profile ---
    val userProfile: Flow<UserProfile?> = db.userDao().getUserProfile()
    
    suspend fun getUserProfileSync(): UserProfile? {
        return db.userDao().getUserProfileSync()
    }

    suspend fun insertOrUpdateProfile(profile: UserProfile) {
        db.userDao().insertOrUpdateProfile(profile)
    }

    suspend fun addPoints(points: Int) {
        db.userDao().addPoints(points)
    }

    // --- Donors ---
    val allDonors: Flow<List<BloodDonor>> = db.donorDao().getAllDonors()

    fun getDonorsByBloodType(bloodType: String): Flow<List<BloodDonor>> {
        return db.donorDao().getDonorsByBloodType(bloodType)
    }

    suspend fun insertDonor(donor: BloodDonor) {
        db.donorDao().insertDonor(donor)
    }

    // --- Blood Requests ---
    val allRequests: Flow<List<BloodRequest>> = db.requestDao().getAllRequests()

    suspend fun insertRequest(request: BloodRequest) {
        db.requestDao().insertRequest(request)
    }

    suspend fun updateRequest(request: BloodRequest) {
        db.requestDao().updateRequest(request)
    }

    suspend fun updateRequestStatus(id: Int, status: String) {
        db.requestDao().updateRequestStatus(id, status)
    }

    suspend fun deleteRequest(request: BloodRequest) {
        db.requestDao().deleteRequest(request)
    }

    // --- Blood Stock ---
    val allStocks: Flow<List<BloodStock>> = db.stockDao().getAllStocks()

    suspend fun insertStock(stock: BloodStock) {
        db.stockDao().insertStock(stock)
    }

    suspend fun updateStockUnits(id: Int, units: Int) {
        db.stockDao().updateStockUnits(id, units)
    }

    // --- Campaign Events ---
    val allEvents: Flow<List<CampaignEvent>> = db.eventDao().getAllEvents()

    suspend fun insertEvent(event: CampaignEvent) {
        db.eventDao().insertEvent(event)
    }

    suspend fun setEventRegistration(id: Int, registered: Boolean, diff: Int) {
        db.eventDao().setEventRegistration(id, registered, diff)
    }

    // --- Chat ---
    val chatMessages: Flow<List<ChatMessage>> = db.chatDao().getChatMessages()

    suspend fun insertChatMessage(message: ChatMessage) {
        db.chatDao().insertMessage(message)
    }

    suspend fun clearChatHistory() {
        db.chatDao().clearChatHistory()
    }

    // --- Notifications ---
    val allNotifications: Flow<List<NotificationItem>> = db.notificationDao().getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = db.notificationDao().getUnreadCount()

    suspend fun insertNotification(notification: NotificationItem) {
        db.notificationDao().insertNotification(notification)
    }

    suspend fun markNotificationAsRead(id: Int) {
        db.notificationDao().markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        db.notificationDao().markAllAsRead()
    }

    suspend fun deleteNotification(id: Int) {
        db.notificationDao().deleteNotification(id)
    }
}
