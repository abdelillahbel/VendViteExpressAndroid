package com.ensb.vendviteexpress.view.ui.distributor.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ensb.vendviteexpress.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DistributorHomeViewModel(context: Context) : ViewModel() {

    private val _navigateToSellerDetails = MutableLiveData<String?>()
    val navigateToSellerDetails: LiveData<String?> = _navigateToSellerDetails

    private val _mapReady = MutableLiveData<GoogleMap>()
    val mapReady: LiveData<GoogleMap> = _mapReady
    private val db = Firebase.firestore
    private val userId = Firebase.auth.currentUser?.uid

    private val sellerMarkers =
        hashMapOf<String, Marker>()

    private val listenerRegistrations: MutableList<ListenerRegistration> =
        mutableListOf()


    private var currentLocationMarker: Marker? = null

    // context
    private val appContext: Context = context
    fun initializeMap(googleMap: GoogleMap, context: Context) {
        _mapReady.value = googleMap
        fetchDistributorLocation(context)
        fetchNearbySellers(context)
    }

    fun fetchDistributorLocation(context: Context) {
        viewModelScope.launch {
            if (userId != null) {
                try {
                    val userDocRef = db.collection("users").document(userId)
                    val snapshot = userDocRef.get().await()
                    val location: GeoPoint? = snapshot.getGeoPoint("location")
                    val name: String? = snapshot.getString("name")
                    val sellerId: String? = snapshot.getString("uid")

                    if (location != null) {
                        updateDistributorLocationOnMap(location, name, sellerId)
                    } else {
                        Toast.makeText(context, "missing location", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun setupSellersListener() {
        val listener = db.collection("users")
            .whereEqualTo("type", "seller")
            .whereEqualTo("active", true)
            // ... (consider Geoqueries on top if distance filtering needed)
            .addSnapshotListener { snapshot, error ->
                try {
                    snapshot?.documentChanges?.forEach { change ->
                        when (change.type) {
                            DocumentChange.Type.ADDED -> handleSellerAdded(change.document)
                            DocumentChange.Type.MODIFIED -> handleSellerModified(change.document)
                            DocumentChange.Type.REMOVED -> handleSellerRemoved(change.document)
                        }
                    }
                    snapshot?.forEach { document ->
                        val sellerLocation = document.getGeoPoint("location")
                        val sellerName = document.getString("name")
                        val sellerId = document.getString("uid")
                        if (sellerLocation != null) {
                            addSellerMarker(
                                sellerLocation,
                                sellerName.toString(),
                                sellerId.toString()
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("DistributorHomeViewModel", "Error fetching distributors:", error)
                }
            }
        listenerRegistrations.add(listener)
    }


    private fun handleSellerAdded(document: DocumentSnapshot) {
        val sellerId = document.id
        val sellerLocation = document.getGeoPoint("location")
        val sellerName = document.getString("name")

        if (sellerLocation != null && sellerName != null) {
            val marker = addSellerMarker(sellerLocation, sellerName, sellerId)
            sellerMarkers[sellerId] = marker
        }
    }

    private fun handleSellerModified(document: DocumentSnapshot) {
        val sellerId = document.id
        val sellerLocation = document.getGeoPoint("location")
        val sellerName = document.getString("name")

        val marker = sellerMarkers[sellerId]
        if (marker != null && sellerLocation != null && sellerName != null) {
            marker.position = LatLng(sellerLocation.latitude, sellerLocation.longitude)
            marker.title = "$sellerId | $sellerName"
        }
    }

    private fun handleSellerRemoved(document: DocumentSnapshot) {
        val sellerId = document.id
        val marker = sellerMarkers.remove(sellerId)
        marker?.remove()
    }


     fun fetchNearbySellers(context: Context) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users")
                    .whereEqualTo("type", "seller")
                    .whereEqualTo("active", true)
                    .get()
                    .await()

                snapshot.forEach { document ->
                    val sellerLocation = document.getGeoPoint("location")
                    val sellerName = document.getString("name")
                    val sellerId = document.getString("uid")
                    if (sellerLocation != null) {
                        addSellerMarker(
                            sellerLocation,
                            sellerName.toString(),
                            sellerId.toString()
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("DistributorHomeViewModel", "Error fetching sellers:", e)
                Toast.makeText(
                    context,
                    "Error fetching sellers.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun addSellerMarker(location: GeoPoint, name: String, sellerId: String): Marker {
        val latLng = LatLng(location.latitude, location.longitude)
        val vectorDrawable = ContextCompat.getDrawable(appContext, R.drawable.ic_store_on_map)
        _mapReady.value?.setOnMarkerClickListener { marker ->
            val titleData = marker.title?.split("|")
            if (titleData != null && titleData.size >= 2) {
                val mSellerId = titleData[0].trim()
                viewModelScope.launch {
                    _navigateToSellerDetails.value = mSellerId
                    // fetchAndShowDialog(mSellerId)
                    // todo UX improvement
                }
                // todo  todo UX improvement
                _mapReady.value?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                // _mapReady.value?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))

            } else {
                // todo
            }
            true

        }
        return _mapReady.value?.addMarker(
            MarkerOptions()
                .position(LatLng(location.latitude, location.longitude))
                .title("$sellerId | $name")
                .icon(getCustomMarkerIcon(vectorDrawable!!))
        )!!


    }

    private fun showDistributorAlert() {
        AlertDialog.Builder(appContext)
            .setTitle("This is You")
            .setMessage("This marker represents your current location.")
            .setPositiveButton("Got it!") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private suspend fun fetchAndShowDialog(sellerId: String) {
        try {
            val docRef = db.collection("users").document(sellerId)
            val snapshot = docRef.get().await()
            val name = snapshot.getString("name")
            val type = snapshot.getString("type")

            if (name != null && type != null) {
                showDialog(name, type)
            } else {
               // error
            }
        } catch (e: Exception) {
            Log.e("DistributorHomeViewModel", "Error fetching distributor", e)
        }

    }

    private fun showDialog(name: String, type: String) {
        android.app.AlertDialog.Builder(appContext)
            .setTitle("Seller Details")
            .setMessage("Name: $name\nType: $type")
            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun updateDistributorLocationOnMap(
        location: GeoPoint,
        name: String?,
        distributorId: String?
    ) {
        val latLng = LatLng(location.latitude, location.longitude)
        val vectorDrawable = ContextCompat.getDrawable(appContext, R.drawable.ic_profile_on_map)

        if (currentLocationMarker == null) {
            currentLocationMarker = _mapReady.value?.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("You ($name)")
                    .icon(getCustomMarkerIcon(vectorDrawable!!))
            )

        } else {
            currentLocationMarker?.position = latLng
        }
        _mapReady.value?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        // _mapReady.value?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
    }


    private fun getCustomMarkerIcon(vectorDrawable: Drawable): BitmapDescriptor {
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
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    suspend fun getUserLocation(): Location? {
        return try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            Log.e("DistributorHomeViewModel", "Error fetching user location:", e)
            null // Or a default location object todo
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
            } catch (e: Exception) {
                Log.e("SellerHomeViewModel", "Error updating location:", e)
            }
        }
    }

    fun getAndUpdateUserLocatoinInFirestore(context: Context) {
        viewModelScope.launch {          // Using viewModelScope
            val location = getUserLocation()
            if (location != null) {
                updateUserLocationInFirestore(location)
            } else {
                Toast.makeText(context, "we can't retrieve your location!", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
    fun onSellerNavigated() {
        _navigateToSellerDetails.value = null
    }
}