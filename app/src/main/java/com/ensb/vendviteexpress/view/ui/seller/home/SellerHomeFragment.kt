package com.ensb.vendviteexpress.view.ui.seller.home

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import com.ensb.vendviteexpress.R
import com.ensb.vendviteexpress.databinding.FragmentHomeBinding
import com.ensb.vendviteexpress.utils.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.Locale


class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var sellerHomeViewModel: SellerHomeViewModel
    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentHomeBinding.inflate(inflater, container, false)
        // sellerHomeViewModel = ViewModelProvider(this)[SellerHomeViewModel::class.java]
        sellerHomeViewModel = ViewModelProvider(
            this,
            SellerHomeViewModelFactory(requireContext())
        )[SellerHomeViewModel::class.java]

        binding.homeViewModel = sellerHomeViewModel
        binding.lifecycleOwner = this

        if (Utils.checkLocationPermission(requireContext())) {
            sellerHomeViewModel.getAndUpdateUserLocatoinInFirestore(requireContext(), this)
        } else {
            Utils.requestLocationPermission(this)
        }

        val updateLocationBtn = binding.updateLocationBtn

        val userId = Firebase.auth.currentUser?.uid
        fetchLocationFromFirebase(userId.toString())

        sellerHomeViewModel.navigateToDistributorDetails.observe(viewLifecycleOwner) { distributorId ->
            distributorId?.let {
                val args = Bundle()
                args.putString("distributorId", distributorId)
                findNavController().navigate(
                    R.id.action_homeFragment_to_itemListDialogFragment,
                    args
                )
                sellerHomeViewModel.onDistributorNavigated() // Reset the LiveData
            }
        }

        binding.postNeedFab.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_postNeedsDialogFragment
            )
        }

        val mapFragment =
            childFragmentManager.findFragmentById(binding.mapFragment.id) as SupportMapFragment
        mapFragment.getMapAsync(this)
        // Observe changes on map ready - similar concept as before
        sellerHomeViewModel.mapReady.observe(viewLifecycleOwner) { googleMap ->
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(0.0, 0.0), 14f))
        }
        updateLocationBtn.setOnClickListener {
            if (Utils.checkLocationPermission(requireContext())) {
                sellerHomeViewModel.getAndUpdateUserLocatoinInFirestore(requireContext(), this)
                fetchLocationFromFirebase(userId.toString())
                sellerHomeViewModel.fetchSellerLocation(requireContext())
                sellerHomeViewModel.fetchDistributors(requireContext())
            } else {
                Utils.requestLocationPermission(this)
            }
        }

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                sellerHomeViewModel.fetchSellerLocation(requireContext())
            } else {
                // Handle permission denial case
            }
        }

        if (Utils.checkLocationPermission(requireContext())) {
            sellerHomeViewModel.fetchSellerLocation(requireContext())
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        sellerHomeViewModel.initializeMap(googleMap, requireContext())  // Added requireContext()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchLocationFromFirebase(userId: String) {
        GlobalScope.launch(Dispatchers.IO) { // Coroutine for offloading network task
            try {
                val snapshot =
                    Firebase.firestore.collection("users").document(userId).get().await()
                val geoPoint = snapshot.getGeoPoint("location")
                val name = snapshot.getString("name")

                if (geoPoint != null) {
                    // UI handling needs to be on the main thread
                    GlobalScope.launch(Dispatchers.Main) {
                        binding.locationTextView.text =
                            getReadableAddressFromGeoPoint(requireContext(), geoPoint)
                    }
                } else {
                    Toast.makeText(requireContext(), "geoPoint not found!", Toast.LENGTH_SHORT)
                        .show()
                    // Handle null case appropriately
                }
            } catch (e: Exception) {
                //  Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                Log.e("SellerHomeFragment", e.message.toString())
                // Handle Firestore exceptions
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getReadableAddressFromGeoPoint(
        context: Context,
        geoPoint: GeoPoint
    ): String? {
        val geocoder = Geocoder(context, Locale.FRENCH)
        return try {
            val addresses: List<Address>? = geocoder.getFromLocationName(
                "${geoPoint.latitude},${geoPoint.longitude}",
                1 // Max results
            )
            addresses?.getOrNull(0)?.getAddressLine(0)
        } catch (e: IOException) {
            Log.e("Geocoding", "Error retrieving address:", e)
            null
        }
    }

}

class SellerHomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SellerHomeViewModel(context) as T
    }
}