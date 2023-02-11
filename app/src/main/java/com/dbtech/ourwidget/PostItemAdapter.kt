package com.dbtech.ourwidget

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.withContext

class PostItemAdapter : RecyclerView.Adapter<PostItemAdapter.PostItemViewHolder>() {
    var data = listOf<Post>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : PostItemViewHolder = PostItemViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: PostItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }




    class PostItemViewHolder(val rootView: CardView) : RecyclerView.ViewHolder(rootView) {
        val postImg = rootView.findViewById<ImageView>(R.id.image)
        val deletePostButton = rootView.findViewById<Button>(R.id.delete_post)
        val authorView = rootView.findViewById<TextView>(R.id.author)
        companion object {
            fun inflateFrom(parent: ViewGroup) : PostItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.post_item, parent, false) as CardView
                return PostItemViewHolder(view)
            }
        }
        fun bind(item: Post) {
// ImageView in your Activity
//            val httpsReference = Firebase.storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/android-test-20601.appspot.com/o/images/-NKNd47M77VR7XZRwd58/image:17")
            val imageView = postImg
            val user = Firebase.auth.currentUser
            var currentUserId : String? = null
            user?.let {
                currentUserId = user.uid
            }
            if(currentUserId == item.author) {
                authorView.text = "You added:"
            } else {
                Firebase.database.reference.child("userData").child(item.author).child("displayName").get().addOnSuccessListener {
                    val authorDisplayName = it.value
                    authorView.text = "${authorDisplayName} added:"
                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                }
            }

            val storageRef = Firebase.storage.reference
          val imageRef = storageRef.child(item.imageRef)
            val databaseRef = Firebase.database.reference
            val postRef = databaseRef.child("groups").child(item.groupId).child("posts").child(item.postId)
            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {byteArray ->
                // Use the bytes to display the image
//                val byteArray: ByteArray = stream.toByteArray()
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                imageView.setImageBitmap(bitmap)
                if(item.removable) {
                    deletePostButton.setOnClickListener {
                        postRef.setValue(null)
                        imageRef.delete().addOnSuccessListener {
                            // File deleted successfully
                            rootView.visibility = View.GONE
                        }.addOnFailureListener {
                            // Uh-oh, an error occurred!
                        }

                    }
                    deletePostButton.visibility = View.VISIBLE
                }
            }.addOnFailureListener {
                // Handle any errors
            }

        }
            }
        }



