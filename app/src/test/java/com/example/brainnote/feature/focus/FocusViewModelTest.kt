package com.example.brainnote.feature.focus

import com.example.brainnote.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FocusViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: FocusViewModel

    @Before
    fun setup() {
        viewModel = FocusViewModel()
    }

    @Test
    fun `initial state is correct`() {
        val state = viewModel.uiState.value
        assertEquals(FocusState.FOCUSING, state.focusState)
        assertFalse(state.isRunning)
        assertEquals(25, state.focusDurationMinutes)
        assertEquals(5, state.breakDurationMinutes)
        assertEquals(25 * 60, state.timeRemaining)
        assertFalse(state.showSettingsDialog)
    }

    @Test
    fun `togglePlayPause toggles isRunning state`() {
        assertFalse(viewModel.uiState.value.isRunning)

        viewModel.togglePlayPause()
        assertTrue(viewModel.uiState.value.isRunning)

        viewModel.togglePlayPause()
        assertFalse(viewModel.uiState.value.isRunning)
    }

    @Test
    fun `timer ticks and decrements timeRemaining when running`() = runTest {
        viewModel.togglePlayPause() // Start timer
        assertTrue(viewModel.uiState.value.isRunning)

        val initialTime = viewModel.uiState.value.timeRemaining

        // Advance coroutine virtual time by 2 seconds
        advanceTimeBy(2010L) 

        assertEquals(initialTime - 2, viewModel.uiState.value.timeRemaining)
    }

    @Test
    fun `timer transitions to break state when focusing timer reaches zero`() = runTest {
        // Update settings to 1 minute for faster testing
        viewModel.updateSettings(1, 5)
        assertEquals(60, viewModel.uiState.value.timeRemaining)

        viewModel.togglePlayPause() // Start timer
        assertTrue(viewModel.uiState.value.isRunning)

        // Advance time by 60 seconds (60 ticks)
        advanceTimeBy(60010L)

        val state = viewModel.uiState.value
        assertEquals(FocusState.BREAKING, state.focusState)
        assertFalse(state.isRunning)
        assertEquals(5 * 60, state.timeRemaining)
    }

    @Test
    fun `timer transitions to focus state when breaking timer reaches zero`() = runTest {
        // Transition to BREAKING state first
        viewModel.skipSession()
        assertEquals(FocusState.BREAKING, viewModel.uiState.value.focusState)
        assertEquals(5 * 60, viewModel.uiState.value.timeRemaining)

        // Update break settings to 1 minute
        viewModel.updateSettings(25, 1)
        assertEquals(60, viewModel.uiState.value.timeRemaining)

        viewModel.togglePlayPause() // Start break timer
        assertTrue(viewModel.uiState.value.isRunning)

        // Advance time by 60 seconds
        advanceTimeBy(60010L)

        val state = viewModel.uiState.value
        assertEquals(FocusState.FOCUSING, state.focusState)
        assertFalse(state.isRunning)
        assertEquals(25 * 60, state.timeRemaining)
    }

    @Test
    fun `resetTimer stops timer and resets timeRemaining`() = runTest {
        viewModel.togglePlayPause()
        advanceTimeBy(5000L) // tick 5 seconds
        assertTrue(viewModel.uiState.value.timeRemaining < 25 * 60)

        viewModel.resetTimer()
        val state = viewModel.uiState.value
        assertFalse(state.isRunning)
        assertEquals(25 * 60, state.timeRemaining)
    }

    @Test
    fun `skipSession transitions state and resets duration`() {
        // Start in focusing mode
        assertEquals(FocusState.FOCUSING, viewModel.uiState.value.focusState)

        viewModel.skipSession()
        assertEquals(FocusState.BREAKING, viewModel.uiState.value.focusState)
        assertEquals(5 * 60, viewModel.uiState.value.timeRemaining)

        viewModel.skipSession()
        assertEquals(FocusState.FOCUSING, viewModel.uiState.value.focusState)
        assertEquals(25 * 60, viewModel.uiState.value.timeRemaining)
    }

    @Test
    fun `updateSettings updates durations and resets timer`() {
        viewModel.togglePlayPause()
        assertTrue(viewModel.uiState.value.isRunning)

        viewModel.updateSettings(30, 10)

        val state = viewModel.uiState.value
        assertEquals(30, state.focusDurationMinutes)
        assertEquals(10, state.breakDurationMinutes)
        assertEquals(30 * 60, state.timeRemaining)
        assertFalse(state.isRunning)
        assertFalse(state.showSettingsDialog)
    }

    @Test
    fun `setShowSettingsDialog updates dialog state`() {
        assertFalse(viewModel.uiState.value.showSettingsDialog)

        viewModel.setShowSettingsDialog(true)
        assertTrue(viewModel.uiState.value.showSettingsDialog)

        viewModel.setShowSettingsDialog(false)
        assertFalse(viewModel.uiState.value.showSettingsDialog)
    }
}
