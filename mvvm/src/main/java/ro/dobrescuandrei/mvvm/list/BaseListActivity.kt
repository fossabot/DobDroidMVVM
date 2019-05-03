package ro.dobrescuandrei.mvvm.list

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.miguelcatalan.materialsearchview.MaterialSearchView
import ro.andreidobrescu.basefilter.BaseFilter
import ro.andreidobrescu.declarativeadapterkt.BaseDeclarativeAdapter
import ro.andreidobrescu.declarativeadapterkt.view.HeaderView
import ro.dobrescuandrei.mvvm.BaseActivity
import ro.dobrescuandrei.mvvm.R
import ro.dobrescuandrei.mvvm.list.item_decoration.StickyHeadersItemDecoration
import ro.dobrescuandrei.mvvm.navigation.ARG_FILTER

abstract class BaseListActivity<VIEW_MODEL : BaseListViewModel<*, FILTER>, ADAPTER : BaseDeclarativeAdapter, FILTER : BaseFilter> : BaseActivity<VIEW_MODEL>()
{
    lateinit var recyclerView : RecyclerViewMod
    lateinit var emptyView : TextView
    var addButton : FloatingActionButton? = null
    var stickyHeadersItemDecoration : StickyHeadersItemDecoration? = null

    override fun loadDataFromIntent()
    {
        val initialFilter=intent?.getSerializableExtra(ARG_FILTER) as? FILTER
        if (initialFilter!=null) viewModel.filterLiveData.value=initialFilter
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        recyclerView=findViewById(R.id.recyclerView)
        emptyView=findViewById(R.id.emptyView)
        addButton=findViewById(R.id.addButton)

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
    }

    abstract fun provideAdapter() : ADAPTER
    open fun provideLayoutManager() : RecyclerView.LayoutManager = RecyclerViewDefaults.layoutManagerInstantiator(this)
    open fun provideItemDecoration() : RecyclerView.ItemDecoration? = RecyclerViewDefaults.itemDecorationInstantiator(this)
    open fun shouldLoadMoreOnScroll() : Boolean = true
    open fun provideEmptyViewText() : String = getString(R.string.no_items)
    open fun hasStickyHeaders() : Boolean = false
    open fun provideStickyHeaderModelClass(position : Int) : Class<*>? = null
    open fun provideStickyHeaderView(position : Int) : HeaderView<*>? = null

    override fun onBackPressed()
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
            }
            else
            {
                super.onBackPressed()
            }
        }
        catch (e : Exception)
        {
            super.onBackPressed()
        }
    }

    override fun onDestroy()
    {
        stickyHeadersItemDecoration?.onDestroy()
        stickyHeadersItemDecoration=null

        super.onDestroy()
    }

    override fun onKeyboardOpened()
    {
        addButton?.visibility=View.GONE
        super.onKeyboardOpened()
    }

    override fun onKeyboardClosed()
    {
        addButton?.visibility=View.VISIBLE
        super.onKeyboardClosed()
    }

    open fun onAddButtonClicked()
    {
    }
}
