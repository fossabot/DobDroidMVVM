## Chapter 7. Multi chooser list screens

To create multi chooser list screens:

### Modify your model

```kotlin
class Issue
(
    val id : ID = 0,
    var name : String = "",
    isSelected : Boolean = false
) : BaseSelectableModel(isSelected)
```

### Create a multichoose cell

```
class IssueCellView : MultiChooserCellView<Issue>
{
    constructor(context: Context?) : super(context)
    constructor(context: Context?, layout: Int) : super(context, layout)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun layout() = R.layout.cell_issue

    override fun setData(issue : Issue)
    {
        nameLabel.text=issue.name

        cell.setOnCellClickListener(withModel = issue) {
            //stuff to do on issue clicked, when the activity is not in choose mode
        }
    }
}
```

### Create the activity

```kotlin
class IssueListActivity : BaseMultichooseListActivity<IssueListFragment, Issue>()
{
    override fun provideFragment() = FragmentFactory.newIssueListFragment()

    override fun onItemsChoosed(issues : List<Issue>)
    {
        BackgroundEventBus.post(OnIssuesChoosedEvent(issues))
        finish()
    }

    @Subscribe
    fun onIssueAdded(event : OnIssueAddedEvent)
    {
        super.onItemAdded(event.issue)
    }
}
```

### Create the activity routing

```kotlin
fun startIssueListActivity(from : Context, filter : IssueFilter = IssueFilter())
{
    val i=Intent(from, IssueListActivity::class.java)
    i.setChooseMode()
    i.setFilter(filter)
    from.startActivity(i)
}
```

### Use the activity

```kotlin
ActivityRouter.startIssueListActivity(from = it.context)
OnActivityResult<OnIssuesChoosedEvent> { event ->
    viewModel.notifyChange { document ->
        document.issues.addAll(event.issues)
    }
}
```

### Next chapter: [Details screens](https://github.com/andob/DobDroidMVVM/blob/master/tutorial/details.md)
