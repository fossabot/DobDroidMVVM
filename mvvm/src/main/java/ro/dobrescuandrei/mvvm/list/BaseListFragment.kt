package ro.dobrescuandrei.mvvm.list

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.miguelcatalan.materialsearchview.MaterialSearchView
import org.greenrobot.eventbus.Subscribe
import ro.andreidobrescu.basefilter.BaseFilter
import ro.andreidobrescu.declarativeadapterkt.BaseDeclarativeAdapter
import ro.andreidobrescu.declarativeadapterkt.view.HeaderView
import ro.dobrescuandrei.mvvm.BaseFragment
import ro.dobrescuandrei.mvvm.R
import ro.dobrescuandrei.mvvm.chooser.BaseContainerActivity
import ro.dobrescuandrei.mvvm.eventbus.OnKeyboardClosedEvent
import ro.dobrescuandrei.mvvm.eventbus.OnKeyboardOpenedEvent
import ro.dobrescuandrei.mvvm.list.item_decoration.StickyHeadersItemDecoration
import ro.dobrescuandrei.mvvm.navigation.ARG_FILTER

abstract class BaseListFragment<VIEW_MODEL : BaseListViewModel<*, FILTER>, ADAPTER : BaseDeclarativeAdapter, FILTER : BaseFilter> : BaseFragment<VIEW_MODEL>()
{
    lateinit var recyclerView : RecyclerViewMod
    lateinit var emptyView : TextView
    var addButton : FloatingActionButton? = null
    var stickyHeadersItemDecoration : StickyHeadersItemDecoration? = null

    override fun loadDataFromArguments()
    {
        val initialFilter=arguments?.getSerializable(ARG_FILTER) as? FILTER
        if (initialFilter!=null) viewModel.filterLiveData.value=initialFilter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view=super.onCreateView(inflater, container, savedInstanceState)!!

        recyclerView=view.findViewById(R.id.recyclerView)
        emptyView=view.findViewById(R.id.emptyView)
        addButton=view.findViewById(R.id.addButton)

        addButton?.setOnClickListener { onAddButtonClicked() }

        searchView?.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String?): Boolean
            {
                if (!TextUtils.isEmpty(query))
                {
                    viewModel.notifyFilterChange { filter ->
                        filter.search=query
                    }

                    searchView?.closeSearch()

                    toolbar?.title="[$query] ${toolbar.title}"
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = true
        })

        recyclerView.layoutManager=provideLayoutManager()

        val adapter=provideAdapter()
        recyclerView.adapter=adapter

        val decoration=provideItemDecoration()
        if (decoration!=null)
        {
            recyclerView.addItemDecoration(decoration)
        }

        if (hasStickyHeaders())
        {
            stickyHeadersItemDecoration=StickyHeadersItemDecoration(
                /*headerViewInstantiator*/  { provideStickyHeaderView(it) },
                /*headerModelClassProvider*/{ provideStickyHeaderModelClass(it) })
            recyclerView.addItemDecoration(stickyHeadersItemDecoration!!)
        }

        if (shouldLoadMoreOnScroll())
        {
            recyclerView.loadMoreDataAction={
                viewModel.loadMoreData()
            }
        }

        emptyView.text = provideEmptyViewText()

        viewModel.onCreate()

        viewModel.firstPageItemsLiveData.observe { items ->
            recyclerView.adapter?.setItems(items as List<Any>)
            recyclerView.scrollToPosition(0)
            viewModel.firstPageItemsLiveData.value=null
        }

        viewModel.nextPageItemsLiveData.observe { items ->
            recyclerView.adapter?.addItems(items as List<Any>)
            viewModel.nextPageItemsLiveData.value=null
        }

        viewModel.isEmptyLiveData.observe { isEmpty ->
            if (isEmpty)
            {
                emptyView.visibility=View.VISIBLE
                recyclerView.visibility=View.GONE
            }
            else
            {
                emptyView.visibility=View.GONE
                recyclerView.visibility=View.VISIBLE
            }
        }

        viewModel.loadData()

        return view
    }

    abstract fun provideAdapter() : ADAPTER
    open fun provideLayoutManager() : RecyclerView.LayoutManager = RecyclerViewDefaults.layoutManagerInstantiator(context!!)
    open fun provideItemDecoration() : RecyclerView.ItemDecoration? = RecyclerViewDefaults.itemDecorationInstantiator(context!!)
    open fun shouldLoadMoreOnScroll() : Boolean = true
    open fun provideEmptyViewText(): String = getString(R.string.no_items)
    open fun hasStickyHeaders() : Boolean = false
    open fun provideStickyHeaderModelClass(position : Int) : Class<*>? = null
    open fun provideStickyHeaderView(position : Int) : HeaderView<*>? = null

    override fun shouldFinishActivityOnBackPressed() : Boolean
    {
        try
        {
            if (viewModel.filterLiveData.value.search!=null)
            {
                viewModel.notifyFilterChange { filter ->
                    filter.search=null
                }

                if (toolbar?.title?.startsWith('[')==true&&
                    toolbar?.title?.contains("] ")==true)
                    toolbar.title=toolbar.title.split("] ").lastOrNull()?:""

                return false
            }

            return true
        }
        catch (e : Exception)
        {
            return true
        }
    }

    override fun onDestroy()
    {
        stickyHeadersItemDecoration?.onDestroy()
        stickyHeadersItemDecoration=null

        super.onDestroy()
    }

    @Subscribe
    override fun onKeyboardOpened(event : OnKeyboardOpenedEvent)
    {
        addButton?.visibility=View.GONE
    }

    @Subscribe
    override fun onKeyboardClosed(event : OnKeyboardClosedEvent)
    {
        addButton?.visibility=View.VISIBLE
    }

    open fun onAddButtonClicked()
    {
    }

    fun chooseMode() : Boolean
    {
        val activity=context as? BaseContainerActivity<*>
        return activity?.chooseMode?:false
    }
}
