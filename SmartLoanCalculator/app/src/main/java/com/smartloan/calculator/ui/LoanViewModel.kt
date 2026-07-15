package com.smartloan.calculator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartloan.calculator.data.*
import com.smartloan.calculator.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoanUiState(val amount: String = "500000", val rate: String = "8.5", val months: String = "60", val fee: String = "0", val extra: String = "0", val result: LoanResult? = null, val error: String? = null)
@HiltViewModel class LoanViewModel @Inject constructor(private val repository: LoanRepository, private val settingsRepository: SettingsRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(LoanUiState()); val state: StateFlow<LoanUiState> = mutableState.asStateFlow()
    val history = repository.history().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()); val settings = settingsRepository.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())
    fun update(block: (LoanUiState) -> LoanUiState) { mutableState.update(block) }
    fun calculate() { val s = state.value; runCatching { LoanCalculator.calculate(LoanInput(s.amount.toDouble(), s.rate.toDouble(), s.months.toInt(), s.fee.toDouble(), extraPayment = s.extra.toDouble())) }.onSuccess { result -> update { it.copy(result = result, error = null) }; viewModelScope.launch { repository.save(LoanInput(s.amount.toDouble(),s.rate.toDouble(),s.months.toInt()), result) } }.onFailure { update { it.copy(error = it.message ?: "Enter valid values") } } }
    fun delete(entry: HistoryEntry) = viewModelScope.launch { repository.delete(entry) }
    fun currency(value: String) = viewModelScope.launch { settingsRepository.setCurrency(value) }
    fun darkMode(value: Boolean) = viewModelScope.launch { settingsRepository.setDarkMode(value) }
}
