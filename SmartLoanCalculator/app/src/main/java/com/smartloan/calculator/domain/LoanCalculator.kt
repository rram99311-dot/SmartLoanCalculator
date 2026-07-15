package com.smartloan.calculator.domain

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.pow

data class LoanInput(val principal: Double, val annualRate: Double, val months: Int, val fee: Double = 0.0, val insurance: Double = 0.0, val extraPayment: Double = 0.0)
data class PaymentRow(val month: Int, val opening: Double, val principal: Double, val interest: Double, val extra: Double, val closing: Double)
data class LoanResult(val emi: Double, val totalInterest: Double, val totalPayment: Double, val rows: List<PaymentRow>)

object LoanCalculator {
    fun calculate(input: LoanInput): LoanResult {
        require(input.principal > 0) { "Loan amount must be greater than zero." }; require(input.months > 0) { "Tenure must be at least one month." }; require(input.annualRate in 0.0..100.0) { "Interest rate must be between 0% and 100%." }
        val monthlyRate = input.annualRate / 1200.0
        val baseEmi = if (monthlyRate == 0.0) input.principal / input.months else input.principal * monthlyRate * (1 + monthlyRate).pow(input.months) / ((1 + monthlyRate).pow(input.months) - 1)
        val emi = baseEmi + input.extraPayment
        var balance = input.principal
        val rows = buildList {
            for (month in 1..input.months) {
                if (balance <= 0.005) break
                val interest = balance * monthlyRate
                val scheduledPrincipal = (baseEmi - interest).coerceAtLeast(0.0)
                val principal = minOf(balance, scheduledPrincipal)
                val extra = minOf((balance - principal).coerceAtLeast(0.0), input.extraPayment)
                val closing = (balance - principal - extra).coerceAtLeast(0.0)
                add(PaymentRow(month, balance, principal, interest, extra, closing)); balance = closing
            }
        }
        val interest = rows.sumOf { it.interest }
        return LoanResult(round(emi), round(interest), round(input.principal + interest + input.fee + input.insurance), rows)
    }
    fun principalFromEmi(emi: Double, annualRate: Double, months: Int): Double { val r = annualRate / 1200; return round(if (r == 0.0) emi * months else emi * ((1 + r).pow(months) - 1) / (r * (1 + r).pow(months))) }
    fun simpleInterest(principal: Double, rate: Double, years: Double) = round(principal * rate * years / 100)
    fun compoundAmount(principal: Double, rate: Double, years: Double, periods: Int) = round(principal * (1 + rate / (100 * periods)).pow(periods * years))
    fun round(value: Double) = BigDecimal(value, MathContext.DECIMAL64).setScale(2, RoundingMode.HALF_UP).toDouble()
}
