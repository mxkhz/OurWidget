package com.dbtech.ourwidget

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.dbtech.ourwidget.databinding.FragmentHomeScreenBinding
import com.dbtech.ourwidget.databinding.FragmentLogInBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeScreenFragment : Fragment() {
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    fun logOut() {
        Firebase.auth.signOut()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        database = Firebase.database.reference
//        database.child("newMessage").setValue("hihi")

        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        val view = binding.root
        val user = Firebase.auth.currentUser
        user?.let {
            val name = user.displayName
            val email = user.email
//
            binding.eMailAddress.text = name.toString()
        }

        binding.logOutButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeScreenFragment_to_signUpFragment)
            Firebase.auth.signOut()
        }

        binding.toMyGroupsFragment.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeScreenFragment_to_myGroupsFragment)
        }
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
            view?.findNavController()?.navigate(R.id.action_addPostFragment_to_signUpFragment)
//                binding.eMailAddress.text = name.toString()
        }
    }
}