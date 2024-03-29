package com.bartoszdrozd.fitapp.ui.challenges

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.challenges.ChallengeEntry
import com.bartoszdrozd.fitapp.utils.AbbreviationFormatter
import com.bartoszdrozd.fitapp.utils.modifyIf
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil

@Composable
fun ChallengesScreen(viewModel: ChallengesViewModel) {
    val challenges by viewModel.challenges.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadChallenges()
    }

    ChallengesView(challenges = challenges)
}

@Composable
fun ChallengesView(challenges: List<ChallengeEntry>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_padding))) {
        items(challenges) { entry ->
            ChallengeEntryView(entry)
        }
    }
}

@Composable
fun ChallengeEntryView(entry: ChallengeEntry) {
    val smallPadding = dimensionResource(R.dimen.small_padding)

    // Limit max progress to 100%
    val progress = (entry.value / entry.challenge.goal).coerceAtMost(1f)

    val df = remember {
        AbbreviationFormatter()
    }


    val progressAnimation = remember {
        // Don't animate completed challenges
        Animatable(if (entry.completedAt != null) progress else 0f)
    }

    LaunchedEffect(Unit) {
        progressAnimation.animateTo(progress, tween(800))
    }

    OutlinedCard(
        Modifier
            .fillMaxWidth()
            .modifyIf(entry.completedAt != null) {
                alpha(0.4f)
            }
    ) {
        Column(Modifier.padding(smallPadding)) {
            Row(Modifier.padding(bottom = smallPadding)) {
                Text(entry.challenge.name, Modifier.weight(1f))

                if (entry.challenge.endDate != null) {
                    Row {
                        Icon(
                            Icons.Filled.Timelapse,
                            contentDescription = "Days until challenge ends"
                        )

                        val daysLeft =
                            Clock.System.now().daysUntil(entry.challenge.endDate, TimeZone.UTC)

                        Text(stringResource(R.string.challenge_days_left, daysLeft))
                    }
                }
            }

            if (!entry.challenge.description.isNullOrBlank()) {
                Text(
                    entry.challenge.description,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = smallPadding)
                )
            }

            if (entry.challenge.startDate > Clock.System.now()) {
                Text(
                    stringResource(
                        R.string.challenge_days_left, Clock.System.now()
                            .daysUntil(entry.challenge.startDate, TimeZone.UTC).toString()
                    )
                )
            } else {
                Row {
                    Column(
                        Modifier
                            .weight(3.5f)
                            .padding(end = smallPadding)
                    ) {
                        ChallengeProgressBar(progressAnimation.value)
                    }

                    Text(
                        "${df.format(entry.value)}/${df.format(entry.challenge.goal)}",
                        Modifier.weight(1f),
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ChallengeProgressBar(progress: Float) {
    val progressColor = MaterialTheme.colorScheme.primary
    Canvas(
        Modifier
            .height(16.dp)
            .fillMaxWidth()
    ) {
        drawProgressBar(Color.Gray, 16.dp.toPx(), 1f)
        drawProgressBar(progressColor, 16.dp.toPx(), progress)
    }
}

private fun DrawScope.drawProgressBar(color: Color, height: Float, progress: Float) {
    val width = size.width * progress
    drawRoundRect(
        color,
        Offset(0f, 0f),
        Size(width, height),
        CornerRadius(4.dp.toPx(), 4.dp.toPx())
    )
}