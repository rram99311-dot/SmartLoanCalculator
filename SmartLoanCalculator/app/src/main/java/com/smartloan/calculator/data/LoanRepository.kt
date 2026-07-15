package com.smartloan.calculator.data

import com.smartloan.calculator.domain.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class LoanRepository @Inject constructor(private val dao: HistoryDao) {
    fun history(): Flow<List<HistoryEntry>> = dao.observeAll()
    suspend fun save(input: LoanInput, result: LoanResult) = dao.insert(HistoryEntry(title = "₹${input.principal} loan", principal = input.principal, rate = input.annualRate, months = input.months, emi = result.emi))
    suspend fun delete(entry: HistoryEntry) = dao.delete(entry)
}
