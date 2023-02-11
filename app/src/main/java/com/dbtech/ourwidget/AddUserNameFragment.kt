package com.dbtech.ourwidget

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.dbtech.ourwidget.databinding.FragmentAddUserNameBinding
import com.dbtech.ourwidget.databinding.FragmentHomeScreenBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddUserNameFragment : Fragment() {
    private var _binding: FragmentAddUserNameBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddUserNameBinding.inflate(inflater, container, false)
        val view = binding.root
        // Inflate the layout for this fragment
        binding.setNameButton.setOnClickListener {
            val user = Firebase.auth.currentUser
            if(binding.userName.text.toString() != "") {
                val profileUpdates = userProfileChangeRequest {
                    displayName = binding.userName.text.toString()
                }

                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            view.findNavController().navigate(R.id.action_addUserNameFragment_to_homeScreenFragment)
                        } else {
                            Toast.makeText(requireContext(), "Please Check your Internet connection.", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                binding.noUsernameAddedText.visibility = View.VISIBLE
            }
            var email : String? = null
            var userId: String? = null
            user?.let {
                email = user.email
                userId = user.uid
            }

            val database = Firebase.database.reference
            database.child("userIds").child(userId!!).setValue(email!!)
            database.child("userData").child(userId!!).child("email").setValue(email!!)
            database.child("userData").child(userId!!).child("displayName").setValue(binding.userName.text.toString())
        }
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
            view?.findNavController()?.navigate(R.id.action_addUserNameFragment_to_signUpFragment)
//                binding.eMailAddress.text = name.toString()
            }
        }
    }
