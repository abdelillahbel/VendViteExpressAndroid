package com.ensb.vendviteexpress.view.ui.seller.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ensb.vendviteexpress.R
import com.ensb.vendviteexpress.databinding.FragmentAccountBinding
import com.ensb.vendviteexpress.databinding.FragmentAccountDistributorBinding
import com.ensb.vendviteexpress.view.ui.auth.AuthActivity
import com.ensb.vendviteexpress.view.ui.distributor.account.DistributorAccountViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AccountFragment : Fragment() {

    private lateinit var accountViewModel: SellerAccountViewModel
    private lateinit var binding: FragmentAccountBinding
    private val userId = Firebase.auth.currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        accountViewModel = ViewModelProvider(this)[SellerAccountViewModel::class.java]
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

        // Inflate the layout for this fragment
        return binding.root
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchUserData(userId: String) {
        GlobalScope.launch(Dispatchers.IO) { // Coroutine for offloading network task
            try {
                val snapshot =
                    Firebase.firestore.collection("users").document(userId).get().await()
                val name = snapshot.getString("name")
                val email = snapshot.getString("email")
                val phoneNumber = snapshot.getString("phoneNumber")

                if (name != null) {
                    // UI handling needs to be on the main thread
                    GlobalScope.launch(Dispatchers.Main) {
                        binding.textViewName.text = name
                        binding.textViewEmail.text = email
                        binding.textViewPhoneNumber.text = phoneNumber

                    }
                } else {
                    Toast.makeText(requireContext(), "User data was not found!", Toast.LENGTH_SHORT)
                        .show()
                    // Handle null case appropriately
                }
            } catch (e: Exception) {
                Log.e("fetchUserData: ", e.message.toString())
                // Handle Firestore exceptions
            }
        }
    }
}