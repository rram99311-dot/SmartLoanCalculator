package com.smartloan.calculator.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(primary = Color(0xFF155EEF), secondary = Color(0xFF455468), tertiary = Color(0xFF087443), surface = Color(0xFFF8FAFC))
private val Dark = darkColorScheme(primary = Color(0xFFB2CCFF), secondary = Color(0xFFC7D1E0), tertiary = Color(0xFF75E0A7))
@Composable fun SmartLoanTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) = MaterialTheme(colorScheme = if (darkTheme) Dark else Light, typography = Typography(), content = content)
