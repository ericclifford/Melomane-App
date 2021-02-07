package main.app.utils

import androidx.annotation.StringRes
import main.app.melomane.App

object Strings {
    fun get(@StringRes stringResource: Int, vararg formatArgs: Any = emptyArray()): String {
        return App.instance.getString(stringResource, *formatArgs)
    }
}