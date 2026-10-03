package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.BloodGroup
import com.example.data.DonorEntity
import com.example.domain.AiDonorMatchingEngine
import com.example.ui.DashboardStats
import com.example.ui.LifeLinkTab
import com.example.ui.components.GlassCard
import com.example.ui.components.SecurityComplianceFooter
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    stats: DashboardStats,
    registeredDonors: List<DonorEntity>,
    onNavigateTab: (LifeLinkTab) -> Unit,
    onQuickBloodGroupMatch: (String) -> Unit,
    onQuickEmergencyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. LifeLink Hero Medical Command Banner
        item {
            HeroCommandBanner(
                onRegisterClick = { onNavigateTab(LifeLinkTab.REGISTER) },
                onMatchClick = { onNavigateTab(LifeLinkTab.MATCHING) },
                onEmergencyClick = onQuickEmergencyClick,
                onInboxLookupClick = { onNavigateTab(LifeLinkTab.NOTIFICATIONS) }
            )
        }

        // 2. Live Dashboard Statistics (Connected strictly to real registered donor data)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Live Telemetry & Registry Statistics",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextAlabaster
                    )
                    StatusPillBadge(
                        text = "REAL-TIME DB",
                        color = MedicalEmerald,
                        backgroundColor = MedicalEmeraldBg
                    )
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    maxItemsInEachRow = 3
                ) {
                    StatMetricCard(
                        title = "Registered Donors",
                        primaryValue = stats.totalRegisteredDonors.toString(),
                        subtitle = "${stats.availableDonorsCount} Available Now",
                        icon = Icons.Default.VerifiedUser,
                        accentColor = CrimsonBright,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_registered_donors")
                    )

                    StatMetricCard(
                        title = "Blood Groups Supported",
                        primaryValue = "${stats.totalStandardBloodGroups} ABO/Rh",
                        subtitle = "${stats.activeBloodGroupsCount}/8 Active in Registry",
                        icon = Icons.Default.Bloodtype,
                        accentColor = RubyAccent,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_blood_groups_supported")
                    )

                    StatMetricCard(
                        title = "Emergency Support",
                        primaryValue = if (stats.emergencySystemOnline) "24/7 ACTIVE" else "STANDBY",
                        subtitle = "${stats.activeEmergencyRequestsCount} SOS • ${stats.acceptedNotificationsCount} Accepted",
                        icon = Icons.Default.Emergency,
                        accentColor = MedicalEmerald,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_emergency_status")
                    )
                }
            }
        }

        // 3. Live Blood Group Matrix across all 8 ABO/Rh Types
        item {
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Blood Group Compatibility & Inventory Matrix",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextAlabaster
                        )
                        Text(
                            text = "Tap any blood group to launch AI Compatibility Matching",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedMauve
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    maxItemsInEachRow = 4
                ) {
                    BloodGroup.ALL_LABELS.forEach { group ->
                        val totalForGroup = stats.bloodGroupDistribution[group] ?: 0
                        val availForGroup = stats.availableByBloodGroup[group] ?: 0
                        val hasAvailable = availForGroup > 0

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (hasAvailable) {
                                        Brush.verticalGradient(
                                            listOf(DeepBloodRed.copy(alpha = 0.55f), GlassCardElevated)
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            listOf(GlassCardElevated, ObsidianBlack)
                                        )
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (hasAvailable) CrimsonBright else DarkCrimsonBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onQuickBloodGroupMatch(group) }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = group,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    color = if (hasAvailable) CrimsonBright else TextSoftRose
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$totalForGroup Reg",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextAlabaster
                                )
                                Text(
                                    text = "$availForGroup Avail",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (hasAvailable) MedicalEmerald else TextMutedMauve
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Verified Real Registered Donors Feed (Privacy Masked)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Verified Registered Donors (${registeredDonors.size})",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextAlabaster
                    )
                    Text(
                        text = "Zero-mock registry • Contact information masked for privacy",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedMauve
                    )
                }

                OutlinedButton(
                    onClick = { onNavigateTab(LifeLinkTab.REGISTER) },
                    modifier = Modifier.testTag("dashboard_add_donor_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RubyAccent),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrightCrimsonBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Donor")
                }
            }
        }

        if (registeredDonors.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.testTag("empty_donors_card"),
                    isHighlighted = false
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(DeepBloodRed.copy(alpha = 0.4f))
                                .border(1.dp, CrimsonBright, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "No Fake Donors",
                                tint = RubyAccent,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Registered Donors Yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextAlabaster
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "LifeLink strictly stores and matches ONLY real registered blood donors. No fake or sample donor records are pre-loaded. Register a donor to begin AI matching and emergency dispatch.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMutedMauve
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { onNavigateTab(LifeLinkTab.REGISTER) },
                            modifier = Modifier.testTag("empty_state_register_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Register First Real Donor")
                        }
                    }
                }
            }
        } else {
            items(registeredDonors, key = { it.id }) { donor ->
                RegisteredDonorDirectoryCard(donor = donor)
            }
        }

        item {
            SecurityComplianceFooter()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HeroCommandBanner(
    onRegisterClick: () -> Unit,
    onMatchClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onInboxLookupClick: () -> Unit
) {
    GlassCard(
        isHighlighted = true,
        cornerRadius = 24.dp,
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_medical),
                contentDescription = "LifeLink Medical Command Center",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ObsidianBlack.copy(alpha = 0.72f),
                                ObsidianBlack.copy(alpha = 0.88f),
                                ObsidianBlack.copy(alpha = 0.97f)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusPillBadge(
                            text = "LIFELINK MEDICAL AI",
                            color = RubyAccent,
                            backgroundColor = DeepBloodRed.copy(alpha = 0.65f)
                        )
                        StatusPillBadge(
                            text = "EMERGENCY READY",
                            color = EmergencyPulseRed,
                            backgroundColor = EmergencyDarkBg
                        )
                    }

                    Text(
                        text = "LifeLink",
                        style = MaterialTheme.typography.displaySmall,
                        color = TextAlabaster
                    )

                    Text(
                        text = "AI Based Blood Donor Matching and Emergency Support System",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSoftRose
                    )

                    Text(
                        text = "Precision ABO/Rh compatibility scoring, real-time proximity prioritization, rapid emergency dispatch, and private donor notification lookup.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMutedMauve
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onRegisterClick,
                            modifier = Modifier.testTag("hero_register_donor_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Register Donor")
                        }

                        Button(
                            onClick = onMatchClick,
                            modifier = Modifier.testTag("hero_ai_match_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = GlassCardElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RubyAccent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonSearch,
                                contentDescription = null,
                                tint = RubyAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AI Blood Match", color = TextAlabaster)
                        }

                        Button(
                            onClick = onEmergencyClick,
                            modifier = Modifier.testTag("hero_emergency_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyDarkBg),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmergencyPulseRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Emergency,
                                contentDescription = null,
                                tint = EmergencyPulseRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Emergency Request", color = TextSoftRose)
                        }

                        OutlinedButton(
                            onClick = onInboxLookupClick,
                            modifier = Modifier.testTag("hero_donor_lookup_button"),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCrimsonBorder),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = TextSoftRose,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Donor Notification Lookup", color = TextSoftRose)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    primaryValue: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        cornerRadius = 18.dp,
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = TextMutedMauve,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = primaryValue,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            color = TextAlabaster
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = accentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RegisteredDonorDirectoryCard(donor: DonorEntity) {
    GlassCard(
        modifier = Modifier.testTag("donor_directory_card_${donor.id}"),
        cornerRadius = 16.dp,
        contentPadding = PaddingValues(14.dp)
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
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(listOf(CrimsonPrimary, DeepBloodRed))
                        )
                        .border(1.dp, RubyAccent, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = donor.bloodGroup,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = donor.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextAlabaster
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Registered Donor",
                            tint = MedicalEmerald,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = RubyAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${donor.location} • ${donor.gender}, ${donor.age} yrs",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSoftRose
                        )
                    }
                    Text(
                        text = "Phone: ${AiDonorMatchingEngine.maskPhoneNumber(donor.phoneNumber)} • Last Donation: ${donor.lastDonationDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedMauve
                    )
                }
            }

            StatusPillBadge(
                text = if (donor.isAvailable) "AVAILABLE" else "UNAVAILABLE",
                color = if (donor.isAvailable) MedicalEmerald else EmergencyAmber,
                backgroundColor = if (donor.isAvailable) MedicalEmeraldBg else EmergencyAmberBg
            )
        }
    }
}
