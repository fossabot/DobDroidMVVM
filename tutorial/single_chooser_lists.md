## Chapter 6. Single chooser list screens

A single chooser list screen is a list screen in which the user can tap on a list cell, and the corresponding data model will be choosed. Creating single chooser lists is easy.

First, let your cell extend ``ChooserCellView`` instead of ``CellView``:

```kotlin
class RestaurantCellView : ChooserCellView<Restaurant>
{
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun layout() : Int = R.layout.cell_restaurant

    override fun setData(restaurant : Restaurant)
    {
        nameLabel.text=restaurant.name

        nameLabel.setOnCellClickListener(withModel = restaurant) {
            if (it.context is RestaurantListActivity)
                ActivityRouter.startRestaurantDetailsActivity(from = it.context, restaurant = restaurant)
            else ActivityRouter.startEditRestaurantActivity(from = it.context, restaurant = restaurant)
        }
    }
}
```

Please use ``setOnCellClickListener`` instead of ``setOnClickListener`` on the view that, by clicking will choose the item.

In ``Events.kt`` create an event:

```kotlin
class OnRestaurantChoosedEvent(val restaurant : Restaurant)
```

In your list container activity:

```kotlin
override fun onItemChoosed(restaurant : Restaurant)
{
    BackgroundEventBus.post(OnRestaurantChoosedEvent(restaurant))
    finish()
}
```

In your activity router:

```kotlin
fun startChooseRestaurantActivity(from : Context)
{
    val i=Intent(from, RestaurantListActivity::class.java)
    i.setChooseMode()
    from.startActivity(i)
}
```

And then:

```kotlin
ActivityRouter.startChooseRestaurantActivity(from = it.context)
OnActivityResult<OnRestaurantChoosedEvent> { event ->
    println(event.restaurant.toString())
}
```

Note: a list container activity will behave like a single chooser when the choose mode flag is set. Otherwise, it will behave like a normal list. The idea is to use the same common code with list screens, single and multiple list screens, so that the user can have the same feature set, search, filters, pagination, infinite scrolling etc.

### Next chapter: Multi chooser list screens (todo add link)
