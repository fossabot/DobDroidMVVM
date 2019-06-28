## Chapter 5. List screens

Lists contains the following features:

- infinite scrolling, paging
- search, filters
- showing an empty view when there is no list item
- showing the standard loading / error states presented earlier
- add button support

### Example

Create a data model:

```kotlin
open class Restaurant
(
    var id : ID = uuid(),
    var name : String = "",
    var rating : Int = 3,
    var type : Int? = null
) : Serializable
{
    companion object
    {
        const val TYPE_NORMAL = 1
        const val TYPE_FAST_FOOD = 2
    }
}
```

Create a filter model:

```kotlin
class RestaurantFilter
(
    var rating : Int? = null,
    var type : Int? = null
) : BaseFilter()
```

All filter models must extend ``BaseFilter``:

```kotlin
open class BaseFilter
(
    var search : String? = null,
    var offset : Int = 0,
    var limit : Int = BaseFilterDefaults.limit
) : Serializable
```

The default page limit is 100. To change it, simply use in the Application class:

```kotlin
BaseFilterDefaults.limit=1000
```

When the user scrolls the list and a new page is loaded, the offset will be increased. Also, when the user searches something, the search field will be modified. All these behaviors are bundled within the library.

### Create the ViewModel:

```kotlin
class RestaurantListViewModel : BaseListViewModel<Restaurant, RestaurantFilter>(RestaurantFilter())
{
    override fun getItems(filter : RestaurantFilter) : Single<List<Restaurant>> =
        GetRestaurantsRequest(filter).execute()
}
```

```kotlin
class GetRestaurantsRequest
(
    val filter : RestaurantFilter
) : BaseRequest<Single<List<Restaurant>>>()
{
    override fun execute() = ApiClient.Instance.fetchRestaurants(this)
}
``` 

```kotlin
@POST("/restaurants/list")
fun fetchRestaurants(
    @Body body : GetRestaurantsRequest
) : Single<List<Restaurant>>
``` 

### Create a list cell:

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/nameLabel"
        android:background="?selectableItemBackground"
        android:clickable="true"
        android:padding="@dimen/main_margin"/>

</FrameLayout>
```

In order to use the cell in a picker list, please extend ``ChooserCellView``. Provide the layout with the layout method and bind the model to the view in setData. Please use ``setOnCellClickListener`` instead of the vanilla ``setOnClickListener`` to the view that can user can click to choose the item. Please see chapter 6 - simple list choosers.  

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

### Create a layout:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <androidx.appcompat.widget.Toolbar
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/black"
            app:titleTextColor="@color/white"
            app:title="@string/restaurants"
            android:id="@+id/toolbar"/>

        <com.miguelcatalan.materialsearchview.MaterialSearchView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:id="@+id/searchView"/>

    </FrameLayout>

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <ro.dobrescuandrei.mvvm.list.RecyclerViewMod
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:id="@+id/recyclerView"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:id="@+id/emptyView"
            android:visibility="gone"
            android:textColor="@color/black"
            android:layout_gravity="center"
            android:layout_margin="@dimen/main_margin"/>

        <com.google.android.material.floatingactionbutton.FloatingActionButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginRight="@dimen/main_margin"
            android:layout_marginBottom="@dimen/main_margin"
            android:layout_gravity="right|bottom"
            android:src="@drawable/ic_plus_white_24dp"
            android:id="@+id/addButton"
            app:fabSize="normal"/>

    </FrameLayout>

</LinearLayout>
```

### Create a fragment:

```kotlin
class RestaurantListFragment : BaseListFragment<RestaurantListViewModel, SimpleDeclarativeAdapter<Restaurant>, RestaurantFilter>()
{
```

You must provide the following configuration options:

```kotlin
    override fun provideAdapter() = SimpleDeclarativeAdapter { RestaurantCellView(it) }
    override fun provideEmptyViewText() = getString(R.string.no_restaurants)
    override fun viewModelClass() = RestaurantListViewModel::class.java
    override fun layout() = R.layout.fragment_restaurant_list
```

The ``provideAdapter`` expects a DeclarativeAdapter object. Please check out my library, [DeclarativeAdapter](https://github.com/andob/DeclarativeAdapter-kt) 

You can also include more, optional configuration options:

```kotlin
open fun provideLayoutManager() : RecyclerView.LayoutManager = ...
open fun provideItemDecoration() : RecyclerView.ItemDecoration? = ...
open fun shouldLoadMoreOnScroll() : Boolean = true
```

By default, the layout manager is a vertical linear layout manager and the item decoration is a ``DividerItemDecoration``.

To change these defaults, in the application class please use:

```kotlin
RecyclerViewDefaults.itemDecorationInstantiator={ context -> null }
RecyclerViewDefaults.layoutManagerInstantiator={ context -> StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL) }
```

Sticky header configuration, see [chapter 8 - details screens](https://github.com/andob/DobDroidMVVM/blob/master/tutorial/details.md)

```kotlin
open fun hasStickyHeaders() : Boolean = false
open fun provideStickyHeaderModelClass(position : Int) : Class<*>? = null
open fun provideStickyHeaderView(position : Int) : HeaderView<*>? = null
```

Back to our fragment implementation, I need to configure our list filters:

```kotlin
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view=super.onCreateView(inflater, container, savedInstanceState)

        toolbar.setupBackIcon()
        toolbar.setMenu(R.menu.menu_restaurants)
        toolbar[R.id.filterByRating]={
            ShowDialog.withList(context = context!!,
                title = R.string.choose_rating,
                onClick = { index, value ->
                    viewModel.notifyFilterChange { filter ->
                        filter.rating=value
                    }
                },
                values = listOf(1,2,3,4,5))
        }

        toolbar[R.id.filterByType]={
            ShowDialog.withList(context = context!!,
                title = R.string.choose_type,
                onClick = { index, value ->
                    viewModel.notifyFilterChange { filter ->
                        filter.type=index+1
                    }
                },
                values = listOf(
                    getString(R.string.normal),
                    getString(R.string.fast_food)))
        }

        return view
    }
```

Note that when the user selects a filter option, I modify my filter model from the ViewModel with the ``notifyFilterChange`` method. This methods notifies the ViewModel that the filter object is being changed, and the list will be reloaded by recalling ``getItems`` (and, in this case, recalling the API with the new filter).

Also note that I used a ``toolbar`` in the fragment and NOT in the activity. This is actually a great idea because the filter options from the fragment are logically linked to the fragment, not the activity. A quick example: what if we have an activity with two tabs, two fragments, one tab containing restaurants and the other containing dishes. We must have different filters for each list. In this case, each fragment must have different toolbars, with different filter icons. Putting the toolbar in the activity will result in hackish code (I've been there...).

Back to our fragment implementation, overrride the method that is called when the add button is pressed:

```kotlin
    override fun onAddButtonClicked()
    {
        ActivityRouter.startAddRestaurantActivity(from = context!!)
    }
```

If you don't have an add button, simply do not override this method and do not add an add button in the layout.

I usually create a refresh command for each list screen. The ``loadData`` method will reload the list.

```
    @Subscribe
    fun refresh(command : RefreshRestaurantListCommand)
    {
        viewModel.loadData()
    }
}
```

### Create and route the activity:

Create a container activity

```kotlin
class RestaurantListActivity : BaseFragmentContainerActivity<RestaurantListFragment, Restaurant>()
{
    override fun provideFragment() =
        FragmentFactory.newRestaurantListFragment()

    override fun onItemChoosed(restaurant : Restaurant) {}
}
```

On the activity router, create the method that starts the activity:

```kotlin
fun startRestaurantListActivity(from : Context)
{
    val i=Intent(from, RestaurantListActivity::class.java)
    from.startActivity(i)
}
```

You can also specify the initial filter that will be applied when the user enters the activity:

```kotlin
fun startRestaurantListActivity(from : Context, initialFilter : RestaurantFilter = RestaurantFilter())
{
    val i=Intent(from, RestaurantListActivity::class.java)
    i.setFilter(initialFilter)
    from.startActivity(i)
}
```

```kotlin
startRestaurantListActivity(from = this, initialFilter = RestaurantFilter().apply { search="something" })
```

### Standard ListViewModel LiveDatas

The ``ListViewModel`` provides the following live datas:

```kotlin
val firstPageItemsLiveData : MutableLiveData<List<MODEL>>
val nextPageItemsLiveData  : MutableLiveData<List<MODEL>>
val isEmptyLiveData : MutableLiveData<Boolean>
```

You can observe them if you want:

```kotlin
isEmptyLiveData.observe { isEmpty ->
    println(if(isEmpty) "the list is now empty" else "the list is not empty")
}
```

### Next chapter: [Single chooser list screens](https://github.com/andob/DobDroidMVVM/blob/master/tutorial/single_chooser_lists.md)
