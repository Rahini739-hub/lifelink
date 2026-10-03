package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.LifeLinkDatabase
import com.example.data.LifeLinkRepository
import com.example.data.RegistrationResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("LifeLink", appName)
    }

    @Test
    fun `repository starts with zero fake donors and blocks duplicate phone registration`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, LifeLinkDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repo = LifeLinkRepository(db.lifeLinkDao())

        // Zero fake donors initially
        assertTrue(repo.allDonors.first().isEmpty())

        // First registration succeeds
        val firstResult = repo.registerDonor(
            fullName = "Dr. Kavya Nair",
            age = 28,
            gender = "Female",
            bloodGroup = "O-",
            location = "Chennai",
            phoneNumber = "+91 9876543210",
            email = "kavya@lifelink.org",
            lastDonationDate = "Never",
            isAvailable = true
        )
        assertTrue(firstResult is RegistrationResult.Success)

        // Duplicate registration with same normalized phone is strictly rejected
        val duplicateResult = repo.registerDonor(
            fullName = "Another Name",
            age = 32,
            gender = "Male",
            bloodGroup = "A+",
            location = "Chennai",
            phoneNumber = "91-98765-43210",
            email = "other@lifelink.org",
            lastDonationDate = "Never",
            isAvailable = true
        )
        assertTrue(duplicateResult is RegistrationResult.DuplicatePhone)
        assertEquals(1, repo.allDonors.first().size)

        db.close()
    }
}
