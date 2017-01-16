package im.years.recyclerviewwrapper;

/**
 * Created by alvinzeng on 19/10/2016.
 */

import android.support.annotation.ColorRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by alvinzeng on 2/26/16.
 */
@Deprecated
public abstract class BriefListFragment<T> extends ListFragment {
    protected abstract int itemViewRes();

    public abstract void onBindViewItemHolder(BaseViewHolder holder, T item, int position);

    protected SampleListAdapter sampleListAdapter;

    @Override
    protected void initViews() {
        super.initViews();
        setAdapter(sampleListAdapter = new SampleListAdapter());
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

    class SampleListAdapter extends BaseQuickAdapter<T, BaseViewHolder> {

        public SampleListAdapter() {
            super(BriefListFragment.this.itemViewRes(), null);
        }

        @Override
        protected View getItemView(int layoutResId, ViewGroup parent) {

            View view = this.mLayoutInflater.inflate(layoutResId, parent, false);

            if (itemViewBackground() > 0) {
                LinearLayout linearLayout = new LinearLayout(parent.getContext());
                linearLayout.setBackgroundColor(parent.getContext().getResources().getColor(itemViewBackground()));
                linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                view = linearLayout;
            }

            return view;
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, T t) {
            BriefListFragment.this.onBindViewItemHolder(baseViewHolder, t, getItems().indexOf(t));
        }
    }
}
