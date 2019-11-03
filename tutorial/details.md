## Chapter 8. Details screens

Details screens are simple list screens with a twist.

The ViewModel must provide a heterogeneous list of items to be displayed on the list (in this case, a restaurant, a sticky header, more restaurants etc). 

```kotlin
class RestaurantDetailsViewModel : BaseDetailsViewModel<Restaurant>()
{
    var firstPageStickyHeaderIndex  : Int = Int.MAX_VALUE
    var secondPageStickyHeaderIndex : Int = Int.MAX_VALUE

    override fun getItems() : Single<List<Any>> =
            GetRestaurantDetailsRequest(model.id).execute()
                .map { restaurant ->
                    val items=mutableListOf<Any>()
                    
                    firstPageStickyHeaderIndex=items.size
                    items.add(FirstPageStickyHeader())
                    for (i in 1..10)
                        items.add(restaurant)

                    secondPageStickyHeaderIndex=items.size
                    items.add(SecondPageStickyHeader())
                    for (i in 1..10)
                        items.add(restaurant)
                    
                    return@map items
                }
}
```

The activity:

```kotlin
class RestaurantDetailsActivity : BaseDetailsActivity<Restaurant, RestaurantDetailsViewModel, DeclarativeAdapter>()
{
    override fun viewModelClass() : Class<RestaurantDetailsViewModel> = RestaurantDetailsViewModel::class.java
    override fun layout() : Int = R.layout.activity_restaurant_details

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        toolbar.setupBackIcon()
        toolbar.setTitle(R.string.restaurant)
    }

    override fun provideAdapter() : DeclarativeAdapter
    {
        val adapter=DeclarativeAdapter()

        adapter.whenInstanceOf(Restaurant::class,
                use = { RestaurantCellView(it) })
            .whenInstanceOf(
                FirstPageStickyHeader::class,
                use = { FirstPageStickyHeaderView(it) })
            .whenInstanceOf(
                SecondPageStickyHeader::class,
                use = { SecondPageStickyHeaderView(it) })

        return adapter
    }
```

To configure the sticky headers:

```kotlin
    override fun provideStickyHeaderModelClass(position: Int): Class<*>? = when
    {
        position>=viewModel.secondPageStickyHeaderIndex -> SecondPageStickyHeader::class.java
        position>=viewModel.firstPageStickyHeaderIndex  -> FirstPageStickyHeader::class.java
        else -> null
    }

    override fun provideStickyHeaderView(position: Int): HeaderView<*>? = when
    {
        position>=viewModel.secondPageStickyHeaderIndex -> SecondPageStickyHeaderView(this)
        position>=viewModel.firstPageStickyHeaderIndex  -> FirstPageStickyHeaderView(this)
        else -> null
    }
}
```

In the activity router:

```kotlin
fun startRestaurantDetailsActivity(from : Context, restaurant : Restaurant)
{
    val i=Intent(from, RestaurantDetailsActivity::class.java)
    i.setModel(restaurant)
    from.startActivity(i)
}
```

### Next chapter: [Editor screens](https://github.com/andob/DobDroidMVVM/blob/master/tutorial/editors.md)
