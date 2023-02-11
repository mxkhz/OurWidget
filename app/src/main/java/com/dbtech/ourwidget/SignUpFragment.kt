package com.dbtech.ourwidget

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dbtech.ourwidget.databinding.FragmentSignUpBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUpFragment : Fragment() {
    lateinit var viewModel: SignUpViewModel
    lateinit var userViewModel: UserViewModel
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    fun signUp(email: String, password: String) {
        val auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(requireContext(), "createUserWithEmail:success",
                        Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    view?.findNavController()?.navigate(R.id.action_signUpFragment_to_addUserNameFragment)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        // Inflate the layout for this fragment

        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.signUpButton.setOnClickListener {
            val email = binding.eMailAddress.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            if(viewModel.isValidEmail(email)) {
                binding.notValidEmail.visibility = View.GONE
            } else {
                binding.notValidEmail.visibility = View.VISIBLE
            }
            if(viewModel.isValidPassword(password)) {
                binding.notValidPassword.visibility = View.GONE
            } else {
                binding.notValidPassword.visibility = View.VISIBLE
            }
            if(viewModel.doPasswordsMatch(password, confirmPassword)) {
                binding.notMatchingPasswords.visibility = View.GONE
            } else {
                binding.notMatchingPasswords.visibility = View.VISIBLE
            }
            if(viewModel.isValidEmail(email) && viewModel.isValidPassword(password) && viewModel.doPasswordsMatch(password, confirmPassword)) {
                signUp(binding.eMailAddress.text.toString(), binding.password.text.toString())
            }
        }
        binding.toLogInPageButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_signUpFragment_to_logInFragment)
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
            if(currentUser != null){
                currentUser?.let {
                    // Name, email address, and profile photo Url
                    val name = currentUser.displayName
                    if (name != "" && name != "null" && name != null){
                        view?.findNavController()?.navigate(R.id.action_signUpFragment_to_homeScreenFragment)
                    } else {
                        view?.findNavController()?.navigate(R.id.action_signUpFragment_to_addUserNameFragment)
                    }
//                binding.eMailAddress.text = name.toString()
                }
            }
    }
}