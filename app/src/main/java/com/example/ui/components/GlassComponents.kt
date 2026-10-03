package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BloodGroup
import com.example.ui.theme.BrightCrimsonBorder
import com.example.ui.theme.CrimsonBright
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CrimsonVoid
import com.example.ui.theme.DarkCrimsonBorder
import com.example.ui.theme.DeepBloodRed
import com.example.ui.theme.EmergencyAmber
import com.example.ui.theme.EmergencyDarkBg
import com.example.ui.theme.EmergencyPulseRed
import com.example.ui.theme.GlassCardElevated
import com.example.ui.theme.GlassCardSurface
import com.example.ui.theme.MedicalEmerald
import com.example.ui.theme.MedicalEmeraldBg
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.RubyAccent
import com.example.ui.theme.TextAlabaster
import com.example.ui.theme.TextMutedMauve
import com.example.ui.theme.TextSoftRose

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    isEmergency: Boolean = false,
    isHighlighted: Boolean = false,
    cornerRadius: Dp = 20.dp,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val borderBrush = when {
        isEmergency -> Brush.linearGradient(
            colors = listOf(EmergencyPulseRed, EmergencyAmber, CrimsonPrimary)
        )
        isHighlighted -> Brush.linearGradient(
            colors = listOf(CrimsonBright, RubyAccent.copy(alpha = 0.6f), DarkCrimsonBorder)
        )
        else -> Brush.linearGradient(
            colors = listOf(
                BrightCrimsonBorder.copy(alpha = 0.55f),
                DarkCrimsonBorder.copy(alpha = 0.35f),
                Color(0xFF220810)
            )
        )
    }

    val backgroundBrush = when {
        isEmergency -> Brush.verticalGradient(
            colors = listOf(
                EmergencyDarkBg.copy(alpha = 0.92f),
                GlassCardSurface.copy(alpha = 0.96f)
            )
        )
        isHighlighted -> Brush.verticalGradient(
            colors = listOf(
                GlassCardElevated.copy(alpha = 0.95f),
                GlassCardSurface.copy(alpha = 0.92f)
            )
        )
        else -> Brush.verticalGradient(
            colors = listOf(
                GlassCardSurface.copy(alpha = 0.90f),
                CrimsonVoid.copy(alpha = 0.94f)
            )
        )
    }

    Card(
        modifier = modifier
            .border(
                width = if (isEmergency || isHighlighted) 1.5.dp else 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(cornerRadius)
            ),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isEmergency) 10.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = backgroundBrush)
                .padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun LifeLinkTopBar(
    emergencyCount: Int,
    onEmergencyQuickClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = ObsidianBlack.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, DarkCrimsonBorder.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(CrimsonBright, DeepBloodRed)
                            )
                        )
                        .border(1.dp, RubyAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bloodtype,
                        contentDescription = "LifeLink Emblem",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LifeLink",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.4.sp
                            ),
                            color = TextAlabaster
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MedicalEmeraldBg)
                                .border(1.dp, MedicalEmerald.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "AI MEDICAL",
                                style = MaterialTheme.typography.labelSmall,
                                color = MedicalEmerald
                            )
                        }
                    }
                    Text(
                        text = "AI Based Blood Donor Matching & Emergency Support",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedMauve,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextButton(
                onClick = onEmergencyQuickClick,
                modifier = Modifier
                    .testTag("top_bar_emergency_button")
                    .clip(RoundedCornerShape(12.dp))
                    .background(EmergencyPulseRed.copy(alpha = 0.18f * pulseAlpha + 0.12f))
                    .border(
                        1.dp,
                        EmergencyPulseRed.copy(alpha = pulseAlpha),
                        RoundedCornerShape(12.dp)
                    ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = TextAlabaster)
            ) {
                Icon(
                    imageVector = Icons.Default.Emergency,
                    contentDescription = "Emergency Mode",
                    tint = EmergencyPulseRed,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (emergencyCount > 0) "SOS ($emergencyCount)" else "SOS MODE",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSoftRose
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BloodGroupSelectorGrid(
    selectedGroup: String,
    onSelectGroup: (String) -> Unit,
    modifier: Modifier = Modifier,
    tagPrefix: String = "blood_group"
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        maxItemsInEachRow = 4
    ) {
        BloodGroup.ALL_LABELS.forEach { group ->
            val isSelected = selectedGroup.equals(group, ignoreCase = true)
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) CrimsonPrimary else GlassCardElevated,
                label = "bgColor"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) RubyAccent else DarkCrimsonBorder,
                label = "borderColor"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("${tagPrefix}_$group")
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelectGroup(group) },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bloodtype,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else RubyAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = group,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = if (isSelected) Color.White else TextAlabaster
                    )
                }
            }
        }
    }
}

@Composable
fun AiScoreRingBadge(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 68.dp
) {
    val ringColor = when {
        score >= 85 -> MedicalEmerald
        score >= 70 -> CrimsonBright
        else -> EmergencyAmber
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = 6.dp.toPx()
            drawArc(
                color = DarkCrimsonBorder.copy(alpha = 0.45f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = (score.coerceIn(0, 100) / 100f) * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = TextAlabaster
            )
            Text(
                text = "AI MATCH",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = ringColor
            )
        }
    }
}

@Composable
fun StatusPillBadge(
    text: String,
    color: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, color.copy(alpha = 0.6f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
fun SecurityComplianceFooter(modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 14.dp,
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.HealthAndSafety,
                contentDescription = "Medical Security Shield",
                tint = RubyAccent,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = "LifeLink Zero-Mock & Donor Privacy Protocol",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSoftRose
                )
                Text(
                    text = "Only verified registered donors are stored & matched. Duplicate phone numbers are blocked, and personal contact details remain masked.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedMauve
                )
            }
        }
    }
}
