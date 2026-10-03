package com.example

import com.example.data.BloodGroup
import com.example.data.DonorEntity
import com.example.data.LifeLinkRepository
import com.example.domain.AiDonorMatchingEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun bloodGroupCompatibility_isMedicallyAccurate() {
        // Universal donor O- can donate to all 8 groups
        for (recipient in BloodGroup.ALL_LABELS) {
            assertTrue(BloodGroup.isCompatibleDonor("O-", recipient))
        }
        // AB+ can only donate RBCs to AB+
        assertTrue(BloodGroup.isCompatibleDonor("AB+", "AB+"))
        assertFalse(BloodGroup.isCompatibleDonor("AB+", "O+"))
        assertFalse(BloodGroup.isCompatibleDonor("A+", "B+"))
        assertTrue(BloodGroup.isCompatibleDonor("A-", "AB+"))
    }

    @Test
    fun aiDonorMatching_prioritizesNearbyCompatibleAvailableDonors() {
        val donors = listOf(
            DonorEntity(
                id = 1L,
                fullName = "Donor Far Compatible",
                age = 29,
                gender = "Male",
                bloodGroup = "O-",
                location = "Mumbai",
                phoneNumber = "9876500001",
                normalizedPhone = "9876500001",
                email = "far@example.com",
                lastDonationDate = "Never",
                isAvailable = true
            ),
            DonorEntity(
                id = 2L,
                fullName = "Donor Nearby Exact",
                age = 26,
                gender = "Female",
                bloodGroup = "A+",
                location = "Chennai",
                phoneNumber = "9876500002",
                normalizedPhone = "9876500002",
                email = "near@example.com",
                lastDonationDate = "Never",
                isAvailable = true
            ),
            DonorEntity(
                id = 3L,
                fullName = "Donor Incompatible",
                age = 31,
                gender = "Male",
                bloodGroup = "B+",
                location = "Chennai",
                phoneNumber = "9876500003",
                normalizedPhone = "9876500003",
                email = "inc@example.com",
                lastDonationDate = "Never",
                isAvailable = true
            )
        )

        val matches = AiDonorMatchingEngine.matchDonors(
            allRegisteredDonors = donors,
            requiredBloodGroup = "A+",
            patientLocation = "Chennai",
            isEmergency = true
        )

        // Incompatible B+ donor must never be returned for A+ patient
        assertEquals(2, matches.size)
        // Nearby exact match in Chennai must be ranked #1 ahead of Mumbai donor
        assertEquals("Donor Nearby Exact", matches.first().donor.fullName)
        assertTrue(matches.first().aiMatchScore > matches.last().aiMatchScore)
        // Phone number must be privacy masked
        assertEquals("•••• ••• 0002", matches.first().maskedPhone)
    }

    @Test
    fun phoneNormalization_stripsFormattingConsistently() {
        assertEquals("919876543210", LifeLinkRepository.normalizePhoneNumber("+91 987-654-3210"))
    }
}
