package ro.dobrescuandrei.mvvm

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.michaelflisar.bundlebuilder.BundleArgs
import com.miguelcatalan.materialsearchview.MaterialSearchView
import org.greenrobot.eventbus.Subscribe
import ro.dobrescuandrei.mvvm.eventbus.BackgroundEventBus
import ro.dobrescuandrei.mvvm.eventbus.ForegroundEventBus
import ro.dobrescuandrei.mvvm.eventbus.OnKeyboardClosedEvent
import ro.dobrescuandrei.mvvm.eventbus.OnKeyboardOpenedEvent
import ro.dobrescuandrei.mvvm.utils.getRootMessage
import ro.dobrescuandrei.utils.onCreateOptionsMenuFromFragment
import ro.dobrescuandrei.utils.onOptionsItemSelected

abstract class BaseFragment<VIEW_MODEL : BaseViewModel> : JBaseFragment<VIEW_MODEL>()
{
    var searchView : MaterialSearchView? = null

    abstract fun layout() : Int
    open fun loadDataFromArguments() {}

    fun viewModel() = ViewModelProviders.of(this)[viewModelClass()]

    fun <T : Any?> LiveData<T>.observeNullable(observer : (T?) -> (Unit))
    {
        observe(this@BaseFragment, Observer(observer))
    }

    fun <T : Any> LiveData<T>.observe(observer : (T) -> (Unit))
    {
        observe(this@BaseFragment, Observer<T?> { value ->
            if (value!=null)
                observer(value)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view=inflater.inflate(layout(), container, false)

        try { BundleArgs.bind(this, arguments) }
        catch (ex : Exception) {}

        loadDataFromArguments()

        toolbar=view.findViewById(R.id.toolbar)
        searchView=view.findViewById(R.id.searchView)

        (context as AppCompatActivity).setSupportActionBar(toolbar)

        if (viewModelClass()!=BaseViewModel::class.java)
        {
            viewModel.onCreate()

            viewModel.errorLiveData.observe { error ->
                if (error.message!=null)
                    (context as BaseActivity<*>).showToast(error.message)
                else if (error.messageStringResource!=null)
                    (context as BaseActivity<*>).showToast(getString(error.messageStringResource))
                else if (error.exception!=null)
                    (context as BaseActivity<*>).showToast(error.exception.getRootMessage())
            }

            viewModel.loadingLiveData.observe { loading ->
                if (loading) (context as BaseActivity<*>).showLoadingDialog()
                else (context as BaseActivity<*>).hideLoadingDialog()
            }
        }

        try { BackgroundEventBus.register(this) }
        catch (ex : Exception) {}

        return view
    }

    @Subscribe
    open fun onKeyboardOpened(event : OnKeyboardOpenedEvent)
    {
    }

    @Subscribe
    open fun onKeyboardClosed(event : OnKeyboardClosedEvent)
    {
        searchView?.closeSearch()
    }

    override fun onResume()
    {
        super.onResume()

        try { ForegroundEventBus.register(this) }
        catch (ex : Exception) {}
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

        toolbar=null
        searchView=null

        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater)
    {
        try
        {
            toolbar?.onCreateOptionsMenuFromFragment()
            searchView?.setMenuItem(toolbar?.menu?.findItem(R.id.searchButton))
        }
        catch (ex : Exception)
        {
            ex.printStackTrace()
        }

        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        toolbar?.onOptionsItemSelected(item)
        return super.onOptionsItemSelected(item)
    }

    open fun shouldFinishActivityOnBackPressed() : Boolean
    {
        return true
    }

    abstract class WithoutViewModel : BaseFragment<BaseViewModel>()
    {
        override fun viewModelClass() : Class<BaseViewModel> = BaseViewModel::class.java
    }
}
