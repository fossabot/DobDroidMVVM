package ro.dobrescuandrei.mvvm.navigation

import android.os.Bundle
import java.io.Serializable

fun <FILTER : Serializable> Bundle.setFilter(filter : FILTER?)
{
    if (filter!=null)
        putSerializable(ARG_FILTER, filter)
}
