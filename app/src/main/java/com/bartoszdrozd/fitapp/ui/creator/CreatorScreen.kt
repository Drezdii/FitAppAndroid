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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.creator.Program
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.ui.programs.Program531BBB4DaysScreen
import com.bartoszdrozd.fitapp.ui.workout.WorkoutItem
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CreatorScreen(creatorViewModel: CreatorViewModel) {
    val currentPage by creatorViewModel.currentPage.collectAsState()
    val selectedProgram by creatorViewModel.selectedProgram.collectAsState()
    val workouts by creatorViewModel.workouts.collectAsState()

    val pagerState = rememberPagerState()
    val programs = listOf(
        Program(
            1, "5/3/1 BBB 4 days"
        )
    )

    LaunchedEffect(currentPage) {
        pagerState.animateScrollToPage(currentPage)
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
                Button(onClick = { creatorViewModel.nextPage() }) {
                    Text(text = stringResource(id = R.string.next))
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalPagerApi::class)
@Composable
fun CreatorView(program: @Composable () -> Unit, workouts: List<List<Workout>>) {
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
                    for (week in workouts.indices) {
                        Tab(
                            text = {
                                Text(
                                    stringResource(
                                        id = R.string.week_with_placeholder,
                                        week + 1
                                    )
                                )
                            },
                            selected = pagerState.currentPage == week,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(week) }
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
                        items(workouts[page]) { workout ->
                            WorkoutItem(workout = workout, onWorkoutClick = {})
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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