package ro.dobrescuandrei.mvvm.multichooser

import android.os.Bundle
import android.view.View
import ro.dobrescuandrei.mvvm.BaseFragment
import ro.dobrescuandrei.mvvm.R
import ro.dobrescuandrei.mvvm.chooser.BaseFragmentContainerActivity
import ro.dobrescuandrei.mvvm.list.RecyclerViewMod

abstract class BaseMultichooseListActivity<FRAGMENT : BaseFragment<*>, MODEL : IMultipleSelectable> : BaseFragmentContainerActivity<FRAGMENT, MODEL>()
{
    override fun layout() : Int = R.layout.activity_multi_choose_fragment_container

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val saveButton=findViewById<View>(R.id.saveButton)
        if (chooseMode)
            saveButton.visibility=View.VISIBLE
        else saveButton.visibility=View.GONE

        saveButton.setOnClickListener {
            val recyclerView=findViewById<RecyclerViewMod>(R.id.recyclerView)
            val data=recyclerView.adapter!!.items as List<MODEL>
            val selectedData=data.filter { it.getIsSelected() }
            onItemsChoosed(selectedData)
        }
    }

    abstract fun onItemsChoosed(items : List<MODEL>)

    fun onItemAdded(item : MODEL)
    {
        findViewById<View>(R.id.saveButton).performClick()
        onItemsChoosed(listOf(item))
    }
}
