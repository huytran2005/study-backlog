package com.example.brainnote.feature.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FocusUiState(
    val focusState: FocusState = FocusState.FOCUSING,
    val isRunning: Boolean = false,
    val focusDurationMinutes: Int = 25,
    val breakDurationMinutes: Int = 5,
    val timeRemaining: Int = 25 * 60,
    val showSettingsDialog: Boolean = false
) {
    val focusDuration: Int get() = focusDurationMinutes * 60
    val breakDuration: Int get() = breakDurationMinutes * 60
}

class FocusViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun togglePlayPause() {
        _uiState.update { state ->
            val nextRunning = !state.isRunning
            if (nextRunning) {
                startTimer()
            } else {
                stopTimer()
            }
            state.copy(isRunning = nextRunning)
        }
    }

    fun resetTimer() {
        stopTimer()
        _uiState.update { state ->
            state.copy(
                isRunning = false,
                timeRemaining = if (state.focusState == FocusState.FOCUSING) state.focusDuration else state.breakDuration
            )
        }
    }

    fun skipSession() {
        stopTimer()
        _uiState.update { state ->
            val nextState = if (state.focusState == FocusState.FOCUSING) FocusState.BREAKING else FocusState.FOCUSING
            val nextDuration = if (nextState == FocusState.FOCUSING) state.focusDuration else state.breakDuration
            state.copy(
                focusState = nextState,
                isRunning = false,
                timeRemaining = nextDuration
            )
        }
    }

    fun updateSettings(focusMinutes: Int, breakMinutes: Int) {
        stopTimer()
        _uiState.update { state ->
            val nextDuration = if (state.focusState == FocusState.FOCUSING) focusMinutes * 60 else breakMinutes * 60
            state.copy(
                focusDurationMinutes = focusMinutes,
                breakDurationMinutes = breakMinutes,
                timeRemaining = nextDuration,
                isRunning = false,
                showSettingsDialog = false
            )
        }
    }

    fun setShowSettingsDialog(show: Boolean) {
        _uiState.update { state ->
            state.copy(showSettingsDialog = show)
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                var finished = false
                _uiState.update { state ->
                    if (state.timeRemaining > 1) {
                        state.copy(timeRemaining = state.timeRemaining - 1)
                    } else {
                        // Timer finished
                        finished = true
                        val nextState = if (state.focusState == FocusState.FOCUSING) FocusState.BREAKING else FocusState.FOCUSING
                        val nextDuration = if (nextState == FocusState.FOCUSING) state.focusDuration else state.breakDuration
                        state.copy(
                            focusState = nextState,
                            isRunning = false,
                            timeRemaining = nextDuration
                        )
                    }
                }
                if (finished) {
                    break
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
