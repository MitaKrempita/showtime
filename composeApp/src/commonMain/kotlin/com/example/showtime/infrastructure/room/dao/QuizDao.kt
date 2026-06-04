package com.example.showtime.infrastructure.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.showtime.infrastructure.room.entity.QuizSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: QuizSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<QuizSessionEntity>)

    @Query("SELECT * FROM quiz_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<QuizSessionEntity>>

    @Query("SELECT MAX(score) FROM quiz_sessions")
    fun getBestScore(): Flow<Float?>

    @Query("SELECT COUNT(*) FROM quiz_sessions")
    fun getGamesPlayedCount(): Flow<Int>
}