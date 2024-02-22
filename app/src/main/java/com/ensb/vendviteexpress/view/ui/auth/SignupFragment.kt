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
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ensb.vendviteexpress.R
import com.ensb.vendviteexpress.databinding.FragmentLoginBinding
import com.ensb.vendviteexpress.databinding.FragmentSignupBinding
import com.ensb.vendviteexpress.utils.Response
import com.ensb.vendviteexpress.utils.Utils.hideKeyboard
import com.ensb.vendviteexpress.utils.Utils.isEmailValid
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.GeoPoint


class SignupFragment : Fragment() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]


        val loginBtn = binding.txtLogin
        val signUpBtn = binding.btnRegister
        val nameEditText = binding.registerNameInputEditText
        val typeEditText = binding.typeNameInputEditText
        val emailEditText = binding.registerEmailInputEditText
        val passwordEditText = binding.registerPasswordInputEditText

        // get user type from string array
        val types = resources.getStringArray(R.array.user_type)
        val userTypeArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.user_type_dropdown_item, types)

        typeEditText.setAdapter(userTypeArrayAdapter)

        setupProgressBar()

        // signup
        signUpBtn.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val phoneNumber: String? = null
            val type = typeEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val location: GeoPoint? = null
            if (name.isNotEmpty() && email.isNotEmpty() && type.isNotEmpty() && password.isNotEmpty() && email.isEmailValid()) {
                hideKeyboard()
                authViewModel.registerUser(name, email, phoneNumber, type, location, password)
            } else {
                Snackbar.make(
                    requireView(),
                    "All information needed!",
                    Snackbar.LENGTH_SHORT
                ).show()
            }


        }


        authViewModel.authState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Loading -> showProgressBar()

                is Response.Success -> {
                    hideProgressBar()
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
                    hideProgressBar()
                    Toast.makeText(context, result.e.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }

        // switch to login fragment
        loginBtn.setOnClickListener {

            findNavController().navigate(
                R.id.action_signupFragment_to_loginFragment, null, NavOptions.Builder()
                    .setPopUpTo(R.id.signupFragment, true) // Key change!
                    .build()
            )

        }


        return binding.root
    }

    private fun setupProgressBar() {
        binding.indicator.apply {
//            setTextSize(resources.getDimension(com.intuit.sdp.R.dimen._30sdp))
            setTextColor(ResourcesCompat.getColorStateList(resources, R.color.white, null)!!)
            setTypeface(R.font.poppins_regular)
            setProgressIndicatorColor("#FFFFFF")
            setText(R.string.loading)
            setImageResource(R.drawable.logo)
            setTrackColor("#FFA30000")
        }

    }

    private fun hideProgressBar() {
        binding.indicator.apply {
            visibility = View.GONE
            stopAnimation()
        }
    }

    private fun showProgressBar() {
        binding.indicator.apply {
            visibility = View.VISIBLE
            startAnimation()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.indicator.apply {
            stopAnimation()
        }
    }

}