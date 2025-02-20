package com.indeavour.caltracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.MutableList
import kotlin.collections.get

class DailyLayout: Fragment() {
    val database by lazy { AppDatabase.getDatabase(this.requireContext()) }
    lateinit var dailyGroups: List<ExerciseGroup>
    lateinit var dailyExercises: List<Exercise>
    lateinit var viewGroup: ViewGroup
    lateinit var latestSession: ExerciseSession
    var oldExercises: List<Exercise> = listOf()
    val doneButton: Button by lazy { requireView().findViewById<Button>(R.id.button) }
    val groupToCheckBoxMap: Map<Int, Int> = mapOf(0 to R.id.checkBoxBi, 1 to R.id.checkBoxLegs, 2 to R.id.checkBoxCore, 3 to R.id.checkBoxTri)
    lateinit var checkBox: CheckBox
    val groupToTextViewMap: Map<Int, Int> = mapOf(0 to R.id.textViewBi, 1 to R.id.textViewLegs, 2 to R.id.textViewCore, 3 to R.id.textViewTri)
    lateinit var textViewLastDate: TextView
    lateinit var textView: TextView
    val groupToLayoutMap: Map<Int, Int> = mapOf(0 to R.id.linearLayoutBi, 1 to R.id.linearLayoutLegs, 2 to R.id.linearLayoutCore, 3 to R.id.linearLayoutTri)
    lateinit var layout: LinearLayout
    val groupToProgressBarMap: Map<Int, Int> = mapOf(0 to R.id.progressBarBi, 1 to R.id.progressBarLegs, 2 to R.id.progressBarCore, 3 to R.id.progressBarTri)
    lateinit var progressBar: ProgressBar
    var created: Boolean = false
    var editTexts: MutableList<MutableList<EditText>> = mutableListOf<MutableList<EditText>>(mutableListOf<EditText>(), mutableListOf<EditText>(), mutableListOf<EditText>(), mutableListOf<EditText>())
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.daily_layout, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doneButton.setOnClickListener {
            var isEmpty: Boolean = false
            var isInvalid: Boolean = false
            for (l in editTexts) {
                for (txt in l) {
                    if (txt.text.toString() == ""){
                        isEmpty = true
                        break
                    }
                    if (!Regex("\\d+").matches(txt.text.toString())){
                        isInvalid = true
                    }
                }
                if (isEmpty){
                    break
                }
            }
            if (isEmpty){
                Toast.makeText(requireContext(), "Please fill in all sets", Toast.LENGTH_SHORT).show()
            }
            else if (isInvalid) {
                Toast.makeText(requireContext(), "Please fill in all sets with numbers only", Toast.LENGTH_SHORT).show()
            }
            else {
                lifecycleScope.launch(Dispatchers.IO){
                    val exerciseGroups: List<ExerciseGroup> = database.exerciseGroupDao().getByGroupId(dailyExercises.map { t -> t.exerciseGroupId }.toIntArray())
                    var biId: Int = 0
                    var biProgress: Int = 0
                    var legsId: Int = 0
                    var legsProgress: Int = 0
                    var coreId: Int = 0
                    var coreProgress: Int = 0
                    var triId: Int = 0
                    var triProgress: Int = 0
                    for (group in exerciseGroups){
                        var e = dailyExercises.filter { exercise -> exercise.exerciseGroupId == group.exerciseGroupId }[0]
                        database.exerciseDao().update(e)
                        if (group.type == 0){
                            biId = e.exerciseId
                            biProgress = e.currentReps?.split(',')?.map { it.replace("[","").replace("]","").replace(" ","").toInt() }?.sum() ?: 0
                        } else if (group.type == 1) {
                            legsId = e.exerciseId
                            legsProgress = e.currentReps?.split(',')?.map { it.replace("[","").replace("]","").replace(" ","").toInt() }?.sum() ?: 0
                        } else if (group.type == 2) {
                            coreId = e.exerciseId
                            coreProgress = e.currentReps?.split(',')?.map { it.replace("[","").replace("]","").replace(" ","").toInt() }?.sum() ?: 0
                        } else if (group.type == 3) {
                            triId = e.exerciseId
                            triProgress = e.currentReps?.split(',')?.map { it.replace("[","").replace("]","").replace(" ","").toInt() }?.sum() ?: 0
                        }
                    }
                    database.exerciseSessionDao().insertAll(ExerciseSession(database.exerciseSessionDao().getByLastId()+1, biId, biProgress, legsId, legsProgress, coreId, coreProgress, triId, triProgress,
                        LocalDate.now().toString()))
                    for (exercise in dailyExercises){
                        var currReps: MutableList<Int?> = exercise.currentReps?.split(',')?.map { it.replace("[","").replace("]","").replace(" ","").toInt() }!!.toMutableList()
                        for (i in 0..currReps.size-1){
                            currReps[i] = null
                        }
                        database.exerciseDao().update(exercise)
                    }
                    changeExercises()
                }
                lifecycleScope.launch(Dispatchers.Main){
                    delay(100)
                    populateCards()
                    for (group in dailyGroups){
                        progressBar = requireView().findViewById<ProgressBar>(groupToProgressBarMap[group.type] ?: 0)
                        progressBar.progress = 0
                    }

                }
            }
        }
        lifecycleScope.launch(Dispatchers.IO){
            populateCardData()
        }
        lifecycleScope.launch(Dispatchers.Main){
            populateCards()
            var col: Int = 0
            var reps: MutableList<Int?> = mutableListOf<Int?>()
            for (group in dailyGroups){
                col = 0
                if (dailyExercises[group.type!!].currentReps == null){
                    for (i in 0..dailyExercises[group.type].sets!!-1){
                        reps.add(null)
                    }
                } else {
                    reps = dailyExercises[group.type].currentReps?.split(',')?.map { if (it.contains("null")) null else it.replace("[","").replace("]","").replace(" ","").toInt() } !!.toMutableList()
                }
                for (editText in editTexts[group.type]){
                    editText.setText(if (reps[col] != null) reps[col].toString() else "")
                    col = col.plus(1)
                }
            }
        }
    }

    fun onFragmentResume(){
        if (created){
            lifecycleScope.launch(Dispatchers.IO){
                oldExercises = dailyExercises
                populateCardData()
            }
            lifecycleScope.launch(Dispatchers.Main){
                delay(100)
                populateCards()
            }
        }
    }

    fun populateCardData() {
        dailyGroups = database.exerciseGroupDao().getByActive()
        dailyExercises = database.exerciseDao().getByActive()
        latestSession = database.exerciseSessionDao().getByLatest() ?: ExerciseSession(0, 0, 0, 0, 0, 0, 0, 0, 0, "")
    }

    @SuppressLint("SetTextI18n")
    fun populateCards() {
        for (group in dailyGroups){
            if (oldExercises.isEmpty()){
                for (editText in editTexts[group.type!!]){
                    viewGroup = editText.parent as ViewGroup
                    viewGroup.removeView(editText)
                }
                editTexts[group.type] = mutableListOf<EditText>()
            } else if (oldExercises[group.type!!].exerciseId != dailyExercises[group.type].exerciseId){
                for (editText in editTexts[group.type]){
                    viewGroup = editText.parent as ViewGroup
                    viewGroup.removeView(editText)
                }
                editTexts[group.type] = mutableListOf<EditText>()
            }
        }
        lateinit var exercise: Exercise
        for (group in dailyGroups) {
            checkBox = requireView().findViewById<CheckBox>(groupToCheckBoxMap[group.type!!] ?: 0)
            checkBox.text = group.exerciseGroupName
            textView = requireView().findViewById<TextView>(groupToTextViewMap[group.type] ?: 0)
            exercise = dailyExercises.filter { d -> d.exerciseGroupId == group.exerciseGroupId }[0]
            textView.text = exercise.exerciseDescription
            layout = requireView().findViewById<LinearLayout>(groupToLayoutMap[group.type] ?: 0)
            for (i in 0..exercise.sets!!-1) {
                if (oldExercises.isEmpty()){
                    editTexts[group.type].add(EditText(this.requireContext()))
                    editTexts[group.type][i].setHint("Set ${i.plus(1)}")
                    editTexts[group.type][i].inputType = InputType.TYPE_CLASS_NUMBER
                    editTexts[group.type][i].focusable = View.FOCUSABLE
                    editTexts[group.type][i].isFocusableInTouchMode = true
                    editTexts[group.type][i].doOnTextChanged { value, start, count, end -> updateSets(
                        group.type
                    ) }
                    if (editTexts[group.type][i].parent != null) {
                        viewGroup = editTexts[group.type][i].parent as ViewGroup
                        viewGroup.removeView(editTexts[group.type][i])
                    }
                    layout.addView(editTexts[group.type][i])
                } else if (dailyExercises[group.type].exerciseId != oldExercises[group.type].exerciseId) {
                    editTexts[group.type].add(EditText(this.requireContext()))
                    editTexts[group.type][i].setHint("Set ${i.plus(1)}")
                    editTexts[group.type][i].inputType = InputType.TYPE_CLASS_NUMBER
                    editTexts[group.type][i].focusable = View.FOCUSABLE
                    editTexts[group.type][i].isFocusableInTouchMode = true
                    editTexts[group.type][i].doOnTextChanged { value, start, count, end -> updateSets(
                        group.type
                    ) }
                    if (editTexts[group.type][i].parent != null) {
                        viewGroup = editTexts[group.type][i].parent as ViewGroup
                        viewGroup.removeView(editTexts[group.type][i])
                    }
                    layout.addView(editTexts[group.type][i])
                }

            }
            progressBar = requireView().findViewById<ProgressBar>(groupToProgressBarMap[group.type] ?: 0)
            progressBar.progress = exercise.currentReps?.split(',')?.map { if (it.contains("null")) 0 else it.replace("[","").replace("]","").replace(" ","").toInt() }?.sum() ?: 0
            progressBar.max = exercise.sets.times(exercise.reps!!)
            textViewLastDate = requireView().findViewById<TextView>(R.id.textViewLastCompleted)
            textViewLastDate.text = "Last Completed \n${latestSession.dateCompleted}"
            created = true
        }
    }

    fun changeExercises(){
        lateinit var old: ExerciseGroup
        lateinit var new: ExerciseGroup
        for (group in dailyGroups){
            old = group
            old.isActive = false
            var types = database.exerciseGroupDao().getByType(group.type!!)
            for (i in 0..types.size-1){
                if (types[i].isActive!!){
                    new = if (i == types.size-1) types[0] else types[i+1]
                    new.isActive = true
                    break
                }
            }
            database.exerciseGroupDao().update(old)
            database.exerciseGroupDao().update(new)
        }
        populateCardData()
    }

    fun updateSets(row: Int){
        var currReps: MutableList<Int> = mutableListOf()
        for (i in 0..editTexts[row].size-1){
            currReps.add(if (Regex("\\d+").matches(editTexts[row][i].text.toString())) editTexts[row][i].text.toString().toInt() else 0)
        }
        var exercise = dailyExercises.filter { e -> e.exerciseGroupId ==  dailyGroups.filter { g -> g.type == row }[0].exerciseGroupId}[0]
        exercise.currentReps = currReps.toString()
        progressBar = requireView().findViewById<ProgressBar>(groupToProgressBarMap[row] ?: 0)
        progressBar.progress = exercise.currentReps?.split(',')?.map { it.replace("[","").replace("]","").replace(" ","").toInt() }?.sum() ?: 0
        progressBar.max = exercise.sets?.times(exercise.reps!!)!!
    }

    fun storeReps(){
        var currReps: MutableList<Int?> = mutableListOf()
        for (group in dailyGroups){
            currReps.clear()
            for (editText in editTexts[group.type!!]){
                currReps.add(if (Regex("\\d+").matches(editText.text.toString())) editText.text.toString().toInt() else null)
            }
            dailyExercises[group.type].currentReps = currReps.toString()
        }
        lifecycleScope.launch(Dispatchers.IO){
            for (exercise in dailyExercises) {
                database.exerciseDao().update(exercise)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun loadReps(){
        if (created){
            if (dailyExercises[0].currentReps != null){
                var col: Int = 0
                var reps: List<Int?> = listOf<Int?>()
                for (group in dailyGroups){
                    col = 0
                    reps = dailyExercises[group.type!!].currentReps?.split(',')?.map { if (it.contains("null")) null else it.replace("[","").replace("]","").replace(" ","").toInt() } !!
                    for (editText in editTexts[group.type]){
                        editText.setText(if (reps[col] != null) reps[col].toString() else "")
                        col = col.plus(1)
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        storeReps()
    }

    override fun onStart() {
        super.onStart()
        loadReps()
    }

}