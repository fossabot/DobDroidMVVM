package ro.dobrescuandrei.mvvm.editor

import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ro.dobrescuandrei.mvvm.BaseViewModel
import ro.dobrescuandrei.mvvm.R
import ro.dobrescuandrei.mvvm.eventbus.ForegroundEventBus
import ro.dobrescuandrei.mvvm.eventbus.OnEditorModel
import kotlin.properties.ReadWriteProperty

abstract class BaseEditorViewModel<MODEL : Any> : BaseViewModel
{
    @PublishedApi
    internal val model : MutableLiveData<MODEL> by lazy { MutableLiveData<MODEL>() }

    @PublishedApi
    internal var shouldNotifyModelLiveDataOnPropertyChange : Boolean = true

    @PublishedApi
    internal var addMode : Boolean = true

    var isValid : Boolean = false

    open fun  addMode() = addMode
    open fun editMode() = !addMode

    abstract fun add (model : MODEL) : Observable<Unit>
    abstract fun edit(model : MODEL) : Observable<Unit>

    open fun provideErrorMessage(ex : Throwable? = null) = R.string.you_have_errors_please_correct

    constructor(model : MODEL)
    {
        this.model.value=model
    }

    override fun onCreate()
    {
        super.onCreate()

        if (addMode)
            onCreateForAdd()
        else onCreateForEdit(model.value!!)
    }

    open fun onCreateForAdd() {}
    open fun onCreateForEdit(model : MODEL) {}

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

    fun <FIELD_TYPE, MODEL : Any, VIEW_MODEL : BaseEditorViewModel<MODEL>>
        observable(viewModelChangeNotifyier : (MODEL, FIELD_TYPE) -> (Unit))
            : ReadWriteProperty<VIEW_MODEL, FIELD_TYPE> =
                EditorViewModelChangeDelegate(viewModelChangeNotifyier)

    fun onSaveButtonClicked()
    {
        if (isValid)
        {
            showLoading()

            model.value?.let { model ->
                (if (addMode())
                    add(model)
                else edit(model))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Unit>
                    {
                        override fun onSubscribe(d: Disposable) {}
                        override fun onComplete() {}

                        override fun onNext(result: Unit)
                        {
                            hideLoading()

                            if (addMode())
                                ForegroundEventBus.post(OnEditorModel.AddedEvent(model))
                            else ForegroundEventBus.post(OnEditorModel.EditedEvent(model))
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
            showError(provideErrorMessage())
        }
    }
}
