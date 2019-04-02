package ro.dobrescuandrei.mvvm.utils

import android.content.Intent
import android.os.Bundle
import java.io.Serializable

fun <MODEL : Serializable> Intent.setModel(model : MODEL)
{
    putExtra(ARG_MODEL, model)
}

fun Intent.setChooseMode()
{
    putExtra(ARG_CHOOSE_MODE, true)
}

fun Intent.setAddMode()
{
    putExtra(ARG_ADD_MODE, true)
}

fun Intent.setEditMode()
{
    putExtra(ARG_ADD_MODE, false)
}

fun <FILTER : Serializable> Intent.setFilter(filter : FILTER)
{
    putExtra(ARG_INITIAL_FILTER, filter)
}

fun Intent.setSearch(search : String)
{
    putExtra(ARG_INITIAL_SEARCH, search)
}
