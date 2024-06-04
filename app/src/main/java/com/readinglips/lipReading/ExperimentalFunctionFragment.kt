package com.readinglips.lipReading

import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import com.readinglips.R
import com.readinglips.databinding.FragmentExperimentalFunctionDialogBinding

class ExperimentalFunctionFragment : DialogFragment() {
    lateinit var binding : FragmentExperimentalFunctionDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentExperimentalFunctionDialogBinding.inflate(layoutInflater,container,false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        val cameraSharedPreferences = requireActivity().getSharedPreferences("camera",MODE_PRIVATE)

        val lipReadingTime = cameraSharedPreferences.getString("lipReadingTime","3")

        val lipReadingQuality = cameraSharedPreferences.getString("lipReadingQuality","480p")

        when(lipReadingTime){
            "5"->{
                binding.rbtn5sec.isChecked=true
            }
            "7"->{
                binding.rbtn7sec.isChecked=true
            }
            "9"->{
                binding.rbtn9sec.isChecked=true
            }
            else->{
                binding.rbtn3sec.isChecked=true
            }
        }
        when(lipReadingQuality){
            "720p"->{
                binding.rbtn720p.isChecked=true

            }
            "1080p"->{
                binding.rbtn1080p.isChecked=true
            }
            "2160p"->{
                binding.rbtn2160p.isChecked=true
            }
            else->{
                binding.rbtn480p.isChecked=true
            }
        }


        binding.btnSettingSave.setOnClickListener {
            val checkedRadioId = binding.rgSelectLipReadingTime.checkedRadioButtonId
            val checkedQualityId = binding.rgSelectLipReadingQuality.checkedRadioButtonId
            val selectedTime = view?.findViewById<RadioButton>(checkedRadioId)?.text.toString()
            Log.d("ExperimentalFunctionFragment_Tag", selectedTime.substring(0,1))
            val selectedQuality = view?.findViewById<RadioButton>(checkedQualityId)?.text.toString()
            val editor = cameraSharedPreferences.edit()
            editor.putString("lipReadingTime",selectedTime.substring(0,1))
            editor.putString("lipReadingQuality",selectedQuality)
            editor.apply()
            dismiss()
        }

        return binding.root
    }





}