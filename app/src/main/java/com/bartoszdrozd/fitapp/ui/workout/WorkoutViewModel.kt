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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val getWorkoutUseCase: GetWorkoutUseCase,
    private val saveWorkoutUseCase: SaveWorkoutUseCase,
    private val gson: Gson
) : ViewModel() {
    private val _workoutUiState: MutableStateFlow<WorkoutUiState> =
        MutableStateFlow(WorkoutUiState())
    val workoutUiState: StateFlow<WorkoutUiState> = _workoutUiState

    // Keep track of last temporary ID
    private var lastTempIndex: Int = -1
        get() {
            return field--
        }

    fun loadWorkout(id: Int) {
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
        viewModelScope.launch {
            saveWorkoutUseCase(_workoutUiState.value.workout!!)
        }
    }

    private fun getWorkoutCopy(): Workout = gson.fromJson(
        gson.toJson(_workoutUiState.value.workout!!, Workout::class.java),
        Workout::class.java
    )

    fun deleteSet(set: WorkoutSet, exerciseId: Int) {
        val workout = getWorkoutCopy()

        val exercise = workout.exercises.find { it.id == exerciseId }!!
        val sets = exercise.sets.toMutableList()
        sets.remove(set)
        exercise.sets = sets

        updateState(workout)
    }

    fun updateSet(set: WorkoutSet, exerciseId: Int) {
        val workout = getWorkoutCopy()

        val sets = workout.exercises.find { it.id == exerciseId }!!.sets.toMutableList()
        sets.find { it.id == set.id }?.apply {
            reps = set.reps
            weight = set.weight
            completed = set.completed
        }
        workout.exercises.find { it.id == exerciseId }!!.sets = sets

        updateState(workout)
    }

    fun addExercise(exerciseInfoId: Int) {
        val workout = getWorkoutCopy()

        val exercises = workout.exercises.toMutableList()
        exercises.add(Exercise(lastTempIndex, exerciseInfoId))
        workout.exercises = exercises

        updateState(workout)
    }

    fun addSet(exercise: Exercise) {
        val workout = getWorkoutCopy()

        val sets = workout.exercises.find { it.id == exercise.id }!!.sets.toMutableList()
        sets.add(WorkoutSet(lastTempIndex, 0, 0.0, false))
        workout.exercises.find { it.id == exercise.id }!!.sets = sets

        updateState(workout)
    }

    fun deleteExercise(exercise: Exercise) {
        val workout = getWorkoutCopy()

        val exercises = workout.exercises.toMutableList()
        exercises.remove(exercise)
        workout.exercises = exercises

        updateState(workout)
    }

    private fun updateState(workout: Workout) {
        _workoutUiState.value = _workoutUiState.value.copy(workout = workout, isDirty = true)
    }
}

data class WorkoutUiState(
    val workout: Workout? = null,
    val isLoading: Boolean = false,
    val isDirty: Boolean = false
)