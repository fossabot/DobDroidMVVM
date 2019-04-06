package ro.dobrescuandrei.mvvm.editor;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;

public class EditorViewModelChangeDelegate<FIELD_TYPE, MODEL, VIEW_MODEL extends BaseEditorViewModel<MODEL>> implements ReadWriteProperty<VIEW_MODEL, FIELD_TYPE>
{
    public final Function2<MODEL, FIELD_TYPE, Unit> viewModelChangeNotifyier;
    public FIELD_TYPE value = null;

    public EditorViewModelChangeDelegate(Function2<MODEL, FIELD_TYPE, Unit> viewModelChangeNotifyier)
    {
        this.viewModelChangeNotifyier = viewModelChangeNotifyier;
    }

    @Override
    public FIELD_TYPE getValue(VIEW_MODEL viewModel, @NotNull KProperty<?> kProperty)
    {
        return value;
    }

    @Override
    public void setValue(VIEW_MODEL viewModel, @NotNull KProperty<?> kProperty, final FIELD_TYPE newValue)
    {
        value=newValue;

        viewModel.notifyChange(new Function1<MODEL, Unit>()
        {
            @Override
            public Unit invoke(MODEL model)
            {
                viewModelChangeNotifyier.invoke(model, newValue);
                return null;
            }
        });
    }
}
