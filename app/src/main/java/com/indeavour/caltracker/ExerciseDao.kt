package com.indeavour.caltracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM Exercise")
    fun getAll(): List<Exercise>

    @Query("SELECT * FROM Exercise WHERE exerciseId IN (:exerciseIds)")
    fun getByIds(exerciseIds: IntArray): List<Exercise>

    @Query("SELECT * FROM Exercise WHERE exercise_group_id = :exerciseId")
    fun getByGroupId(exerciseId: Int): List<Exercise>

    @Query("SELECT * FROM Exercise WHERE exercise_group_id = :id AND " +
            "exercise_number LIKE :num LIMIT 1")
    fun getByGroupAndStep(id: Int, num: Int): Exercise

    @Query("SELECT e.exerciseId, e.exercise_group_id, e.exercise_description, e.exercise_number, e.exercise_progress, e.reps, e.sets, e.currentReps FROM Exercise e INNER JOIN ExerciseGroup eg ON e.exercise_group_id = eg.exerciseGroupId WHERE eg.isActive = True AND eg.progress = e.exercise_number ORDER BY eg.type")
    fun getByActive(): List<Exercise>

    @Insert
    fun insertAll(vararg exercises: Exercise)

    @Delete
    fun delete(exercise: Exercise)

    @Update(entity = Exercise::class)
    fun update(exercise: Exercise)
}