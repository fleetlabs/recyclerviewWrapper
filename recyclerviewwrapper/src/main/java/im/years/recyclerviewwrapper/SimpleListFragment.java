package im.years.recyclerviewwrapper;

import android.support.annotation.ColorRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvinzeng on 24/10/2016.
 */

public abstract class SimpleListFragment<VH extends BaseViewHolder, T> extends ListFragment {
    protected abstract int itemViewRes();

    public abstract void onBindViewItemHolder(VH holder, T item, int position);

    protected SampleListAdapter sampleListAdapter;

    Constructor holderConstructor;

    @Override
    protected void initViews() {
        super.initViews();

        try {
            holderConstructor = getActualTypeClass(this.getClass()).getConstructor(new Class[]{View.class});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        setAdapter(sampleListAdapter = new SampleListAdapter(itemViewRes(), null));
    }

    protected ArrayList<T> getItems() {
        return (ArrayList<T>) sampleListAdapter.getData();
    }

    protected T getItem(int position) {
        return sampleListAdapter.getItem(position);
    }

    protected
    @ColorRes
    int itemViewBackground() {
        return 0;
    }

    private Class getActualTypeClass(Class entity) {

        Type superclass = entity.getGenericSuperclass();
        boolean find = (superclass instanceof ParameterizedType);
        while (!find) {
            superclass = ((Class)superclass).getGenericSuperclass();
            find = (superclass instanceof ParameterizedType);
        }

        ParameterizedType type = (ParameterizedType) superclass;
        Class entityClass = (Class) type.getActualTypeArguments()[0];
        return entityClass;
    }

    class SampleListAdapter extends BaseQuickAdapter<T> {

        public SampleListAdapter(int layoutResId, List<T> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, T t) {
            onBindViewItemHolder((VH) baseViewHolder, t, getItems().indexOf(t));
        }

        @Override
        protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
            try {

                View view = getItemView(itemViewRes(), parent);

                if (itemViewBackground() > 0) {
                    LinearLayout linearLayout = new LinearLayout(parent.getContext());
                    linearLayout.setBackgroundColor(parent.getContext().getResources().getColor(itemViewBackground()));
                    linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    linearLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    view = linearLayout;
                }

                return (BaseViewHolder) holderConstructor.newInstance(view);
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return super.onCreateDefViewHolder(parent, viewType);
        }
    }
}
