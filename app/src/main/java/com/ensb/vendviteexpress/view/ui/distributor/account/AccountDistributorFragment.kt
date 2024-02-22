package com.ensb.vendviteexpress.view.ui.distributor.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ensb.vendviteexpress.databinding.FragmentAccountDistributorBinding
import com.ensb.vendviteexpress.view.ui.auth.AuthActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AccountDistributorFragment : Fragment() {
    private lateinit var accountViewModel: DistributorAccountViewModel
    private lateinit var binding: FragmentAccountDistributorBinding
    private val userId = Firebase.auth.currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAccountDistributorBinding.inflate(inflater, container, false)
        accountViewModel = ViewModelProvider(this)[DistributorAccountViewModel::class.java]
        binding.accountViewModel = accountViewModel
        binding.lifecycleOwner = this

        fetchUserData(userId.toString())

        binding.logoutBtn.setOnClickListener {
            accountViewModel.logout()

            Intent(activity, AuthActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }


        }

        return binding.root
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchUserData(userId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val snapshot =
                    Firebase.firestore.collection("users").document(userId).get().await()
                val name = snapshot.getString("name")
                val email = snapshot.getString("email")
                val phoneNumber = snapshot.getString("phoneNumber")

                if (name != null) {
                    GlobalScope.launch(Dispatchers.Main) {
                        binding.textViewName.text = name
                        binding.textViewEmail.text = email
                        binding.textViewPhoneNumber.text = phoneNumber

                    }
                } else {
                    Toast.makeText(requireContext(), "User data was not found!", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Log.e("fetchUserData: ", e.message.toString())
            }
        }
    }

}