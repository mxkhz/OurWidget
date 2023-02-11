package com.dbtech.ourwidget

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.dbtech.ourwidget.databinding.FragmentHomeScreenBinding
import com.dbtech.ourwidget.databinding.FragmentRequestsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.NonCancellable.children

class RequestsFragment : Fragment() {
    private var _binding: FragmentRequestsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    var requests = mutableSetOf<Request>()
    val adapter = RequestItemAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequestsBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.requestsList.adapter = adapter
//        requests.add(Request("aidwj", "udiawh", "daiwj"))
        adapter.data = requests.toList()
        // Inflate the layout for this fragment
        return view
    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
//            Firebase.auth.signOut()
        val currentUser = Firebase.auth.currentUser
        if(currentUser == null){
            view?.findNavController()?.navigate(R.id.action_requestsFragment_to_signUpFragment)
//                binding.eMailAddress.text = name.toString()
        }
        val user = Firebase.auth.currentUser
        lateinit var userId: String
        var email: String? = null
        user?.let {
            userId = user.uid
            email = user.email
        }
        val databaseReference = Firebase.database.reference.child("requests").orderByKey().equalTo(userId)
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)
                for(requestSnapshot in dataSnapshot.children) {
                    Log.d(TAG, "groupId:" + requestSnapshot.key!!)
                    val groupId = requestSnapshot.child("groupId").getValue()
                    val inviter = requestSnapshot.child("inviter").getValue()
                    requests.add(Request(groupId.toString(), inviter.toString(), requestSnapshot.key!!))
                    adapter.data = requests.toList()
                }

                // A new comment has been added, add it to the displayed list
                val request = dataSnapshot.getValue()

                // ...
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
//                val newComment = dataSnapshot.getValue<Comment>()
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
//                val movedComment = dataSnapshot.getValue<Comment>()
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(context, "Failed to load requests.",
                    Toast.LENGTH_SHORT).show()
            }
        }
        databaseReference.addChildEventListener(childEventListener)
    }
}