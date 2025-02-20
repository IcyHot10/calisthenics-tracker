package com.indeavour.caltracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

class StatsLayout: Fragment() {
    val database by lazy { AppDatabase.getDatabase(this.requireContext()) }
    lateinit var exerciseGroups: List<ExerciseGroup>
    lateinit var exercises: List<Exercise>
    lateinit var group: ExerciseGroup
    lateinit var series: LineGraphSeries<DataPoint>
    var maxX = 0.0
    val progressBarExercise: ProgressBar by lazy { requireView().findViewById<ProgressBar>(R.id.progressBarExercise) }
    val progressBarStep: ProgressBar by lazy { requireView().findViewById<ProgressBar>(R.id.progressBarStepProgress) }
    val textViewExerciseProgress: TextView by lazy {requireView().findViewById<TextView>(R.id.textViewExerciseProgress)}
    val textViewStepProgress: TextView by lazy {requireView().findViewById<TextView>(R.id.textViewStepProgress)}
    val spinnerExercise: Spinner by lazy {requireView().findViewById<Spinner>(R.id.spinnerExerciseGroup)}
    val spinnerStep: Spinner by lazy {requireView().findViewById<Spinner>(R.id.spinnerStep)}
    val lineGraphView: GraphView by lazy { requireView().findViewById<GraphView>(R.id.idGraphView) }
    val radioGroup: RadioGroup by lazy { requireView().findViewById<RadioGroup>(R.id.radioGroup) }
    val radioButton10: RadioButton by lazy { requireView().findViewById<RadioButton>(R.id.radioButton10) }
    val radioButtonAll: RadioButton by lazy { requireView().findViewById<RadioButton>(R.id.radioButtonAll) }
    var created: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.stats_layout, container, false)!!

    fun onFragmentResume() {
        if (created){
            lifecycleScope.launch(Dispatchers.IO) {
                exerciseGroups = database.exerciseGroupDao().getAll()
                created = true
                exercises = database.exerciseDao().getByGroupId(exerciseGroups[0].exerciseGroupId)
                updateExerciseComponentsData()
                updateStepComponentsData()
            }
            lifecycleScope.launch(Dispatchers.Main) {
                delay(100)
                updateExerciseComponents()
                updateStepComponents()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO){
            exerciseGroups = database.exerciseGroupDao().getAll()
            exercises = database.exerciseDao().getByGroupId(exerciseGroups[0].exerciseGroupId)
        }
        lifecycleScope.launch(Dispatchers.Main){
            val exerciseSpinnerAdapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, exerciseGroups.map { it.exerciseGroupName })
            spinnerExercise.adapter = exerciseSpinnerAdapter
            val stepSpinnerAdapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, exercises.map { it.exerciseDescription })
            spinnerStep.adapter = stepSpinnerAdapter
        }
        lifecycleScope.launch(Dispatchers.IO){
            delay(300)
            updateExerciseComponentsData()
            updateStepComponentsData()
        }
        lifecycleScope.launch(Dispatchers.Main){
            delay(350)
            radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
                lifecycleScope.launch(Dispatchers.IO){
                    updateStepComponentsData()
                }
                lifecycleScope.launch(Dispatchers.Main){
                    delay(100)
                    populateGraph()
                }
            })
            spinnerExercise.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    lifecycleScope.launch(Dispatchers.IO){
                        updateExerciseComponentsData()
                    }
                    lifecycleScope.launch(Dispatchers.Main){
                        delay(100)
                        updateExerciseComponents()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

            spinnerStep.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    lifecycleScope.launch(Dispatchers.IO){
                        updateStepComponentsData()
                    }
                    lifecycleScope.launch(Dispatchers.Main){
                        delay(100)
                        updateStepComponents()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
            created = true
            updateExerciseComponents()
            updateStepComponents()
        }
    }

    fun populateGraph() {
        lineGraphView.removeAllSeries()
        lineGraphView.animate()
        lineGraphView.title = "Step History"
        lineGraphView.titleTextSize = 50.0f
        lineGraphView.viewport.setMaxX(maxX)
        lineGraphView.viewport.setMaxY(100.0)
        lineGraphView.viewport.isYAxisBoundsManual = true
        lineGraphView.viewport.isScalable = true
        lineGraphView.viewport.isScrollable = true
        lineGraphView.addSeries(series)
    }

    @SuppressLint("SetTextI18n")
    fun updateStepComponents(){
        val exercise = exercises.filter { it.exerciseDescription == spinnerStep.selectedItem }[0]
        textViewStepProgress.text = "${
            exercise.currentReps?.split(',')?.map { it.replace("[","").replace("]","").replace(" ","").toInt() }?.sum()
                ?.times(100)?.div(exercise.sets!!.times(exercise.reps!!))?.toInt() ?: 0}%"
        progressBarStep.progress = exercise.currentReps?.split(',')?.map { it.replace("[","").replace("]","").replace(" ","").toInt() }?.sum()!!
        progressBarStep.max = exercise.sets!!.times(exercise.reps!!)

        populateGraph()
    }

    fun updateExerciseComponentsData(){
        exerciseGroups = database.exerciseGroupDao().getAll()
        exercises = database.exerciseDao().getByGroupId(exerciseGroups[0].exerciseGroupId)
        group = exerciseGroups[0]
        exercises = database.exerciseDao().getByGroupId(exerciseGroups.filter { it.exerciseGroupName == group.exerciseGroupName }[0].exerciseGroupId)
    }

    fun updateStepComponentsData(){
        val exercise = exercises[0]
        lateinit var hist: List<ExerciseSession>
        var progress = 0
        var array: MutableList<DataPoint> = mutableListOf<DataPoint>()
        if (radioGroup.checkedRadioButtonId == radioButtonAll.id){
            val group = exerciseGroups.filter { g -> g.exerciseGroupId == exercise.exerciseGroupId }[0]
            hist = database.exerciseSessionDao().getByExerciseId(exercise.exerciseId)
            var point1 = 0.0
            for (point in hist){
                when (group.type) {
                    0 -> progress = point.biProgress
                    1 -> progress = point.legsProgress
                    2 -> progress = point.coreProgress
                    3 -> progress = point.trProgress
                }
                array.add(DataPoint(point1, progress.times(100).div(exercise.sets!!.times(
                    exercise.reps!!
                )).toDouble()))
                point1 = point1.plus(1)
            }
            series = LineGraphSeries(
                array.toTypedArray()
            )
            maxX = point1
        } else {
            maxX = 10.0
            val group = exerciseGroups.filter { g -> g.exerciseGroupId == exercise.exerciseGroupId }[0]
            hist = database.exerciseSessionDao().getByExerciseIdLast10(exercise.exerciseId)
            var point1 = 0.0
            for (point in hist){
                when (group.type) {
                    0 -> progress = point.biProgress
                    1 -> progress = point.legsProgress
                    2 -> progress = point.coreProgress
                    3 -> progress = point.trProgress
                }
                array.add(DataPoint(point1, progress.times(100).div(exercise.sets!!.times(exercise.reps!!)).toDouble()))
                point1 = point1.plus(1)
            }
            series = LineGraphSeries(
                array.toTypedArray()
            )
        }
    }
    @SuppressLint("SetTextI18n")
    fun updateExerciseComponents(){
        textViewExerciseProgress.text = "${(group.progress?.times(100)?.div(exercises.size-1)?.toInt())}%"
        progressBarExercise.progress = group.progress!!
        progressBarExercise.max = exercises.size-1
        val exerciseSpinnerAdapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, exercises.map { it.exerciseDescription })
        spinnerStep.adapter = exerciseSpinnerAdapter
    }
}