package com.example.landmarkremark.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.landmarkremark.R
import com.example.landmarkremark.adapter.NoteAdapter
import com.example.landmarkremark.base.BaseFragment
import com.example.landmarkremark.base.Util.Companion.isLocationPermissionAccess
import com.example.landmarkremark.base.Util.Companion.restartApplication
import com.example.landmarkremark.data.models.Note
import com.example.landmarkremark.data.models.User
import com.example.landmarkremark.databinding.FragmentMapBinding
import com.example.landmarkremark.ui.login.LoginVm
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment :
    BaseFragment<FragmentMapBinding>(FragmentMapBinding::inflate){
    private var mMap: GoogleMap? = null
    private val viewModel by viewModels<MapVm>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var adapter: NoteAdapter

    private var myLocation : LatLng? = null
    private var user: User? = null
    private var isGoogleMapAvailable : Boolean = false

    override fun initObserve() {
        viewModel.user.observe(viewLifecycleOwner) {
            val userEvent = it.getContentIfNotHandled()
            userEvent?.let {
                if(it.uid.isEmpty()) {
                    restartApplication(requireContext())
                } else {
                    user = it
                }
            }
        }

        viewModel.notes.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()) {
                adapter.noteList = it
                adapter.notifyDataSetChanged()

                if(isGoogleMapAvailable) {
                    addNotesToMap(it)
                }
            }
        }

        viewModel.exception.observe(viewLifecycleOwner) {
            if(!it.hasBeenHandled) {
                Toast.makeText(context, it.getContentIfNotHandled(), Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if(!it.hasBeenHandled && it.getContentIfNotHandled() == true) {
                binding.pb.visibility = View.VISIBLE
            } else {
                binding.pb.visibility = View.INVISIBLE
            }
        }
    }

    override fun initialize() {
        viewModel.getUser()

        // Init google map
        initGoogleMap()

        // Init note list recycleView
        initNoteRecycleView()

        // Init Click event of Views
        initClickViewEvent()
    }

    private fun initGoogleMap() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.mapView.onCreate(null)
        binding.mapView.onResume()
        try {
            MapsInitializer.initialize(requireContext().applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.mapView.getMapAsync(mapCallBack)
    }

    private fun initNoteRecycleView() {
        adapter = NoteAdapter(mutableListOf())
        adapter.onItemClick = { note ->
            if(isGoogleMapAvailable) {
                val location = LatLng(note.latitude, note.longitude)
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            }
        }
        adapter.onItemDetailClick = { note ->
            if (user != null) {
                val bundle = bundleOf(
                    "myLocation" to Gson().toJson(myLocation),
                    "user" to Gson().toJson(user),
                    "selectedNote" to Gson().toJson(note),
                )
                navController.navigate(R.id.action_mapFragment_to_noteFragment, bundle)
            }
        }
        binding.rcNotes.adapter = adapter
        binding.rcNotes.layoutManager = LinearLayoutManager(context)
        viewModel.getNotes()
    }

    private fun initClickViewEvent() {
        binding.ivLogout.setOnClickListener{
            viewModel.logout()
        }
        binding.ivAdd.setOnClickListener{
            val bundle = bundleOf(
                "myLocation" to Gson().toJson(myLocation),
                "user" to Gson().toJson(user)
            )
            navController.navigate(R.id.action_mapFragment_to_noteFragment, bundle)
        }
    }

    @SuppressLint("MissingPermission")
    private var mapCallBack = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        //get current location
        if (isLocationPermissionAccess(requireContext())
        ) {
            mMap?.uiSettings?.isMyLocationButtonEnabled = true
            mMap?.isMyLocationEnabled = true
        }
        getCurrentLocation()
    }

    /**
     * get current location of user
     * */
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (isLocationPermissionAccess(requireContext())
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Handle location
                        val latitude = location.latitude
                        val longitude = location.longitude
                        myLocation = LatLng(latitude, longitude)
                        myLocation?.let {
                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                            isGoogleMapAvailable = true
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                }
        }
    }

    /**
     * Set marker of note on the google map
     * */
    private fun setCustomMarker(note: Note) : Marker? {
        val latLng = LatLng(note.latitude, note.longitude)
        val markerIcon : BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)
        val markerOptions : MarkerOptions = MarkerOptions().position(latLng)
            .title(note.userEmail)
            .icon(markerIcon)
        val marker = mMap?.addMarker(markerOptions)
        return marker
    }

    private fun addNotesToMap(notes: List<Note>) {
        for(note in notes) {
            setCustomMarker(note)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MapFragment()
    }
}