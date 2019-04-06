package ro.dobrescuandrei.mvvm.details

import ro.andreidobrescu.declarativeadapterkt.BaseDeclarativeAdapter
import ro.dobrescuandrei.mvvm.list.BaseListActivity
import ro.dobrescuandrei.mvvm.navigation.ARG_MODEL
import ro.dobrescuandrei.mvvm.utils.DummyFilter

abstract class BaseDetailsActivity<MODEL : Any, VIEW_MODEL : BaseDetailsViewModel<MODEL>, ADAPTER : BaseDeclarativeAdapter> : BaseListActivity<VIEW_MODEL, ADAPTER, DummyFilter>()
{
    override fun loadDataFromIntent()
    {
        val model=intent?.getSerializableExtra(ARG_MODEL) as? MODEL
        if (model!=null) viewModel.model=model
    }

    override fun shouldLoadMoreOnScroll() : Boolean = false
    override fun hasStickyHeaders() : Boolean = true
}