package ro.dobrescuandrei.demonewlibs.router

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems

object ShowDialog
{
    fun <T> withList(
        context : Context,
        title: Int,
        cancelable: Boolean = true,
        onClick: ((Int, T) -> (Unit))? = null,
        values: List<T>)
    {

        MaterialDialog(context)
            .title(title)
            .cancelable(cancelable)
            .show() {
                listItems(items = values.map { it.toString() }) { dialog, position, text ->
                    onClick?.invoke(position, values[position])
                }
            }
    }
}