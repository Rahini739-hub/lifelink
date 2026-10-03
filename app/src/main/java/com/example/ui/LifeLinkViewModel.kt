package com.example.ui

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BloodGroup
import com.example.data.BloodRequestEntity
import com.example.data.DonorEntity
import com.example.data.DonorNotificationEntity
import com.example.data.LifeLinkRepository
import com.example.data.RegistrationResult
import com.example.domain.AiDonorMatchingEngine
import com.example.domain.DonorMatchResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

enum class LifeLinkTab(val route: String, val title: String) {
    DASHBOARD("dashboard", "Dashboard"),
    REGISTER("register", "Register Donor"),
    MATCHING("matching", "AI Match"),
    EMERGENCY("emergency", "Emergency"),
    NOTIFICATIONS("notifications", "Donor Inbox")
}

data class RegistrationFormState(
    val fullName: String = "",
    val age: String = "",
    val gender: String = "Male",
    val bloodGroup: String = "O+",
    val location: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val lastDonationDate: String = "",
    val neverDonatedBefore: Boolean = true,
    val isAvailable: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isSubmitting: Boolean = false
)

data class MatchingQueryState(
    val patientName: String = "",
    val requiredBloodGroup: String = "O+",
    val patientLocation: String = "",
    val unitsNeeded: String = "1",
    val isEmergency: Boolean = false,
    val hospitalNotes: String = "",
    val requesterPhone: String = "",
    val includeStandbyInEmergency: Boolean = false,
    val hasSearched: Boolean = false,
    val activeRequestId: Long? = null,
    val notifiedDonorIdsForCurrentSearch: Set<Long> = emptySet(),
    val statusBannerMessage: String? = null,
    val errorMessage: String? = null
)

data class DonorLookupState(
    val enteredPhone: String = "",
    val activeLookupPhone: String = "",
    val hasPerformedLookup: Boolean = false,
    val lookupError: String? = null,
    val actionFeedback: String? = null
)

data class DashboardStats(
    val totalRegisteredDonors: Int = 0,
    val availableDonorsCount: Int = 0,
    val activeBloodGroupsCount: Int = 0,
    val totalStandardBloodGroups: Int = 8,
    val bloodGroupDistribution: Map<String, Int> = BloodGroup.ALL_LABELS.associateWith { 0 },
    val availableByBloodGroup: Map<String, Int> = BloodGroup.ALL_LABELS.associateWith { 0 },
    val totalRequestsCount: Int = 0,
    val activeEmergencyRequestsCount: Int = 0,
    val totalNotificationsSent: Int = 0,
    val acceptedNotificationsCount: Int = 0,
    val emergencySystemOnline: Boolean = true
)

class LifeLinkViewModel(
    private val repository: LifeLinkRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(LifeLinkTab.DASHBOARD)
    val selectedTab: StateFlow<LifeLinkTab> = _selectedTab.asStateFlow()

    private val _registrationForm = MutableStateFlow(RegistrationFormState())
    val registrationForm: StateFlow<RegistrationFormState> = _registrationForm.asStateFlow()

    private val _matchingQuery = MutableStateFlow(MatchingQueryState())
    val matchingQuery: StateFlow<MatchingQueryState> = _matchingQuery.asStateFlow()

    private val _donorLookup = MutableStateFlow(DonorLookupState())
    val donorLookup: StateFlow<DonorLookupState> = _donorLookup.asStateFlow()

    val registeredDonors: StateFlow<List<DonorEntity>> = repository.allDonors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBloodRequests: StateFlow<List<BloodRequestEntity>> = repository.allBloodRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<DonorNotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dashboardStats: StateFlow<DashboardStats> = combine(
        registeredDonors,
        allBloodRequests,
        allNotifications
    ) { donors, requests, notifications ->
        val groupCounts = BloodGroup.ALL_LABELS.associateWith { group ->
            donors.count { it.bloodGroup.equals(group, ignoreCase = true) }
        }
        val availableGroupCounts = BloodGroup.ALL_LABELS.associateWith { group ->
            donors.count { it.bloodGroup.equals(group, ignoreCase = true) && it.isAvailable }
        }
        val representedGroupsCount = groupCounts.values.count { it > 0 }
        DashboardStats(
            totalRegisteredDonors = donors.size,
            availableDonorsCount = donors.count { it.isAvailable },
            activeBloodGroupsCount = representedGroupsCount,
            totalStandardBloodGroups = BloodGroup.ALL_LABELS.size,
            bloodGroupDistribution = groupCounts,
            availableByBloodGroup = availableGroupCounts,
            totalRequestsCount = requests.size,
            activeEmergencyRequestsCount = requests.count { it.isEmergency },
            totalNotificationsSent = notifications.size,
            acceptedNotificationsCount = notifications.count {
                it.status == DonorNotificationEntity.STATUS_ACCEPTED
            },
            emergencySystemOnline = true
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    /**
     * Reactive AI Match Results strictly computed from real registered donors.
     */
    val aiMatchResults: StateFlow<List<DonorMatchResult>> = combine(
        registeredDonors,
        _matchingQuery
    ) { donors, query ->
        if (!query.hasSearched || query.patientLocation.isBlank()) {
            emptyList()
        } else {
            AiDonorMatchingEngine.matchDonors(
                allRegisteredDonors = donors,
                requiredBloodGroup = query.requiredBloodGroup,
                patientLocation = query.patientLocation,
                isEmergency = query.isEmergency,
                includeUnavailableInEmergency = query.isEmergency && query.includeStandbyInEmergency
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Strictly scoped Donor Notification Lookup by registered phone number.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val lookedUpDonorProfile: StateFlow<DonorEntity?> = _donorLookup
        .flatMapLatest { state ->
            if (!state.hasPerformedLookup || state.activeLookupPhone.isBlank()) {
                flowOf(null)
            } else {
                repository.observeDonorByPhone(state.activeLookupPhone)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val lookedUpNotifications: StateFlow<List<DonorNotificationEntity>> = _donorLookup
        .flatMapLatest { state ->
            if (!state.hasPerformedLookup || state.activeLookupPhone.isBlank()) {
                flowOf(emptyList())
            } else {
                repository.observeNotificationsForPhone(state.activeLookupPhone)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: LifeLinkTab) {
        _selectedTab.value = tab
        if (tab == LifeLinkTab.EMERGENCY) {
            _matchingQuery.update { it.copy(isEmergency = true, errorMessage = null) }
        }
    }

    fun navigateToEmergencyRequestWithBloodGroup(bloodGroup: String? = null) {
        _matchingQuery.update {
            it.copy(
                isEmergency = true,
                requiredBloodGroup = bloodGroup ?: it.requiredBloodGroup,
                errorMessage = null,
                statusBannerMessage = null
            )
        }
        _selectedTab.value = LifeLinkTab.EMERGENCY
    }

    // --- Donor Registration Handlers ---

    fun updateRegFullName(value: String) {
        _registrationForm.update { it.copy(fullName = value, errorMessage = null, successMessage = null) }
    }

    fun updateRegAge(value: String) {
        val filtered = value.filter { it.isDigit() }.take(3)
        _registrationForm.update { it.copy(age = filtered, errorMessage = null, successMessage = null) }
    }

    fun updateRegGender(value: String) {
        _registrationForm.update { it.copy(gender = value, errorMessage = null, successMessage = null) }
    }

    fun updateRegBloodGroup(value: String) {
        _registrationForm.update { it.copy(bloodGroup = value, errorMessage = null, successMessage = null) }
    }

    fun updateRegLocation(value: String) {
        _registrationForm.update { it.copy(location = value, errorMessage = null, successMessage = null) }
    }

    fun updateRegPhone(value: String) {
        _registrationForm.update { it.copy(phoneNumber = value, errorMessage = null, successMessage = null) }
    }

    fun updateRegEmail(value: String) {
        _registrationForm.update { it.copy(email = value, errorMessage = null, successMessage = null) }
    }

    fun updateRegLastDonationDate(value: String) {
        _registrationForm.update {
            it.copy(
                lastDonationDate = value,
                neverDonatedBefore = value.isBlank(),
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun updateRegNeverDonated(neverDonated: Boolean) {
        _registrationForm.update {
            it.copy(
                neverDonatedBefore = neverDonated,
                lastDonationDate = if (neverDonated) "" else it.lastDonationDate,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun updateRegAvailability(isAvailable: Boolean) {
        _registrationForm.update { it.copy(isAvailable = isAvailable, errorMessage = null, successMessage = null) }
    }

    fun clearRegistrationMessages() {
        _registrationForm.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun submitDonorRegistration() {
        val state = _registrationForm.value
        val fullName = state.fullName.trim()
        val ageInt = state.age.trim().toIntOrNull()
        val location = state.location.trim()
        val phone = state.phoneNumber.trim()
        val normalizedPhone = LifeLinkRepository.normalizePhoneNumber(phone)
        val email = state.email.trim()
        val lastDonation = if (state.neverDonatedBefore || state.lastDonationDate.isBlank()) {
            "Never"
        } else {
            state.lastDonationDate.trim()
        }

        // Strict Registration Validation Rules
        if (fullName.length < 2) {
            _registrationForm.update { it.copy(errorMessage = "Please enter the donor's full legal name (minimum 2 characters).") }
            return
        }
        if (ageInt == null || ageInt !in 18..65) {
            _registrationForm.update { it.copy(errorMessage = "Eligible blood donor age must be between 18 and 65 years.") }
            return
        }
        if (state.bloodGroup !in BloodGroup.ALL_LABELS) {
            _registrationForm.update { it.copy(errorMessage = "Please select a valid ABO/Rh blood group.") }
            return
        }
        if (location.length < 2) {
            _registrationForm.update { it.copy(errorMessage = "Please enter a valid city, locality, or hospital zone.") }
            return
        }
        if (normalizedPhone.length !in 7..15) {
            _registrationForm.update { it.copy(errorMessage = "Please enter a valid phone number (7 to 15 digits).") }
            return
        }
        if (email.isBlank() || !isValidEmail(email)) {
            _registrationForm.update { it.copy(errorMessage = "Please enter a valid email address (e.g. donor@domain.com).") }
            return
        }
        if (lastDonation != "Never" && !isValidDate(lastDonation)) {
            _registrationForm.update { it.copy(errorMessage = "Last donation date must be in YYYY-MM-DD format (e.g. 2026-05-15) or select 'Never donated'.") }
            return
        }

        _registrationForm.update { it.copy(isSubmitting = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            when (
                val result = repository.registerDonor(
                    fullName = fullName,
                    age = ageInt,
                    gender = state.gender,
                    bloodGroup = state.bloodGroup,
                    location = location,
                    phoneNumber = phone,
                    email = email,
                    lastDonationDate = lastDonation,
                    isAvailable = state.isAvailable
                )
            ) {
                is RegistrationResult.Success -> {
                    _registrationForm.update {
                        RegistrationFormState(
                            bloodGroup = state.bloodGroup,
                            gender = state.gender,
                            successMessage = "Donor '$fullName' (${state.bloodGroup}) registered successfully in LifeLink!"
                        )
                    }
                    // Pre-fill lookup phone so the newly registered donor can easily check notifications
                    _donorLookup.update {
                        it.copy(enteredPhone = phone)
                    }
                }
                is RegistrationResult.DuplicatePhone -> {
                    _registrationForm.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = "Duplicate Registration Blocked: A donor with phone number '${result.phone}' is already registered."
                        )
                    }
                }
                is RegistrationResult.ValidationError -> {
                    _registrationForm.update {
                        it.copy(isSubmitting = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    // --- AI Matching & Emergency Dispatch Handlers ---

    fun updateMatchPatientName(value: String) {
        _matchingQuery.update { it.copy(patientName = value, errorMessage = null, statusBannerMessage = null) }
    }

    fun updateMatchBloodGroup(value: String) {
        _matchingQuery.update {
            it.copy(
                requiredBloodGroup = value,
                activeRequestId = null,
                notifiedDonorIdsForCurrentSearch = emptySet(),
                errorMessage = null,
                statusBannerMessage = null
            )
        }
    }

    fun updateMatchLocation(value: String) {
        _matchingQuery.update {
            it.copy(
                patientLocation = value,
                activeRequestId = null,
                notifiedDonorIdsForCurrentSearch = emptySet(),
                errorMessage = null,
                statusBannerMessage = null
            )
        }
    }

    fun updateMatchUnitsNeeded(value: String) {
        val clean = value.filter { it.isDigit() }.take(2)
        _matchingQuery.update { it.copy(unitsNeeded = clean, errorMessage = null) }
    }

    fun updateMatchEmergencyMode(isEmergency: Boolean) {
        _matchingQuery.update {
            it.copy(
                isEmergency = isEmergency,
                errorMessage = null,
                statusBannerMessage = if (isEmergency) {
                    "EMERGENCY MODE ACTIVE: High-priority AI matching & rapid donor notification enabled."
                } else {
                    null
                }
            )
        }
    }

    fun updateMatchHospitalNotes(value: String) {
        _matchingQuery.update { it.copy(hospitalNotes = value, errorMessage = null) }
    }

    fun updateMatchRequesterPhone(value: String) {
        _matchingQuery.update { it.copy(requesterPhone = value, errorMessage = null) }
    }

    fun updateIncludeStandbyInEmergency(include: Boolean) {
        _matchingQuery.update { it.copy(includeStandbyInEmergency = include) }
    }

    fun runAiDonorMatching(autoDispatchIfEmergency: Boolean = false) {
        val state = _matchingQuery.value
        if (state.patientLocation.trim().length < 2) {
            _matchingQuery.update {
                it.copy(errorMessage = "Please enter the patient or hospital location to calculate distance and compatibility.")
            }
            return
        }

        _matchingQuery.update {
            it.copy(
                hasSearched = true,
                errorMessage = null,
                statusBannerMessage = null
            )
        }

        if (autoDispatchIfEmergency || state.isEmergency) {
            dispatchNotificationsToAllMatchedDonors()
        }
    }

    /**
     * Dispatches a blood request notification to a single matched registered donor.
     */
    fun notifySingleMatchedDonor(match: DonorMatchResult) {
        viewModelScope.launch {
            val state = _matchingQuery.value
            val currentMatches = aiMatchResults.value
            val requestId = ensureActiveBloodRequestCreated(state, currentMatches.size)

            repository.dispatchNotificationToDonor(
                requestId = requestId,
                donor = match.donor,
                patientName = state.patientName.trim().ifBlank {
                    if (state.isEmergency) "Emergency Trauma Case" else "Patient Blood Request"
                },
                requiredBloodGroup = state.requiredBloodGroup,
                requestLocation = state.patientLocation.trim(),
                approximateDistanceKm = match.approximateDistanceKm,
                isEmergency = state.isEmergency,
                aiMatchScore = match.aiMatchScore,
                matchingSummary = match.matchingReasons.take(3).joinToString(" • ")
            )

            val updatedSet = _matchingQuery.value.notifiedDonorIdsForCurrentSearch + match.donor.id
            repository.updateRequestNotifiedCount(requestId, updatedSet.size)

            _matchingQuery.update {
                it.copy(
                    activeRequestId = requestId,
                    notifiedDonorIdsForCurrentSearch = updatedSet,
                    statusBannerMessage = "Notification dispatched to ${match.donor.fullName} (${match.donor.bloodGroup}, ${AiDonorMatchingEngine.formatDistance(match.approximateDistanceKm)} km away)."
                )
            }
        }
    }

    /**
     * Creates a BloodRequest record and notifies all compatible nearby matched donors.
     */
    fun dispatchNotificationsToAllMatchedDonors() {
        viewModelScope.launch {
            val state = _matchingQuery.value
            if (state.patientLocation.trim().length < 2) {
                _matchingQuery.update {
                    it.copy(errorMessage = "Please enter the patient or hospital location before dispatching notifications.")
                }
                return@launch
            }

            val matches = AiDonorMatchingEngine.matchDonors(
                allRegisteredDonors = registeredDonors.value,
                requiredBloodGroup = state.requiredBloodGroup,
                patientLocation = state.patientLocation,
                isEmergency = state.isEmergency,
                includeUnavailableInEmergency = state.isEmergency && state.includeStandbyInEmergency
            )

            val requestId = ensureActiveBloodRequestCreated(state, matches.size)

            if (matches.isEmpty()) {
                _matchingQuery.update {
                    it.copy(
                        hasSearched = true,
                        activeRequestId = requestId,
                        statusBannerMessage = if (state.isEmergency) {
                            "Emergency Request Logged (#$requestId). No compatible registered donors currently match ${state.requiredBloodGroup} near ${state.patientLocation.trim()}. Register compatible donors to dispatch."
                        } else {
                            "No compatible registered donors found for ${state.requiredBloodGroup}. Only real registered donors are matched."
                        }
                    )
                }
                return@launch
            }

            val notifiedIds = mutableSetOf<Long>()
            for (match in matches) {
                repository.dispatchNotificationToDonor(
                    requestId = requestId,
                    donor = match.donor,
                    patientName = state.patientName.trim().ifBlank {
                        if (state.isEmergency) "CRITICAL EMERGENCY CASE" else "Patient Blood Request"
                    },
                    requiredBloodGroup = state.requiredBloodGroup,
                    requestLocation = state.patientLocation.trim(),
                    approximateDistanceKm = match.approximateDistanceKm,
                    isEmergency = state.isEmergency,
                    aiMatchScore = match.aiMatchScore,
                    matchingSummary = match.matchingReasons.take(3).joinToString(" • ")
                )
                notifiedIds.add(match.donor.id)
            }

            val allNotified = _matchingQuery.value.notifiedDonorIdsForCurrentSearch + notifiedIds
            repository.updateRequestNotifiedCount(requestId, allNotified.size)

            _matchingQuery.update {
                it.copy(
                    hasSearched = true,
                    activeRequestId = requestId,
                    notifiedDonorIdsForCurrentSearch = allNotified,
                    statusBannerMessage = if (state.isEmergency) {
                        "EMERGENCY DISPATCH COMPLETE: Notified ${notifiedIds.size} compatible registered donor(s) near ${state.patientLocation.trim()}!"
                    } else {
                        "Dispatched blood request notifications to ${notifiedIds.size} compatible registered donor(s)."
                    }
                )
            }
        }
    }

    private suspend fun ensureActiveBloodRequestCreated(
        state: MatchingQueryState,
        matchedCount: Int
    ): Long {
        state.activeRequestId?.let { return it }
        val units = state.unitsNeeded.toIntOrNull()?.coerceIn(1, 20) ?: 1
        val newRequest = BloodRequestEntity(
            patientName = state.patientName.trim().ifBlank {
                if (state.isEmergency) "Emergency Trauma Patient" else "Standard Patient"
            },
            requiredBloodGroup = state.requiredBloodGroup,
            location = state.patientLocation.trim(),
            unitsNeeded = units,
            isEmergency = state.isEmergency,
            hospitalNotes = state.hospitalNotes.trim(),
            requesterPhone = state.requesterPhone.trim(),
            matchedDonorCount = matchedCount,
            notifiedDonorCount = 0
        )
        return repository.createBloodRequest(newRequest)
    }

    // --- Donor Notification Lookup & Response Handlers ---

    fun updateLookupPhoneInput(phone: String) {
        _donorLookup.update {
            it.copy(enteredPhone = phone, lookupError = null, actionFeedback = null)
        }
    }

    fun performDonorNotificationLookup() {
        val raw = _donorLookup.value.enteredPhone.trim()
        val normalized = LifeLinkRepository.normalizePhoneNumber(raw)
        if (normalized.length < 7) {
            _donorLookup.update {
                it.copy(
                    hasPerformedLookup = false,
                    lookupError = "Please enter your registered phone number (at least 7 digits) to view your private notifications."
                )
            }
            return
        }

        viewModelScope.launch {
            val donor = repository.getDonorByPhone(raw)
            if (donor == null) {
                _donorLookup.update {
                    it.copy(
                        activeLookupPhone = normalized,
                        hasPerformedLookup = true,
                        lookupError = "No registered donor found with phone number '$raw'. Please verify your digits or register first."
                    )
                }
            } else {
                _donorLookup.update {
                    it.copy(
                        activeLookupPhone = normalized,
                        hasPerformedLookup = true,
                        lookupError = null,
                        actionFeedback = null
                    )
                }
            }
        }
    }

    fun respondToDonorNotification(notification: DonorNotificationEntity, accept: Boolean) {
        viewModelScope.launch {
            repository.respondToNotification(notification.id, accept)
            val actionWord = if (accept) "ACCEPTED" else "DECLINED"
            _donorLookup.update {
                it.copy(
                    actionFeedback = "You have $actionWord the ${notification.requiredBloodGroup} blood request at ${notification.requestLocation}."
                )
            }
        }
    }

    fun toggleLookedUpDonorAvailability(isAvailable: Boolean) {
        val activePhone = _donorLookup.value.activeLookupPhone
        if (activePhone.isBlank()) return
        viewModelScope.launch {
            repository.updateDonorAvailability(activePhone, isAvailable)
            _donorLookup.update {
                it.copy(
                    actionFeedback = if (isAvailable) {
                        "Your donor status is now set to AVAILABLE for matching."
                    } else {
                        "Your donor status is now set to UNAVAILABLE (Standby)."
                    }
                )
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return try {
            val pattern = Patterns.EMAIL_ADDRESS
            if (pattern != null) {
                pattern.matcher(email).matches()
            } else {
                Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email)
            }
        } catch (_: Exception) {
            Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email)
        }
    }

    private fun isValidDate(dateStr: String): Boolean {
        if (!Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(dateStr)) return false
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                isLenient = false
            }
            val parsed = sdf.parse(dateStr) ?: return false
            parsed.time <= System.currentTimeMillis() + 86_400_000L
        } catch (_: Exception) {
            false
        }
    }

    class Factory(private val repository: LifeLinkRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LifeLinkViewModel::class.java)) {
                return LifeLinkViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
