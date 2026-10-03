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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.BloodGroup
import com.example.data.DonorNotificationEntity
import com.example.domain.AiDonorMatchingEngine
import com.example.domain.DonorMatchResult
import com.example.ui.MatchingQueryState
import com.example.ui.components.AiScoreRingBadge
import com.example.ui.components.BloodGroupSelectorGrid
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusPillBadge
import com.example.ui.theme.BrightCrimsonBorder
import com.example.ui.theme.CrimsonBright
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkCrimsonBorder
import com.example.ui.theme.DeepBloodRed
import com.example.ui.theme.EmergencyAmber
import com.example.ui.theme.EmergencyAmberBg
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

private val LOCATION_SUGGESTIONS = listOf("Chennai", "Bangalore", "Mumbai", "Delhi", "Hyderabad", "New York", "London")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiMatchingScreen(
    queryState: MatchingQueryState,
    matchResults: List<DonorMatchResult>,
    totalRegisteredDonorsCount: Int,
    allNotifications: List<DonorNotificationEntity>,
    isDedicatedEmergencyTab: Boolean,
    onPatientNameChange: (String) -> Unit,
    onBloodGroupChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onUnitsChange: (String) -> Unit,
    onEmergencyToggle: (Boolean) -> Unit,
    onHospitalNotesChange: (String) -> Unit,
    onRunMatching: (Boolean) -> Unit,
    onNotifySingleDonor: (DonorMatchResult) -> Unit,
    onNotifyAllMatchedDonors: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val effectiveEmergency = isDedicatedEmergencyTab || queryState.isEmergency
    val compatibleGroups = BloodGroup.getCompatibleDonorGroupsFor(queryState.requiredBloodGroup)

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextAlabaster,
        unfocusedTextColor = TextAlabaster,
        focusedContainerColor = ObsidianBlack.copy(alpha = 0.78f),
        unfocusedContainerColor = ObsidianBlack.copy(alpha = 0.58f),
        focusedBorderColor = if (effectiveEmergency) EmergencyPulseRed else CrimsonBright,
        unfocusedBorderColor = DarkCrimsonBorder,
        focusedLabelColor = RubyAccent,
        unfocusedLabelColor = TextMutedMauve,
        cursorColor = CrimsonBright
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag(if (isDedicatedEmergencyTab) "emergency_mode_screen" else "ai_matching_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Banner
        item {
            GlassCard(
                isEmergency = effectiveEmergency,
                isHighlighted = !effectiveEmergency
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (effectiveEmergency) Icons.Default.Emergency else Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (effectiveEmergency) EmergencyPulseRed else RubyAccent,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (effectiveEmergency) {
                                    "Emergency Blood Request & Rapid Dispatch"
                                } else {
                                    "AI Blood Donor Matching Engine"
                                },
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextAlabaster
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (effectiveEmergency) {
                                "CRITICAL PRIORITY MODE: Elevates emergency requests, prioritizes nearest compatible available donors, and dispatches instant alerts."
                            } else {
                                "Multi-factor AI scoring evaluates ABO/Rh compatibility, distance in km, recovery interval, and donor availability."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSoftRose
                        )
                    }
                }
            }
        }

        // Error or Dispatch Status Banner
        if (queryState.errorMessage != null) {
            item {
                GlassCard(
                    isEmergency = true,
                    modifier = Modifier.testTag("matching_error_banner")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = EmergencyPulseRed
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = queryState.errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSoftRose
                        )
                    }
                }
            }
        }

        if (queryState.statusBannerMessage != null) {
            item {
                GlassCard(
                    isHighlighted = true,
                    modifier = Modifier.testTag("matching_status_banner")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MedicalEmerald
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = queryState.statusBannerMessage,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextAlabaster
                        )
                    }
                }
            }
        }

        // Patient Request Configuration Card
        item {
            GlassCard(isEmergency = effectiveEmergency) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1. Select Required Patient Blood Group *",
                        style = MaterialTheme.typography.titleMedium,
                        color = RubyAccent
                    )
                    StatusPillBadge(
                        text = "Compatible: ${compatibleGroups.joinToString(", ")}",
                        color = MedicalEmerald,
                        backgroundColor = MedicalEmeraldBg
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                BloodGroupSelectorGrid(
                    selectedGroup = queryState.requiredBloodGroup,
                    onSelectGroup = onBloodGroupChange,
                    tagPrefix = "match_blood_group"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "2. Patient Location & Clinical Priority *",
                    style = MaterialTheme.typography.titleMedium,
                    color = RubyAccent
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = queryState.patientLocation,
                    onValueChange = onLocationChange,
                    label = { Text("Patient / Hospital Location *") },
                    placeholder = { Text("e.g. Apollo Hospital, Chennai or Brooklyn, New York") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = RubyAccent)
                    },
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("match_location_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LOCATION_SUGGESTIONS.forEach { loc ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GlassCardElevated)
                                .border(1.dp, DarkCrimsonBorder, RoundedCornerShape(8.dp))
                                .clickable { onLocationChange(loc) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = loc,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSoftRose
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = queryState.patientName,
                        onValueChange = onPatientNameChange,
                        label = { Text("Patient / Case Name") },
                        placeholder = { Text("e.g. ICU Ward 4 Case") },
                        leadingIcon = {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = RubyAccent)
                        },
                        singleLine = true,
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(0.68f)
                            .testTag("match_patient_name_input")
                    )

                    OutlinedTextField(
                        value = queryState.unitsNeeded,
                        onValueChange = onUnitsChange,
                        label = { Text("Units") },
                        placeholder = { Text("2") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(0.32f)
                            .testTag("match_units_input")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = queryState.hospitalNotes,
                    onValueChange = onHospitalNotesChange,
                    label = { Text("Hospital / Urgency Notes (Optional)") },
                    placeholder = { Text("e.g. Emergency surgery at Trauma Center") },
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("match_notes_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Emergency Mode Toggle Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (effectiveEmergency) EmergencyDarkBg else GlassCardElevated)
                        .border(
                            width = if (effectiveEmergency) 1.5.dp else 1.dp,
                            color = if (effectiveEmergency) EmergencyPulseRed else DarkCrimsonBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = null,
                            tint = if (effectiveEmergency) EmergencyPulseRed else RubyAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (effectiveEmergency) {
                                    "EMERGENCY PRIORITY MODE ENABLED"
                                } else {
                                    "Enable Emergency Blood Request Mode"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                color = if (effectiveEmergency) EmergencyPulseRed else TextAlabaster
                            )
                            Text(
                                text = "Highlights request as Critical SOS, boosts priority ranking & notifies nearby compatible donors immediately.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSoftRose
                            )
                        }
                    }
                    Switch(
                        checked = effectiveEmergency,
                        onCheckedChange = onEmergencyToggle,
                        modifier = Modifier.testTag("match_emergency_mode_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = EmergencyPulseRed,
                            uncheckedThumbColor = TextSoftRose,
                            uncheckedTrackColor = DarkCrimsonBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onRunMatching(effectiveEmergency) },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("run_ai_matching_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (effectiveEmergency) EmergencyPulseRed else CrimsonPrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = if (effectiveEmergency) Icons.Default.Emergency else Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (effectiveEmergency) {
                                "Emergency Match & Notify Nearby Donors"
                            } else {
                                "Find Compatible Registered Donors"
                            },
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Results Section
        if (queryState.hasSearched) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Compatible Donor Matches (${matchResults.size})",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextAlabaster
                        )
                        Text(
                            text = "Required: ${queryState.requiredBloodGroup} • Location: ${queryState.patientLocation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSoftRose
                        )
                    }

                    if (matchResults.isNotEmpty()) {
                        Button(
                            onClick = onNotifyAllMatchedDonors,
                            modifier = Modifier.testTag("notify_all_matched_donors_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (effectiveEmergency) EmergencyPulseRed else DeepBloodRed
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Notify All (${matchResults.size})")
                        }
                    }
                }
            }

            if (matchResults.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier.testTag("no_compatible_donors_card"),
                        isEmergency = effectiveEmergency
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bloodtype,
                                contentDescription = null,
                                tint = RubyAccent,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No Compatible Registered Donors Found for ${queryState.requiredBloodGroup}",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextAlabaster
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (totalRegisteredDonorsCount == 0) {
                                    "There are currently 0 registered donors in the database. LifeLink never uses fake or unregistered donors. Please register a compatible donor (${compatibleGroups.joinToString(", ")}) first."
                                } else {
                                    "Out of $totalRegisteredDonorsCount registered donor(s), none are currently available and medically compatible with ${queryState.requiredBloodGroup} (${compatibleGroups.joinToString(", ")})."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMutedMauve
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = onNavigateToRegister,
                                modifier = Modifier.testTag("no_matches_register_button"),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RubyAccent),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    tint = RubyAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Register Compatible Donor", color = TextSoftRose)
                            }
                        }
                    }
                }
            } else {
                items(matchResults, key = { it.donor.id }) { match ->
                    val existingNotification = allNotifications.firstOrNull {
                        it.donorId == match.donor.id &&
                            it.requiredBloodGroup.equals(queryState.requiredBloodGroup, ignoreCase = true) &&
                            it.requestLocation.equals(queryState.patientLocation.trim(), ignoreCase = true)
                    }
                    val isNotified = match.donor.id in queryState.notifiedDonorIdsForCurrentSearch || existingNotification != null

                    AiMatchedDonorCard(
                        match = match,
                        requiredBloodGroup = queryState.requiredBloodGroup,
                        isEmergencyRequest = effectiveEmergency,
                        isNotified = isNotified,
                        notificationStatus = existingNotification?.status,
                        onNotifyDonor = { onNotifySingleDonor(match) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AiMatchedDonorCard(
    match: DonorMatchResult,
    requiredBloodGroup: String,
    isEmergencyRequest: Boolean,
    isNotified: Boolean,
    notificationStatus: String?,
    onNotifyDonor: () -> Unit
) {
    GlassCard(
        modifier = Modifier.testTag("matched_donor_card_${match.donor.id}"),
        isEmergency = isEmergencyRequest && match.isEmergencyPriority,
        isHighlighted = !isEmergencyRequest && match.aiMatchScore >= 85
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                AiScoreRingBadge(score = match.aiMatchScore)

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = match.donor.fullName,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextAlabaster
                        )
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Registered Donor",
                            tint = MedicalEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(listOf(CrimsonPrimary, DeepBloodRed))
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${match.donor.bloodGroup} → $requiredBloodGroup",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                        }

                        StatusPillBadge(
                            text = "${AiDonorMatchingEngine.formatDistance(match.approximateDistanceKm)} km away",
                            color = RubyAccent,
                            backgroundColor = GlassCardElevated
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Location: ${match.donor.location} • ${match.donor.gender}, ${match.donor.age} yrs",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSoftRose
                    )
                    Text(
                        text = "Privacy Masked: ${match.maskedPhone} • ${match.maskedEmail}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedMauve
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // AI Score Sub-Factor Breakdown Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianBlack.copy(alpha = 0.65f))
                .border(1.dp, DarkCrimsonBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ScoreFactorItem(
                label = "Blood Match",
                value = "${match.compatibilityScore}/35",
                color = CrimsonBright
            )
            ScoreFactorItem(
                label = "Proximity",
                value = "${match.proximityScore}/35",
                color = RubyAccent
            )
            ScoreFactorItem(
                label = "Readiness",
                value = "${match.readinessScore}/30",
                color = MedicalEmerald
            )
            ScoreFactorItem(
                label = "Status",
                value = if (match.donor.isAvailable) "AVAILABLE" else "STANDBY",
                color = if (match.donor.isAvailable) MedicalEmerald else EmergencyAmber
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // AI Matching Reasons
        Text(
            text = "AI Matching Reasons & Clinical Assessment:",
            style = MaterialTheme.typography.labelLarge,
            color = TextSoftRose
        )
        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            match.matchingReasons.forEach { reason ->
                val isCriticalReason = reason.startsWith("CRITICAL")
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isCriticalReason) EmergencyDarkBg else GlassCardElevated
                        )
                        .border(
                            1.dp,
                            if (isCriticalReason) EmergencyPulseRed else BrightCrimsonBorder.copy(alpha = 0.6f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "✓ $reason",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isCriticalReason) EmergencyPulseRed else TextAlabaster
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Dispatch Notification Action or Live Donor Response Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (notificationStatus == DonorNotificationEntity.STATUS_ACCEPTED) {
                StatusPillBadge(
                    text = "DONOR ACCEPTED REQUEST",
                    color = MedicalEmerald,
                    backgroundColor = MedicalEmeraldBg
                )
            } else if (notificationStatus == DonorNotificationEntity.STATUS_DECLINED) {
                StatusPillBadge(
                    text = "DONOR DECLINED",
                    color = EmergencyAmber,
                    backgroundColor = EmergencyAmberBg
                )
            } else if (isNotified) {
                StatusPillBadge(
                    text = "NOTIFICATION SENT • AWAITING RESPONSE",
                    color = RubyAccent,
                    backgroundColor = GlassCardElevated
                )
            } else {
                Text(
                    text = "Direct notification protects donor privacy",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedMauve
                )
            }

            Button(
                onClick = onNotifyDonor,
                enabled = !isNotified,
                modifier = Modifier.testTag("notify_donor_button_${match.donor.id}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEmergencyRequest) EmergencyPulseRed else CrimsonPrimary,
                    disabledContainerColor = GlassCardElevated
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = if (isNotified) Icons.Default.CheckCircle else Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isNotified) "Notified" else "Notify Donor"
                )
            }
        }
    }
}

@Composable
private fun ScoreFactorItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedMauve
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
    }
}
