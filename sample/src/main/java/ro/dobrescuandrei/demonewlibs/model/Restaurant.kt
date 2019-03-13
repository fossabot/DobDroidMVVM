package ro.dobrescuandrei.demonewlibs.model

import android.content.res.Resources
import ro.dobrescuandrei.demonewlibs.R
import ro.dobrescuandrei.demonewlibs.model.utils.ID
import ro.dobrescuandrei.demonewlibs.model.utils.uuid
import ro.dobrescuandrei.mvvm.utils.NO_VALUE_INT
import java.io.Serializable

open class Restaurant
(
    var id : ID = uuid(),
    var name : String = "",
    var rating : Int = 3,
    var type : Int = NO_VALUE_INT
) : Serializable
{
    companion object
    {
        const val TYPE_NORMAL = 1
        const val TYPE_FAST_FOOD = 2
    }

    fun getTypeAsString(resources : Resources) = resources.getString(when(type)
    {
        TYPE_NORMAL -> R.string.normal
        TYPE_FAST_FOOD -> R.string.fast_food
        else -> R.string.empty
    })
}