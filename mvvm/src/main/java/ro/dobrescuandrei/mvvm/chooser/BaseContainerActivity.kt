package ro.dobrescuandrei.mvvm.chooser

import ro.dobrescuandrei.mvvm.BaseActivity
import ro.dobrescuandrei.mvvm.utils.ARG_CHOOSE_MODE

abstract class BaseContainerActivity<MODEL> : BaseActivity.WithoutViewModel()
{
    var chooseMode : Boolean = false

    override fun loadDataFromIntent()
    {
        chooseMode=intent?.getBooleanExtra(ARG_CHOOSE_MODE, false)?:false
    }

    open fun onItemChoosed(item : MODEL)
    {
    }
}