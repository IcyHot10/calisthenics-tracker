package com.indeavour.caltracker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class ExerciseGroup(
    @PrimaryKey val exerciseGroupId: Int,
    @ColumnInfo(name = "exercise_group_name") val exerciseGroupName: String?,
    @ColumnInfo(name = "progress") var progress: Int?,
    @ColumnInfo(name = "type") val type: Int?,
    @ColumnInfo(name = "isActive") var isActive: Boolean?
)