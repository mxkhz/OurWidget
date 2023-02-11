package com.dbtech.ourwidget

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpViewModel : ViewModel() {
    fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    fun isValidPassword(target: String?): Boolean {
        var isValid = true
        if (target!!.length < 6) {
            isValid = false
        }
        return isValid
    }

    fun doPasswordsMatch(passwordOne: String, passwordTwo: String) : Boolean {
        var isValid = false
        if(passwordOne==passwordTwo) {
            isValid = true
        }
        return isValid
    }
}