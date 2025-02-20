package com.indeavour.caltracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExerciseGroupDao {
    @Query("SELECT * FROM ExerciseGroup")
    fun getAll(): List<ExerciseGroup>

    @Query("SELECT * FROM ExerciseGroup WHERE exerciseGroupId IN (:exerciseGroupIds)")
    fun getByGroupId(exerciseGroupIds: IntArray): List<ExerciseGroup>

    @Query("SELECT exerciseGroupId, Count(*) AS count FROM ExerciseGroup INNER JOIN Exercise ON exerciseGroupId = exercise_group_id GROUP BY exerciseGroupId")
    fun getByNumInGroups(): List<GroupCountTuple>

    @Query("SELECT * FROM ExerciseGroup WHERE type = :exerciseType")
    fun getByType(exerciseType: Int): List<ExerciseGroup>

    @Query("SELECT * FROM ExerciseGroup WHERE exercise_group_name LIKE :name LIMIT 1")
    fun getByName(name: String): ExerciseGroup

    @Query("SELECT * FROM ExerciseGroup WHERE isActive = True")
    fun getByActive(): List<ExerciseGroup>

    @Insert
    fun insertAll(vararg exerciseGroups: ExerciseGroup)

    @Delete
    fun delete(exerciseGroup: ExerciseGroup)

    @Update(entity = ExerciseGroup::class)
    fun update(exerciseGroup: ExerciseGroup)
}