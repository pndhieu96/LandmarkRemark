package com.example.landmarkremark.ui.map

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.landmarkremark.R
import com.example.landmarkremark.adapter.NoteAdapter
import com.example.landmarkremark.base.BaseFragment
import com.example.landmarkremark.base.Util.Companion.hideKeyboard
import com.example.landmarkremark.base.Util.Companion.isLocationPermissionAccess
import com.example.landmarkremark.base.Util.Companion.restartApplication
import com.example.landmarkremark.data.models.Note
import com.example.landmarkremark.data.models.User
import com.example.landmarkremark.databinding.FragmentMapBinding
import com.example.landmarkremark.ui.note.NoteFragment.Companion.REQUEST_KEY_NOTE_BACK_TO_MAP
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment :
    BaseFragment<FragmentMapBinding>(FragmentMapBinding::inflate) {
    private val viewModel by viewModels<MapVm>()

    // Variable to track the state of Google Map
    private var mMap: GoogleMap? = null
    // Variable to interact with the Fused Location Provider service
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Check if google map is available for use
    private var isGoogleMapAvailable : Boolean = false

    // Adapter to map the list of notes to RecyclerView
    private lateinit var adapter: NoteAdapter

    // Value of current location of user
    private var myLocation : LatLng? = null
    // Sign in value of user
    private var user: User? = null

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
            if(!it.hasBeenHandled) {
                val note = it.getContentIfNotHandled() ?: mutableListOf()
                adapter.noteList = note
                adapter.notifyDataSetChanged()

                if(isGoogleMapAvailable) {
                    addNotesToMap(note)
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

        // Listen the result when back from NoteFragment
        setFragmentResultListener(REQUEST_KEY_NOTE_BACK_TO_MAP) { _, bundle ->
            viewModel.getNotes()
        }
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
            setMarkerForCurrentNote(note)
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
    }

    private fun initClickViewEvent() {
        binding.ivLogout.setOnClickListener{
            viewModel.logout()
        }
        binding.ivAdd.setOnClickListener{
            getCurrentLocation {
                val bundle = bundleOf(
                    "myLocation" to Gson().toJson(myLocation),
                    "user" to Gson().toJson(user)
                )
                navController.navigate(R.id.action_mapFragment_to_noteFragment, bundle)
            }
        }
        binding.ivReload.setOnClickListener {
            mMap?.clear()
            viewModel.getNotes()
            binding.edtSearch.setText("")
            getCurrentLocation {
            }
        }
        binding.btnSearch.setOnClickListener {
            searchNotes()
        }
    }

    /**
     * Search note by email and content from text of EditText
     */
    private fun searchNotes() {
        mMap?.clear()
        val query = binding.edtSearch.text.toString()
        if(query.isNotEmpty()) {
            viewModel.getNotesByEmailOrContent(query)
        } else {
            viewModel.getNotes()
        }
        hideKeyboard(binding.edtSearch)
    }

    @SuppressLint("MissingPermission")
    private var mapCallBack = OnMapReadyCallback { googleMap ->
        isGoogleMapAvailable = true
        mMap = googleMap

        mMap?.setOnMarkerClickListener {
            viewModel.getNotesByCoordinator(it.position)
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it.position, 15f))
            return@setOnMarkerClickListener false
        }

        //get current location
        if (isLocationPermissionAccess(requireContext())
        ) {
            mMap?.uiSettings?.isMyLocationButtonEnabled = true
            mMap?.isMyLocationEnabled = true
        }

        getCurrentLocation{}

        viewModel.getNotes()
    }

    /**
     * Set marker for the selection of a note from the RecyclerView
     * */
    private fun setMarkerForCurrentNote(note: Note) {
        if(isGoogleMapAvailable) {
            val location = LatLng(note.latitude, note.longitude)
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            mMap?.clear()
            setCustomMarker(note, R.drawable.ic_current_location)
        }
    }

    /**
     * get current location of user
     * */
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(callbackSuccess: ((LatLng) -> Unit)) {
        if (isLocationPermissionAccess(requireContext()) && isGoogleMapAvailable) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Handle location
                        val latitude = location.latitude
                        val longitude = location.longitude
                        myLocation = LatLng(latitude, longitude)
                        myLocation?.let {
                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                            callbackSuccess.invoke(it)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                }
        }
    }

    /**
     * Add a custom marker for the note on the google map
     * */
    private fun setCustomMarker(note: Note, resourceId: Int) : Marker? {
        val latLng = LatLng(note.latitude, note.longitude)
        val markerIcon : BitmapDescriptor = BitmapDescriptorFactory.fromResource(resourceId)
        val markerOptions : MarkerOptions = MarkerOptions().position(latLng)
            .title(note.userEmail)
            .icon(markerIcon)
        val marker = mMap?.addMarker(markerOptions)
        return marker
    }

    /**
     * Add markers for the list of notes on the Google Map.
     */
    private fun addNotesToMap(notes: List<Note>) {
        for(note in notes) {
            setCustomMarker(note, R.drawable.ic_marker)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MapFragment()
    }
}