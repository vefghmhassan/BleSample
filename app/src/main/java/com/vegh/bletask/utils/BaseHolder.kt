package com.fitroad.android.utils

import androidx.annotation.NonNull
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class BaseHolder<T : ViewDataBinding>(itemView: T) : RecyclerView.ViewHolder(itemView.root) {
    val binding: T = itemView
}