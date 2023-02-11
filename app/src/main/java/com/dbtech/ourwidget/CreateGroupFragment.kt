package com.dbtech.ourwidget

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.dbtech.ourwidget.databinding.FragmentAddUserNameBinding
import com.dbtech.ourwidget.databinding.FragmentCreateGroupBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class CreateGroupFragment : Fragment() {
    private var _binding: FragmentCreateGroupBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.createGroupButton.setOnClickListener {
            val groupName = binding.groupName.text.toString()
            val user = Firebase.auth.currentUser
            lateinit var userId: String
            var email: String? = null
            user?.let {
                userId = user.uid
                email = user.email
            }
            database = Firebase.database.reference
            val key = database.child("groups").push().getKey()
            database.child("groups").child(key!!).child("groupName").setValue(groupName)
            database.child("groups").child(key!!).child("members").child(userId!!).child("email").setValue(email!!)
            database.child("groups").child(key!!).child("members").child(userId!!).child("accepted").setValue("true")
            database.child("groups").child(key!!).child("admin").setValue(userId!!)
            database.child("userData").child(userId).child("groups").child(key!!).setValue(groupName)


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
            view?.findNavController()?.navigate(R.id.action_createGroupFragment_to_signUpFragment)
//                binding.eMailAddress.text = name.toString()
        }
    }

}