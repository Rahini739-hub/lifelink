package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LifeLinkDao {
    @Query("SELECT * FROM registered_donors ORDER BY registeredAt DESC")
    fun observeAllDonors(): Flow<List<DonorEntity>>

    @Query("SELECT * FROM registered_donors WHERE normalizedPhone = :normalizedPhone LIMIT 1")
    suspend fun getDonorByNormalizedPhone(normalizedPhone: String): DonorEntity?

    @Query("SELECT * FROM registered_donors WHERE normalizedPhone = :normalizedPhone LIMIT 1")
    fun observeDonorByNormalizedPhone(normalizedPhone: String): Flow<DonorEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDonor(donor: DonorEntity): Long

    @Query("UPDATE registered_donors SET isAvailable = :isAvailable WHERE normalizedPhone = :normalizedPhone")
    suspend fun updateDonorAvailability(normalizedPhone: String, isAvailable: Boolean)

    @Query("UPDATE registered_donors SET lastDonationDate = :lastDonationDate, isAvailable = :isAvailable WHERE normalizedPhone = :normalizedPhone")
    suspend fun updateDonorDonationStatus(normalizedPhone: String, lastDonationDate: String, isAvailable: Boolean)

    @Query("SELECT * FROM blood_requests ORDER BY isEmergency DESC, createdAt DESC")
    fun observeAllBloodRequests(): Flow<List<BloodRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBloodRequest(request: BloodRequestEntity): Long

    @Query("UPDATE blood_requests SET notifiedDonorCount = :notifiedCount WHERE id = :requestId")
    suspend fun updateRequestNotifiedCount(requestId: Long, notifiedCount: Int)

    @Query("SELECT * FROM donor_notifications WHERE donorNormalizedPhone = :normalizedPhone ORDER BY isEmergency DESC, createdAt DESC")
    fun observeNotificationsForPhone(normalizedPhone: String): Flow<List<DonorNotificationEntity>>

    @Query("SELECT * FROM donor_notifications ORDER BY isEmergency DESC, createdAt DESC")
    fun observeAllNotifications(): Flow<List<DonorNotificationEntity>>

    @Query("SELECT * FROM donor_notifications WHERE requestId = :requestId AND donorId = :donorId LIMIT 1")
    suspend fun getExistingNotification(requestId: Long, donorId: Long): DonorNotificationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: DonorNotificationEntity): Long

    @Query("UPDATE donor_notifications SET status = :status, respondedAt = :respondedAt WHERE id = :notificationId")
    suspend fun updateNotificationStatus(notificationId: Long, status: String, respondedAt: Long)
}
