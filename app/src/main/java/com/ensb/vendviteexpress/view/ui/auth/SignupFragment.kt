package com.ensb.vendviteexpress.view.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ensb.vendviteexpress.R
import com.ensb.vendviteexpress.utils.Response
import com.google.android.material.textfield.TextInputEditText


class SignupFragment : Fragment() {
    private lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        val loginBtn = view.findViewById<TextView>(R.id.txtLogin)
        val signUpBtn = view.findViewById<Button>(R.id.btn_register)
        val nameEditText = view.findViewById<TextInputEditText>(R.id.register_name_input_editText)
        val typeEditText = view.findViewById<AutoCompleteTextView>(R.id.type_name_input_editText)
        val emailEditText = view.findViewById<TextInputEditText>(R.id.registerEmailInputEditText)
        val passwordEditText =
            view.findViewById<TextInputEditText>(R.id.register_password_input_editText)

        // get user type from string array
        val types = resources.getStringArray(R.array.user_type)
        val userTypeArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.user_type_dropdown_item, types)

        typeEditText.setAdapter(userTypeArrayAdapter)

        // signup
        signUpBtn.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val type = typeEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            authViewModel.registerUser(name, email, type, password)

        }


        authViewModel.authResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Success -> {
                    Toast.makeText(
                        context,
                        "You have successfully registered",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    findNavController().navigate(
                        R.id.action_signupFragment_to_loginFragment, null, NavOptions.Builder()
                            .setPopUpTo(R.id.signupFragment, true) // Key change!
                            .build()
                    )
                }

                is Response.Failure -> {
                    // Handle signup errors (display an error message based on result.exception)
                    Toast.makeText(context, result.e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // switch to login
        loginBtn.setOnClickListener {

            findNavController().navigate(
                R.id.action_signupFragment_to_loginFragment, null, NavOptions.Builder()
                    .setPopUpTo(R.id.signupFragment, true) // Key change!
                    .build()
            )

        }


        return view
    }

}