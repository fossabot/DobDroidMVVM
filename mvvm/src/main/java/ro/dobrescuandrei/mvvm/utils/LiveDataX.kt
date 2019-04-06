package ro.dobrescuandrei.mvvm.utils

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.notify()
{
    this.value=this.value
}
