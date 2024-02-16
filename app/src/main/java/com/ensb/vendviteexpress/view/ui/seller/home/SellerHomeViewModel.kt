package com.ensb.vendviteexpress.view.ui.seller.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ensb.vendviteexpress.R
import com.ensb.vendviteexpress.utils.USERS
import com.ensb.vendviteexpress.utils.Utils
import com.ensb.vendviteexpress.view.ui.seller.SellerActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SellerHomeViewModel(context: Context) : ViewModel() {

    private val _navigateToDistributorDetails = MutableLiveData<String?>() // distributorId
    val navigateToDistributorDetails: LiveData<String?> = _navigateToDistributorDetails

    private val _mapReady = MutableLiveData<GoogleMap>()
    val mapReady: LiveData<GoogleMap> = _mapReady
    private val db = Firebase.firestore
    private val userId = Firebase.auth.currentUser?.uid

    private var currentLocationMarker: Marker? = null

    // context
    private val appContext: Context = context
    fun initializeMap(googleMap: GoogleMap, context: Context) {
        _mapReady.value = googleMap
        fetchSellerLocation(context) // We'll implement this
        fetchDistributors(context)  //  We'll implement this also
    }

    fun fetchSellerLocation(context: Context) {
        viewModelScope.launch {
            if (userId != null) {
                try {
                    val userDocRef = db.collection("users").document(userId)
                    val snapshot = userDocRef.get().await()
                    val location: GeoPoint? = snapshot.getGeoPoint("location")
                    val name: String? = snapshot.getString("name")
                    val sellerId: String? = snapshot.getString("uid")

                    if (location != null) {
                        updateSellerLocationOnMap(location, name, sellerId)
                    } else {
                        //  Handle the missing location case (show alternative UI, prompt for input)
                    }
                } catch (e: Exception) {
                    // Handle Firestore errors
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun fetchDistributors(context: Context) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users")
                    .whereEqualTo("type", "distributor")
                    .whereEqualTo("active", true)
                    // Geoqueries will likely replace this general query
                    .get()
                    .await()

                snapshot.forEach { document ->
                    val distributorLocation = document.getGeoPoint("location")
                    val distributorName = document.getString("name")
                    val distributorId = document.getString("uid")
                    if (distributorLocation != null) {
                        addDistributorMarker(
                            distributorLocation,
                            distributorName.toString(),
                            distributorId.toString()
                        )
                    }
                } // You may consider else here when location is null, as per business logic
            } catch (e: Exception) {
                // Consider more refined error handling
                Log.e("SellerHomeViewModel", "Error fetching distributors:", e)
                Toast.makeText(
                    context,
                    "Error fetching distributors. Check Logs.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun addDistributorMarker(
        location: GeoPoint,
        name: String,
        distributorId: String
    ) {
        val latLng = LatLng(location.latitude, location.longitude)
        val vectorDrawable = ContextCompat.getDrawable(appContext, R.drawable.ic_truck)
        _mapReady.value?.addMarker(
            MarkerOptions()
                .position(LatLng(location.latitude, location.longitude))
                .title("$distributorId | $name")
                .icon(getCustomMarkerIcon(vectorDrawable!!))

        )

        _mapReady.value?.setOnMarkerClickListener { marker ->

            val titleData = marker.title?.split("|")
            if (titleData != null && titleData.size >= 2) {
                val mDistributorId = titleData[0].trim()
                viewModelScope.launch {
                    _navigateToDistributorDetails.value = mDistributorId
                    //  fetchAndShowDialog(mDistributorId)
                }
                _mapReady.value?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            } else {
                //  Data wasn't structured as expected
            }
            true

        }
    }

    private suspend fun fetchAndShowDialog(distributorId: String) {
        try {
            val docRef = db.collection("users").document(distributorId)
            val snapshot = docRef.get().await()
            val name = snapshot.getString("name")
            val type = snapshot.getString("type")

            if (name != null && type != null) {
                showDialog(name, type)
            } else {
                // Handle missing data case
            }
        } catch (e: Exception) {
            Log.e("SellerHomeViewModel", "Error fetching distributor", e)
        }
    }

    private fun showDialog(name: String, type: String) {
        AlertDialog.Builder(appContext)
            .setTitle("Distributor Details")
            .setMessage("Name: $name\nType: $type")
            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun updateSellerLocationOnMap(location: GeoPoint, name: String?, sellerId: String?) {
        val latLng = LatLng(location.latitude, location.longitude)
        val vectorDrawable = ContextCompat.getDrawable(appContext, R.drawable.ic_profile_on_map)
        if (currentLocationMarker == null) {
            // Add a new marker if it doesn't exist
            currentLocationMarker = _mapReady.value?.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("You ($name)") // Consider a more dynamic title if needed
                    .icon(getCustomMarkerIcon(vectorDrawable!!)) // Using a helper function
            )
        } else {
            // Marker exists; update its position
            currentLocationMarker?.position = latLng
        }

        _mapReady.value?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
    }

    // Helper to get a custom marker icon


    private fun getCustomMarkerIcon(vectorDrawable: Drawable): BitmapDescriptor {

        // Essential if vector isn't already sized
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context) // Assuming 'context' exists in your VM
    }

    @SuppressLint("MissingPermission")
    suspend fun getUserLocation(): Location? {
        return try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            Log.e("SellerHomeViewModel", "Error fetching user location:", e)
            null // Or a default location object based on your logic
        }
    }

    private suspend fun updateUserLocationInFirestore(location: Location) {
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            try {
                val geoPoint = GeoPoint(location.latitude, location.longitude)
                Firebase.firestore.collection("users").document(userId)
                    .update("location", geoPoint)
                    .await()
                Toast.makeText(
                    appContext,
                    "Location has been updated successfully!",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Log.e("SellerHomeViewModel", "Error updating location:", e)
                // Handle the error
            }
        }
    }

    fun getAndUpdateUserLocatoinInFirestore(context: Context, fragment: Fragment) {
        viewModelScope.launch {
            if (ActivityCompat.checkSelfPermission(
                    appContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    appContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    fragment.requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    Utils.LOCATION_PERMISSION_CODE
                )
                return@launch
            }

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        viewModelScope.launch {
                            updateUserLocationInFirestore(location)
                        }

                    } else {
                        Toast.makeText(
                            context,
                            "we can't retrieve your location!",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        // Handle the case where the location couldn't be determined
                    }

                }


        }
    }

    fun onDistributorNavigated() {
        _navigateToDistributorDetails.value = null
    }
}