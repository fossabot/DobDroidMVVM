package ro.dobrescuandrei.mvvm.utils

import android.os.Bundle
import java.io.Serializable

fun <FILTER : Serializable> Bundle.setFilter(filter : FILTER?)
{
    if (filter!=null)
        putSerializable(ARG_INITIAL_FILTER, filter)
}

fun Bundle.setSearch(search : String?)
{
    if (search!=null)
        putSerializable(ARG_INITIAL_SEARCH, search)
}
