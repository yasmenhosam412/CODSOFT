package com.example.codsoft1

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.codsoft1.databinding.ActivityMain3Binding
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class MainActivity3 : AppCompatActivity() {

    private lateinit var binding: ActivityMain3Binding
    private var selectedDate: String = ""
    private var selectedColor: String = ""

    public  var indexNote : Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        indexNote = intent.getIntExtra("noteIndex", -1)
        var title = intent.getStringExtra("title")
        var desc = intent.getStringExtra("description")
        var color = intent.getStringExtra("color")
        var time = intent.getStringExtra("time")
        var completed = intent.getBooleanExtra("completed", false)

        if (indexNote != -1) {
            binding.title.setText(title)
            binding.desc.setText(desc)
            selectedColor = color ?: "#FFFFFFFF"
            binding.textInputLayout.boxBackgroundColor =
                android.graphics.Color.parseColor(selectedColor)

            binding.time.setText(time)

        }

        Toast.makeText(this, "$indexNote", Toast.LENGTH_SHORT).show()



        binding.imageButton7.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setTitle("Pick a Color")
                .setPreferenceName("ColorPickerDialog")
                .setPositiveButton("OK", ColorEnvelopeListener { envelope, _ ->
                    selectedColor = String.format("#%06X", (0xFFFFFF and envelope.color))
                    binding.textInputLayout.boxBackgroundColor = envelope.color
                })
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .show()
        }

        // Date Picker Button
        binding.imageButton6.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.time.setText(selectedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        // Save Button
        binding.button.setOnClickListener {
            val title = binding.title.text.toString()
            val desc = binding.desc.text.toString()

            if (selectedDate.isEmpty()) {
                selectedDate = binding.time.text.toString()
            }
            if (selectedColor.isEmpty()) {
                selectedColor = "#FFFFFFFF"
            }


            if (title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (indexNote != -1){
                saveEditedNotes()
            }else{
                saveNotes(title, desc, selectedDate, selectedColor, false)

            }




            Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveEditedNotes() {

        var  sharedPreferences = getSharedPreferences("Notes" , Context.MODE_PRIVATE)
        var editor = sharedPreferences.edit()

        var noteKey = indexNote + 1

        var newTitle = binding.title.text
        var newDesc = binding.desc.text
        val updatedDate = if (selectedDate.isEmpty()) binding.time.text.toString() else selectedDate
        val updatedColor = if (selectedColor.isEmpty()) "#FFFFFFFF" else selectedColor

        editor.putString("title$noteKey" , newTitle.toString())
        editor.putString("desc$noteKey" , newDesc.toString())
        editor.putString("time$noteKey" , updatedDate.toString())
        editor.putString("color$noteKey" , updatedColor.toString())

        editor.apply()
        Toast.makeText(this, "Done Update", Toast.LENGTH_SHORT).show()

    }

    private fun saveNotes(
        title: String,
        desc: String,
        date: String,
        color: String,
        isCompleted: Boolean
    ) {
        try {
            val sharedPreferences: SharedPreferences =
                getSharedPreferences("Notes", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            // Retrieve the current note count
            val currentCount = sharedPreferences.getInt("noteCount", 0)

            // Create a new note key based on the current count (this ensures unique keys for each note)
            val noteKey = currentCount + 1

            // Save the note count incremented by 1
            editor.putInt("noteCount", noteKey)

            // Save note details with the noteKey as part of the key (title, desc, date, color, completed)
            editor.putString("title$noteKey", title)
            editor.putString("desc$noteKey", desc)
            editor.putString("time$noteKey", date)
            editor.putString("color$noteKey", color)
            editor.putBoolean("comp$noteKey", isCompleted)

            // Apply changes to SharedPreferences
            editor.apply()

        } catch (e: Exception) {
            Toast.makeText(this, "Error saving note: $e", Toast.LENGTH_SHORT).show()
        }
    }


}
