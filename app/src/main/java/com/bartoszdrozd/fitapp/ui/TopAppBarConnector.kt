package com.bartoszdrozd.fitapp.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TopAppBarConnector {
    private val _state = MutableStateFlow(TopAppBarState({}, {}, true))
    val appBarState: StateFlow<TopAppBarState> = _state

    private var _defaultState = TopAppBarState({}, {})

    suspend fun setAppBarState(state: TopAppBarState) {
        _state.emit(state)
    }

    fun setDefaultState(state: TopAppBarState) {
        _defaultState = state
    }

    suspend fun resetToDefault() {
        _state.emit(_defaultState)
    }
}