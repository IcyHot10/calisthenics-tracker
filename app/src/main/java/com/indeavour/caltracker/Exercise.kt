package com.indeavour.caltracker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = ExerciseGroup::class, parentColumns =  ["exerciseGroupId"], childColumns =  ["exercise_group_id"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)])
data class Exercise(
    @PrimaryKey val exerciseId: Int,
    @ColumnInfo(name = "exercise_group_id") val exerciseGroupId: Int,
    @ColumnInfo(name = "exercise_number") val exerciseNumber: Int?,
    @ColumnInfo(name = "exercise_description") val exerciseDescription: String?,
    @ColumnInfo(name = "exercise_progress") val exerciseProgress: Int?,
    @ColumnInfo(name = "sets") val sets: Int?,
    @ColumnInfo(name = "reps") val reps: Int?,
    @ColumnInfo(name = "currentReps") var currentReps: String?
)
