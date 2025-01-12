package com.vegh.bletask.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * BaseBottomSheetFragment extend [BottomSheetDialogFragment] for init [ViewDataBinding]
 *
 * @param VB
 */
abstract class BaseBottomSheetFragment<VB : ViewDataBinding> : BottomSheetDialogFragment() {
    // Initialize layout
    abstract fun layoutId(): Int
    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, layoutId(), container, false)

        return binding.root
    }
}
