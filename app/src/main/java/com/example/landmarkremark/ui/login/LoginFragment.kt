package com.example.landmarkremark.ui.login

import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.landmarkremark.R
import com.example.landmarkremark.base.BaseFragment
import com.example.landmarkremark.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment
    : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel by viewModels<LoginVm>()

    override fun initObserve() {
        viewModel.user.observe(viewLifecycleOwner) {
            if(!it.getContentIfNotHandled()?.uid.isNullOrEmpty()) {
                navController.navigate(R.id.action_loginFragment_to_mapFragment)
            }
        }

        viewModel.exception.observe(viewLifecycleOwner) {
            Toast.makeText(context, it.getContentIfNotHandled(), Toast.LENGTH_LONG).show()
        }
    }

    override fun initialize() {
        binding.edtEmail.setText("hieupnd@gmail.com")
        binding.edtPass.setText("123456")
        binding.btnLogin.setOnClickListener {
            viewModel.login(
                binding.edtEmail.text.toString(),
                binding.edtPass.text.toString(),
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}