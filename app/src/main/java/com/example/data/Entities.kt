package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registered_donors",
    indices = [Index(value = ["normalizedPhone"], unique = true)]
)
data class DonorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val age: Int,
    val gender: String,
    val bloodGroup: String,
    val location: String,
    val phoneNumber: String,
    val normalizedPhone: String,
    val email: String,
    val lastDonationDate: String, // YYYY-MM-DD or "Never"
    val isAvailable: Boolean,
    val registeredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "blood_requests")
data class BloodRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientName: String,
    val requiredBloodGroup: String,
    val location: String,
    val unitsNeeded: Int,
    val isEmergency: Boolean,
    val hospitalNotes: String,
    val requesterPhone: String,
    val matchedDonorCount: Int,
    val notifiedDonorCount: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "donor_notifications",
    indices = [Index(value = ["donorNormalizedPhone"]), Index(value = ["requestId"])]
)
data class DonorNotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val requestId: Long,
    val donorId: Long,
    val donorNormalizedPhone: String,
    val donorName: String,
    val patientName: String,
    val requiredBloodGroup: String,
    val donorBloodGroup: String,
    val requestLocation: String,
    val donorLocation: String,
    val approximateDistanceKm: Double,
    val isEmergency: Boolean,
    val aiMatchScore: Int,
    val matchingSummary: String,
    val status: String = STATUS_PENDING, // PENDING, ACCEPTED, DECLINED
    val createdAt: Long = System.currentTimeMillis(),
    val respondedAt: Long? = null
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_ACCEPTED = "ACCEPTED"
        const val STATUS_DECLINED = "DECLINED"
    }
}
