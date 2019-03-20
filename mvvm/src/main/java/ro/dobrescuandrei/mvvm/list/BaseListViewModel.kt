package ro.dobrescuandrei.mvvm.list

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import ro.dobrescuandrei.mvvm.BaseViewModel
import ro.dobrescuandrei.mvvm.utils.RESULTS_PER_PAGE

abstract class BaseListViewModel<MODEL, FILTER>
(
    var filter : FILTER
) : BaseViewModel()
{
    var search : String? = null
    var offset : Int = 0

    private var doneLoadingPages : Boolean = false

    val firstPageItems : MutableLiveData<List<MODEL>> by lazy { MutableLiveData<List<MODEL>>() }
    val nextPageItems  : MutableLiveData<List<MODEL>> by lazy { MutableLiveData<List<MODEL>>() }
    val isEmpty : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    fun searchMode() : Boolean = search!=null
    abstract fun getItems() : List<MODEL>
    open fun limit() : Int = RESULTS_PER_PAGE

    override fun onCreate()
    {
        super.onCreate()

        firstPageItems.value=null
        nextPageItems.value=null
        isEmpty.value=false
    }

    fun loadData()
    {
        doneLoadingPages=false
        offset=0

        loadMoreData()
    }

    @SuppressLint("CheckResult")
    fun loadMoreData()
    {
        if (doneLoadingPages)
            return

        val isFirstPage=offset==0
        val items=getItems()

        doneLoadingPages=items.size<limit()

        if (isFirstPage)
        {
            firstPageItems.value=items
            isEmpty.value=items.isEmpty()
        }
        else
        {
            nextPageItems.value=items
        }

        if (!doneLoadingPages)
            offset+=limit()
    }
}
