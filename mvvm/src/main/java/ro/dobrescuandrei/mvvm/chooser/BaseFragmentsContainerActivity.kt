package ro.dobrescuandrei.mvvm.chooser

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import ro.dobrescuandrei.mvvm.BaseFragment
import ro.dobrescuandrei.mvvm.R
import ro.dobrescuandrei.mvvm.navigation.ARG_FILTER
import ro.dobrescuandrei.mvvm.navigation.setFilter
import ro.dobrescuandrei.mvvm.utils.SimpleFragmentPagerAdapter
import ro.dobrescuandrei.utils.setupWithViewPager

abstract class BaseFragmentsContainerActivity<MODEL> : BaseContainerActivity<MODEL>()
{
    lateinit var viewPager : ViewPager

    abstract fun provideFragments() : Array<BaseFragment<*>>
    abstract fun provideBottomNavigationMenu() : Int
    open fun provideInitialTab() : Int = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setSupportActionBar(Toolbar(this))

        val initialTab=provideInitialTab()
        val fragments=provideFragments()

        viewPager=findViewById<ViewPager>(R.id.viewPager)
        viewPager.offscreenPageLimit=100
        viewPager.currentItem=initialTab
        viewPager.adapter=SimpleFragmentPagerAdapter(
            fragmentManager = supportFragmentManager,
            fragments = fragments)

        for (fragment in fragments)
        {
            val fragmentArguments=fragment.arguments?:Bundle()
            fragmentArguments.setFilter(intent?.getSerializableExtra(ARG_FILTER))
            fragment.arguments=fragmentArguments
        }

        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.inflateMenu(provideBottomNavigationMenu())
        bottomNavigationView.setupWithViewPager(viewPager, initialTab)
    }

    override fun layout() : Int = R.layout.activity_fragments_container

    fun adapter() : SimpleFragmentPagerAdapter = viewPager.adapter as SimpleFragmentPagerAdapter

    override fun onBackPressed()
    {
        if (adapter().shouldFinishActivityOnBackPressed(currentTab = viewPager.currentItem))
            super.onBackPressed()
    }

    override fun onCreateOptionsMenuForFragments(menu: Menu)
    {
        adapter().onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelectedForFragments(item: MenuItem)
    {
        adapter().onOptionsItemSelected(item, currentTab = viewPager.currentItem)
    }
}
