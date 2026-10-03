package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.RegistrationFormState
import com.example.ui.components.BloodGroupSelectorGrid
import com.example.ui.components.GlassCard
import com.example.ui.components.SecurityComplianceFooter
import com.example.ui.components.StatusPillBadge
import com.example.ui.theme.CrimsonBright
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkCrimsonBorder
import com.example.ui.theme.EmergencyDarkBg
import com.example.ui.theme.EmergencyPulseRed
import com.example.ui.theme.GlassCardElevated
import com.example.ui.theme.MedicalEmerald
import com.example.ui.theme.MedicalEmeraldBg
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.RubyAccent
import com.example.ui.theme.TextAlabaster
import com.example.ui.theme.TextMutedMauve
import com.example.ui.theme.TextSoftRose

private val GENDER_OPTIONS = listOf("Male", "Female", "Other")
private val QUICK_CITIES = listOf("Chennai", "Bangalore", "Mumbai", "Delhi", "Hyderabad", "New York", "London")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DonorRegistrationScreen(
    formState: RegistrationFormState,
    onFullNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onBloodGroupChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onLastDonationDateChange: (String) -> Unit,
    onNeverDonatedChange: (Boolean) -> Unit,
    onAvailabilityChange: (Boolean) -> Unit,
    onSubmitRegistration: () -> Unit,
    onGoToMatching: () -> Unit,
    onGoToDonorLookup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextAlabaster,
        unfocusedTextColor = TextAlabaster,
        focusedContainerColor = ObsidianBlack.copy(alpha = 0.75f),
        unfocusedContainerColor = ObsidianBlack.copy(alpha = 0.55f),
        focusedBorderColor = CrimsonBright,
        unfocusedBorderColor = DarkCrimsonBorder,
        focusedLabelColor = RubyAccent,
        unfocusedLabelColor = TextMutedMauve,
        cursorColor = CrimsonBright
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("donor_registration_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassCard(isHighlighted = true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Verified Blood Donor Registration",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextAlabaster
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Register real donor credentials into the LifeLink medical registry. Duplicate phone numbers are strictly prevented.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMutedMauve
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusPillBadge(
                        text = "REAL DONORS ONLY",
                        color = RubyAccent,
                        backgroundColor = GlassCardElevated
                    )
                }
            }
        }

        // Validation / Duplicate Error or Success Feedback Banner
        if (formState.errorMessage != null) {
            item {
                GlassCard(
                    isEmergency = true,
                    modifier = Modifier.testTag("registration_error_banner")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Registration Error",
                            tint = EmergencyPulseRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = formState.errorMessage,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextSoftRose
                        )
                    }
                }
            }
        }

        if (formState.successMessage != null) {
            item {
                GlassCard(
                    modifier = Modifier.testTag("registration_success_banner"),
                    isHighlighted = true
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Registration Successful",
                            tint = MedicalEmerald,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Registration Complete",
                                style = MaterialTheme.typography.titleMedium,
                                color = MedicalEmerald
                            )
                            Text(
                                text = formState.successMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextAlabaster
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onGoToMatching,
                            modifier = Modifier.testTag("post_reg_go_matching_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Test AI Matching")
                        }
                        OutlinedButton(
                            onClick = onGoToDonorLookup,
                            modifier = Modifier.testTag("post_reg_go_lookup_button"),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RubyAccent),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Open Donor Inbox", color = TextSoftRose)
                        }
                    }
                }
            }
        }

        // Main Registration Form Card
        item {
            GlassCard {
                Text(
                    text = "1. Personal & Biometric Profile",
                    style = MaterialTheme.typography.titleMedium,
                    color = RubyAccent
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = formState.fullName,
                    onValueChange = onFullNameChange,
                    label = { Text("Full Name *") },
                    placeholder = { Text("e.g. Dr. Arjun Mehta") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = RubyAccent)
                    },
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_full_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = formState.age,
                        onValueChange = onAgeChange,
                        label = { Text("Age (18–65) *") },
                        placeholder = { Text("28") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(0.4f)
                            .testTag("reg_age_input")
                    )

                    Column(modifier = Modifier.weight(0.6f)) {
                        Text(
                            text = "Gender *",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextMutedMauve
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            GENDER_OPTIONS.forEach { gender ->
                                val selected = formState.gender.equals(gender, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("reg_gender_$gender")
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (selected) CrimsonPrimary else GlassCardElevated)
                                        .border(
                                            1.dp,
                                            if (selected) RubyAccent else DarkCrimsonBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { onGenderChange(gender) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = gender,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (selected) Color.White else TextAlabaster
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "2. ABO / Rh Blood Group *",
                    style = MaterialTheme.typography.titleMedium,
                    color = RubyAccent
                )
                Spacer(modifier = Modifier.height(10.dp))

                BloodGroupSelectorGrid(
                    selectedGroup = formState.bloodGroup,
                    onSelectGroup = onBloodGroupChange,
                    tagPrefix = "reg_blood_group"
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "3. Location & Contact Verification",
                    style = MaterialTheme.typography.titleMedium,
                    color = RubyAccent
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = formState.location,
                    onValueChange = onLocationChange,
                    label = { Text("City / Area / Hospital Zone *") },
                    placeholder = { Text("e.g. Adyar, Chennai or Brooklyn, New York") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = RubyAccent)
                    },
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_location_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QUICK_CITIES.forEach { city ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GlassCardElevated)
                                .border(1.dp, DarkCrimsonBorder, RoundedCornerShape(8.dp))
                                .clickable { onLocationChange(city) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = city,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSoftRose
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = formState.phoneNumber,
                    onValueChange = onPhoneChange,
                    label = { Text("Phone Number (Unique Donor ID) *") },
                    placeholder = { Text("e.g. +91 9876543210 or 5552348910") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = RubyAccent)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_phone_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = formState.email,
                    onValueChange = onEmailChange,
                    label = { Text("Email Address *") },
                    placeholder = { Text("e.g. arjun.mehta@hospital.org") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = RubyAccent)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_email_input")
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "4. Donation History & Availability",
                    style = MaterialTheme.typography.titleMedium,
                    color = RubyAccent
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GlassCardElevated.copy(alpha = 0.6f))
                        .clickable { onNeverDonatedChange(!formState.neverDonatedBefore) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = formState.neverDonatedBefore,
                        onCheckedChange = onNeverDonatedChange,
                        modifier = Modifier.testTag("reg_never_donated_checkbox"),
                        colors = CheckboxDefaults.colors(
                            checkedColor = CrimsonPrimary,
                            uncheckedColor = RubyAccent
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "First-time donor / No prior donation date",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextAlabaster
                    )
                }

                if (!formState.neverDonatedBefore) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = formState.lastDonationDate,
                        onValueChange = onLastDonationDateChange,
                        label = { Text("Last Donation Date (YYYY-MM-DD)") },
                        placeholder = { Text("e.g. 2026-04-10") },
                        leadingIcon = {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = RubyAccent)
                        },
                        singleLine = true,
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_last_donation_input")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (formState.isAvailable) MedicalEmeraldBg else EmergencyDarkBg)
                        .border(
                            1.dp,
                            if (formState.isAvailable) MedicalEmerald else DarkCrimsonBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (formState.isAvailable) {
                                "Available for Blood Donation"
                            } else {
                                "Currently Unavailable (Standby)"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = if (formState.isAvailable) MedicalEmerald else TextSoftRose
                        )
                        Text(
                            text = "Controls whether AI Matching includes you in active donor dispatches",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedMauve
                        )
                    }
                    Switch(
                        checked = formState.isAvailable,
                        onCheckedChange = onAvailabilityChange,
                        modifier = Modifier.testTag("reg_availability_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MedicalEmerald,
                            uncheckedThumbColor = TextSoftRose,
                            uncheckedTrackColor = DarkCrimsonBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onSubmitRegistration,
                    enabled = !formState.isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("submit_donor_registration_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (formState.isSubmitting) "Registering Donor..." else "Register Verified Blood Donor",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        item {
            SecurityComplianceFooter()
        }
    }
}
