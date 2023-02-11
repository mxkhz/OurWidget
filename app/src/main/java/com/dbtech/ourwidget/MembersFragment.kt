package com.dbtech.ourwidget

import android.content.ContentValues
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
import com.dbtech.ourwidget.databinding.FragmentMembersBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MembersFragment : Fragment() {
    private var _binding: FragmentMembersBinding? = null
    private val binding get() = _binding!!
    var members = mutableSetOf<Member>()
    val adapter = MembersItemAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMembersBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.addMember.setOnClickListener {
                val action = MembersFragmentDirections.actionMembersFragmentToAddMemberFragment(MembersFragmentArgs.fromBundle(requireArguments()).groupId)
                view.findNavController().navigate(action)

        }
        binding.membersList.adapter = adapter


        // Inflate the layout for this fragment
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
//            Firebase.auth.signOut()
        val currentUser = Firebase.auth.currentUser
        if(currentUser == null){
            view?.findNavController()?.navigate(R.id.action_membersFragment_to_signUpFragment)
//                binding.eMailAddress.text = name.toString()
        }
        val groupId = GroupFragmentArgs.fromBundle(requireArguments()).groupId
        val database = Firebase.database.reference
        val databaseRef = database.child("groups").child(groupId).child("members")
        database.child("groups").child(groupId).child("admin").get().addOnSuccessListener {
            val adminUid = it.value
            var removable : Boolean = false
//            Log.i(TAG, "removable" + removable)

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(ContentValues.TAG, "onChildAdded:" + dataSnapshot.key!!)

                // A new comment has been added, add it to the displayed list
                val email : String = dataSnapshot.child("email").getValue().toString()
                val userId = dataSnapshot.key!!

                Log.i(TAG, "removable" + removable)
                if (dataSnapshot.child("accepted").getValue().toString() == "true") {
                database.child("userData").child(userId).child("displayName").get().addOnSuccessListener {
                    Log.i("firebase", "Got value ${it.value}")
                    val displayName : String = it.value.toString()
                    // Can't remove yourself if you are the admin
                    if (adminUid.toString() == userId) {
                        removable = false
                    } else if (currentUser?.uid.toString() == adminUid.toString()) {
                        removable = true
                    }
                    members.add(Member(email, displayName, removable, userId, groupId))
                    adapter.data = members.toList()


                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                } }


//                Log.i("groupid", groupId.toString())
            }
            // ...
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(ContentValues.TAG, "onChildChanged: ${dataSnapshot.key}")

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                val newComment = dataSnapshot.getValue()
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(ContentValues.TAG, "onChildRemoved:" + dataSnapshot.key!!)

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(ContentValues.TAG, "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                val movedComment = dataSnapshot.getValue()
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(context, "Failed to load comments.",
                    Toast.LENGTH_SHORT).show()
            }
        }
            databaseRef.addChildEventListener(childEventListener)
        }

    }

}