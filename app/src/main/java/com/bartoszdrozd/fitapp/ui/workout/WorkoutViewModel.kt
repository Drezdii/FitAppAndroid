package com.bartoszdrozd.fitapp.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bartoszdrozd.fitapp.domain.workout.DeleteWorkoutUseCase
import com.bartoszdrozd.fitapp.domain.workout.GetWorkoutUseCase
import com.bartoszdrozd.fitapp.domain.workout.SaveWorkoutUseCase
import com.bartoszdrozd.fitapp.model.workout.Exercise
import com.bartoszdrozd.fitapp.model.workout.ExerciseType
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.model.workout.WorkoutSet
import com.bartoszdrozd.fitapp.utils.EventType
import com.bartoszdrozd.fitapp.utils.Result
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
    private val gson: Gson
) : ViewModel() {
    private val _workoutUiState = MutableStateFlow(WorkoutUiState())
    private val _openExercises: MutableStateFlow<List<Long>> = MutableStateFlow(emptyList())
    private val _savingResultEvent = Channel<Result<*>>()
    private val _deleteResultEvent = Channel<Result<*>>()

    private val _eventChannel = Channel<EventType<*>>();

    private lateinit var _lastCleanWorkoutState: Workout

    val workoutUiState: StateFlow<WorkoutUiState> = _workoutUiState
    val openExercises: StateFlow<List<Long>> = _openExercises
    val events: Flow<EventType<*>> = _eventChannel.receiveAsFlow()

    // Keep track of the last temporary ID
    private var tempIndex: Long = -1
        get() {
            return field--
        }

    fun loadWorkout(id: Long) {
        viewModelScope.launch {
            getWorkoutUseCase(id).collect {
                when (it) {
                    is Result.Success -> {
                        // Instantly mark new workout as dirty to display 'Save' button
                        _workoutUiState.value =
                            WorkoutUiState(it.data, false, isDirty = it.data.id == 0L)
                        _lastCleanWorkoutState = it.data
                    }
                    is Result.Error -> _eventChannel.send(EventType.Error(it.exception))
                    is Result.Loading -> _workoutUiState.value =
                        _workoutUiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun saveWorkout() {
        viewModelScope.launch {
            val res = saveWorkoutUseCase(_workoutUiState.value.workout)

            if (res is Result.Success) {
                _eventChannel.send(EventType.Saved)
            } else {
                _eventChannel.send(EventType.Error((res as Result.Error).exception))
            }

            // Reload workout only if a new one was being added
            if (res is Result.Success && workoutUiState.value.workout.id == 0L) {
                loadWorkout(res.data)
            }
        }
    }

    fun deleteWorkout() {
        viewModelScope.launch {
            val res = deleteWorkoutUseCase(_workoutUiState.value.workout)
            if (res is Result.Success) {
                _eventChannel.send(EventType.Deleted)
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

    fun onClickExpand(id: Long) {
        _openExercises.value = _openExercises.value.toMutableList().also {
            if (it.contains(id)) it.remove(id) else it.add(id)
        }
    }

    fun changeWorkoutState() {
        if (_workoutUiState.value.workout.startDate == null) {
            val workout = getWorkoutCopy().copy(startDate = Clock.System.now())
            _workoutUiState.value = _workoutUiState.value.copy(workout = workout)
        } else {
            val workout = getWorkoutCopy().copy(endDate = Clock.System.now())
            _workoutUiState.value = _workoutUiState.value.copy(workout = workout)
        }

        saveWorkout()
    }

    private fun updateWorkoutState(workout: Workout) {
        _workoutUiState.value = _workoutUiState.value.copy(workout = workout, isDirty = true)
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