package com.example.data

import kotlinx.coroutines.flow.Flow

/**
 * Modular Remote Gateway contract so LifeLink can seamlessly connect to a cloud
 * REST/GraphQL API and FCM Push Notification service without altering ViewModel/UI layers.
 */
interface RemoteSyncGateway {
    suspend fun onDonorRegistered(donor: DonorEntity)
    suspend fun onEmergencyRequestCreated(request: BloodRequestEntity, notifications: List<DonorNotificationEntity>)
    suspend fun onNotificationDispatched(notification: DonorNotificationEntity)
    suspend fun onNotificationResponded(notificationId: Long, status: String)
}

class DefaultRemoteSyncGateway : RemoteSyncGateway {
    override suspend fun onDonorRegistered(donor: DonorEntity) {
        // Hook ready for remote REST/Cloud database sync
    }

    override suspend fun onEmergencyRequestCreated(
        request: BloodRequestEntity,
        notifications: List<DonorNotificationEntity>
    ) {
        // Hook ready for remote emergency dispatch & push notification gateway (FCM/SMS)
    }

    override suspend fun onNotificationDispatched(notification: DonorNotificationEntity) {
        // Hook ready for real-time push notification delivery
    }

    override suspend fun onNotificationResponded(notificationId: Long, status: String) {
        // Hook ready for remote status acknowledgement
    }
}

sealed class RegistrationResult {
    data class Success(val donorId: Long) : RegistrationResult()
    data class DuplicatePhone(val phone: String) : RegistrationResult()
    data class ValidationError(val message: String) : RegistrationResult()
}

class LifeLinkRepository(
    private val dao: LifeLinkDao,
    private val remoteGateway: RemoteSyncGateway = DefaultRemoteSyncGateway()
) {
    val allDonors: Flow<List<DonorEntity>> = dao.observeAllDonors()
    val allBloodRequests: Flow<List<BloodRequestEntity>> = dao.observeAllBloodRequests()
    val allNotifications: Flow<List<DonorNotificationEntity>> = dao.observeAllNotifications()

    fun observeNotificationsForPhone(rawPhone: String): Flow<List<DonorNotificationEntity>> {
        val normalized = normalizePhoneNumber(rawPhone)
        return dao.observeNotificationsForPhone(normalized)
    }

    fun observeDonorByPhone(rawPhone: String): Flow<DonorEntity?> {
        val normalized = normalizePhoneNumber(rawPhone)
        return dao.observeDonorByNormalizedPhone(normalized)
    }

    suspend fun getDonorByPhone(rawPhone: String): DonorEntity? {
        val normalized = normalizePhoneNumber(rawPhone)
        if (normalized.isBlank()) return null
        return dao.getDonorByNormalizedPhone(normalized)
    }

    suspend fun registerDonor(
        fullName: String,
        age: Int,
        gender: String,
        bloodGroup: String,
        location: String,
        phoneNumber: String,
        email: String,
        lastDonationDate: String,
        isAvailable: Boolean
    ): RegistrationResult {
        val normalizedPhone = normalizePhoneNumber(phoneNumber)
        if (normalizedPhone.length < 7) {
            return RegistrationResult.ValidationError("Please enter a valid phone number (at least 7 digits).")
        }

        val existing = dao.getDonorByNormalizedPhone(normalizedPhone)
        if (existing != null) {
            return RegistrationResult.DuplicatePhone(phoneNumber.trim())
        }

        val entity = DonorEntity(
            fullName = fullName.trim(),
            age = age,
            gender = gender.trim(),
            bloodGroup = bloodGroup.trim().uppercase(),
            location = location.trim(),
            phoneNumber = phoneNumber.trim(),
            normalizedPhone = normalizedPhone,
            email = email.trim(),
            lastDonationDate = lastDonationDate.trim().ifBlank { "Never" },
            isAvailable = isAvailable,
            registeredAt = System.currentTimeMillis()
        )

        val id = dao.insertDonor(entity)
        remoteGateway.onDonorRegistered(entity.copy(id = id))
        return RegistrationResult.Success(id)
    }

    suspend fun updateDonorAvailability(rawPhone: String, isAvailable: Boolean) {
        val normalized = normalizePhoneNumber(rawPhone)
        if (normalized.isNotBlank()) {
            dao.updateDonorAvailability(normalized, isAvailable)
        }
    }

    suspend fun createBloodRequest(request: BloodRequestEntity): Long {
        return dao.insertBloodRequest(request)
    }

    suspend fun dispatchNotificationToDonor(
        requestId: Long,
        donor: DonorEntity,
        patientName: String,
        requiredBloodGroup: String,
        requestLocation: String,
        approximateDistanceKm: Double,
        isEmergency: Boolean,
        aiMatchScore: Int,
        matchingSummary: String
    ): Long {
        val existing = dao.getExistingNotification(requestId, donor.id)
        if (existing != null) {
            return existing.id
        }
        val notification = DonorNotificationEntity(
            requestId = requestId,
            donorId = donor.id,
            donorNormalizedPhone = donor.normalizedPhone,
            donorName = donor.fullName,
            patientName = patientName.ifBlank { "Emergency Patient" },
            requiredBloodGroup = requiredBloodGroup,
            donorBloodGroup = donor.bloodGroup,
            requestLocation = requestLocation,
            donorLocation = donor.location,
            approximateDistanceKm = approximateDistanceKm,
            isEmergency = isEmergency,
            aiMatchScore = aiMatchScore,
            matchingSummary = matchingSummary,
            status = DonorNotificationEntity.STATUS_PENDING
        )
        val notifId = dao.insertNotification(notification)
        remoteGateway.onNotificationDispatched(notification.copy(id = notifId))
        return notifId
    }

    suspend fun updateRequestNotifiedCount(requestId: Long, notifiedCount: Int) {
        dao.updateRequestNotifiedCount(requestId, notifiedCount)
    }

    suspend fun respondToNotification(notificationId: Long, accept: Boolean) {
        val status = if (accept) {
            DonorNotificationEntity.STATUS_ACCEPTED
        } else {
            DonorNotificationEntity.STATUS_DECLINED
        }
        dao.updateNotificationStatus(notificationId, status, System.currentTimeMillis())
        remoteGateway.onNotificationResponded(notificationId, status)
    }

    companion object {
        /**
         * Normalizes a phone number to digits only (stripping spaces, dashes, parentheses, plus)
         * so duplicate checks and donor notification lookups are reliable and consistent.
         */
        fun normalizePhoneNumber(raw: String): String {
            val digits = raw.filter { it.isDigit() }
            // If a 10+ digit number has country code prefix variations, keep full digits
            // while ensuring exact consistency for lookup & duplicate prevention.
            return digits
        }
    }
}
