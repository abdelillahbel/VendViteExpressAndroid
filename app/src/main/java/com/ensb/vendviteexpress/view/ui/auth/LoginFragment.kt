package com.ensb.vendviteexpress.view.ui.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ensb.vendviteexpress.R
import com.ensb.vendviteexpress.utils.Response
import com.ensb.vendviteexpress.utils.USERS
import com.ensb.vendviteexpress.view.ui.distributor.DistributorActivity
import com.ensb.vendviteexpress.view.ui.seller.SellerActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {
    private lateinit var authViewModel: AuthViewModel
    private val auth: FirebaseAuth = Firebase.auth


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
        }


        authViewModel.authResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Success -> {
                    lifecycleScope.launch { // Access viewModel properties or functions here
                        val userName = authViewModel.getUserName()
                        if (userName != null) {
                            Toast.makeText(context, "Welcome back $userName", Toast.LENGTH_LONG)
                                .show()
                        } else {
                            Toast.makeText(context, "Welcome back", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    fetchUserDataAndNavigate()
                }

                is Response.Failure -> {
                    // Display error message based on result.e
                    Toast.makeText(context, result.e.message, Toast.LENGTH_SHORT).show()

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

    private fun fetchUserDataAndNavigate() {
        val userDocRef = Firebase.firestore.collection(USERS).document(auth.currentUser!!.uid)

        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val userType = document.getString("type")
                saveUserTypeToSharedPreferences(userType)
                when (userType) {
                    "seller" -> {
                        val intent = Intent(context, SellerActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }


                    "distributor" -> {
                        val intent = Intent(context, DistributorActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }

                    else -> {
                        // Handle an unexpected user type or a missing "type" field
                        Toast.makeText(context, "Unknown user", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Handle the case where no user document exists
                Toast.makeText(context, "you are not registered", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            // Handle Firestore errors
            Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserTypeToSharedPreferences(userType: String?) {
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("user_type", userType)
            apply() // Or commit() for synchronous saving
        }
    }

}