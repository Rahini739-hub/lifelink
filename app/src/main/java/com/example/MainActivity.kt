package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Emergency
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.LifeLinkDatabase
import com.example.data.LifeLinkRepository
import com.example.ui.LifeLinkTab
import com.example.ui.LifeLinkViewModel
import com.example.ui.components.LifeLinkTopBar
import com.example.ui.screens.AiMatchingScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DonorNotificationsScreen
import com.example.ui.screens.DonorRegistrationScreen
import com.example.ui.theme.CrimsonBright
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CrimsonVoid
import com.example.ui.theme.DarkCrimsonBorder
import com.example.ui.theme.DeepBloodRed
import com.example.ui.theme.EmergencyPulseRed
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.TextAlabaster
import com.example.ui.theme.TextMutedMauve
import com.example.ui.theme.TextSoftRose

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(ObsidianBlack.toArgb())
        )
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val repository = remember(context) {
                    val database = LifeLinkDatabase.getInstance(context)
                    LifeLinkRepository(database.lifeLinkDao())
                }
                val lifeLinkViewModel: LifeLinkViewModel = viewModel(
                    factory = LifeLinkViewModel.Factory(repository)
                )
                LifeLinkApp(viewModel = lifeLinkViewModel)
            }
        }
    }
}

@Composable
fun LifeLinkApp(viewModel: LifeLinkViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val registeredDonors by viewModel.registeredDonors.collectAsStateWithLifecycle()
    val registrationForm by viewModel.registrationForm.collectAsStateWithLifecycle()
    val matchingQuery by viewModel.matchingQuery.collectAsStateWithLifecycle()
    val matchResults by viewModel.aiMatchResults.collectAsStateWithLifecycle()
    val allNotifications by viewModel.allNotifications.collectAsStateWithLifecycle()
    val donorLookup by viewModel.donorLookup.collectAsStateWithLifecycle()
    val lookedUpDonor by viewModel.lookedUpDonorProfile.collectAsStateWithLifecycle()
    val lookedUpNotifications by viewModel.lookedUpNotifications.collectAsStateWithLifecycle()

    // Handle system Back press on secondary tabs to return to Dashboard
    BackHandler(enabled = selectedTab != LifeLinkTab.DASHBOARD) {
        viewModel.selectTab(LifeLinkTab.DASHBOARD)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ObsidianBlack, CrimsonVoid, ObsidianBlack)
                )
            )
    ) {
        val isExpandedScreen = maxWidth >= 700.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                LifeLinkTopBar(
                    emergencyCount = stats.activeEmergencyRequestsCount,
                    onEmergencyQuickClick = {
                        viewModel.navigateToEmergencyRequestWithBloodGroup()
                    },
                    modifier = Modifier.statusBarsPadding()
                )
            },
            bottomBar = {
                if (!isExpandedScreen) {
                    LifeLinkBottomNavigationBar(
                        selectedTab = selectedTab,
                        totalNotificationsCount = stats.totalNotificationsSent,
                        onSelectTab = viewModel::selectTab
                    )
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isExpandedScreen) {
                    LifeLinkNavigationRail(
                        selectedTab = selectedTab,
                        totalNotificationsCount = stats.totalNotificationsSent,
                        onSelectTab = viewModel::selectTab
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val contentModifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 960.dp)

                    when (selectedTab) {
                        LifeLinkTab.DASHBOARD -> {
                            DashboardScreen(
                                stats = stats,
                                registeredDonors = registeredDonors,
                                onNavigateTab = viewModel::selectTab,
                                onQuickBloodGroupMatch = { group ->
                                    viewModel.updateMatchBloodGroup(group)
                                    viewModel.selectTab(LifeLinkTab.MATCHING)
                                },
                                onQuickEmergencyClick = {
                                    viewModel.navigateToEmergencyRequestWithBloodGroup()
                                },
                                modifier = contentModifier
                            )
                        }

                        LifeLinkTab.REGISTER -> {
                            DonorRegistrationScreen(
                                formState = registrationForm,
                                onFullNameChange = viewModel::updateRegFullName,
                                onAgeChange = viewModel::updateRegAge,
                                onGenderChange = viewModel::updateRegGender,
                                onBloodGroupChange = viewModel::updateRegBloodGroup,
                                onLocationChange = viewModel::updateRegLocation,
                                onPhoneChange = viewModel::updateRegPhone,
                                onEmailChange = viewModel::updateRegEmail,
                                onLastDonationDateChange = viewModel::updateRegLastDonationDate,
                                onNeverDonatedChange = viewModel::updateRegNeverDonated,
                                onAvailabilityChange = viewModel::updateRegAvailability,
                                onSubmitRegistration = viewModel::submitDonorRegistration,
                                onGoToMatching = { viewModel.selectTab(LifeLinkTab.MATCHING) },
                                onGoToDonorLookup = {
                                    viewModel.performDonorNotificationLookup()
                                    viewModel.selectTab(LifeLinkTab.NOTIFICATIONS)
                                },
                                modifier = contentModifier
                            )
                        }

                        LifeLinkTab.MATCHING -> {
                            AiMatchingScreen(
                                queryState = matchingQuery,
                                matchResults = matchResults,
                                totalRegisteredDonorsCount = registeredDonors.size,
                                allNotifications = allNotifications,
                                isDedicatedEmergencyTab = false,
                                onPatientNameChange = viewModel::updateMatchPatientName,
                                onBloodGroupChange = viewModel::updateMatchBloodGroup,
                                onLocationChange = viewModel::updateMatchLocation,
                                onUnitsChange = viewModel::updateMatchUnitsNeeded,
                                onEmergencyToggle = viewModel::updateMatchEmergencyMode,
                                onHospitalNotesChange = viewModel::updateMatchHospitalNotes,
                                onRunMatching = viewModel::runAiDonorMatching,
                                onNotifySingleDonor = viewModel::notifySingleMatchedDonor,
                                onNotifyAllMatchedDonors = viewModel::dispatchNotificationsToAllMatchedDonors,
                                onNavigateToRegister = { viewModel.selectTab(LifeLinkTab.REGISTER) },
                                modifier = contentModifier
                            )
                        }

                        LifeLinkTab.EMERGENCY -> {
                            AiMatchingScreen(
                                queryState = matchingQuery,
                                matchResults = matchResults,
                                totalRegisteredDonorsCount = registeredDonors.size,
                                allNotifications = allNotifications,
                                isDedicatedEmergencyTab = true,
                                onPatientNameChange = viewModel::updateMatchPatientName,
                                onBloodGroupChange = viewModel::updateMatchBloodGroup,
                                onLocationChange = viewModel::updateMatchLocation,
                                onUnitsChange = viewModel::updateMatchUnitsNeeded,
                                onEmergencyToggle = viewModel::updateMatchEmergencyMode,
                                onHospitalNotesChange = viewModel::updateMatchHospitalNotes,
                                onRunMatching = viewModel::runAiDonorMatching,
                                onNotifySingleDonor = viewModel::notifySingleMatchedDonor,
                                onNotifyAllMatchedDonors = viewModel::dispatchNotificationsToAllMatchedDonors,
                                onNavigateToRegister = { viewModel.selectTab(LifeLinkTab.REGISTER) },
                                modifier = contentModifier
                            )
                        }

                        LifeLinkTab.NOTIFICATIONS -> {
                            DonorNotificationsScreen(
                                lookupState = donorLookup,
                                lookedUpDonor = lookedUpDonor,
                                notifications = lookedUpNotifications,
                                onPhoneInputChange = viewModel::updateLookupPhoneInput,
                                onPerformLookup = viewModel::performDonorNotificationLookup,
                                onToggleDonorAvailability = viewModel::toggleLookedUpDonorAvailability,
                                onRespondToNotification = viewModel::respondToDonorNotification,
                                modifier = contentModifier
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LifeLinkBottomNavigationBar(
    selectedTab: LifeLinkTab,
    totalNotificationsCount: Int,
    onSelectTab: (LifeLinkTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkCrimsonBorder.copy(alpha = 0.7f))
            .navigationBarsPadding(),
        containerColor = ObsidianBlack.copy(alpha = 0.96f),
        contentColor = TextAlabaster
    ) {
        LifeLinkTab.entries.forEach { tab ->
            val selected = selectedTab == tab
            val (selectedIcon, unselectedIcon) = tab.icons()

            NavigationBarItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                modifier = Modifier.testTag("nav_tab_${tab.route}"),
                icon = {
                    if (tab == LifeLinkTab.NOTIFICATIONS && totalNotificationsCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = CrimsonBright,
                                    contentColor = Color.White
                                ) {
                                    Text(totalNotificationsCount.toString())
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selected) selectedIcon else unselectedIcon,
                                contentDescription = tab.title
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (selected) selectedIcon else unselectedIcon,
                            contentDescription = tab.title
                        )
                    }
                },
                label = {
                    Text(
                        text = tab.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = if (tab == LifeLinkTab.EMERGENCY) EmergencyPulseRed else Color.White,
                    selectedTextColor = if (tab == LifeLinkTab.EMERGENCY) EmergencyPulseRed else TextSoftRose,
                    indicatorColor = DeepBloodRed,
                    unselectedIconColor = TextMutedMauve,
                    unselectedTextColor = TextMutedMauve
                )
            )
        }
    }
}

@Composable
private fun LifeLinkNavigationRail(
    selectedTab: LifeLinkTab,
    totalNotificationsCount: Int,
    onSelectTab: (LifeLinkTab) -> Unit
) {
    NavigationRail(
        modifier = Modifier
            .fillMaxHeight()
            .border(1.dp, DarkCrimsonBorder.copy(alpha = 0.6f)),
        containerColor = ObsidianBlack.copy(alpha = 0.95f),
        contentColor = TextAlabaster
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            LifeLinkTab.entries.forEach { tab ->
                val selected = selectedTab == tab
                val (selectedIcon, unselectedIcon) = tab.icons()

                NavigationRailItem(
                    selected = selected,
                    onClick = { onSelectTab(tab) },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .testTag("rail_tab_${tab.route}"),
                    icon = {
                        if (tab == LifeLinkTab.NOTIFICATIONS && totalNotificationsCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = CrimsonPrimary,
                                        contentColor = Color.White
                                    ) {
                                        Text(totalNotificationsCount.toString())
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (selected) selectedIcon else unselectedIcon,
                                    contentDescription = tab.title
                                )
                            }
                        } else {
                            Icon(
                                imageVector = if (selected) selectedIcon else unselectedIcon,
                                contentDescription = tab.title
                            )
                        }
                    },
                    label = { Text(tab.title) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = if (tab == LifeLinkTab.EMERGENCY) EmergencyPulseRed else Color.White,
                        selectedTextColor = if (tab == LifeLinkTab.EMERGENCY) EmergencyPulseRed else TextSoftRose,
                        indicatorColor = DeepBloodRed,
                        unselectedIconColor = TextMutedMauve,
                        unselectedTextColor = TextMutedMauve
                    )
                )
            }
        }
    }
}

private fun LifeLinkTab.icons(): Pair<ImageVector, ImageVector> = when (this) {
    LifeLinkTab.DASHBOARD -> Icons.Filled.Dashboard to Icons.Outlined.Dashboard
    LifeLinkTab.REGISTER -> Icons.Filled.PersonAdd to Icons.Outlined.PersonAdd
    LifeLinkTab.MATCHING -> Icons.Filled.AutoAwesome to Icons.Outlined.AutoAwesome
    LifeLinkTab.EMERGENCY -> Icons.Filled.Emergency to Icons.Outlined.Emergency
    LifeLinkTab.NOTIFICATIONS -> Icons.Filled.Notifications to Icons.Outlined.Notifications
}
