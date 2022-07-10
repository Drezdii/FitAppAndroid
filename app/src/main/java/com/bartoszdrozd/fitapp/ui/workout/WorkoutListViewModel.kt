package com.bartoszdrozd.fitapp.ui.workout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bartoszdrozd.fitapp.domain.workout.GetUserWorkoutsUseCase
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutListViewModel @Inject constructor(
    private val getUserWorkoutsUseCase: GetUserWorkoutsUseCase,
) : ViewModel() {
    private val _workouts: MutableStateFlow<List<Workout>> =
        MutableStateFlow(emptyList())
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val workouts: StateFlow<List<Workout>> = _workouts
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getWorkouts() {
        viewModelScope.launch {
            getUserWorkoutsUseCase(Unit).collect {
                if (it !is Result.Loading) {
                    _isLoading.value = false
                }

                when (it) {
                    is Result.Success -> {
                        _workouts.value = it.data.sortedByDescending { wrk ->
                            wrk.date
                        }
                    }
                    is Result.Error -> {
                        // Handle error here
                        Log.e("WorkoutListViewModel", "Error while getting workouts.")
                    }
                    is Result.Loading -> _isLoading.value = true
                }
            }
        }
    }
}