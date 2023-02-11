package com.dbtech.ourwidget

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dbtech.ourwidget.databinding.FragmentAddMemberBinding
import com.dbtech.ourwidget.databinding.FragmentAddPostBinding
import com.dbtech.ourwidget.databinding.FragmentCreateGroupBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddMemberFragment : Fragment() {
    private var _binding: FragmentAddMemberBinding? = null
    lateinit var viewModel: SignUpViewModel
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        _binding = FragmentAddMemberBinding.inflate(inflater, container, false)
        val view = binding.root
        database = Firebase.database.reference


        binding.addMemberButton.setOnClickListener {
            if(viewModel.isValidEmail(binding.memberEmail.text.toString())) {
                binding.notValidEmail.visibility = View.GONE
                val myIdsQuery = database.child("userIds").orderByValue().equalTo(binding.memberEmail.text.toString())

                val childEventListener = object : ChildEventListener {
                    override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                        Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)
                        var memberToAddId = dataSnapshot.key
                        val user = Firebase.auth.currentUser
                        val groupId = AddMemberFragmentArgs.fromBundle(requireArguments()).groupId
                        var displayName : String? = null
                        var inviterEmail : String? = null
                        user?.let {
                            displayName = user.displayName
                            inviterEmail = user.email
                        }
                        val key = database.child("requests").child(memberToAddId.toString()).push().getKey()
                        database.child("requests").child(memberToAddId.toString()).child(key!!).child("inviter").setValue(displayName)
                        database.child("requests").child(memberToAddId.toString()).child(key!!).child("groupId").setValue(groupId)
                        database.child("groups").child(groupId).child("members").child(memberToAddId.toString()).child("email").setValue(binding.memberEmail.text.toString())
                        database.child("groups").child(groupId).child("members").child(memberToAddId.toString()).child("accepted").setValue("false")
                    // A new comment has been added, add it to the displayed list
//                        val comment = dataSnapshot.getValue<Comment>()

                        // ...
                    }

                    override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                        Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")

                        // A comment has changed, use the key to determine if we are displaying this
                        // comment and if so displayed the changed comment.
//                        val newComment = dataSnapshot.getValue<Comment>()
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
//                        val movedComment = dataSnapshot.getValue<Comment>()
                        val commentKey = dataSnapshot.key

                        // ...
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                        Toast.makeText(context, "Failed to load member.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                myIdsQuery.addChildEventListener(childEventListener)
            } else {
                binding.notValidEmail.visibility = View.VISIBLE
            }
        }
        // Inflate the layout for this fragment
        return view
    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
//            Firebase.auth.signOut()
        val currentUser = Firebase.auth.currentUser
        if(currentUser == null){
            view?.findNavController()?.navigate(R.id.action_addMemberFragment_to_signUpFragment)
//                binding.eMailAddress.text = name.toString()
        }
    }
}