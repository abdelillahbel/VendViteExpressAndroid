package com.ensb.vendviteexpress.view.ui.distributor.home

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ensb.vendviteexpress.R
import com.ensb.vendviteexpress.databinding.FragmentHomeDistributorBinding
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

class HomeDistributorFragment : Fragment(), OnMapReadyCallback {

    private lateinit var distributorHomeViewModel: DistributorHomeViewModel
    private lateinit var binding: FragmentHomeDistributorBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeDistributorBinding.inflate(inflater, container, false)
        // homeViewModel = ViewModelProvider(this)[SellerHomeViewModel::class.java]
        distributorHomeViewModel = ViewModelProvider(
            this,
            DistributorHomeViewModelFactory(requireContext())
        )[DistributorHomeViewModel::class.java]

        binding.distributorHomeViewModel = distributorHomeViewModel
        binding.lifecycleOwner = this

        val userId = Firebase.auth.currentUser?.uid
        fetchLocationFromFirebase(userId.toString())

        val updateLocationBtn = binding.updateLocationBtn

        val mapFragment =
            childFragmentManager.findFragmentById(binding.distributorMapFragment.id) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Observe changes on map ready - similar concept as before
        distributorHomeViewModel.mapReady.observe(viewLifecycleOwner) { googleMap ->
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(0.0, 0.0), 14f))
        }
        updateLocationBtn.setOnClickListener {
            if (Utils.checkLocationPermission(requireContext())) {
                distributorHomeViewModel.getAndUpdateUserLocatoinInFirestore(requireContext())
                fetchLocationFromFirebase(userId.toString())
                distributorHomeViewModel.fetchDistributorLocation(requireContext())
                distributorHomeViewModel.fetchNearbySellers(requireContext())
            } else {
                Utils.requestLocationPermission(this)
            }
        }


        distributorHomeViewModel.navigateToSellerDetails.observe(viewLifecycleOwner) { sellerId ->
            sellerId?.let {
                val args = Bundle()
                args.putString("sellerId", sellerId)
                findNavController().navigate(
                    R.id.action_homeDistributorFragment_to_sellerInfoDialogFragment,
                    args
                )
                distributorHomeViewModel.onSellerNavigated()
            }
        }


        distributorHomeViewModel.setupSellersListener()

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                distributorHomeViewModel.fetchDistributorLocation(requireContext())
            } else {
                // todo permission denial case
                // ask for permission
            }
        }

        if (Utils.checkLocationPermission(requireContext())) {
            distributorHomeViewModel.fetchDistributorLocation(requireContext())
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        distributorHomeViewModel.initializeMap(googleMap, requireContext())
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchLocationFromFirebase(userId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val snapshot =
                    Firebase.firestore.collection("users").document(userId).get().await()
                val geoPoint = snapshot.getGeoPoint("location")
                val name = snapshot.getString("name")

                if (geoPoint != null) {
                    GlobalScope.launch(Dispatchers.Main) {
                        binding.locationTextView.text =
                            getReadableAddressFromGeoPoint(requireContext(), geoPoint)
                    }
                } else {
                    Toast.makeText(requireContext(), "geoPoint not found!", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                //  Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                Log.e("SellerHomeFragment", e.message.toString())
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

class DistributorHomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DistributorHomeViewModel(context) as T
    }
}