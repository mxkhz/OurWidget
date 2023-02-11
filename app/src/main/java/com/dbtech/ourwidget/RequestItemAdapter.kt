package com.dbtech.ourwidget

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class RequestItemAdapter : RecyclerView.Adapter<RequestItemAdapter.RequestItemViewHolder>() {
    var data = listOf<Request>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RequestItemViewHolder = RequestItemViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: RequestItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }




    class RequestItemViewHolder(val rootView: CardView) : RecyclerView.ViewHolder(rootView) {
        val requestText = rootView.findViewById<TextView>(R.id.inviter)
        val acceptButton = rootView.findViewById<Button>(R.id.accept)
        val declineButton = rootView.findViewById<Button>(R.id.decline)
        companion object {
            fun inflateFrom(parent: ViewGroup) : RequestItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.request_item, parent, false) as CardView
                return RequestItemViewHolder(view)
            }
        }
        fun bind(item: Request) {
            val user = Firebase.auth.currentUser
            lateinit var userId: String
            var email: String? = null
            user?.let {
                userId = user.uid
                email = user.email
            }
            requestText.text = "${item.inviter} invited you to a group"
            val databaseRef = Firebase.database.reference
            val requestsReference = databaseRef.child("requests").child(userId).child(item.requestKey)
            val groupReference = databaseRef.child("groups").child(item.groupId)
            val userReference = databaseRef.child("userData").child(userId)
            acceptButton.setOnClickListener {
                groupReference.child("members").child(userId).child("accepted").setValue("true")
                userReference.child("groups").child(item.groupId).setValue("")
                requestsReference.setValue(null)
                rootView.visibility = View.GONE
            }
            declineButton.setOnClickListener {
                requestsReference.setValue(null)
                rootView.visibility = View.GONE
            }

        }
    }
}