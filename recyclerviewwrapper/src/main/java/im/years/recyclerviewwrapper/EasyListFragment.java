package im.years.recyclerviewwrapper;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvinzeng on 19/10/2016.
 */

public abstract class EasyListFragment<T, V extends View> extends ListFragment {
    public abstract void onBindViewItemHolder(BaseViewHolder holder, V view, T item, int position);

    protected SampleListAdapter sampleListAdapter;
    Constructor viewConstructor;
    Class viewClass;

    private Class getActualTypeClass(Class entity) {
        ParameterizedType type = (ParameterizedType) entity.getGenericSuperclass();
        Class entityClass = (Class) type.getActualTypeArguments()[1];
        return entityClass;
    }

    @Override
    protected void initViews() {
        super.initViews();

        try {
            viewClass = getActualTypeClass(this.getClass());
            viewConstructor = viewClass.getConstructor(new Class[]{Context.class});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        setAdapter(sampleListAdapter = new SampleListAdapter(null));
    }

    private View getItemView() {
        View realItemView = null;


        try {
            realItemView = (View) viewConstructor.newInstance(getContext());
            //For androidannotations
            Method method = realItemView.getClass().getMethod("onFinishInflate");
            method.setAccessible(true);
            method.invoke(realItemView);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        View view = realItemView;

        if (itemViewBackground() > 0) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setBackgroundColor(getContext().getResources().getColor(itemViewBackground()));
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(realItemView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            view = linearLayout;
        }

        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        return view;
    }

    protected ArrayList<T> getItems() {
        return (ArrayList<T>) sampleListAdapter.getData();
    }

    protected T getItem(int position) {
        return sampleListAdapter.getItem(position);
    }

    protected void reloadData() {
        sampleListAdapter.notifyDataSetChanged();
    }

    protected
    @ColorRes
    int itemViewBackground() {
        return 0;
    }

    class SampleListAdapter extends BaseQuickAdapter<T> {

        public SampleListAdapter(List<T> data) {
            super(data);
        }

        @Override
        protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
            return new BaseViewHolder(EasyListFragment.this.getItemView());
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, T t) {
            View view = baseViewHolder.getConvertView();
            if (view.getClass().getName().equals(viewClass.getName())) {
                EasyListFragment.this.onBindViewItemHolder(baseViewHolder, (V) baseViewHolder.getConvertView(), t, getItems().indexOf(t));
            } else {
                EasyListFragment.this.onBindViewItemHolder(baseViewHolder, (V) ((ViewGroup) view).getChildAt(0), t, getItems().indexOf(t));
            }
        }
    }
}
