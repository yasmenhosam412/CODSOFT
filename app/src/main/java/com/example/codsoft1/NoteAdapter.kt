package com.example.codsoft1

import android.app.Activity
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.codsoft1.R

class NoteAdapter(
    var activity: Activity, var note: ArrayList<NoteClass>, var click: X
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    interface X {
        fun DeleteClick(position: Int)
        fun viewClick(position: Int)
        fun EditClick(position: Int)
        fun ChangeClick(position: Int)
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<CheckBox>(R.id.checkBox)
        var desc = itemView.findViewById<TextView>(R.id.textView8)
        var inex = itemView.findViewById<TextView>(R.id.textView2)
        var dur = itemView.findViewById<TextView>(R.id.textView7)
        var coco = itemView.findViewById<CardView>(R.id.marg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return note.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val notess = note[position]

        holder.title.text = notess.title
        holder.desc.text = notess.desc
        holder.dur.text = notess.date

        holder.inex.text ="${notess.noteInndex}"


        holder.title.isChecked = notess.isCompleted

        holder.coco.setOnClickListener {
            click.ChangeClick(position)
        }


        holder.coco.setCardBackgroundColor(Color.parseColor(notess.color))

        // Long-click listener to view the note
        holder.coco.setOnLongClickListener {
            click.viewClick(position)
            true
        }
    }

    fun updateNotes(newNotes: ArrayList<NoteClass>) {
        note = newNotes
        notifyDataSetChanged()
    }


    fun attachSwipeToRecyclerView(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        // Handle delete action
                        click.DeleteClick(position)
                    }

                    ItemTouchHelper.RIGHT -> {
                        // Handle edit action
                        click.EditClick(position)
                    }
                }
            }

            override fun onChildDraw(
                c: android.graphics.Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )

                val itemView = viewHolder.itemView

                // Colors for swipe actions
                val deleteBackground = Color.parseColor("#FF0000")
                val editBackground = Color.parseColor("#604CC3")

                val backgroundPaintDelete = Paint().apply {
                    color = deleteBackground
                }

                val backgroundPaintEdit = Paint().apply {
                    color = editBackground
                }

                // Text paint setup
                val textPaint = Paint().apply {
                    color = Color.WHITE
                    textSize = 75f
                    textAlign = Paint.Align.CENTER
                }

                // Draw background colors for each swipe direction
                if (dX < 0) {
                    // Draw delete action background
                    c.drawRect(
                        itemView.right.toFloat() + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        backgroundPaintDelete
                    )

                    // Draw "Delete" text in the center
                    val text = "Delete Task"
                    val xPos = itemView.right - itemView.height / 4
                    val yPos = itemView.top + itemView.height / 2 + textPaint.textSize / 4
                    c.drawText(text, xPos.toFloat(), yPos, textPaint)
                }

                if (dX > 0) {
                    // Draw edit action background
                    c.drawRect(
                        itemView.left.toFloat(),
                        itemView.top.toFloat(),
                        dX,
                        itemView.bottom.toFloat(),
                        backgroundPaintEdit
                    )

                    // Draw "Edit" text in the center
                    val text = "Edit Task"
                    val xPos = itemView.left + dX - itemView.height / 4
                    val yPos = itemView.top + itemView.height / 2 + textPaint.textSize / 4
                    c.drawText(text, xPos, yPos, textPaint)
                }
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


}
