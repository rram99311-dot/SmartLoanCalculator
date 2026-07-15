package com.smartloan.calculator.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "calculation_history") data class HistoryEntry(@PrimaryKey(autoGenerate = true) val id: Long = 0, val title: String, val principal: Double, val rate: Double, val months: Int, val emi: Double, val createdAt: Long = System.currentTimeMillis(), val favorite: Boolean = false)
@Dao interface HistoryDao { @Query("SELECT * FROM calculation_history ORDER BY createdAt DESC") fun observeAll(): Flow<List<HistoryEntry>>; @Insert suspend fun insert(entry: HistoryEntry); @Delete suspend fun delete(entry: HistoryEntry) }
@Database(entities = [HistoryEntry::class], version = 1, exportSchema = false) abstract class LoanDatabase : RoomDatabase() { abstract fun historyDao(): HistoryDao }
