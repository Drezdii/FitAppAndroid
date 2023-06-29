package com.bartoszdrozd.fitapp.ui.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bartoszdrozd.fitapp.domain.challenges.GetChallengesUseCase
import com.bartoszdrozd.fitapp.model.challenges.ChallengeEntry
import com.bartoszdrozd.fitapp.utils.ResultValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val getChallengesUseCase: GetChallengesUseCase
) : ViewModel() {
    private val _challenges: MutableStateFlow<List<ChallengeEntry>> =
        MutableStateFlow(emptyList())
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val challenges: StateFlow<List<ChallengeEntry>> = _challenges
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadChallenges() {
        viewModelScope.launch {
            getChallengesUseCase(Unit).collect { res ->
                if (res is ResultValue.Success) {
                    _challenges.value =
                        res.data.sortedBy { it.challengeId }.sortedBy { it.completedAt }
                }
            }
        }
    }
}