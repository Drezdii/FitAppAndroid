package com.bartoszdrozd.fitapp.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bartoszdrozd.fitapp.domain.workout.GetCompletedAndActiveWorkouts
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.EventType
import com.bartoszdrozd.fitapp.utils.ResultValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutListViewModel @Inject constructor(
    private val getUserWorkoutsUseCase: GetCompletedAndActiveWorkouts,
) : ViewModel() {
    private val _workouts: MutableStateFlow<List<Workout>> =
        MutableStateFlow(emptyList())
    private val _eventsChannel = Channel<EventType<*>>();

    val workouts: StateFlow<List<Workout>> = _workouts
    val events = _eventsChannel.receiveAsFlow()

    fun getWorkouts() {
        viewModelScope.launch {
            getUserWorkoutsUseCase(Unit).collect {
                when (it) {
                    is ResultValue.Success -> {
                        _workouts.value = it.data.sortedByDescending { wrk ->
                            wrk.date
                        }
                    }
                    is ResultValue.Error -> {
                        _eventsChannel.send(EventType.Error(it.exception))
                    }
                    else -> {}
                }
            }
        }
    }
}