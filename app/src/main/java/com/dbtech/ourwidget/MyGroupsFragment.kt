package com.dbtech.ourwidget

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.dbtech.ourwidget.databinding.FragmentHomeScreenBinding
import com.dbtech.ourwidget.databinding.FragmentMyGroupsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MyGroupsFragment : Fragment() {
    private var _binding: FragmentMyGroupsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    var groups = mutableSetOf<Group>()
    val adapter = GroupItemAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyGroupsBinding.inflate(inflater, container, false)
        binding.requests.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_myGroupsFragment_to_requestsFragment)
        }

        binding.goToCreateGroupFragment.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_myGroupsFragment_to_createGroupFragment)
        }

        binding.groupsList.adapter = adapter
//        val groups = mutableListOf(Group("hi"))
        Log.i("groups", groups.toString())




        val view = binding.root
        // Inflate the layout for this fragment
        return view
    }

    override fun onStart() {
        super.onStart()
        val currentUser = Firebase.auth.currentUser
        if(currentUser == null){
            view?.findNavController()?.navigate(R.id.action_myGroupsFragment_to_signUpFragment)
//                binding.eMailAddress.text = name.toString()
        }
        lateinit var userId : String
        currentUser?.let {
            // Name, email address, and profile photo Url
            userId = currentUser.uid
        }
//
        database = Firebase.database.reference
        val databaseRef = database.child("userData").child(userId).child("groups")
        fun emptyFun(input: String) : Unit {

        }
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

                // A new comment has been added, add it to the displayed list
                val groupId = dataSnapshot.key
                val groupName = dataSnapshot.getValue()
                database.child("groups").child(groupId.toString()).child("groupName").get().addOnSuccessListener {
                    Log.i("firebase", "Got value ${it.value}")
                    if(it.value.toString() != "null" || it.value != null) {
                        groups.add(Group(groupId.toString(), it.value.toString(), ::emptyFun))
                        adapter.data = groups.toList()
                    }

                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                }

                Log.i("groupid", groupId.toString())
            }
                // ...
                override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    val newComment = dataSnapshot.getValue()
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
                    val movedComment = dataSnapshot.getValue()
                    val commentKey = dataSnapshot.key

                    // ...
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                    Toast.makeText(context, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        databaseRef.addChildEventListener(childEventListener)

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}