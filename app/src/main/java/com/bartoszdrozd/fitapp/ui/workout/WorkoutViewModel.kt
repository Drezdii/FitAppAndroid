package com.bartoszdrozd.fitapp.ui.workout

import android.app.Application
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.domain.workout.DeleteWorkoutUseCase
import com.bartoszdrozd.fitapp.domain.workout.GetWorkoutUseCase
import com.bartoszdrozd.fitapp.domain.workout.SaveWorkoutUseCase
import com.bartoszdrozd.fitapp.domain.workout.WorkoutForegroundService
import com.bartoszdrozd.fitapp.model.workout.Exercise
import com.bartoszdrozd.fitapp.model.workout.ExerciseType
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.model.workout.WorkoutSet
import com.bartoszdrozd.fitapp.ui.TopAppBarConnector
import com.bartoszdrozd.fitapp.ui.TopAppBarState
import com.bartoszdrozd.fitapp.utils.EventType
import com.bartoszdrozd.fitapp.utils.ResultValue
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val getWorkoutUseCase: GetWorkoutUseCase,
    private val saveWorkoutUseCase: SaveWorkoutUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase,
    private val gson: Gson,
    private val application: Application,
    private val topAppBarConnector: TopAppBarConnector
) : ViewModel() {
    private val _workoutUiState = MutableStateFlow(WorkoutUiState())
    private val _openExercises: MutableStateFlow<List<Long>> = MutableStateFlow(emptyList())

    private val _eventsChannel = Channel<EventType<*>>()

    private lateinit var _lastCleanWorkoutState: Workout

    val workoutUiState: StateFlow<WorkoutUiState> = _workoutUiState
    val events: Flow<EventType<*>> = _eventsChannel.receiveAsFlow()

    // Keep track of the last temporary ID
    private var tempIndex: Long = -1
        get() {
            return field--
        }

    fun loadWorkout(id: Long) {
        viewModelScope.launch {
            getWorkoutUseCase(id).collect {
                when (it) {
                    is ResultValue.Success -> {
                        val workout = it.data
                        // Instantly mark new workout as dirty to display 'Save' button
                        _workoutUiState.value =
                            WorkoutUiState(workout, false, isDirty = workout.id == 0L)
                        _lastCleanWorkoutState = workout

                        if (workout.isActive) {
                            startTrackingService(workout)
                        }

                        if (!workout.isActive) {
                            stopTrackingService(workout.id)
                        }
                    }

                    is ResultValue.Error -> _eventsChannel.send(EventType.Error(it.exception))
                    else -> {}
                }
            }
        }
    }

    suspend fun updateTopAppBar(title: String) {
        val state = TopAppBarState(
            { Text(title) },
            {
                IconButton(onClick = { deleteWorkout() }) {
                    Icon(
                        Icons.Outlined.DeleteForever,
                        contentDescription = stringResource(R.string.delete_workout)
                    )
                }
            },
            showBackButton = true
        )

        topAppBarConnector.setAppBarState(state)
    }

    private fun startTrackingService(workout: Workout) {
        if (workout.id == 0L) {
            return
        }

        val workoutService =
            Intent(application.applicationContext, WorkoutForegroundService::class.java)

        workoutService.putExtra("workoutId", workout.id)
        workoutService.putExtra("startDate", workout.startDate!!.epochSeconds)
        workoutService.putExtra("programId", workout.workoutProgramDetails?.id)
        workoutService.putExtra("programWeek", workout.workoutProgramDetails?.week)

        workoutService.action = WorkoutForegroundService.START_WORKOUT

        application.applicationContext.startService(workoutService)
    }

    private fun stopTrackingService(workoutId: Long) {
        val workoutService =
            Intent(application.applicationContext, WorkoutForegroundService::class.java)

        workoutService.putExtra("workoutId", workoutId)
        workoutService.action = WorkoutForegroundService.STOP_WORKOUT

        application.applicationContext.startService(workoutService)
    }

    fun saveWorkout() {
        viewModelScope.launch {
            val res = saveWorkoutUseCase(_workoutUiState.value.workout)

            if (res is ResultValue.Success) {
                _eventsChannel.send(EventType.Saved)
            } else {
                _eventsChannel.send(EventType.Error((res as ResultValue.Error).exception))
            }

            // Reload workout only if a new one was being added
            if (res is ResultValue.Success && workoutUiState.value.workout.id == 0L) {
                loadWorkout(res.data)
            }
        }
    }

    fun deleteWorkout() {
        viewModelScope.launch {
            val res = deleteWorkoutUseCase(_workoutUiState.value.workout)
            if (res is ResultValue.Success) {
                _eventsChannel.send(EventType.Deleted)
            }
        }
    }

    private fun getWorkoutCopy(): Workout = gson.fromJson(
        gson.toJson(_workoutUiState.value.workout, Workout::class.java),
        Workout::class.java
    )

    fun deleteSet(set: WorkoutSet, exerciseId: Long) {
        val workout = getWorkoutCopy()

        val exercise = workout.exercises.find { it.id == exerciseId }!!
        val sets = exercise.sets.toMutableList()
        sets.remove(set)
        exercise.sets = sets

        updateWorkoutState(workout)
    }

    fun updateSet(set: WorkoutSet, exerciseId: Long) {
        val workout = getWorkoutCopy()

        val sets = workout.exercises.find { it.id == exerciseId }!!.sets.toMutableList()
        sets.find { it.id == set.id }?.apply {
            reps = set.reps
            weight = set.weight
            completed = set.completed
        }
        workout.exercises.find { it.id == exerciseId }!!.sets = sets

        updateWorkoutState(workout)
    }

    fun addExercise(exerciseType: ExerciseType) {
        val workout = getWorkoutCopy()

        val exercises = workout.exercises.toMutableList()
        val exercise = Exercise(tempIndex, exerciseType)
        exercises.add(exercise)
        workout.exercises = exercises

        updateWorkoutState(workout)
        _openExercises.value = _openExercises.value.toMutableList().also { it.add(exercise.id) }
    }

    fun addSet(exercise: Exercise) {
        val workout = getWorkoutCopy()

        val sets = workout.exercises.find { it.id == exercise.id }!!.sets.toMutableList()

        val lastSet = if (sets.isNotEmpty()) sets.last() else WorkoutSet(0, 0, 0.0, false)

        sets.add(WorkoutSet(tempIndex, lastSet.reps, lastSet.weight, false))

        workout.exercises.find { it.id == exercise.id }!!.sets = sets

        updateWorkoutState(workout)
    }

    fun deleteExercise(exercise: Exercise) {
        val workout = getWorkoutCopy()

        val exercises = workout.exercises.toMutableList()
        exercises.remove(exercise)
        workout.exercises = exercises

        updateWorkoutState(workout)
    }

    fun updateDate(newDate: LocalDate) {
        updateWorkoutState(_workoutUiState.value.workout.copy(date = newDate))
    }

    fun changeCompletionState() {
        if (_workoutUiState.value.workout.startDate == null) {
            val workout = getWorkoutCopy().copy(startDate = Clock.System.now())
            updateWorkoutState(workout, false)
        } else {
            val workout = getWorkoutCopy().copy(endDate = Clock.System.now())
            updateWorkoutState(workout, false)
        }

        saveWorkout()
    }

    private fun updateWorkoutState(workout: Workout, isDirty: Boolean = true) {
        _workoutUiState.value = _workoutUiState.value.copy(workout = workout, isDirty = isDirty)
    }

    fun cancelChanges() {
        _workoutUiState.value = WorkoutUiState(
            _lastCleanWorkoutState,
            isLoading = false,
            isDirty = false
        )
    }
}

data class WorkoutUiState(
    val workout: Workout = Workout(-1L),
    val isLoading: Boolean = false,
    val isDirty: Boolean = false,
)