package ro.dobrescuandrei.mvvm.multichooser

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import ro.andreidobrescu.declarativeadapterkt.view.CellView
import ro.dobrescuandrei.mvvm.R
import ro.dobrescuandrei.mvvm.chooser.BaseContainerActivity
import ro.dobrescuandrei.utils.getKolor
import ro.dobrescuandrei.utils.setBackgroundKolor

abstract class MultiChooserCellView<MODEL : IMultipleSelectable> : CellView<MODEL>
{
    constructor(context: Context?) : super(context)
    constructor(context: Context?, layout: Int) : super(context, layout)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    fun View.setOnCellClickListener(withModel : MODEL, onClickListener : (View) -> (Unit))
    {
        setupBackground(data = withModel)

        setOnClickListener { view ->
            val activity=context as? BaseContainerActivity<MODEL>
            if (activity?.chooseMode==true)
            {
                val data=withModel
                data.setIsSelected(!data.getIsSelected())
                setupBackground(data)
            }
            else
            {
                onClickListener(view)
            }
        }
    }

    private fun setupBackground(data : MODEL)
    {
        val activity=context as? BaseContainerActivity<MODEL>
        if (activity?.chooseMode==true&&data.getIsSelected())
        {
            setBackgroundKolor(context.getKolor(R.color.light_green))
        }
        else
        {
            val outValue=TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            setBackgroundResource(outValue.resourceId)
        }
    }
}
