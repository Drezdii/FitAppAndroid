package com.bartoszdrozd.fitapp.ui.progress

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bartoszdrozd.fitapp.domain.progress.GetClosestChallengesUseCase
import com.bartoszdrozd.fitapp.domain.progress.GetPBsUseCase
import com.bartoszdrozd.fitapp.model.challenges.ChallengeEntry
import com.bartoszdrozd.fitapp.model.stats.PersonalBest
import com.bartoszdrozd.fitapp.utils.ResultValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressScreenViewModel @Inject constructor(
    private val getPBsUseCase: GetPBsUseCase,
    private val getClosestChallengesUseCase: GetClosestChallengesUseCase
) :
    ViewModel() {
    private val _personalBests = MutableStateFlow<List<PersonalBest>>(listOf())
    private val _closestChallenges = MutableStateFlow<List<ChallengeEntry>>(listOf())

    val personalBests: StateFlow<List<PersonalBest>> = _personalBests
    val closestChallenges: StateFlow<List<ChallengeEntry>> = _closestChallenges

    fun getPersonalBests() {
        viewModelScope.launch {
            getPBsUseCase(Unit).collect {
                when (it) {
                    is ResultValue.Success -> _personalBests.value = it.data
                    is ResultValue.Error -> Log.d("TEST", it.exception.toString())
                    ResultValue.Loading -> TODO()
                }
            }
        }
    }

    fun getClosestChallenges() {
        viewModelScope.launch {
            getClosestChallengesUseCase(Unit).collect {
                when (it) {
                    is ResultValue.Success -> _closestChallenges.value = it.data
                    is ResultValue.Error -> Log.d("TEST", it.exception.toString())
                    ResultValue.Loading -> TODO()
                }
            }
        }
    }
}