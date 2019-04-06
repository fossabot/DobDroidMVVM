package ro.dobrescuandrei.mvvm.list

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ro.andreidobrescu.basefilter.BaseFilter
import ro.dobrescuandrei.mvvm.BaseViewModel
import ro.dobrescuandrei.mvvm.utils.NonNullableLiveData

abstract class BaseListViewModel<MODEL, FILTER : BaseFilter> : BaseViewModel
{
    val filterLiveData : NonNullableLiveData<FILTER>

    constructor(filter : FILTER)
    {
        filterLiveData=NonNullableLiveData(initialValue = filter)
    }

    private var doneLoadingPages : Boolean = false
    private var isLoadingPage : Boolean = false

    val firstPageItemsLiveData : MutableLiveData<List<MODEL>> by lazy { MutableLiveData<List<MODEL>>() }
    val nextPageItemsLiveData  : MutableLiveData<List<MODEL>> by lazy { MutableLiveData<List<MODEL>>() }
    val isEmptyLiveData : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    abstract fun getItems(filter : FILTER) : Single<List<MODEL>>

    override fun onCreate()
    {
        super.onCreate()

        firstPageItemsLiveData.value=null
        nextPageItemsLiveData.value=null
        isEmptyLiveData.value=false
    }

    fun loadData()
    {
        doneLoadingPages=false
        filterLiveData.value.offset=0

        loadMoreData()
    }

    @SuppressLint("CheckResult")
    fun loadMoreData()
    {
        if (doneLoadingPages)
            return

        if (isLoadingPage)
            return

        isLoadingPage=true

        val isFirstPage=filterLiveData.value.offset==0
        if (isFirstPage)
            showLoading()

        getItems(filterLiveData.value).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = { ex ->
                if (isFirstPage)
                    hideLoading()
            }, onSuccess = { items ->
                if (isFirstPage)
                    hideLoading()

                isLoadingPage=false
                doneLoadingPages=items.size<filterLiveData.value.limit

                if (isFirstPage)
                {
                    firstPageItemsLiveData.value=items
                    isEmptyLiveData.value=items.isEmpty()
                }
                else
                {
                    nextPageItemsLiveData.value=items
                }

                if (!doneLoadingPages)
                    filterLiveData.value.offset+=filterLiveData.value.limit
            })
    }

    fun notifyFilterChange(consumer : (FILTER) -> (Unit))
    {
        consumer(filterLiveData.value)
        loadData()
    }
}
