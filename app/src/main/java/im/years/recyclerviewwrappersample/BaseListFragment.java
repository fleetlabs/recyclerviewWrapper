package im.years.recyclerviewwrappersample;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.years.recyclerviewwrapper.RecyclerViewHelper;

/**
 * Created by alvinzeng on 13/01/2017.
 */

public abstract class BaseListFragment extends Fragment {

    protected RecyclerViewHelper recyclerViewHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutRes(), container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(im.years.recyclerviewwrapper.R.id.recyclerView);
        recyclerViewHelper = RecyclerViewHelper.attach(this, recyclerView);

        recyclerViewHelper.setOnRefreshListener(new RecyclerViewHelper.OnRefreshListener() {

            @Override
            public void onLoadData(int page) {
                BaseListFragment.this.onLoadData(page);
            }

            @Override
            public int loadMorePageSize() {
                return 1;
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    protected void initViews() {
        //Empty
    }

    protected void onLoadData(final int page) {
    }

    protected
    @LayoutRes
    int getLayoutRes() {
        return im.years.recyclerviewwrapper.R.layout.az_list_fragment;
    }
}
