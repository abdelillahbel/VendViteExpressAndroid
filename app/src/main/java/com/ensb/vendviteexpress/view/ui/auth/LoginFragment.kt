package com.ensb.vendviteexpress.view.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ensb.vendviteexpress.R
import com.ensb.vendviteexpress.utils.Response
import com.ensb.vendviteexpress.utils.Utils.safeNavigate
import com.ensb.vendviteexpress.utils.Utils.startMainActivity
import com.ensb.vendviteexpress.view.MainActivity
import com.google.android.material.textfield.TextInputEditText


class LoginFragment : Fragment() {
    private lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val signUp = view.findViewById<TextView>(R.id.txtSignUp)
        val logInBtn = view.findViewById<Button>(R.id.btn_login)
        val emailEditText = view.findViewById<TextInputEditText>(R.id.login_email_input_editText)
        val passwordEditText =
            view.findViewById<TextInputEditText>(R.id.login_password_input_editText)

        logInBtn.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            authViewModel.loginUser(email, password)

            authViewModel.authResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Response.Success -> {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)

                    }

                    is Response.Failure -> {
                        // Display error message based on result.e
                        Toast.makeText(context, result.e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }


        signUp.setOnClickListener {

            findNavController().navigate(
                R.id.action_loginFragment_to_signupFragment, null, NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true) // Key change!
                    .build()
            )
        }


        return view
    }

}