package com.bartoszdrozd.fitapp.ui.workout.planned

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bartoszdrozd.fitapp.domain.workout.GetPlannedWorkoutsUseCase
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.ResultValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlannedWorkoutsViewModel @Inject constructor(
    private val getPlannedWorkoutsUseCase: GetPlannedWorkoutsUseCase
) : ViewModel() {
    private val _workouts: MutableStateFlow<List<Workout>> =
        MutableStateFlow(emptyList())
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val workouts: StateFlow<List<Workout>> = _workouts

    fun getPlannedWorkouts() {
        viewModelScope.launch {
            getPlannedWorkoutsUseCase(Unit).collect {
                if (it !is ResultValue.Loading) {
                    _isLoading.value = false
                }

                when (it) {
                    is ResultValue.Success -> {
                        _workouts.value = it.data.sortedByDescending { wrk ->
                            wrk.date
                        }
                    }
                    is ResultValue.Error -> {
                        // Handle error here
                        Log.e("HistoryViewModel", "Error while getting completed workouts.")
                    }
                    is ResultValue.Loading -> _isLoading.value = true
                }
            }
        }
    }
}