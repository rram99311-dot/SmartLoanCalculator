package com.smartloan.calculator.util

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.smartloan.calculator.domain.LoanResult
import java.io.File
import java.io.FileOutputStream

object Exporters {
    fun csv(context: Context, result: LoanResult): File = File(context.cacheDir, "amortization.csv").also { file -> file.printWriter().use { out -> out.println("Month,Opening,Principal,Interest,Extra,Closing"); result.rows.forEach { out.println("${it.month},${it.opening},${it.principal},${it.interest},${it.extra},${it.closing}") } } }
    fun pdf(context: Context, result: LoanResult): File = File(context.cacheDir, "loan-summary.pdf").also { file ->
        val document = PdfDocument(); val page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create()); val paint = Paint().apply { textSize = 16f }; val canvas = page.canvas
        canvas.drawText("Smart Loan Calculator", 48f, 60f, paint); canvas.drawText("Monthly EMI: ${result.emi}", 48f, 96f, paint); canvas.drawText("Total interest: ${result.totalInterest}", 48f, 122f, paint)
        document.finishPage(page); FileOutputStream(file).use(document::writeTo); document.close()
    }
}
