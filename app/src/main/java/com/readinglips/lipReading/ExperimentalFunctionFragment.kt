package com.readinglips.lipReading

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.readinglips.R
import com.readinglips.databinding.FragmentExperimentalFunctionDialogBinding

class ExperimentalFunctionFragment : DialogFragment() {
    lateinit var binding : FragmentExperimentalFunctionDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentExperimentalFunctionDialogBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }




}