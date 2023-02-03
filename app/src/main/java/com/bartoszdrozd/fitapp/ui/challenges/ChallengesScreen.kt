package com.bartoszdrozd.fitapp.ui.challenges

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
    LazyColumn {
        items(challenges) { entry ->
            ChallengeEntryView(entry)
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun ChallengeEntryView(entry: ChallengeEntry) {
    val smallPadding = dimensionResource(R.dimen.small_padding)

    // Limit max progress to 100%
    val progress = (entry.value / entry.challenge.goal).coerceAtMost(1f)

    val df = remember {
        AbbreviationFormatter()
    }


    val progressAnimation = remember {
        Animatable(0f)
    }

    LaunchedEffect(Unit) {
        progressAnimation.animateTo(progress, tween(1000))
    }

    OutlinedCard(
        Modifier
            .fillMaxWidth()
            .padding(bottom = smallPadding)
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
    Canvas(
        Modifier
            .progressSemantics(progress)
            .height(16.dp)
            .fillMaxWidth()
    ) {
        drawProgressBar(Color.Gray, 16.dp.toPx(), 1f)
        drawProgressBar(Color.Green, 16.dp.toPx(), progress)
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