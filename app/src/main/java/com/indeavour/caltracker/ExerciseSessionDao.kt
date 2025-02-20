package com.indeavour.caltracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExerciseSessionDao {
    @Query("SELECT * FROM ExerciseSession")
    fun getAll(): List<ExerciseSession>

    @Query("SELECT * FROM ExerciseSession WHERE bi_id IN (:exerciseId)")
    fun getByBiId(exerciseId: Int): List<ExerciseSession>

    @Query("SELECT * FROM ExerciseSession WHERE legs_id IN (:exerciseId)")
    fun getByLegsId(exerciseId: Int): List<ExerciseSession>

    @Query("SELECT * FROM ExerciseSession WHERE core_id IN (:exerciseId)")
    fun getByCoreId(exerciseId: Int): List<ExerciseSession>

    @Query("SELECT * FROM ExerciseSession WHERE tri_id IN (:exerciseId)")
    fun getByTriId(exerciseId: Int): List<ExerciseSession>

    @Query("SELECT * FROM ExerciseSession WHERE tri_id = :exerciseId OR bi_id = :exerciseId OR legs_id = :exerciseId OR core_id = :exerciseId")
    fun getByExerciseId(exerciseId: Int): List<ExerciseSession>

    @Query("SELECT * FROM ExerciseSession WHERE tri_id = :exerciseId OR bi_id = :exerciseId OR legs_id = :exerciseId OR core_id = :exerciseId ORDER BY date_completed DESC LIMIT 10")
    fun getByExerciseIdLast10(exerciseId: Int): List<ExerciseSession>

    @Query("SELECT * FROM ExerciseSession ORDER BY date_completed DESC LIMIT 1")
    fun getByLatest(): ExerciseSession

    @Query("SELECT exerciseSessionId FROM ExerciseSession ORDER BY exerciseSessionId DESC LIMIT 1")
    fun getByLastId(): Int

    @Insert
    fun insertAll(vararg exerciseSessions: ExerciseSession)

    @Delete
    fun delete(exerciseSession: ExerciseSession)

    @Update(entity = ExerciseSession::class)
    fun update(exerciseSession: ExerciseSession)
}