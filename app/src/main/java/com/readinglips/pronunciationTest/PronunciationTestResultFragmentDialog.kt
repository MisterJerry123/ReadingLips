package com.readinglips.pronunciationTest

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.readinglips.R
import com.readinglips.databinding.FragmentPronunciationTestLoadingDialogBinding
import com.readinglips.databinding.FragmentPronunciationTestResultDialogBinding

class PronunciationTestResultFragmentDialog:DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentPronunciationTestResultDialogBinding.inflate(inflater, container, false)
        //
        // dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        val displayMetrics = Resources.getSystem().displayMetrics
        val width = displayMetrics.widthPixels*.8
        val height = displayMetrics.heightPixels*0.8
        val window = dialog?.window
        window?.setLayout(width.toInt(),height.toInt())
        dialog?.window?.setBackgroundDrawableResource(R.drawable.design_pronunciation_result_dialog)


        binding.btnFinish.setOnClickListener {
            dismiss()
        }
        return binding.root
    }
}
