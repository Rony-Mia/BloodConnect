package com.example.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BloodRepository(private val db: AppDatabase) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // --- Firebase Sync Helpers ---
    fun getRemoteUserProfile(): Flow<UserProfile?> = callbackFlow {
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        val listener = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObject(UserProfile::class.java))
            }
        awaitClose { listener.remove() }
    }

    fun getRemoteRequests(): Flow<List<BloodRequest>> = callbackFlow {
        val listener = firestore.collection("requests")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val requests = snapshot?.toObjects(BloodRequest::class.java) ?: emptyList()
                trySend(requests)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveProfileToFirestore(profile: UserProfile) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).set(profile).await()
    }

    suspend fun saveRequestToFirestore(request: BloodRequest) {
        val docRef = if (request.id > 0) {
            firestore.collection("requests").document(request.id.toString())
        } else {
            firestore.collection("requests").document()
        }
        
        // If it's a new request from local, use Firestore's ID if needed, 
        // but here we assume a unified object
        firestore.collection("requests").document(docRef.id).set(request.copy(
            status = request.status.ifEmpty { "Pending" }
        )).await()
    }

    // --- User Profile (Local + Remote Bridge) ---
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

    suspend fun clearAllLocalData() {
        db.donorDao().deleteAllDonors()
        db.requestDao().deleteAllRequests()
        db.stockDao().deleteAllStocks()
        db.eventDao().deleteAllEvents()
        db.chatDao().clearChatHistory()
        db.notificationDao().deleteAllNotifications()
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
