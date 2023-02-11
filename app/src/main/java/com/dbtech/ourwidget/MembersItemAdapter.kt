package com.dbtech.ourwidget

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.withContext

class MembersItemAdapter : RecyclerView.Adapter<MembersItemAdapter.MembersItemViewHolder>() {
    var data = listOf<Member>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : MembersItemViewHolder = MembersItemViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: MembersItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }




    class MembersItemViewHolder(val rootView: CardView) : RecyclerView.ViewHolder(rootView) {
        val emailView = rootView.findViewById<TextView>(R.id.member_email)
        val displayNameView = rootView.findViewById<TextView>(R.id.member_username)
        val removeUserView = rootView.findViewById<Button>(R.id.remove_user)
        companion object {
            fun inflateFrom(parent: ViewGroup) : MembersItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.member_item, parent, false) as CardView
                return MembersItemViewHolder(view)
            }
        }
        fun bind(item: Member) {
            emailView.text = "(${item.email})"
            displayNameView.text = item.username
            val databaseRef = Firebase.database.reference
            if (item.removable) {
                val memberRef = databaseRef.child("groups").child(item.groupId).child("members").child(item.userId)
                removeUserView.visibility = View.VISIBLE
                removeUserView.setOnClickListener {
                    memberRef.setValue(null)
                    rootView.visibility = View.GONE
                }
            }

        }
    }
}



