package com.example.landmarkremark.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.viewbinding.ViewBinding
import com.example.landmarkremark.ui.login.LoginFragment
import com.example.landmarkremark.ui.map.MapFragment
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseFragment<T: ViewBinding>
    (private val inflateMethod : (LayoutInflater, ViewGroup?, Boolean) -> T) : Fragment() {

    private var _binding : T? = null
    val binding: T get() = _binding!!
    val navController: NavController by lazy { findNavController(this) }
    val className = this::class.simpleName
    var isInitView = AtomicBoolean(false)

    /**
     * Listen to live data events from view model
     * */
    abstract fun initObserve()

    /**
     * Initialize necessary functionality
     * */
    abstract fun initialize()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(_binding == null) {
            _binding = inflateMethod.invoke(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserve()

        initialize()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(className == MapFragment::class.simpleName
                    || className == LoginFragment::class.simpleName) {
                    requireActivity().finish()
                } else {
                    navController.popBackStack()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}