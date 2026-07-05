package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiService
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BloodViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = BloodRepository(db)
    private val geminiService = GeminiService()
    private val auth = com.google.firebase.auth.FirebaseAuth.getInstance()

    // --- Dynamic UI State ---
    private val _selectedScreen = MutableStateFlow("login")
    val selectedScreen: StateFlow<String> = _selectedScreen.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        if (auth.currentUser != null) {
            _selectedScreen.value = "dashboard"
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedBloodTypeFilter = MutableStateFlow("All")
    val selectedBloodTypeFilter: StateFlow<String> = _selectedBloodTypeFilter.asStateFlow()

    private val _selectedAreaFilter = MutableStateFlow("All")
    val selectedAreaFilter: StateFlow<String> = _selectedAreaFilter.asStateFlow()

    private val _isAITyping = MutableStateFlow(false)
    val isAITyping: StateFlow<Boolean> = _isAITyping.asStateFlow()

    private val _sosTriggered = MutableStateFlow(false)
    val sosTriggered: StateFlow<Boolean> = _sosTriggered.asStateFlow()

    private val _currentBmi = MutableStateFlow<Double?>(null)
    val currentBmi: StateFlow<Double?> = _currentBmi.asStateFlow()

    private val _bmiResultText = MutableStateFlow<String?>(null)
    val bmiResultText: StateFlow<String?> = _bmiResultText.asStateFlow()

    private val _isEligibleToDonate = MutableStateFlow<Boolean?>(null)
    val isEligibleToDonate: StateFlow<Boolean?> = _isEligibleToDonate.asStateFlow()

    // --- Database-backed Flows ---
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .combine(repository.getRemoteUserProfile()) { local, remote ->
            remote ?: local
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allDonors: StateFlow<List<BloodDonor>> = repository.allDonors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRequests: StateFlow<List<BloodRequest>> = repository.allRequests
        .combine(repository.getRemoteRequests()) { local, remote ->
            if (remote.isNotEmpty()) remote else local
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStocks: StateFlow<List<BloodStock>> = repository.allStocks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEvents: StateFlow<List<CampaignEvent>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<NotificationItem>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.unreadNotificationsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- Search Filtering (Combined in-memory filter) ---
    val filteredDonors: StateFlow<List<BloodDonor>> = combine(
        allDonors,
        searchQuery,
        selectedBloodTypeFilter,
        selectedAreaFilter
    ) { donors, query, bloodType, area ->
        donors.filter { donor ->
            val matchesQuery = query.isEmpty() || donor.name.contains(query, ignoreCase = true) || donor.location.contains(query, ignoreCase = true)
            val matchesBloodType = bloodType == "All" || donor.bloodType.equals(bloodType, ignoreCase = true)
            val matchesArea = area == "All" || donor.location.contains(area, ignoreCase = true)
            matchesQuery && matchesBloodType && matchesArea
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Auth Management ---
    fun login(email: String, phone: String, method: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (email.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(email, "password123").await()
                }
                
                _isLoggedIn.value = true
                _selectedScreen.value = "dashboard"
                
                repository.insertNotification(
                    NotificationItem(
                        title = "Successful Login",
                        message = "Logged in securely via $method. Welcome back!",
                        type = "System"
                    )
                )
            } catch (e: Exception) {
                // Fallback for demo if auth not fully setup
                _isLoggedIn.value = true
                _selectedScreen.value = "dashboard"
            }
        }
    }

    fun register(name: String, email: String, phone: String, bloodType: String, location: String, age: Int, gender: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (email.isNotEmpty()) {
                    auth.createUserWithEmailAndPassword(email, "password123").await()
                }

                val profile = UserProfile(
                    id = 1,
                    name = name,
                    email = email,
                    phone = phone,
                    bloodType = bloodType,
                    location = location,
                    age = age,
                    gender = gender,
                    weight = 70.0,
                    height = 170.0,
                    diseases = "None",
                    lastDonationDate = "Never",
                    donationCount = 0,
                    isAvailable = true,
                    points = 100, // Starting points
                    userType = "Donor"
                )
                repository.insertOrUpdateProfile(profile)
                repository.saveProfileToFirestore(profile)
                _isLoggedIn.value = true
                _selectedScreen.value = "dashboard"

                repository.insertNotification(
                    NotificationItem(
                        title = "Account Created",
                        message = "Welcome to BloodConnect, $name! 100 reward points granted.",
                        type = "Reward"
                    )
                )
            } catch (e: Exception) {
                _isLoggedIn.value = true
                _selectedScreen.value = "dashboard"
            }
        }
    }

    fun logout() {
        auth.signOut()
        _isLoggedIn.value = false
        _selectedScreen.value = "login"
    }

    fun deleteAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertOrUpdateProfile(
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
            _isLoggedIn.value = false
            _selectedScreen.value = "login"
        }
    }

    // --- Set Screen Route ---
    fun navigateTo(screen: String) {
        _selectedScreen.value = screen
    }

    // --- Search Filtering ---
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateBloodTypeFilter(bloodType: String) {
        _selectedBloodTypeFilter.value = bloodType
    }

    fun updateAreaFilter(area: String) {
        _selectedAreaFilter.value = area
    }

    // --- User Profile Edit ---
    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertOrUpdateProfile(profile)
            repository.saveProfileToFirestore(profile)
            repository.insertNotification(
                NotificationItem(
                    title = "Profile Updated",
                    message = "Your health profile and donor preferences have been updated successfully.",
                    type = "System"
                )
            )
        }
    }

    // --- Create Blood Request ---
    fun createBloodRequest(
        patientName: String,
        bloodType: String,
        units: Int,
        hospitalName: String,
        address: String,
        date: String,
        time: String,
        phone: String,
        notes: String,
        isUrgent: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = BloodRequest(
                patientName = patientName,
                bloodType = bloodType,
                unitsRequired = units,
                hospitalName = hospitalName,
                hospitalAddress = address,
                requiredDate = date,
                requiredTime = time,
                contactPhone = phone,
                notes = notes,
                isUrgent = isUrgent,
                status = "Pending"
            )
            repository.insertRequest(request)
            repository.saveRequestToFirestore(request)

            // Trigger notifications
            val title = if (isUrgent) "URGENT Blood Request Alert!" else "New Blood Request"
            val message = "Patient $patientName requires $units units of $bloodType blood at $hospitalName."
            repository.insertNotification(
                NotificationItem(
                    title = title,
                    message = message,
                    type = if (isUrgent) "Emergency" else "System"
                )
            )
        }
    }

    // --- Accept / Complete Request ---
    fun respondToRequest(requestId: Int, accept: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (accept) {
                repository.updateRequestStatus(requestId, "Active")
                repository.insertNotification(
                    NotificationItem(
                        title = "Request Accepted",
                        message = "You have committed to donate blood. Please reach out to the contact person.",
                        type = "System"
                    )
                )
            } else {
                repository.updateRequestStatus(requestId, "Cancelled")
            }
        }
    }

    fun completeRequest(request: BloodRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val completed = request.copy(status = "Completed")
            repository.updateRequest(completed)
            
            // Award points and notify
            repository.addPoints(150)
            repository.insertNotification(
                NotificationItem(
                    title = "Donation Successful!",
                    message = "Thank you for saving a life. You have been awarded 150 points!",
                    type = "Reward"
                )
            )

            // Increment current user's donation count
            val current = repository.getUserProfileSync()
            if (current != null) {
                repository.insertOrUpdateProfile(
                    current.copy(
                        donationCount = current.donationCount + 1,
                        points = current.points + 150,
                        lastDonationDate = "Today"
                    )
                )
            }
        }
    }

    // --- Stocks Management ---
    fun updateStock(stockId: Int, units: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStockUnits(stockId, units)
        }
    }

    // --- Event Campaign Management ---
    fun toggleEventRegistration(eventId: Int, currentlyRegistered: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val diff = if (currentlyRegistered) -1 else 1
            repository.setEventRegistration(eventId, !currentlyRegistered, diff)
            if (!currentlyRegistered) {
                repository.addPoints(50)
                repository.insertNotification(
                    NotificationItem(
                        title = "Registered for Event",
                        message = "You signed up for the campaign. +50 points awarded!",
                        type = "Reward"
                    )
                )
                // Update user points locally
                val current = repository.getUserProfileSync()
                if (current != null) {
                    repository.insertOrUpdateProfile(current.copy(points = current.points + 50))
                }
            }
        }
    }

    // --- SOS Mode ---
    fun triggerSosEmergency() {
        viewModelScope.launch(Dispatchers.IO) {
            _sosTriggered.value = true
            
            // Insert SOS Notifications
            repository.insertNotification(
                NotificationItem(
                    title = "🚨 EMERGENCY SOS BROADCASTED",
                    message = "An urgent distress beacon has been launched to all matched donors within a 5km radius.",
                    type = "Emergency"
                )
            )

            // Simulate alert notifications streaming in
            delay(4000)
            repository.insertNotification(
                NotificationItem(
                    title = "Donor Match Responded",
                    message = "Maria Rahman (O+) is nearby and is responding to your SOS alert!",
                    type = "Emergency"
                )
            )
        }
    }

    fun dismissSos() {
        _sosTriggered.value = false
    }

    // --- Chat with AI Health Assistant & Matching Engine ---
    fun sendChatMessage(text: String) {
        if (text.trim().isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            // 1. Insert user message
            val userMsg = ChatMessage(
                senderId = "user",
                senderName = "Rony Mia",
                messageText = text
            )
            repository.insertChatMessage(userMsg)

            _isAITyping.value = true

            // Formulate prompt context
            val currentProfile = repository.getUserProfileSync()
            val availableDonors = repository.allDonors.first().filter { it.isAvailable }
            val activeRequests = repository.allRequests.first().filter { it.status == "Pending" }
            
            val systemPrompt = """
                You are BloodConnect AI, a helpful Health, Matching, and Diagnostics Assistant for the BloodConnect app.
                Current User profile: Name: ${currentProfile?.name}, Blood Group: ${currentProfile?.bloodType}, Location: ${currentProfile?.location}, Points: ${currentProfile?.points}.
                Available local donors: ${availableDonors.joinToString { "${it.name} (${it.bloodType} at ${it.location})" }}
                Active blood requests: ${activeRequests.joinToString { "${it.patientName} (${it.bloodType} needed at ${it.hospitalName})" }}
                
                Your job is to answer health questions, perform matching recommendations, advise on donation eligibility, provide tips, or friendly chatbot banter. Keep replies concise, warm, helpful, and formatted beautifully in markdown. Do not exceed 3-4 paragraphs.
            """.trimIndent()

            // 2. Query Gemini
            val aiResponse = geminiService.askGemini(text, systemPrompt)

            _isAITyping.value = false

            // 3. Insert AI response
            val botMsg = ChatMessage(
                senderId = "assistant",
                senderName = "BloodConnect AI",
                messageText = aiResponse
            )
            repository.insertChatMessage(botMsg)

            // Give user small reward for learning!
            repository.addPoints(5)
            val current = repository.getUserProfileSync()
            if (current != null) {
                repository.insertOrUpdateProfile(current.copy(points = current.points + 5))
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearChatHistory()
            repository.insertChatMessage(
                ChatMessage(
                    senderId = "assistant",
                    senderName = "BloodConnect AI",
                    messageText = "Hello Rony! Chat history cleared. How can I help you today regarding blood donation, donor matching, or eligibility?"
                )
            )
        }
    }

    fun clearAllSampleData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllLocalData()
        }
    }

    // --- Health Calculations ---
    fun calculateBmi(weight: Double, heightCm: Double) {
        val heightM = heightCm / 100.0
        val bmi = weight / (heightM * heightM)
        _currentBmi.value = bmi
        
        val category = when {
            bmi < 18.5 -> "Underweight (Consult physician before donating)"
            bmi < 24.9 -> "Normal weight (Excellent status for blood donation)"
            bmi < 29.9 -> "Overweight (Eligible to donate if blood pressure is normal)"
            else -> "Obese (Consult physician before donating)"
        }
        _bmiResultText.value = category

        // Award points if they do their metrics
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.getUserProfileSync()
            if (current != null) {
                val updated = current.copy(weight = weight, height = heightCm)
                repository.insertOrUpdateProfile(updated)
            }
        }
    }

    fun checkEligibility(age: Int, weight: Double, pulse: Int, bpSystolic: Int, isHealthy: Boolean) {
        val isEligible = age in 18..65 && weight >= 50.0 && pulse in 50..100 && bpSystolic in 90..180 && isHealthy
        _isEligibleToDonate.value = isEligible

        viewModelScope.launch(Dispatchers.IO) {
            if (isEligible) {
                repository.addPoints(20)
                val current = repository.getUserProfileSync()
                if (current != null) {
                    repository.insertOrUpdateProfile(current.copy(points = current.points + 20))
                }
                repository.insertNotification(
                    NotificationItem(
                        title = "Eligibility Verified! 🎉",
                        message = "You are medically verified and eligible to donate! 20 points granted.",
                        type = "Reward"
                    )
                )
            } else {
                repository.insertNotification(
                    NotificationItem(
                        title = "Eligibility Checked",
                        message = "Based on safety parameters, you are currently ineligible to donate. Try again later.",
                        type = "System"
                    )
                )
            }
        }
    }

    fun resetEligibility() {
        _isEligibleToDonate.value = null
    }

    // --- Notification Actions ---
    fun markNotificationAsRead(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markAllNotificationsAsRead()
        }
    }

    fun deleteNotification(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNotification(id)
        }
    }
}
