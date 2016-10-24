package im.years.recyclerviewwrapper;

/**
 * Created by alvinzeng on 24/10/2016.
 */

import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.HashSet;
import java.util.Set;

import static com.chad.library.adapter.base.BaseQuickAdapter.EMPTY_VIEW;
import static com.chad.library.adapter.base.BaseQuickAdapter.FOOTER_VIEW;
import static com.chad.library.adapter.base.BaseQuickAdapter.HEADER_VIEW;
import static com.chad.library.adapter.base.BaseQuickAdapter.LOADING_VIEW;

public abstract class SimpleClickListener implements RecyclerView.OnItemTouchListener {
    private GestureDetectorCompat mGestureDetector;
    private RecyclerView recyclerView;
    protected BaseQuickAdapter baseQuickAdapter;
    private boolean mIsPrepressed = false;
    private boolean mIsShowPress = false;
    private View mPressedView = null;

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        if (recyclerView == null) {
            this.recyclerView = rv;
            this.baseQuickAdapter = (BaseQuickAdapter) recyclerView.getAdapter();
            mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new ItemTouchHelperGestureListener(recyclerView));
        }
        if (!mGestureDetector.onTouchEvent(e) && e.getActionMasked() == MotionEvent.ACTION_UP && mIsShowPress) {
            if (mPressedView != null) {
                mPressedView.setPressed(false);
                mPressedView = null;
            }
            mIsShowPress = false;
            mIsPrepressed = false;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {

        private RecyclerView recyclerView;

        @Override
        public boolean onDown(MotionEvent e) {
            mIsPrepressed = true;
            mPressedView = recyclerView.findChildViewUnder(e.getX(), e.getY());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                /**
                 * when   click   Outside the region  ,mPressedView is null
                 */
                if (mPressedView != null && mPressedView.getBackground() != null) {
                    mPressedView.getBackground().setHotspot(e.getRawX(), e.getY() - mPressedView.getY());
                }
            }
            super.onDown(e);
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            if (mIsPrepressed && mPressedView != null) {
                if (!isChildTouch(mPressedView, e)) {
                    mPressedView.setPressed(true);
                    mIsShowPress = true;
                }
            }
            super.onShowPress(e);
        }

        public ItemTouchHelperGestureListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mIsPrepressed && mPressedView != null) {
                final View pressedView = mPressedView;
                BaseViewHolder vh = (BaseViewHolder) recyclerView.getChildViewHolder(pressedView);

                if (isHeaderOrFooterPosition(vh.getLayoutPosition())) {
                    return false;
                }
                if (!tapView(pressedView, e, false)) {
                    onItemClick(baseQuickAdapter, pressedView, vh.getLayoutPosition() - baseQuickAdapter.getHeaderLayoutCount());
                    mPressedView.setPressed(true);
                    resetPressedView(pressedView);
                }
            }
            return true;
        }

        private boolean isChildTouch(View view, MotionEvent e) {
            BaseViewHolder vh = (BaseViewHolder) recyclerView.getChildViewHolder(mPressedView);

            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    View v = ((ViewGroup) view).getChildAt(i);
                    if (!inRangeOfView(v, e)) {
                        continue;
                    }
                    if (isChildTouch(v, e)) {
                        return true;
                    }
                }
            }

            if (inRangeOfView(view, e) && view.isEnabled()) {
                Set<Integer> viewIds = vh.getItemChildLongClickViewIds();
                if (viewIds == null) {
                    viewIds = new HashSet<>();
                }

                viewIds.addAll(vh.getChildClickViewIds());
                if (viewIds.contains(view.getId())) {
                    return true;
                }
            }

            if (inRangeOfView(view, e) && view.isEnabled() && view.isClickable()) {
                return true;
            }

            return false;
        }

        private boolean tapView(View view, MotionEvent e, boolean isLongPress) {

            BaseViewHolder vh = (BaseViewHolder) recyclerView.getChildViewHolder(mPressedView);

            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    View v = ((ViewGroup) view).getChildAt(i);
                    if (!inRangeOfView(v, e)) {
                        continue;
                    }
                    if (tapView(v, e, isLongPress)) {
                        return true;
                    }
                }
            }

            if (inRangeOfView(view, e) && view.isEnabled()) {
                Set<Integer> viewIds = isLongPress ? vh.getItemChildLongClickViewIds(): vh.getChildClickViewIds();
                if (viewIds != null && viewIds.contains(view.getId())) {
                    if (isLongPress) {
                        onItemChildLongClick(baseQuickAdapter, view, vh.getLayoutPosition() - baseQuickAdapter.getHeaderLayoutCount());
                        view.setPressed(true);
                        mIsShowPress = true;
                    } else {
                        onItemChildClick(baseQuickAdapter, view, vh.getLayoutPosition() - baseQuickAdapter.getHeaderLayoutCount());
                        view.setPressed(true);
                        resetPressedView(view);
                    }

                    return true;
                }
            }

            //Block click
            if (inRangeOfView(view, e) && view.isEnabled() && view.isClickable()) {
                return true;
            }

            //Block long click
            if (isLongPress && vh.getChildClickViewIds() != null && vh.getChildClickViewIds().contains(view.getId())) {
                return true;
            }

            return false;
        }

        private void resetPressedView(final View pressedView) {
            if (pressedView != null) {
                pressedView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (pressedView != null) {
                            pressedView.setPressed(false);
                        }

                    }
                }, 100);
            }

            mIsPrepressed = false;
            mPressedView = null;
        }

        @Override
        public void onLongPress(MotionEvent e) {

            if (mIsPrepressed && mPressedView != null) {
                final View pressedView = mPressedView;
                BaseViewHolder vh = (BaseViewHolder) recyclerView.getChildViewHolder(pressedView);

                if (!isHeaderOrFooterPosition(vh.getLayoutPosition())) {
                    if (!tapView(pressedView, e, true)) {
                        onItemLongClick(baseQuickAdapter, mPressedView, vh.getLayoutPosition() - baseQuickAdapter.getHeaderLayoutCount());
                        mPressedView.setPressed(true);
                        mIsShowPress = true;
                    }
                }
            }
        }
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     *
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     */
    public abstract void onItemClick(BaseQuickAdapter adapter, View view, int position);

    /**
     * callback method to be invoked when an item in this view has been
     * click and held
     *
     * @param view     The view whihin the AbsListView that was clicked
     * @param position The position of the view int the adapter
     * @return true if the callback consumed the long click ,false otherwise
     */
    public abstract void onItemLongClick(BaseQuickAdapter adapter, View view, int position);

    public abstract void onItemChildClick(BaseQuickAdapter adapter, View view, int position);

    public abstract void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position);

    public boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        if (view.getVisibility() != View.VISIBLE) {
            return false;
        }
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getRawX() < x
                || ev.getRawX() > (x + view.getWidth())
                || ev.getRawY() < y
                || ev.getRawY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }

    private boolean isHeaderOrFooterPosition(int position) {
        /**
         *  have a headview and EMPTY_VIEW FOOTER_VIEW LOADING_VIEW
         */
        int type = baseQuickAdapter.getItemViewType(position);
        return (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW);
    }

}
