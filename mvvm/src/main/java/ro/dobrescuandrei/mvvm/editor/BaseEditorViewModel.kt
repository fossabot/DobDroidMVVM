package ro.dobrescuandrei.mvvm.editor

import androidx.lifecycle.MutableLiveData
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

    abstract fun add (model : MODEL) : ID
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
                        model.id=add(model)
                    else edit(model)

                    if (addMode())
                        ForegroundEventBus.post(OnEditorModel.AddedEvent(model))
                    else ForegroundEventBus.post(OnEditorModel.EditedEvent(model))

                    ForegroundEventBus.post(OnEditorModel.AddedOrEditedEvent(model))
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
