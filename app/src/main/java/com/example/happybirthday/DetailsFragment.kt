package com.example.happybirthday

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.happybirthday.databinding.DetailsFragmentBinding
import android.text.Editable

import android.text.TextWatcher
import androidx.annotation.RequiresApi
import java.util.*
import androidx.fragment.app.activityViewModels

class DetailsFragment  : Fragment() {
    private lateinit var binding : DetailsFragmentBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DetailsFragmentBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            teName.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    viewModel.name.postValue(s.toString())
                    if (viewModel.birthday.value != null && s.isNotEmpty()) {
                        bShowBirthdayScreen.isEnabled = true
                    } else if (s.isEmpty()) {
                        bShowBirthdayScreen.isEnabled = false
                    }
                }
            })

            dpBirthday.minDate = System.currentTimeMillis() - 365 * 24 * 3600 * 1000L // until 1 year
            dpBirthday.maxDate = System.currentTimeMillis()
            dpBirthday.setOnDateChangedListener { datePicker, year, month, day ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)
                viewModel.birthday.postValue(calendar.time)
                if (viewModel.name.value != null && viewModel.name.value!!.isNotEmpty()) {
                    bShowBirthdayScreen.isEnabled = true
                }

            }
            iPicture.setOnClickListener {
                viewModel.pickImageLiveData.postValue(DetailsFragment::class.java.name)
            }

            bShowBirthdayScreen.setOnClickListener {
                findNavController().navigate(DetailsFragmentDirections.actionFragFirstToSecondFragment())
            }

            viewModel.pictureLiveData.observe(viewLifecycleOwner) {(bitmap, uri) ->
                if (viewModel.photoFile1 != null) {
                    binding.iPicture.setImageBitmap(bitmap)
                }
            }
        }
    }
}