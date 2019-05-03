package ro.dobrescuandrei.mvvm.list

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ro.dobrescuandrei.mvvm.list.item_decoration.DividerItemDecoration

object RecyclerViewDefaults
{
    var layoutManagerInstantiator : (Context) -> (RecyclerView.LayoutManager) =
        { context -> LinearLayoutManager(context) }

    var itemDecorationInstantiator : (Context) -> (RecyclerView.ItemDecoration?) =
        { context -> DividerItemDecoration(context) }
}
