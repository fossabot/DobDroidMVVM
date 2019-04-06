package ro.dobrescuandrei.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ro.dobrescuandrei.mvvm.utils.ErrorHolder

abstract class BaseViewModel : ViewModel()
{
    val errorLiveData : MutableLiveData<ErrorHolder> by lazy { MutableLiveData<ErrorHolder>() }
    val loadingLiveData : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    open fun onCreate()
    {
        errorLiveData.value=null
        loadingLiveData.value=false
    }

    fun showLoading()
    {
        loadingLiveData.value=true
    }

    fun hideLoading()
    {
        loadingLiveData.value=false
    }

    fun showError(error : Int)
    {
        this.errorLiveData.value=ErrorHolder(messageStringResource = error)
    }

    fun showError(error : String)
    {
        this.errorLiveData.value=ErrorHolder(message = error)
    }

    fun showError(error : Throwable)
    {
        this.errorLiveData.value=ErrorHolder(exception = error)
    }
}
