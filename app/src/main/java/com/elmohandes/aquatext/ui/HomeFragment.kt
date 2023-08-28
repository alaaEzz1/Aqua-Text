package com.elmohandes.aquatext.ui

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.elmohandes.aquatext.R
import com.elmohandes.aquatext.databinding.FragmentHomeBinding
import com.elmohandes.aquatext.utils.CustomProgressDialog

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.bind(view)

        (activity as AppCompatActivity).supportActionBar!!.title = getString(R.string.app_name)

        setupActions()

        return view
    }

    private fun setupActions() {
        val dialog = CustomProgressDialog(requireActivity())
        binding.homeImageText.setOnClickListener {
            dialog.startLoading()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Handler.createAsync(Looper.myLooper()!!).postDelayed({
                    dialog.dismissDialog()
                    dialog.dismissDialog()
                    Navigation.findNavController(requireView()).navigate(R.id.action_home_to_imageToText)
                }, 1000)

            }else{
                dialog.dismissDialog()
                Navigation.findNavController(requireView()).navigate(R.id.action_home_to_imageToText)
            }
        }
    }
}