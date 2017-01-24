package im.years.recyclerviewwrapper;

import android.app.Activity;
import android.os.Looper;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import im.years.recyclerviewwrapper.decoration.HorizontalDividerItemDecoration;

/**
 * Created by alvinzeng on 13/01/2017.
 */

public class RecyclerViewHelper {
    public static RecyclerViewHelper attach(Fragment fragment, RecyclerView recyclerView) {
        return new RecyclerViewHelper(fragment, recyclerView);
    }

    public interface SimpleList<VH extends BaseViewHolder, T> extends ListClick<T> {
        int itemViewRes();

        @ColorRes
        int itemViewBackground();

        void onBindViewItemHolder(VH holder, T item, int position);
    }

    public interface EasyList<T, V extends View> extends ListClick<T> {
        @ColorRes
        int itemViewBackground();

        void onBindViewItemHolder(BaseViewHolder holder, V view, T item, int position);
    }

    public interface BriefList<T> extends ListClick<T> {
        int itemViewRes();

        @ColorRes
        int itemViewBackground();

        void onBindViewItemHolder(BaseViewHolder holder, T item, int position);
    }

    public interface ListClick<T> {
        void onItemClick(View view, T item, int position);
        void onItemLongClick(View view, T item, int position);
        void onItemChildClick(View view, T item, int position);
        void onItemChildLongClick(View view, T item, int position);
    }

    public interface OnRefreshListener {
        void onLoadData(int page);

        int loadMorePageSize();
    }

    private RecyclerView mRecyclerView;
    private Fragment mFragment;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BaseQuickAdapter mQuickAdapter;

    View emptyView;

    protected Integer currentPage = 0;
    boolean isEnabledLoadMore;
    boolean isEnabledRefresh;

    OnRefreshListener onRefreshListener;

    private RecyclerViewHelper(Fragment fragment, RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        this.mFragment = fragment;
        if (recyclerView.getParent() != null && recyclerView.getParent() instanceof SwipeRefreshLayout) {
            mSwipeRefreshLayout = (SwipeRefreshLayout) recyclerView.getParent();
        }

        setup();
    }

    private void setup() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(false);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (mQuickAdapter != null && isEnabledLoadMore) {
                        mQuickAdapter.loadMoreComplete();
                    }

                    if (onRefreshListener != null) {
                        onRefreshListener.onLoadData(1);
                    }
                }
            });

            mSwipeRefreshLayout.setColorSchemeResources(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mFragment.getActivity()));
        RecyclerViewAdapterHelper.attach(mFragment, this);

        if (mFragment instanceof ListClick) {
            final ListClick listClick = (ListClick) mFragment;
            mRecyclerView.addOnItemTouchListener(new SimpleClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    listClick.onItemClick(view, adapter.getItem(position), position);
                }

                @Override
                public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                    listClick.onItemLongClick(view, adapter.getItem(position), position);
                }

                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    listClick.onItemChildClick(view, adapter.getItem(position), position);
                }

                @Override
                public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                    listClick.onItemChildLongClick(view, adapter.getItem(position), position);
                }
            });
        }
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        mQuickAdapter = adapter;
        mRecyclerView.setAdapter(mQuickAdapter);
    }

    public BaseQuickAdapter getAdapter() {
        return mQuickAdapter;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public void enableRefresh() {
        if (mSwipeRefreshLayout == null) {
            throw new RuntimeException("Did you add SwipeRefreshLayout in your layout?");
        }

        isEnabledRefresh = true;
        mSwipeRefreshLayout.setEnabled(true);
    }

    public void disableRefresh() {
        if (mSwipeRefreshLayout == null) {
            throw new RuntimeException("Did you add SwipeRefreshLayout in your layout?");
        }

        isEnabledRefresh = false;
        mSwipeRefreshLayout.setEnabled(false);
    }

    public void enableLoadMore() {
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }
        isEnabledLoadMore = true;
        if (onRefreshListener != null) {
            mQuickAdapter.setAutoLoadMoreSize(onRefreshListener.loadMorePageSize());
        }

        mQuickAdapter.setEnableLoadMore(true);
        mQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setEnabled(false);
                }
                if (onRefreshListener != null) {
                    onRefreshListener.onLoadData(getCurrentPage() + 1);
                }
            }
        });
    }

    public void disableLoadMore() {
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }
        isEnabledLoadMore = false;

        mQuickAdapter.setEnableLoadMore(false);
        mQuickAdapter.setOnLoadMoreListener(null);
        mQuickAdapter.notifyItemChanged(mRecyclerView.getChildCount());
    }

    public void refresh(final List newData) {
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }
        if (mRecyclerView == null || mRecyclerView.getContext() == null) {
            return;
        }
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            mQuickAdapter.setNewData(newData);
        } else {
            ((Activity) mRecyclerView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mQuickAdapter.setNewData(newData);
                }
            });
        }
    }

    public void endLoading(final boolean success, final int page, final List newData) {
        endLoading(success, page > 1, newData, page);
    }

    private void endLoading(final boolean success, final boolean isMore, final List newData, final int requestPage) {
        if (mRecyclerView == null || mRecyclerView.getContext() == null) {
            return;
        }
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            endLoadingOnUiThread(success, isMore, newData, requestPage);
        } else {
            ((Activity) mRecyclerView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    endLoadingOnUiThread(success, isMore, newData, requestPage);
                }
            });
        }
    }

    private void endLoadingOnUiThread(boolean success, boolean isMore, List newData, final int requestPage) {

        synchronized (this) {
            if (success) {

                if ( isMore && ( requestPage == getCurrentPage() || requestPage > (getCurrentPage() + 1) ) ) {
                    return;
                }

                int newDataSize = newData == null ? 0 : newData.size();
                if (isMore) {

                    if (newDataSize == 0) {
                        mQuickAdapter.loadMoreEnd();
                    } else {
                        mQuickAdapter.loadMoreComplete();
                    }

                    mQuickAdapter.addData(newData);
                } else {
                    if (newDataSize == 0 && emptyView != null && mQuickAdapter.getEmptyView() != emptyView) {
                        mQuickAdapter.setEmptyView(emptyView);
                        mQuickAdapter.setHeaderFooterEmpty(true, false);
                    }

                    if (isEnabledLoadMore) {
                        enableLoadMore();
                    }
                    mQuickAdapter.setNewData(newData);
                }

                currentPage = requestPage;

            } else {
                if (isMore) {
                    mQuickAdapter.loadMoreFail();
                }
            }

            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (isEnabledRefresh) {
                    mSwipeRefreshLayout.setEnabled(true);
                }
            }
        }
    }

    public void setListDivider(@ColorRes int color) {
        mRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(mRecyclerView.getContext())
                        .colorResId(color)
                        .build());
    }

    public void setEmptyView(View view) {
        emptyView = view;
    }

    public void addHeaderView(View header) {
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }

        mQuickAdapter.addHeaderView(header);
    }

    public void addFooterView(View footer) {
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }

        mQuickAdapter.addFooterView(footer);
    }

    public void removeHeaderView(View header) {
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }

        mQuickAdapter.removeHeaderView(header);
    }

    public void removeFooterView(View footer) {
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }

        mQuickAdapter.removeFooterView(footer);
    }

    public Integer getCurrentPage() {
        return currentPage;
    }
}


