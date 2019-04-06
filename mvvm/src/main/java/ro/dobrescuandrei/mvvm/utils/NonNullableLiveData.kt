package ro.dobrescuandrei.mvvm.utils

import androidx.lifecycle.MutableLiveData

class NonNullableLiveData<T : Any> : MutableLiveData<T>
{
    constructor(initialValue : T) : super()
    {
        value=initialValue
    }

    override fun getValue() : T
    {
        return super.getValue()!!
    }
}
