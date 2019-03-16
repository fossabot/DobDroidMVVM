package ro.dobrescuandrei.mvvm.editor

import androidx.lifecycle.MutableLiveData
import ro.dobrescuandrei.mvvm.BaseViewModel
import ro.dobrescuandrei.mvvm.R
import ro.dobrescuandrei.mvvm.eventbus.BackgroundEventBus
import ro.dobrescuandrei.mvvm.eventbus.OnEditorModel

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

    abstract fun add (model : MODEL)
    abstract fun edit(model : MODEL)

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
            model.value?.let { model ->
                try
                {
                    if (addMode())
                        add(model)
                    else edit(model)

                    if (addMode())
                        BackgroundEventBus.post(OnEditorModel.AddedEvent(model))
                    else BackgroundEventBus.post(OnEditorModel.EditedEvent(model))

                    BackgroundEventBus.post(OnEditorModel.AddedOrEditedEvent(model))
                }
                catch (exception : Exception)
                {
                    showError(provideErrorMessage(exception))
                }
            }
        }
        else
        {
            showError(provideErrorMessage())
        }
    }
}
