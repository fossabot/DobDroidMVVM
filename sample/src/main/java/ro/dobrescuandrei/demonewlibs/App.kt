package ro.dobrescuandrei.demonewlibs

import android.app.Application
import com.chibatching.kotpref.Kotpref
import com.franmontiel.localechanger.LocaleChanger
import io.reactivex.plugins.RxJavaPlugins
import ro.dobrescuandrei.demonewlibs.model.utils.LANGUAGE_ENGLISH
import ro.dobrescuandrei.demonewlibs.model.utils.LANGUAGE_ROMANIAN
import java.util.*

class App : Application()
{
    override fun onCreate()
    {
        super.onCreate()

        Kotpref.init(context = this)

        val locales = LinkedList<Locale>()
        locales.add(Locale(LANGUAGE_ENGLISH))
        locales.add(Locale(LANGUAGE_ROMANIAN))
        LocaleChanger.initialize(applicationContext, locales)

        RxJavaPlugins.setErrorHandler { ex ->
            if (BuildConfig.DEBUG)
                ex.printStackTrace()
        }
    }
}