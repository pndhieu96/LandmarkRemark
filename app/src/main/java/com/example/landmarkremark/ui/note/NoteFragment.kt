package com.example.landmarkremark.ui.note

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.landmarkremark.base.BaseFragment
import com.example.landmarkremark.base.Util.Companion.enableViewClickEvent
import com.example.landmarkremark.base.Util.Companion.timestampToDate
import com.example.landmarkremark.data.models.Note
import com.example.landmarkremark.data.models.User
import com.example.landmarkremark.databinding.FragmentNoteBinding
import com.example.landmarkremark.ui.map.MapVm
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteFragment : BaseFragment<FragmentNoteBinding>(FragmentNoteBinding::inflate) {

    enum class FragmentState {
        ADDING,
        EDITING
    }

    private val viewModel by viewModels<NoteVm>()

    // Sign in value of user
    private var user: User? = null
    // Value of current location of user
    private var location : LatLng? = null
    // Value of selected note
    private var selectedNote: Note? = null
    // State of fragment
    private var fragmentState = FragmentState.ADDING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initObserve() {
        viewModel.saveNoteSuccess.observe(viewLifecycleOwner) {
            if(it) {
                Toast.makeText(context, "Save the note successfully", Toast.LENGTH_LONG).show()
                navController.popBackStack()
            }
        }

        viewModel.exception.observe(viewLifecycleOwner) {
            if(!it.hasBeenHandled) {
                Toast.makeText(context, it.getContentIfNotHandled(), Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if(!it.hasBeenHandled && it.getContentIfNotHandled() == true) {
                enableViewsClickEvent(false)
                binding.pb.visibility = View.VISIBLE
            } else {
                enableViewsClickEvent(true)
                binding.pb.visibility = View.INVISIBLE
            }
        }
    }

    override fun initialize() {
        // Get value from arguments
        getValuesFromArguments()

        // Check the state for the fragment whether it's for adding or editing note
        if(selectedNote == null) {
            //State for the fragment is adding
            initAddSate()
        } else {
            //State for the fragment is editing
            initEditState()
        }

        // Init click view event
        initClickViewEvent()
    }

    private fun getValuesFromArguments() {
        val locationJson = arguments?.getString("myLocation")
        location = Gson().fromJson(locationJson, LatLng::class.java)
        val selectedNoteJson = arguments?.getString("selectedNote")
        if(!selectedNoteJson.isNullOrEmpty()) {
            selectedNote = Gson().fromJson(selectedNoteJson, Note::class.java)
        }
        val userJson = arguments?.getString("user")
        if(!userJson.isNullOrEmpty()) {
            user = Gson().fromJson(userJson, User::class.java)
        }
    }

    private fun initNote() {
        selectedNote?.let {
            binding.edtText.setText(it.content)
            binding.tvTime.text = timestampToDate(it.timestamp)
            binding.tvUser.text = it.userEmail
        }
    }

    private fun checkUserEditPermission(): Boolean {
        // If the user id of the selected note is the current user, return true
        return user?.uid == selectedNote?.userId
    }

    private fun initEditState() {
        fragmentState = FragmentState.EDITING

        initNote()

        if(!checkUserEditPermission()) {
            // If the current user has not permission to edit the note
            binding.ivCheck.visibility = View.GONE
            binding.ivDelete.visibility = View.GONE
            enableViewClickEvent(false, binding.edtText)
        }
    }

    private fun initAddSate() {
        fragmentState = FragmentState.ADDING

        binding.ivDelete.visibility = View.GONE
        binding.tvTime.text = timestampToDate(System.currentTimeMillis())
        binding.tvUser.text = user?.email
    }

    private fun initClickViewEvent() {
        binding.ivBack.setOnClickListener {
            navController.popBackStack()
        }
        binding.ivCheck.setOnClickListener {
            saveNote()
        }
        binding.ivDelete.setOnClickListener {
            deleteNote()
        }
    }

    private fun deleteNote() {
        selectedNote?.let {
            viewModel.deleteNote(it)
        }
    }

    private fun enableViewsClickEvent(isEnable: Boolean) {
        enableViewClickEvent(isEnable, binding.ivCheck)
        enableViewClickEvent(isEnable, binding.ivDelete)
    }

    private fun saveNote() {
        val text = binding.edtText.text.toString()
        if (text.isNullOrEmpty() || user == null) {
            Toast.makeText(context, "You must enter content", Toast.LENGTH_LONG).show()
        } else {
            val note = Note(
                noteId = selectedNote?.noteId ?: "",
                userId = user!!.uid,
                userEmail = user!!.email,
                latitude = location?.latitude ?: 0.0,
                longitude = location?.longitude ?: 0.0,
                content = text,
                timestamp = System.currentTimeMillis()
            )
            if(fragmentState == FragmentState.ADDING) {
                viewModel.createNote(note)
            } else if(fragmentState == FragmentState.EDITING) {
                viewModel.editNote(note)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NoteFragment()
    }
}