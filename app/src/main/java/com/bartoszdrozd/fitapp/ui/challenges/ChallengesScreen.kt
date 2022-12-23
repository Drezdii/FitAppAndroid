package com.bartoszdrozd.fitapp.ui.challenges

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.challenges.ChallengeEntry
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

@Composable
fun ChallengeEntryView(entry: ChallengeEntry) {
    val smallPadding = dimensionResource(R.dimen.small_padding)
    // Limit max progress to 100%
    val progress = (entry.value / entry.challenge.goal).coerceAtMost(1f)

    val progressAnimation = remember {
        Animatable(0f)
    }

    LaunchedEffect(Unit) {
        progressAnimation.animateTo(progress, tween(1000))
    }

    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(smallPadding)) {
            Text(entry.challengeId)
            Row {
                Text(entry.value.toString())
                Text("/")
                Text(entry.challenge.goal.toString())
            }

            if (entry.challenge.startDate > Clock.System.now()) {
                Text(
                    "Unlocks in " + Clock.System.now().daysUntil(entry.challenge.startDate, TimeZone.UTC).toString() + " days"
                )
            } else {
                ChallengeProgressBar(progressAnimation.value)
            }
        }
    }
}

@Composable
fun ChallengeProgressBar(progress: Float) {
    // TODO: Change hardcoded height
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