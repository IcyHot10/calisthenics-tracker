package com.indeavour.caltracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import kotlin.concurrent.Volatile


@Database(entities = [Exercise::class, ExerciseGroup::class, ExerciseSession::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseGroupDao(): ExerciseGroupDao
    abstract fun exerciseSessionDao(): ExerciseSessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // If database is not initialized, create it.
                val instance = databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "exercise_db"
                ).build()
                val check = instance.exerciseDao().getAll()
                if (check.isEmpty()){
                    val oa = ExerciseGroup(0, "One Arm Pushups", 0, 0, true)
                    val hs = ExerciseGroup(1, "Hand Stand Pushups", 0, 0, false)
                    val pp = ExerciseGroup(2, "Planche Pushups", 0, 0, false)
                    val pu = ExerciseGroup(3, "Pull Ups", 0, 3, true)
                    val mu = ExerciseGroup(4, "Muscle Ups", 0, 3, false)
                    val d = ExerciseGroup(5, "Dips", 0, 3, false)
                    val s = ExerciseGroup(6, "Squats", 0, 1, true)
                    val l = ExerciseGroup(7, "Lunges", 0, 1, false)
                    val c = ExerciseGroup(8, "Bridges", 0, 2, true)
                    val b = ExerciseGroup(9, "Core", 0, 2, false)
                    val gs = ExerciseGroup(10, "Grip Strength", 0, 3, false)
                    instance.exerciseGroupDao().insertAll(oa, hs, pp, pu, mu, d, s, l, c, b, gs)
                    val oa1 = Exercise(0, 0, 0, "Normal 4x30", 0, 4, 30, listOf(null, null, null, null).toString())
                    val oa2 = Exercise(1, 0, 1, "Deep push ups 2x20",  0, 2, 20, listOf(null, null).toString())
                    val oa3 = Exercise(2, 0, 2, "Diamond 2x30", 0, 2, 30, listOf(null, null).toString())
                    val oa4 = Exercise(3, 0, 3, "Side Staggered 2x25", 0, 2, 25, listOf(null, null).toString())
                    val oa5 = Exercise(4, 0, 4, "Archer 2x25 each arm", 0, 2, 25, listOf(null, null).toString())
                    val oa6 = Exercise(5, 0, 5, "Sliding One Arm 2x20", 0, 2, 20, listOf(null, null).toString())
                    val hs1 = Exercise(10, 1, 0, "Normal 4x30", 0, 4, 30, listOf(null, null, null, null).toString())
                    val hs2 = Exercise(11, 1, 1, "Slight incline 3x25", 0, 3, 25, listOf(null, null, null).toString())
                    val hs3 = Exercise(12, 1, 2, "Large incline 3x25", 0, 3, 25, listOf(null, null, null).toString())
                    val hs4 = Exercise(13, 1, 3, "Well-supported handstand 3x15", 0, 3, 15, listOf(null, null, null).toString())
                    val hs5 = Exercise(14, 1, 4, "Minimal support handstand 3x10", 0, 3, 10, listOf(null, null, null).toString())
                    val pp1 = Exercise(20, 2, 0, "Normal 4x30", 0, 4, 30, listOf(null, null, null, null).toString())
                    val pp2 = Exercise(21, 2, 1, "Hands below chest 3x20", 0, 3, 20, listOf(null, null, null).toString())
                    val pp3 = Exercise(22, 2, 2, "Hands twisted 4x15", 0, 4, 15, listOf(null, null, null, null).toString())
                    val pp4 = Exercise(23, 2, 3, "Band assisted hands twisted 3x15", 0, 3, 15, listOf(null, null, null).toString())
                    val pp5 = Exercise(24, 2, 4, "Band assisted planche 3x10", 0, 3, 10, listOf(null, null, null).toString())
                    val pp6 = Exercise(25, 2, 5, "Planche 3x5", 0, 3, 5, listOf(null, null, null).toString())
                    val pu1 = Exercise(30, 3, 0, "Jack Knife 4x20", 0, 4, 20, listOf(null, null, null, null).toString())
                    val pu2 = Exercise(31, 3, 1, "Negative regular 5x15 (3s)", 0, 5, 15, listOf(null, null, null, null, null).toString())
                    val pu3 = Exercise(32, 3, 2, "Regular 3x12", 0, 3, 12, listOf(null, null, null).toString())
                    val pu4 = Exercise(33, 3, 3, "Towel assisted one arm 4x8", 0, 4, 8, listOf(null, null, null, null).toString())
                    val pu5 = Exercise(34, 3, 4, "One arm 3x10", 0, 3, 10, listOf(null, null, null).toString())
                    val mu1 = Exercise(40, 4, 0, "Negative MU 4x10 (5s)", 0, 4, 10, listOf(null, null, null, null).toString())
                    val mu2 = Exercise(41, 4, 1, "Explosive chest to bar PU 3x5", 0, 3, 5, listOf(null, null, null).toString())
                    val mu3 = Exercise(42, 4, 2, "Band assisted MU 4x10", 0, 4, 10, listOf(null, null, null, null).toString())
                    val mu4 = Exercise(43, 4, 3, "Regular MU 3x8", 0, 3, 8, listOf(null, null, null).toString())
                    val mu5 = Exercise(44, 4, 4, "Strict MU 3x5", 0, 3, 5, listOf(null, null, null).toString())
                    val d1 = Exercise(50, 5, 0, "Band assisted parallel dips 4x20", 0, 4, 20, listOf(null, null, null, null).toString())
                    val d2 = Exercise(51, 5, 1, "Parallel dips 3x12", 0, 3, 12, listOf(null, null, null).toString())
                    val d3 = Exercise(52, 5, 2, "Top of Bar dips 3x12", 0, 3, 12, listOf(null, null, null).toString())
                    val d4 = Exercise(53, 5, 3, "Weighted parallel dips 3x12", 0, 3, 12, listOf(null, null, null).toString())
                    val s1 = Exercise(60, 6, 0, "Bodyweight 3x50", 0, 3, 50, listOf(null, null, null).toString())
                    val s2 = Exercise(61, 6, 1, "Touchdowns 4x20 low (each leg)", 0, 4, 20, listOf(null, null, null, null).toString())
                    val s3 = Exercise(62, 6, 2, "Touchdowns 4x20 high (each leg)", 0, 4, 20, listOf(null, null, null, null).toString())
                    val s4 = Exercise(63, 6, 3, "Assisted Pistol 3x12 (each leg)", 0, 3, 12, listOf(null, null, null).toString())
                    val s5 = Exercise(64, 6, 4, "Pistol 3x10 (each leg)", 0, 3, 10, listOf(null, null, null).toString())
                    val s6 = Exercise(65, 6, 5, "Dragon pistol squats 3x5", 0, 3, 5, listOf(null, null, null).toString())
                    val l1 = Exercise(70, 7, 0, "Bodyweight 3x30 (each leg)", 0, 3, 30, listOf(null, null, null).toString())
                    val l2 = Exercise(71, 7, 1, "Weighted 3x30", 0, 3, 30, listOf(null, null, null).toString())
                    val l3 = Exercise(72, 7, 2, "Explosive bodyweight 5x15", 0, 5, 15, listOf(null, null, null, null, null).toString())
                    val l4 = Exercise(73, 7, 3, "Explosive weighted 5x15", 0, 5, 15, listOf(null, null, null, null, null).toString())
                    val l5 = Exercise(74, 7, 4, "Heavy bulgarian split squats 4x10", 0, 4, 10, listOf(null, null, null, null).toString())
                    val b1 = Exercise(80, 9, 0, "Lying leg raises 3x30", 0, 3, 30, listOf(null, null, null).toString())
                    val b2 = Exercise(81, 9, 1, "Hanging knee raises 5x10", 0, 5, 10, listOf(null, null, null, null, null).toString())
                    val b3 = Exercise(82, 9, 2, "Hanging leg raises 5x10", 0, 5, 10, listOf(null, null, null, null, null).toString())
                    val b4 = Exercise(83, 9, 3, "Band assisted L sit 3x30", 0, 3, 30, listOf(null, null, null).toString())
                    val b5 = Exercise(84, 9, 4, "L sit 2x30", 0, 2, 30, listOf(null, null).toString())
                    val c1 = Exercise(90, 8, 0, "Regular 3x20", 0, 3, 20, listOf(null, null, null).toString())
                    val c2 = Exercise(91, 8, 1, "Elevated Leg 3x20", 0, 3, 20, listOf(null, null, null).toString())
                    val c3 = Exercise(92, 8, 2, "One Leg Gecko Bridges 3x20", 0, 3, 20, listOf(null, null, null).toString())
                    val c4 = Exercise(93, 8, 3, "Weighted Regular 3x20", 0, 3, 20, listOf(null, null, null).toString())
                    val gs1 = Exercise(100, 10, 0, "2 hand 3x30s hold", 0, 3, 30, listOf(null, null, null).toString())
                    val gs2 = Exercise(101, 10, 1, "2 hand 3x45s hold", 0, 3, 45, listOf(null, null, null).toString())
                    val gs3 = Exercise(102, 10, 2, "2 hand 3x60s hold", 0, 3, 60, listOf(null, null, null).toString())
                    val gs4 = Exercise(103, 10, 3, "1 hand w/ towel 3x45s hold", 0, 3, 45, listOf(null, null, null).toString())
                    val gs5 = Exercise(104, 10, 4, "1 hand 60s each hold", 0, 1, 60, listOf(null).toString())
                    instance.exerciseDao().insertAll(oa1, oa2, oa3, oa4, oa5, oa6, hs1, hs2, hs3, hs4, hs5,
                        pp1, pp2, pp3, pp4, pp5, pp6, pu1, pu2, pu3, pu4, pu5, mu1, mu2, mu3, mu4, mu5,
                        d1, d2, d3, d4, s1, s2, s3, s4, s5, s6, l1, l2, l3, l4, l5, c1, c2, c3, c4,
                        b1, b2, b3, b4, b5, gs1, gs2, gs3, gs4, gs5)
                }
                INSTANCE = instance
                instance
            }
        }
    }
}