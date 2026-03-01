package com.example.gradeapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import java.text.DecimalFormat
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var etName: TextInputEditText
    private lateinit var etMatricule: TextInputEditText
    private lateinit var etCourse: TextInputEditText
    private lateinit var etMark: TextInputEditText
    private lateinit var spinnerMajor: Spinner
    private lateinit var btnCalculate: Button
    private lateinit var btnClear: Button
    private lateinit var tvResult: TextView
    private lateinit var resultCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupSpinner()
        setupClickListeners()
    }

    private fun initViews() {
        etName = findViewById(R.id.etName)
        etMatricule = findViewById(R.id.etMatricule)
        etCourse = findViewById(R.id.etCourse)
        etMark = findViewById(R.id.etMark)
        spinnerMajor = findViewById(R.id.spinnerMajor)
        btnCalculate = findViewById(R.id.btnCalculate)
        btnClear = findViewById(R.id.btnClear)
        tvResult = findViewById(R.id.tvResult)
        resultCard = findViewById(R.id.resultCard)
    }

    private fun setupSpinner() {
        val majors = arrayOf("Cyber Security", "Software Engineering", "Networking", "Data Science")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, majors)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMajor.adapter = adapter
    }

    private fun setupClickListeners() {
        btnCalculate.setOnClickListener {
            calculateGrade()
        }

        btnClear.setOnClickListener {
            clearForm()
        }

        etMark.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                previewGrade(s.toString())
            }
        })
    }

    private fun calculateGrade() {
        val name = etName.text.toString().trim()
        val matricule = etMatricule.text.toString().trim()
        val course = etCourse.text.toString().trim()
        val markStr = etMark.text.toString().trim()
        val major = spinnerMajor.selectedItem.toString()

        if (name.isEmpty() || matricule.isEmpty() || course.isEmpty() || markStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val mark = markStr.toDoubleOrNull()
        if (mark == null || mark < 0 || mark > 100) {
            Toast.makeText(this, "Enter valid mark (0-100)", Toast.LENGTH_SHORT).show()
            return
        }

        val gradeInfo = calculateGradeInfo(mark)
        val result = """
            🎓 STUDENT RECORD
            ┌─────────────────────────────┐
            │ Name:        $name
            │ Matricule:   $matricule
            │ Major:       $major
            │ Course:      $course
            │ Mark:        ${DecimalFormat("#.0").format(mark)}/100
            │ Grade:       ${gradeInfo.grade}
            │ GPA Points:  ${gradeInfo.points}
            │ Remark:      ${gradeInfo.remark}
            └─────────────────────────────┘
        """.trimIndent()

        tvResult.text = result
        resultCard.visibility = View.VISIBLE
        exportCsv(name, matricule, major, course, mark, gradeInfo.grade)

        resultCard.post {
            Toast.makeText(this@MainActivity, "✅ Grade calculated successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun previewGrade(markStr: String) {
        val mark = markStr.toDoubleOrNull()
        if (mark != null && mark in 0.0..100.0) {
            val gradeInfo = calculateGradeInfo(mark)
            Toast.makeText(this, "Preview: ${gradeInfo.grade}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearForm() {
        etName.text?.clear()
        etMatricule.text?.clear()
        etCourse.text?.clear()
        etMark.text?.clear()
        spinnerMajor.setSelection(0)
        resultCard.visibility = View.GONE
        tvResult.text = ""
    }

    private data class GradeInfo(val grade: String, val points: Double, val remark: String)

    private fun calculateGradeInfo(mark: Double): GradeInfo {
        return when {
            mark >= 86.0 -> GradeInfo("A", 4.0, "Excellent")
            mark >= 75.0 -> GradeInfo("B+", 3.5, "Very Good")
            mark >= 65.0 -> GradeInfo("B", 3.0, "Good")
            mark >= 60.0 -> GradeInfo("C+", 2.7, "Above Average")
            mark >= 50.0 -> GradeInfo("C", 2.3, "Average")
            mark >= 45.0 -> GradeInfo("D+", 2.0, "Below Average")
            mark >= 35.0 -> GradeInfo("D", 1.5, "Poor")
            mark >= 25.0 -> GradeInfo("E", 1.0, "Very Poor")
            else -> GradeInfo("F", 0.0, "Fail")
        }
    }

    private fun exportCsv(name: String, matricule: String, major: String, course: String, mark: Double, grade: String) {
        try {
            val df = DecimalFormat("#.0")
            val fileName = "grades_${System.currentTimeMillis()}.csv"
            val file = File(filesDir, fileName)
            
            if (!file.exists()) {
                file.writeText("Name,Matricule,Major,Course,Mark,Grade\n")
            }
            
            val line = "\"$name\",\"$matricule\",\"$major\",\"$course\",${df.format(mark)},\"$grade\"\n"
            file.appendText(line)
            Toast.makeText(this, "✅ CSV Saved: $fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show()
        }
    }
}
