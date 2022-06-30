package com.bartoszdrozd.fitapp.ui.creator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bartoszdrozd.fitapp.domain.creator.CreateProgramUseCase
import com.bartoszdrozd.fitapp.domain.creator.SaveProgramCycleUseCase
import com.bartoszdrozd.fitapp.model.creator.Program
import com.bartoszdrozd.fitapp.model.creator.ProgramCycle
import com.bartoszdrozd.fitapp.model.program.ProgramValues
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.data
import com.bartoszdrozd.fitapp.utils.succeeded
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatorViewModel @Inject constructor(
    private val createProgramUseCase: CreateProgramUseCase,
    private val saveProgramCycleUseCase: SaveProgramCycleUseCase
) : ViewModel() {
    private val _currentPage = MutableStateFlow(0)
    private val _selectedProgram: MutableStateFlow<Program?> = MutableStateFlow(null)
    private val _workouts: MutableStateFlow<Map<Int, List<Workout>>> = MutableStateFlow(mapOf())
//    private val _canProceed = MutableStateFlow(false)

    val currentPage: StateFlow<Int> = _currentPage
    val selectedProgram: StateFlow<Program?> = _selectedProgram
    val workouts: StateFlow<Map<Int, List<Workout>>> = _workouts
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
        // Clear any leftover workouts from previous attempts
        _workouts.value = mapOf()
//        _canProceed.value = true
    }

    fun createWorkouts(config: ProgramValues) {
        viewModelScope.launch {
            val res = createProgramUseCase(config)
            if (res.succeeded) {
                _workouts.value = res.data!!
            }
        }
    }

    fun saveWorkouts() {
        val cycle = ProgramCycle(selectedProgram.value!!, workouts.value)
        viewModelScope.launch {
            saveProgramCycleUseCase(cycle)
        }
    }
//
//    fun setCanProceed(value: Boolean) {
//        _canProceed.value = value
//    }
}
