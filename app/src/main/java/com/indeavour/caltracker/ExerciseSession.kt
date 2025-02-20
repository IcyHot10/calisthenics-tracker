package com.indeavour.caltracker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(foreignKeys = [ForeignKey(entity = Exercise::class, parentColumns =  ["exerciseId"], childColumns =  ["bi_id"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE), ForeignKey(entity = Exercise::class, parentColumns =  ["exerciseId"], childColumns =  ["legs_id"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE), ForeignKey(entity = Exercise::class, parentColumns =  ["exerciseId"], childColumns =  ["core_id"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE), ForeignKey(entity = Exercise::class, parentColumns =  ["exerciseId"], childColumns =  ["tri_id"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)])
data class ExerciseSession(
    @PrimaryKey val exerciseSessionId: Int,
    @ColumnInfo(name = "bi_id") val biId: Int,
    @ColumnInfo(name = "bi_progress") val biProgress: Int,
    @ColumnInfo(name = "legs_id") val legsId: Int,
    @ColumnInfo(name = "legs_progress") val legsProgress: Int,
    @ColumnInfo(name = "core_id") val coreId: Int,
    @ColumnInfo(name = "core_progress") val coreProgress: Int,
    @ColumnInfo(name = "tri_id") val trId: Int,
    @ColumnInfo(name = "tri_progress") val trProgress: Int,
    @ColumnInfo(name = "date_completed") val dateCompleted: String?
)