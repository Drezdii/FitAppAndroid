package com.bartoszdrozd.fitapp.ui.creator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.outlinedCardColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.SnackbarMessage
import com.bartoszdrozd.fitapp.model.creator.Program
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.ui.programs.Program531BBB4DaysScreen
import com.bartoszdrozd.fitapp.utils.EventType
import com.bartoszdrozd.fitapp.utils.exerciseTypeToIconId
import com.bartoszdrozd.fitapp.utils.toNameResId
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProgramsScreen(
    creatorViewModel: CreatorViewModel,
    onProgramSaved: () -> Unit,
    showSnackbar: (SnackbarMessage) -> Unit
) {
    val currentPage by creatorViewModel.currentPage.collectAsState()
    val selectedProgram by creatorViewModel.selectedProgram.collectAsState()
    val workouts by creatorViewModel.workouts.collectAsState()
    val isSaving by creatorViewModel.isSaving.collectAsState()

    val pagerState = rememberPagerState()
    val programs = listOf(
        Program(
            1, "5/3/1 BBB 4 days"
        )
    )

    val context = LocalContext.current

    LaunchedEffect(currentPage) {
        pagerState.animateScrollToPage(currentPage)
    }

    LaunchedEffect(Unit) {
        creatorViewModel.events.collect {
            when (it) {
                EventType.Saved -> {
                    showSnackbar(SnackbarMessage(context.getString(R.string.saved)))
                    onProgramSaved()
                }
                // Add more error messages
                is EventType.Error -> showSnackbar(SnackbarMessage(context.getString(R.string.general_error)))
                else -> {}
            }
        }
    }

    Column {
        HorizontalPager(
            count = 2,
            modifier = Modifier
                .weight(1f),
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> ProgramsList(
                    programs,
                    selectedProgram?.id ?: 0
                ) { creatorViewModel.selectProgram(it) }

                1 -> {
                    when (selectedProgram?.id) {
                        1 -> CreatorView(
                            program = { Program531BBB4DaysScreen(creatorViewModel = creatorViewModel) },
                            workouts = workouts
                        )
                    }
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (currentPage != 0) {
                Button(onClick = { creatorViewModel.previousPage() }) {
                    Text(text = stringResource(id = R.string.previous))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (selectedProgram != null) {
                if (workouts.isNotEmpty()) {
                    Button(onClick = { creatorViewModel.saveWorkouts() }, enabled = !isSaving) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(ButtonDefaults.IconSize),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = stringResource(id = R.string.finish))
                        }
                    }
                } else {
                    Button(onClick = { creatorViewModel.nextPage() }) {
                        Text(text = stringResource(id = R.string.next))
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CreatorView(program: @Composable () -> Unit, workouts: Map<Int, List<Workout>>) {
    val pagerState = rememberPagerState()

    Column(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.wrapContentHeight(Alignment.Top)) {
            program()
        }

//        if (workouts.isNotEmpty()) {
//            Text(
//                text = pluralStringResource(
//                    id = R.plurals.plural_workouts,
//                    count = workouts.size,
//                    workouts.size,
//                ),
//                fontSize = 24.sp,
//                modifier = Modifier.padding(8.dp)
//            )
//        }


//        LazyColumn(Modifier.align(Alignment.CenterHorizontally)) {
//            if (workouts.isEmpty()) {
//                item {
//                    Text(text = "No workouts created yet.")
//                }
//            }
//
//            items(workouts) { workout ->
//                WorkoutItem(workout = workout, onWorkoutClick = {})
//            }
//        }
        val coroutineScope = rememberCoroutineScope()

        if (workouts.isNotEmpty()) {
            Column(Modifier.fillMaxWidth()) {
                TabRow(selectedTabIndex = pagerState.currentPage) {
                    for (workoutWeek in workouts) {
                        Tab(
                            text = {
                                Text(
                                    stringResource(
                                        id = R.string.week_with_placeholder,
                                        workoutWeek.key
                                    )
                                )
                            },
                            selected = pagerState.currentPage == workoutWeek.key - 1,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(workoutWeek.key - 1) }
                            }
                        )
                    }
                }
                HorizontalPager(
                    count = workouts.size,
                    modifier = Modifier
                        .weight(1f),
                    state = pagerState,
                ) { page ->
                    LazyColumn(Modifier.align(Alignment.CenterHorizontally)) {
                        items(workouts[page + 1]!!) { workout ->
                            DetailedWorkoutItem(workout = workout)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProgramsList(programs: List<Program>, selectedProgramId: Int, onClick: (Program) -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.8f)
            .verticalScroll(scrollState)
    ) {
        for (program in programs) {
            OutlinedCard(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClick(program)
                    },
                colors = if (selectedProgramId == program.id) {
                    outlinedCardColors(containerColor = Color.Gray.copy(0.4f))
                } else {
                    outlinedCardColors()
                }
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = program.name,
                        Modifier.weight(1f)
                    )

                    if (selectedProgramId == program.id) {
                        Icon(Icons.Outlined.Check, contentDescription = null)
                    }
                }
            }
        }
    }
    // Use this when crash in compose is fixed
//    val scrollState = rememberLazyListState()
//    LazyColumn(state = scrollState) {
//        items(listOf("5/3/1 BBB 4 days", "Test Program", "Test Program 2")) { program ->
//            Card(
//                Modifier
//                    .padding(8.dp)
//                    .fillMaxWidth()
//            ) {
//                Text(text = program, Modifier.padding(32.dp))
//            }
//        }
//    }
}

@Composable
private fun DetailedWorkoutItem(workout: Workout) {
    OutlinedCard(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            Modifier
                .padding(24.dp)
                .height(IntrinsicSize.Min)
        ) {
            val workoutTypeId = exerciseTypeToIconId(workout.type)

            Icon(
                painter = painterResource(workoutTypeId),
                contentDescription = null,
                Modifier
                    .padding(end = 24.dp)
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                for (exercise in workout.exercises) {
                    Column {
                        Text(stringResource(exercise.exerciseType.toNameResId()))
                        for (set in exercise.sets) {
                            Text("${set.reps} x ${set.weight} kg")
                        }
                    }
                }
            }
        }
    }
}