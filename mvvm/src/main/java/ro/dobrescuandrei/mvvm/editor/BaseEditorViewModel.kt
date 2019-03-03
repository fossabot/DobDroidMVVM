package ro.dobrescuandrei.mvvm.editor

import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ro.dobrescuandrei.mvvm.BaseViewModel
import ro.dobrescuandrei.mvvm.R
import ro.dobrescuandrei.mvvm.utils.ForegroundEventBus
import ro.dobrescuandrei.mvvm.utils.Identifiable
import ro.dobrescuandrei.mvvm.utils.OnEditorModel

abstract class BaseEditorViewModel<MODEL : Identifiable<ID>, ID> : BaseViewModel
{
    @PublishedApi
    internal val model : MutableLiveData<MODEL> by lazy { MutableLiveData<MODEL>() }

    @PublishedApi
    internal var shouldNotifyModelLiveDataOnPropertyChange : Boolean = true

    var isValid : Boolean = false

    open fun  addMode() = model.value?.id==null
    open fun editMode() = model.value?.id!=null

    abstract fun add (model : MODEL) : Observable<ID>
    abstract fun edit(model : MODEL) : Observable<ID>

    open fun provideErrorMessage(ex : Throwable? = null) = R.string.you_have_errors_please_correct

    constructor(model : MODEL)
    {
        this.model.value=model
    }

    fun notifyChange(consumer : (MODEL) -> (Unit))
    {
        model.value?.let { model ->
            consumer(model)

            if (shouldNotifyModelLiveDataOnPropertyChange)
            {
                this.model.value=model
            }
        }
    }

    fun onSaveButtonClicked()
    {
        if (isValid)
        {
            loading.value=true

            model.value?.let { model ->
                (if (addMode())
                    add(model)
                else edit(model))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<ID>
                    {
                        override fun onSubscribe(d: Disposable) {}
                        override fun onComplete() {}

                        override fun onNext(id: ID)
                        {
                            model.id=id

                            if (addMode())
                                ForegroundEventBus.post(OnEditorModel.AddedEvent(model))
                            else ForegroundEventBus.post(OnEditorModel.EditedEvent(model))

                            ForegroundEventBus.post(OnEditorModel.AddedOrEditedEvent(model))
                        }

                        override fun onError(exception: Throwable)
                        {
                            hideLoading()
                            showError(provideErrorMessage(exception))
                        }

                    })
            }
        }
        else
        {
            error.value=provideErrorMessage()
        }
    }
}
