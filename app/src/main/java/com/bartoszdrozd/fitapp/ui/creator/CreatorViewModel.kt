package com.bartoszdrozd.fitapp.ui.creator

import androidx.lifecycle.ViewModel
import com.bartoszdrozd.fitapp.model.creator.Program
import com.bartoszdrozd.fitapp.model.workout.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CreatorViewModel : ViewModel() {
    private val _currentPage = MutableStateFlow(0)
    private val _selectedProgram: MutableStateFlow<Program?> = MutableStateFlow(null)
    private val _workouts: MutableStateFlow<List<Workout>> = MutableStateFlow(listOf())
//    private val _canProceed = MutableStateFlow(false)

    val currentPage: StateFlow<Int> = _currentPage
    val selectedProgram: StateFlow<Program?> = _selectedProgram
    val workouts: StateFlow<List<Workout>> = _workouts
//    val canProceed: StateFlow<Boolean> = _canProceed

    fun nextPage() {
        // Return if it's the last page
        if (_currentPage.value == 1) return

        _currentPage.value += 1
    }

    fun previousPage() {
        // Return if it's the first page
        if (_currentPage.value == 0) return

        _currentPage.value -= 1
    }

    fun selectProgram(program: Program) {
        _selectedProgram.value = program
        _workouts.value = listOf()
//        _canProceed.value = true
    }

    fun setWorkouts(workouts: List<Workout>) {
        _workouts.value = workouts
    }
//
//    fun setCanProceed(value: Boolean) {
//        _canProceed.value = value
//    }
}
