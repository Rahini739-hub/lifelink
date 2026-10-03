package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
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
import com.example.data.DonorEntity
import com.example.data.DonorNotificationEntity
import com.example.domain.AiDonorMatchingEngine
import com.example.ui.DonorLookupState
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusPillBadge
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

@Composable
fun DonorNotificationsScreen(
    lookupState: DonorLookupState,
    lookedUpDonor: DonorEntity?,
    notifications: List<DonorNotificationEntity>,
    onPhoneInputChange: (String) -> Unit,
    onPerformLookup: () -> Unit,
    onToggleDonorAvailability: (Boolean) -> Unit,
    onRespondToNotification: (DonorNotificationEntity, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextAlabaster,
        unfocusedTextColor = TextAlabaster,
        focusedContainerColor = ObsidianBlack.copy(alpha = 0.78f),
        unfocusedContainerColor = ObsidianBlack.copy(alpha = 0.58f),
        focusedBorderColor = CrimsonBright,
        unfocusedBorderColor = DarkCrimsonBorder,
        focusedLabelColor = RubyAccent,
        unfocusedLabelColor = TextMutedMauve,
        cursorColor = CrimsonBright
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("donor_notifications_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Donor Notification Lookup Header & Phone Search Box
        item {
            GlassCard(isHighlighted = true) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Private Donor Lookup",
                        tint = RubyAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Private Donor Notification Lookup",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextAlabaster
                        )
                        Text(
                            text = "Enter your registered phone number to view your personal blood-request notifications and respond (Accept / Decline). Another donor's notifications are never shown.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMutedMauve
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = lookupState.enteredPhone,
                    onValueChange = onPhoneInputChange,
                    label = { Text("Enter Registered Phone Number *") },
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
                        .testTag("lookup_phone_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onPerformLookup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("lookup_submit_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lookup My Blood Request Notifications",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Error or Action Feedback
        if (lookupState.lookupError != null) {
            item {
                GlassCard(
                    isEmergency = true,
                    modifier = Modifier.testTag("lookup_error_banner")
                ) {
                    Text(
                        text = lookupState.lookupError,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSoftRose
                    )
                }
            }
        }

        if (lookupState.actionFeedback != null) {
            item {
                GlassCard(
                    isHighlighted = true,
                    modifier = Modifier.testTag("lookup_feedback_banner")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MedicalEmerald
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = lookupState.actionFeedback,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextAlabaster
                        )
                    }
                }
            }
        }

        // Verified Donor Profile Summary & Live Availability Switch
        if (lookedUpDonor != null) {
            item {
                GlassCard(
                    modifier = Modifier.testTag("looked_up_donor_profile_card")
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
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(listOf(CrimsonPrimary, DeepBloodRed))
                                    )
                                    .border(1.dp, RubyAccent, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lookedUpDonor.bloodGroup,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = lookedUpDonor.fullName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextAlabaster
                                )
                                Text(
                                    text = "${lookedUpDonor.location} • Phone Verified",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSoftRose
                                )
                                Text(
                                    text = "Last Donation: ${lookedUpDonor.lastDonationDate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedMauve
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (lookedUpDonor.isAvailable) "Available" else "Standby",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (lookedUpDonor.isAvailable) MedicalEmerald else EmergencyAmber
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = lookedUpDonor.isAvailable,
                                onCheckedChange = onToggleDonorAvailability,
                                modifier = Modifier.testTag("donor_profile_availability_switch"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MedicalEmerald,
                                    uncheckedThumbColor = TextSoftRose,
                                    uncheckedTrackColor = DarkCrimsonBorder
                                )
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Received Blood-Request Notifications (${notifications.size})",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextAlabaster
                    )
                }
            }

            if (notifications.isEmpty()) {
                item {
                    GlassCard(modifier = Modifier.testTag("empty_donor_notifications_card")) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = RubyAccent,
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No Blood-Request Notifications Yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextAlabaster
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "When a patient or emergency responder matches with your blood group (${lookedUpDonor.bloodGroup}) and dispatches an alert, it will appear exclusively here.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMutedMauve
                            )
                        }
                    }
                }
            } else {
                items(notifications, key = { it.id }) { notification ->
                    DonorNotificationCard(
                        notification = notification,
                        onAccept = { onRespondToNotification(notification, true) },
                        onDecline = { onRespondToNotification(notification, false) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DonorNotificationCard(
    notification: DonorNotificationEntity,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    GlassCard(
        modifier = Modifier.testTag("donor_notification_card_${notification.id}"),
        isEmergency = notification.isEmergency
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusPillBadge(
                    text = if (notification.isEmergency) "CRITICAL EMERGENCY" else "STANDARD REQUEST",
                    color = if (notification.isEmergency) EmergencyPulseRed else RubyAccent,
                    backgroundColor = if (notification.isEmergency) EmergencyDarkBg else GlassCardElevated
                )
                StatusPillBadge(
                    text = "AI Score: ${notification.aiMatchScore}%",
                    color = MedicalEmerald,
                    backgroundColor = MedicalEmeraldBg
                )
            }

            val statusColor = when (notification.status) {
                DonorNotificationEntity.STATUS_ACCEPTED -> MedicalEmerald
                DonorNotificationEntity.STATUS_DECLINED -> EmergencyAmber
                else -> TextSoftRose
            }
            Text(
                text = notification.status,
                style = MaterialTheme.typography.labelLarge,
                color = statusColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            if (notification.isEmergency) {
                                listOf(EmergencyPulseRed, DeepBloodRed)
                            } else {
                                listOf(CrimsonPrimary, DeepBloodRed)
                            }
                        )
                    )
                    .border(1.dp, RubyAccent, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (notification.isEmergency) Icons.Default.Emergency else Icons.Default.Bloodtype,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = notification.requiredBloodGroup,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Required Blood Group: ${notification.requiredBloodGroup} (${notification.patientName})",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextAlabaster
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = RubyAccent,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Patient Location: ${notification.requestLocation}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSoftRose
                    )
                }
                Text(
                    text = "Approximate Distance: ${AiDonorMatchingEngine.formatDistance(notification.approximateDistanceKm)} km from ${notification.donorLocation}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = RubyAccent
                )
            }
        }

        if (notification.matchingSummary.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianBlack.copy(alpha = 0.6f))
                    .border(1.dp, DarkCrimsonBorder, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Match Details: ${notification.matchingSummary}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedMauve
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Accept and Decline Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val isAccepted = notification.status == DonorNotificationEntity.STATUS_ACCEPTED
            val isDeclined = notification.status == DonorNotificationEntity.STATUS_DECLINED

            Button(
                onClick = onAccept,
                enabled = !isAccepted,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("accept_notification_${notification.id}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedicalEmerald,
                    contentColor = ObsidianBlack,
                    disabledContainerColor = MedicalEmeraldBg,
                    disabledContentColor = MedicalEmerald
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAccepted) "Accepted" else "Accept Request",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            OutlinedButton(
                onClick = onDecline,
                enabled = !isDeclined,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("decline_notification_${notification.id}"),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDeclined) EmergencyAmber else DarkCrimsonBorder
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (isDeclined) EmergencyAmber else TextSoftRose,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isDeclined) "Declined" else "Decline",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isDeclined) EmergencyAmber else TextSoftRose
                )
            }
        }
    }
}
