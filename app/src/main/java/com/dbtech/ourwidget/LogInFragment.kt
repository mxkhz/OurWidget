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
import com.dbtech.ourwidget.databinding.FragmentLogInBinding
import com.dbtech.ourwidget.databinding.FragmentSignUpBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogInFragment : Fragment() {
    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    fun signIn(email: String, password: String) {
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(
                        requireContext(), "Authentication succeeded.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val user = Firebase.auth.currentUser
//                    view?.findNavController()
//                        ?.navigate(R.id.action_logInFragment_to_homeScreenFragment)
                    user?.let {
                        // Name, email address, and profile photo Url
                        val name = user.displayName
                        if (name != "" && name != "null" && name != null) {
                            view?.findNavController()
                                ?.navigate(R.id.action_logInFragment_to_homeScreenFragment)
                        } else {
                            view?.findNavController()
                                ?.navigate(R.id.action_logInFragment_to_addUserNameFragment)
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.logInButton.setOnClickListener {
            var email = binding.eMailAddress.text.toString()
            var password = binding.password.text.toString()
            if(email != "" && password != "") {
                signIn(email, password)
            }
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
        if(currentUser != null){
            currentUser?.let {
                // Name, email address, and profile photo Url
                val name = currentUser.displayName
                if(name != "" && name != "null" && name != null) {
                    view?.findNavController()?.navigate(R.id.action_signUpFragment_to_homeScreenFragment)
                } else {
                    view?.findNavController()?.navigate(R.id.action_signUpFragment_to_addUserNameFragment)
                }
//                binding.eMailAddress.text = name.toString()
            }
        }
    }
}