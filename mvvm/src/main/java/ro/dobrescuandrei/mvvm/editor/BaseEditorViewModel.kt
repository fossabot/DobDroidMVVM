package ro.dobrescuandrei.mvvm.editor

import android.annotation.SuppressLint
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ro.dobrescuandrei.mvvm.BaseViewModel
import ro.dobrescuandrei.mvvm.R
import ro.dobrescuandrei.mvvm.eventbus.ForegroundEventBus
import ro.dobrescuandrei.mvvm.eventbus.OnEditorModel
import ro.dobrescuandrei.mvvm.utils.NonNullableLiveData
import kotlin.properties.ReadWriteProperty

abstract class BaseEditorViewModel<MODEL : Any> : BaseViewModel
{
    @PublishedApi
    internal val modelLiveData : NonNullableLiveData<MODEL>

    constructor(model : MODEL)
    {
        this.modelLiveData=NonNullableLiveData(initialValue = model)
    }

    @PublishedApi
    internal var shouldNotifyModelLiveDataOnPropertyChange : Boolean = true

    @PublishedApi
    internal var addMode : Boolean = true

    var isValid : Boolean = false

    open fun  addMode() = addMode
    open fun editMode() = !addMode

    abstract fun add (model : MODEL) : Completable
    abstract fun edit(model : MODEL) : Completable

    open fun provideErrorMessage(ex : Throwable? = null) = R.string.you_have_errors_please_correct

    override fun onCreate()
    {
        super.onCreate()

        onCreate(modelLiveData.value)
    }

    open fun onCreate(model : MODEL) {}

    fun notifyChange(consumer : (MODEL) -> (Unit))
    {
        consumer(modelLiveData.value)

        if (shouldNotifyModelLiveDataOnPropertyChange)
        {
            modelLiveData.value=modelLiveData.value
        }
    }

    fun <FIELD_TYPE, MODEL : Any, VIEW_MODEL : BaseEditorViewModel<MODEL>>
        observable(viewModelChangeNotifyier : (MODEL, FIELD_TYPE) -> (Unit))
            : ReadWriteProperty<VIEW_MODEL, FIELD_TYPE> =
                EditorViewModelChangeDelegate(viewModelChangeNotifyier)

    @SuppressLint("CheckResult")
    fun onSaveButtonClicked()
    {
        if (isValid)
        {
            showLoading()

            (if (addMode())
                add(modelLiveData.value)
            else edit(modelLiveData.value))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onError = { exception ->
                    hideLoading()
                    showError(provideErrorMessage(exception))
                }, onComplete = {
                    hideLoading()

                    if (addMode())
                        ForegroundEventBus.post(OnEditorModel.AddedEvent(modelLiveData.value))
                    else ForegroundEventBus.post(OnEditorModel.EditedEvent(modelLiveData.value))
                })
        }
        else
        {
            showError(provideErrorMessage())
        }
    }
}
