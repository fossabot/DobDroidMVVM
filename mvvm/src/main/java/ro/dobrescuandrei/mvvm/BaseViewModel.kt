package ro.dobrescuandrei.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel()
{
    internal val error   : MutableLiveData<Int>     by lazy { MutableLiveData<Int>() }
    internal val loading : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    open fun onCreate()
    {
        error.value=0
        loading.value=false
    }

    fun showLoading()
    {
        loading.value=true
    }

    fun hideLoading()
    {
        loading.value=false
    }

    fun showError(error : Int)
    {
        this.error.value=error
    }
}