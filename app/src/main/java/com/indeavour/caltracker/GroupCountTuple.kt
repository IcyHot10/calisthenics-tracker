package com.indeavour.caltracker

import androidx.room.ColumnInfo

data class GroupCountTuple(
@ColumnInfo(name = "exerciseGroupId") val exerciseGroupId: Int?,
@ColumnInfo(name = "count") val count: Int?
)
