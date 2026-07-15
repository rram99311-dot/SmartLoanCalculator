package com.smartloan.calculator.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class LoanCalculatorTest {
    @Test fun zeroInterestSplitsPrincipalEvenly() { val result = LoanCalculator.calculate(LoanInput(1200.0, 0.0, 12)); assertEquals(100.0, result.emi, 0.001); assertEquals(0.0, result.totalInterest, 0.001) }
    @Test fun standardEmiHasExpectedRange() { val result = LoanCalculator.calculate(LoanInput(500000.0, 8.5, 60)); assertEquals(10258.0, result.emi, 5.0); assertEquals(60, result.rows.size) }
    @Test fun rejectsInvalidInput() { try { LoanCalculator.calculate(LoanInput(-1.0, 8.0, 12)); throw AssertionError("Expected failure") } catch (_: IllegalArgumentException) {} }
    @Test fun principalFromEmiSupportsNoInterest() { assertEquals(1200.0, LoanCalculator.principalFromEmi(100.0, 0.0, 12), 0.001) }
}
