package com.bartoszdrozd.fitapp.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bartoszdrozd.fitapp.domain.workout.GetWorkoutUseCase
import com.bartoszdrozd.fitapp.domain.workout.SaveWorkoutUseCase
import com.bartoszdrozd.fitapp.model.workout.Exercise
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.model.workout.WorkoutSet
import com.bartoszdrozd.fitapp.utils.Result
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val getWorkoutUseCase: GetWorkoutUseCase,
    private val saveWorkoutUseCase: SaveWorkoutUseCase,
    private val gson: Gson
) : ViewModel() {
    private val _workoutUiState: MutableStateFlow<WorkoutUiState> =
        MutableStateFlow(WorkoutUiState())
    private val _openExercises: MutableStateFlow<List<Long>> = MutableStateFlow(emptyList())

    val workoutUiState: StateFlow<WorkoutUiState> = _workoutUiState
    val openExercises: StateFlow<List<Long>> = _openExercises


    // Keep track of last temporary ID
    private var lastTempIndex: Long = -1
        get() {
            return field--
        }

    fun loadWorkout(id: Long) {
        viewModelScope.launch {
            getWorkoutUseCase(id).collect {
                when (it) {
                    is Result.Success -> _workoutUiState.value = WorkoutUiState(it.data, false)
                    is Result.Error -> TODO()
                    is Result.Loading -> _workoutUiState.value =
                        _workoutUiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun saveWorkout() {
        // Mark workout as finished when saving
        val workout = if (_workoutUiState.value.workout!!.endDate == null) {
            getWorkoutCopy().copy(endDate = Clock.System.now())
        } else {
            _workoutUiState.value.workout!!
        }

        viewModelScope.launch {
            val res = saveWorkoutUseCase(workout)

            // Load workout only if a new one was being added
            if (res is Result.Success && workoutUiState.value.workout?.id == 0L) {
                loadWorkout(res.data)
            }
        }
    }

    private fun getWorkoutCopy(): Workout = gson.fromJson(
        gson.toJson(_workoutUiState.value.workout!!, Workout::class.java),
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

    fun addExercise(exerciseInfoId: Int) {
        val workout = getWorkoutCopy()

        val exercises = workout.exercises.toMutableList()
        val exercise = Exercise(lastTempIndex, exerciseInfoId)
        exercises.add(exercise)
        workout.exercises = exercises

        updateWorkoutState(workout)
        _openExercises.value = _openExercises.value.toMutableList().also { it.add(exercise.id) }
    }

    fun addSet(exercise: Exercise) {
        val workout = getWorkoutCopy()

        val sets = workout.exercises.find { it.id == exercise.id }!!.sets.toMutableList()
        sets.add(WorkoutSet(lastTempIndex, 0, 0.0, false))
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

    fun onClickExpand(id: Long) {
        _openExercises.value = _openExercises.value.toMutableList().also {
            if (it.contains(id)) it.remove(id) else it.add(id)
        }
    }

    private fun updateWorkoutState(workout: Workout) {
        _workoutUiState.value = _workoutUiState.value.copy(workout = workout, isDirty = true)
    }
}

data class WorkoutUiState(
    val workout: Workout? = null,
    val isLoading: Boolean = false,
    val isDirty: Boolean = false,
)