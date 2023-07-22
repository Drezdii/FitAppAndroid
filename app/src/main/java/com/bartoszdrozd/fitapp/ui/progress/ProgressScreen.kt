package com.bartoszdrozd.fitapp.ui.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.stats.PersonalBest
import com.bartoszdrozd.fitapp.ui.challenges.ChallengeEntryView
import com.bartoszdrozd.fitapp.utils.exerciseTypeToIconId
import kotlin.math.roundToInt

@Composable
fun ProgressScreen(viewModel: ProgressScreenViewModel, onViewChallenges: () -> Unit) {
    val personalBests by viewModel.personalBests.collectAsState()
    val closestChallenges by viewModel.closestChallenges.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getPersonalBests()
    }

    LaunchedEffect(Unit) {
        viewModel.getClosestChallenges()
    }

    Column(
        Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row {
            Text(stringResource(R.string.personal_bests), fontSize = 20.sp)
        }
        if (personalBests.isEmpty()) {
            Text(stringResource(R.string.no_personal_bests))
        }
        personalBests.forEach {
            PersonalBestCard(it)
        }

        Row {
            Text(
                stringResource(R.string.challenges),
                fontSize = 20.sp,
                modifier = Modifier.alignByBaseline()
            )

            Spacer(Modifier.weight(1f))

            TextButton(onClick = { onViewChallenges() }, Modifier.alignByBaseline()) {
                Text(stringResource(R.string.view_all))
            }
        }

        if (closestChallenges.isEmpty()){
            Text(stringResource(R.string.no_closest_challenges))
        }

        closestChallenges.forEach {
            ChallengeEntryView(it)
        }
    }
}

@Composable
fun PersonalBestCard(pb: PersonalBest) {
    val workoutIconId = exerciseTypeToIconId(pb.exerciseType)

    ElevatedCard {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Icon(
                painter = painterResource(workoutIconId),
                contentDescription = null,
                Modifier
                    .fillMaxHeight(0.7f)
                    .aspectRatio(1f)
                    .align(Alignment.CenterVertically)
            )

            Spacer(Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.End) {
                Row(Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = pb.value.roundToInt().toString(),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.alignByBaseline()
                    )
                    Text(stringResource(R.string.kg_unit), Modifier.alignByBaseline())
                }
                Text(text = pb.date.toString(), fontSize = 12.sp)
            }
        }
    }
}