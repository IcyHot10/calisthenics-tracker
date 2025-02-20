package com.indeavour.caltracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FullLayout : Fragment() {
    val database by lazy { AppDatabase.getDatabase(this.requireContext()) }
    val exerciseComponentMap = mapOf<Int, Int>(0 to R.id.textViewOA, 1 to R.id.textViewHS, 2 to R.id.textViewPP, 3 to R.id.textViewPU, 4 to R.id.textViewMU, 5 to R.id.textViewDips, 6 to R.id.textViewSquats, 7 to R.id.textViewLunges, 8 to R.id.textViewCore, 9 to R.id.textViewBridges, 10 to R.id.textViewGS)
    val stepComponentMap = mapOf<Int, Int>(0 to R.id.textViewCurrentOA, 1 to R.id.textViewCurrentHS, 2 to R.id.textViewCurrentPP, 3 to R.id.textViewCurrentPU, 4 to R.id.textViewCurrentMU, 5 to R.id.textViewCurrentDips, 6 to R.id.textViewCurrentSquats, 7 to R.id.textViewCurrentLunges, 8 to R.id.textViewCurrentCore, 9 to R.id.textViewCurrentBridges, 10 to R.id.textViewCurrentGS)
    val stepProgressComponentMap = mapOf<Int, Int>(0 to R.id.editTextNumberOA, 1 to R.id.editTextNumberHS, 2 to R.id.editTextNumberPP, 3 to R.id.editTextNumberPU, 4 to R.id.editTextNumberMU, 5 to R.id.editTextNumberDips, 6 to R.id.editTextNumberSquats, 7 to R.id.editTextNumberLunges, 8 to R.id.editTextNumberCore, 9 to R.id.editTextNumberBridges, 10 to R.id.editTextNumberGS)
    val nextButtonComponentMap = mapOf<Int, Int>(0 to R.id.floatingActionButtonNextOA, 1 to R.id.floatingActionButtonNextHS, 2 to R.id.floatingActionButtonNextPP, 3 to R.id.floatingActionButtonNextPU, 4 to R.id.floatingActionButtonNextMU, 5 to R.id.floatingActionButtonNextDips, 6 to R.id.floatingActionButtonNextSquats, 7 to R.id.floatingActionButtonNextLunges, 8 to R.id.floatingActionButtonNextCore, 9 to R.id.floatingActionButtonNextBridges, 10 to R.id.floatingActionButtonNextGS)
    val prevButtonComponentMap = mapOf<Int, Int>(0 to R.id.floatingActionButtonPrevOA, 1 to R.id.floatingActionButtonPrevHS, 2 to R.id.floatingActionButtonPrevPP, 3 to R.id.floatingActionButtonPrevPU, 4 to R.id.floatingActionButtonPrevMU, 5 to R.id.floatingActionButtonPrevDips, 6 to R.id.floatingActionButtonPrevSquats, 7 to R.id.floatingActionButtonPrevLunges, 8 to R.id.floatingActionButtonPrevCore, 9 to R.id.floatingActionButtonPrevBridges, 10 to R.id.floatingActionButtonPrevGS)
    lateinit var exerciseTextView: TextView
    lateinit var stepTextView: TextView
    lateinit var stepProgressTextView: EditText
    lateinit var prevButton: FloatingActionButton
    lateinit var nextButton: FloatingActionButton
    lateinit var exerciseGroups:List<ExerciseGroup>
    var exercises: MutableList<Exercise> = mutableListOf()
    var exerciseGroupCounts: List<GroupCountTuple> = listOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        return inflater.inflate(R.layout.full_layout, container, false)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO){
            populateTableData()
        }
        lifecycleScope.launch(Dispatchers.Main){
            populateTableInit()
        }
    }
    fun populateTableData(){
        exerciseGroups = database.exerciseGroupDao().getAll()
        exerciseGroupCounts = database.exerciseGroupDao().getByNumInGroups().toList()
        exercises = mutableListOf()
        for (group in exerciseGroups){
            exercises.add(database.exerciseDao().getByGroupAndStep(group.exerciseGroupId, group.progress!!))
        }
    }
    fun populateTableInit(){
        for (currExercise in exercises){
            val group: ExerciseGroup = exerciseGroups.filter { g -> g.exerciseGroupId == currExercise.exerciseGroupId }[0]
            getComponents(currExercise.exerciseGroupId)
            exerciseTextView.setText(group.exerciseGroupName)
            stepTextView.text = currExercise.exerciseDescription
            stepProgressTextView.setText(group.progress.toString())
            if (exerciseGroupCounts.filter { g -> g.exerciseGroupId == group.exerciseGroupId }[0].count == group.progress?.plus(1)){
                nextButton.isEnabled = false
            } else {
                nextButton.isEnabled = true
            }
            if (group.progress == 0){
                prevButton.isEnabled = false
            } else {
                prevButton.isEnabled = true
            }
            prevButton.setOnClickListener{
                lifecycleScope.launch(Dispatchers.IO){
                    group.progress = group.progress?.minus(1)
                    database.exerciseGroupDao().update(group)
                    populateTableData()
                }
                lifecycleScope.launch(Dispatchers.Main){
                    delay(100)
                    populateTable()
                }
            }
            nextButton.setOnClickListener{
                lifecycleScope.launch(Dispatchers.IO){
                    group.progress = group.progress?.plus(1)
                    database.exerciseGroupDao().update(group)
                    populateTableData()
                }
                lifecycleScope.launch(Dispatchers.Main){
                    delay(100)
                    populateTable()
                }
            }
        }
    }

    fun populateTable(){
        for (currExercise in exercises){
            val group: ExerciseGroup = exerciseGroups.filter { g -> g.exerciseGroupId == currExercise.exerciseGroupId }[0]
            getComponents(currExercise.exerciseGroupId)
            exerciseTextView.setText(group.exerciseGroupName)
            stepTextView.text = currExercise.exerciseDescription
            stepProgressTextView.setText(group.progress.toString())
            if (exerciseGroupCounts.filter { g -> g.exerciseGroupId == group.exerciseGroupId }[0].count == group.progress?.plus(1)){
                nextButton.isEnabled = false
            } else {
                nextButton.isEnabled = true
            }
            if (group.progress == 0){
                prevButton.isEnabled = false
            } else {
                prevButton.isEnabled = true
            }
        }
    }

    fun getComponents(id: Int) {
        exerciseTextView = requireView().findViewById<TextView>(exerciseComponentMap[id] ?: 0 )
        stepTextView = requireView().findViewById<TextView>(stepComponentMap[id] ?: 0 )
        stepProgressTextView = requireView().findViewById<EditText>(stepProgressComponentMap[id] ?: 0 )
        prevButton = requireView().findViewById<FloatingActionButton>(prevButtonComponentMap[id] ?: 0)
        nextButton = requireView().findViewById<FloatingActionButton>(nextButtonComponentMap[id] ?: 0)
    }
}