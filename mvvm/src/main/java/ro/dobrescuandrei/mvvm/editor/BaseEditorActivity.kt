package ro.dobrescuandrei.mvvm.editor

import android.os.Bundle
import android.widget.Button
import ro.dobrescuandrei.mvvm.BaseActivity
import ro.dobrescuandrei.mvvm.R
import ro.dobrescuandrei.mvvm.eventbus.OnEditorModel
import ro.dobrescuandrei.mvvm.navigation.ARG_ADD_MODE
import ro.dobrescuandrei.mvvm.navigation.ARG_MODEL

abstract class BaseEditorActivity<MODEL : Any, VIEW_MODEL : BaseEditorViewModel<MODEL>> : BaseActivity<VIEW_MODEL>()
{
    lateinit var saveButton : Button

    override fun loadDataFromIntent()
    {
        val model=intent?.getSerializableExtra(ARG_MODEL) as? MODEL
        if (model!=null) viewModel.modelLiveData.value=model

        viewModel.addMode=intent?.getBooleanExtra(ARG_ADD_MODE, true)?:true
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        saveButton=findViewById(R.id.saveButton)
        saveButton.setOnClickListener { viewModel.onSaveButtonClicked() }

        viewModel.modelLiveData.observe { model ->
            viewModel.shouldNotifyModelLiveDataOnPropertyChange=false
            show(model)
            viewModel.shouldNotifyModelLiveDataOnPropertyChange=true
        }
    }

    abstract fun show(model : MODEL)

    abstract fun onAdded(event : OnEditorModel.AddedEvent<MODEL>)
    abstract fun onEdited(event : OnEditorModel.EditedEvent<MODEL>)
}
