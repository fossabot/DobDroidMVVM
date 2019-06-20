package ro.dobrescuandrei.mvvm

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.franmontiel.localechanger.LocaleChanger
import com.michaelflisar.bundlebuilder.BundleArgs
import com.miguelcatalan.materialsearchview.MaterialSearchView
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import org.greenrobot.eventbus.Subscribe
import ro.andreidobrescu.activityresulteventbus.ActivityResultEventBus
import ro.dobrescuandrei.mvvm.eventbus.*
import ro.dobrescuandrei.mvvm.utils.getRootMessage
import ro.dobrescuandrei.utils.Keyboard
import ro.dobrescuandrei.utils.onCreateOptionsMenu
import ro.dobrescuandrei.utils.onOptionsItemSelected

abstract class BaseActivity<VIEW_MODEL : BaseViewModel> : JBaseActivity<VIEW_MODEL>()
{
    var searchView : MaterialSearchView? = null
    private var unregistrar : Unregistrar? = null
    private var loadingDialog : AlertDialog? = null

    abstract fun layout() : Int
    open fun loadDataFromIntent() {}

    fun <T : Any?> LiveData<T>.observeNullable(observer : (T?) -> (Unit))
    {
        observe(this@BaseActivity, Observer(observer))
    }

    fun <T : Any> LiveData<T>.observe(observer : (T) -> (Unit))
    {
        observe(this@BaseActivity, Observer<T?> { value ->
            if (value!=null)
                observer(value)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        try { BundleArgs.bind(this, intent.extras) }
        catch (ex : Exception) {}

        loadDataFromIntent()

        setContentView(layout())

        toolbar=findViewById(R.id.toolbar)
        searchView=findViewById(R.id.searchView)

        setSupportActionBar(toolbar)

        if (viewModelClass()!=BaseViewModel::class.java)
        {
            viewModel.onCreate()

            viewModel.errorLiveData.observe { error ->
                if (error.message!=null)
                    showToast(error.message)
                else if (error.messageStringResource!=null)
                    showToast(getString(error.messageStringResource))
                else if (error.exception!=null)
                    showToast(error.exception.getRootMessage())
            }

            viewModel.loadingLiveData.observe { loading ->
                if (loading) showLoadingDialog()
                else hideLoadingDialog()
            }
        }

        try { BackgroundEventBus.register(this) }
        catch (ex : Exception) {}

        unregistrar=KeyboardVisibilityEvent.registerEventListener(this) { isOpen ->
            if (isOpen)
            {
                onKeyboardOpened()
            }
            else
            {
                onKeyboardClosedImmediate()

                Handler().postDelayed({
                    onKeyboardClosed()
                }, 100)
            }
        }
    }

    open fun onKeyboardOpened()
    {
        ForegroundEventBus.post(OnKeyboardOpenedEvent())
    }

    open fun onKeyboardClosedImmediate()
    {
    }

    open fun onKeyboardClosed()
    {
        searchView?.closeSearch()

        ForegroundEventBus.post(OnKeyboardClosedEvent())
    }

    fun showLoadingDialog()
    {
        if (loadingDialog==null)
        {
            val builder=AlertDialog.Builder(this@BaseActivity, R.style.TransparentDialogTheme)
            builder.setCancelable(false)

            val inflater=LayoutInflater.from(this@BaseActivity)
            val view=inflater.inflate(R.layout.dialog_progress, null)
            builder.setView(view)

            loadingDialog=builder.create()
            loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

        loadingDialog?.show()
    }

    fun hideLoadingDialog()
    {
        loadingDialog?.dismiss()
    }

    fun showToast(error : String)
    {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onResume()
    {
        super.onResume()

        try { ForegroundEventBus.register(this) }
        catch (ex : Exception) {}
    }

    override fun onPostResume()
    {
        super.onPostResume()

        ActivityResultEventBus.onActivityPostResumed(this)
    }

    override fun onPause()
    {
        super.onPause()

        try { ForegroundEventBus.unregister(this) }
        catch (ex : Exception) {}
    }

    override fun onDestroy()
    {
        try { BackgroundEventBus.unregister(this) }
        catch (ex : Exception) {}

        ActivityResultEventBus.onActivityDestroyed(this)

        unregistrar?.unregister()
        unregistrar=null

        hideLoadingDialog()
        loadingDialog=null

        toolbar=null
        searchView=null

        super.onDestroy()
    }

    override fun finish()
    {
        Keyboard.close(this)

        super.finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        try
        {
            toolbar?.onCreateOptionsMenu(menuInflater, menu)
            onCreateOptionsMenuForFragments(menu)
            searchView?.setMenuItem(menu.findItem(R.id.searchButton))
        }
        catch (ex : Exception) {}

        return super.onCreateOptionsMenu(menu)
    }

    open fun onCreateOptionsMenuForFragments(menu : Menu)
    {
        for (fragment in supportFragmentManager.fragments)
            fragment.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        toolbar?.onOptionsItemSelected(item)
        onOptionsItemSelectedForFragments(item)
        return super.onOptionsItemSelected(item)
    }

    open fun onOptionsItemSelectedForFragments(item: MenuItem)
    {
        for (fragment in supportFragmentManager.fragments)
            fragment.onOptionsItemSelected(item)
    }

    override fun attachBaseContext(newBase: Context?)
    {
        super.attachBaseContext(LocaleChanger.configureBaseContext(newBase))
    }

    @Subscribe
    fun finish(command : FinishAllActivitiesCommand)
    {
        if (command is FinishAllActivitiesCommand.OfTypes)
        {
            if (command.types.contains(this::class.java))
                finish()
        }
        else if (command is FinishAllActivitiesCommand.Except)
        {
            if (!command.types.contains(this::class.java))
                finish()
        }
        else finish()
    }

    abstract class WithoutViewModel : BaseActivity<BaseViewModel>()
    {
        override fun viewModelClass() : Class<BaseViewModel> = BaseViewModel::class.java
    }
}
