package com.anafthdev.story.foundation.common

import androidx.navigation.Navigator

fun interface OnItemClickListener<T> {

    fun onItemClick(position: Int, item: T, extras: Navigator.Extras?)

}