package com.primefour.utils

import androidx.annotation.StringRes
import com.primefour.melomane.App

object Strings {
    fun get(@StringRes stringResource: Int, vararg formatArgs: Any = emptyArray()): String {
        return App.instance.getString(stringResource, *formatArgs)
    }
}