package com.example.landmarkremark.ui.splash

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.landmarkremark.R
import com.example.landmarkremark.base.BaseFragment
import com.example.landmarkremark.databinding.FragmentSplashBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment
    : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate){

    @Inject lateinit var auth: FirebaseAuth

    val REQUEST_CODE_LOCATION_PERMISSION = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initObserve() {
    }

    override fun initialize() {
        // Check user's current location permission
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If permission is not granted, request permission from user
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_CODE_LOCATION_PERMISSION
            )
        } else {
            moveToNextFragment()
        }
    }

    private fun moveToNextFragment() {
        auth = Firebase.auth

        val handler = Handler()
        handler.postDelayed(Runnable {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                findNavController().navigate(R.id.action_splashFragment_to_mapFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }, 200)
    }

    private fun showErrorMessage() {
        binding.tvMessage.setText("You need to access permission to use the app")
        binding.pb.visibility = View.INVISIBLE
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                moveToNextFragment()
            } else {
                // Permission is denied
                showErrorMessage()
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SplashFragment()
    }
}