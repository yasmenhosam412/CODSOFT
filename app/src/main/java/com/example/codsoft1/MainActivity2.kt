package com.example.codsoft1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codsoft1.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity(), NoteAdapter.X {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var MYnote: ArrayList<NoteClass>
    private lateinit var filteredNotes: ArrayList<NoteClass>
    private lateinit var adapter: NoteAdapter

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MYnote = arrayListOf()
        filteredNotes = arrayListOf()

        adapter = NoteAdapter(this, filteredNotes, this)
        binding.rexc.layoutManager = LinearLayoutManager(this)
        binding.rexc.adapter = adapter

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }

        binding.searchView.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterNotes(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText)
                return true
            }
        })

        adapter.attachSwipeToRecyclerView(binding.rexc)

        binding.imageButton2.setOnClickListener { showCompletedTasks() }
        binding.imageButton.setOnClickListener { showUncompletedTasks() }

        binding.imageButton3.setOnClickListener { refresh() }

        loadNotes()
    }

    private fun refresh() {
        binding.rexc.post {
            filteredNotes.clear()
            filteredNotes.addAll(MYnote)
            adapter.notifyDataSetChanged()
        }
    }

    private fun showCompletedTasks() {
        binding.rexc.post {
            filteredNotes.clear()
            filteredNotes.addAll(MYnote.filter { it.isCompleted })
            adapter.notifyDataSetChanged()
        }
    }

    private fun showUncompletedTasks() {
        binding.rexc.post {
            filteredNotes.clear()
            filteredNotes.addAll(MYnote.filter { !it.isCompleted })
            adapter.notifyDataSetChanged()
        }
    }

    private fun loadNotes() {
        try {
            val sharedPreferences = getSharedPreferences("Notes", Context.MODE_PRIVATE)
            val noteCount = sharedPreferences.getInt("noteCount", 0)

            MYnote.clear()
            for (i in 1..noteCount) {
                val title = sharedPreferences.getString("title$i", null) ?: continue
                val desc = sharedPreferences.getString("desc$i", null) ?: continue
                val date = sharedPreferences.getString("time$i", "Unknown") ?: "Unknown"
                val color = sharedPreferences.getString("color$i", "#FFFFFFFF") ?: "#FFFFFFFF"
                val isCompleted = sharedPreferences.getBoolean("comp$i", false)

                MYnote.add(NoteClass(title, desc, date, color, isCompleted, i))
            }

            refresh()
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading notes: $e", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNotes() {
        val sharedPreferences = getSharedPreferences("Notes", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.clear()
        editor.putInt("noteCount", MYnote.size)

        MYnote.forEachIndexed { index, note ->
            val key = index + 1
            editor.putString("title$key", note.title)
            editor.putString("desc$key", note.desc)
            editor.putString("time$key", note.date)
            editor.putString("color$key", note.color)
            editor.putBoolean("comp$key", note.isCompleted)
        }

        editor.apply()
    }

    private fun filterNotes(query: String?) {
        binding.rexc.post {
            filteredNotes.clear()
            if (query.isNullOrBlank()) {
                filteredNotes.addAll(MYnote)
            } else {
                filteredNotes.addAll(MYnote.filter {
                    it.title.contains(query, ignoreCase = true)
                })
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun DeleteClick(position: Int) {
        val note = filteredNotes[position]
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes") { _, _ ->
                binding.rexc.post {
                    MYnote.remove(note)
                    filteredNotes.remove(note)
                    saveNotes()
                    adapter.notifyDataSetChanged()
                }
                Toast.makeText(this, "Note deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .create()
            .show()
        refresh()
    }

    override fun viewClick(position: Int) {
        val note = filteredNotes[position]

        val dialog = AlertDialog.Builder(this)
            .setTitle(note.title)  // Set the title of the note
            .setMessage("Description: ${note.desc}\nTime: ${note.date}\nCompleted: ${if (note.isCompleted) "Yes" else "No"}")
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }


    override fun EditClick(position: Int) {
        val note = filteredNotes[position]
        val intent = Intent(this, MainActivity3::class.java).apply {
            putExtra("noteIndex", MYnote.indexOf(note))
            putExtra("title", note.title)
            putExtra("description", note.desc)
            putExtra("time", note.date)
            putExtra("color", note.color)
            putExtra("completed", note.isCompleted)
        }
        startActivity(intent)
        refresh()
    }

    override fun ChangeClick(position: Int) {
        binding.rexc.post {
            val note = filteredNotes[position]
            note.isCompleted = !note.isCompleted
            adapter.notifyItemChanged(position)
            saveNotes()
        }
    }
}
