package im.years.recyclerviewwrappersample;

/**
 * Created by alvinzeng on 19/10/2016.
 */

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import java.util.ArrayList;

import im.years.recyclerviewwrapper.SimpleListFragment;
import im.years.recyclerviewwrapper.view.ListEmptyView;


/**
 * Created by alvinzeng on 1/29/16.
 */
public class TestSimpleListFragment extends SimpleListFragment<MyViewHolder, ContentMock> {
    @Override
    protected int itemViewRes() {
        return R.layout.item_hello_list;
    }

    @Override
    public void onBindViewItemHolder(MyViewHolder holder, ContentMock item, int position) {
        holder.setText(R.id.textView, item.title);
        holder.addOnClickListener(R.id.button);
        holder.addOnClickListener(R.id.textView);
        holder.addOnLongClickListener(R.id.button);
    }

    @Override
    protected void initViews() {
        super.initViews();

        enableRefresh();
        enableLoadMore();

        setEmptyView(new ListEmptyView(getContext()));

        //addHeaderView(LayoutInflater.from(getContext()).inflate(R.layout.view_footer, null));
        //addFooterView(LayoutInflater.from(getContext()).inflate(R.layout.view_footer, null));

        setListDivider(R.color.list_divider);


        ArrayList<ContentMock> contentMocks = getItems();

        ContentMock contentMock = new ContentMock("未命名", "ddddddddddd");
        contentMocks.add(contentMock);
        contentMock = new ContentMock("ceshiaa", "ddddddddddddddd");
        contentMocks.add(contentMock);
        contentMock = new ContentMock("ceshiaa", "ddddddddddddddd");
        contentMocks.add(contentMock);
        contentMock = new ContentMock("ceshiaa", "ddddddddddddddd");
        contentMocks.add(contentMock);
        contentMock = new ContentMock("ceshiaa", "ddddddddddddddd");
        contentMocks.add(contentMock);
    }

    @Override
    protected void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ArrayList<ContentMock> contentMocks = new ArrayList<ContentMock>();

                ContentMock contentMock = new ContentMock("未命名111", "ddddddddddd");
                contentMocks.add(contentMock);
                contentMock = new ContentMock("ceshiaa", "ddddddddddddddd");
                contentMocks.add(contentMock);
                contentMock = new ContentMock("ceshiaa", "ddddddddddddddd");
                contentMocks.add(contentMock);
                contentMock = new ContentMock("ceshiaa", "ddddddddddddddd");
                contentMocks.add(contentMock);
                contentMock = new ContentMock("ceshiaa", "ddddddddddddddd");
                contentMocks.add(contentMock);

                //contentMocks.clear();

                endLoading(true, false, contentMocks);
            }
        }, 2000);
    }

    @Override
    protected void onLoadMore() {
        Log.e("TTTTT", "onLoadMore");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ArrayList<ContentMock> contentMocks = new ArrayList<ContentMock>();

                ContentMock contentMock = new ContentMock("未命名22222", "ddddddddddd");
                contentMocks.add(contentMock);
                contentMock = new ContentMock("ceshiaa", "ddddddddddddddd");
                contentMocks.add(contentMock);
                contentMock = new ContentMock("ceshiaa", "ddddddddddddddd");
                contentMocks.add(contentMock);
                contentMock = new ContentMock("ceshiaa", "ddddddddddddddd");
                contentMocks.add(contentMock);
                contentMock = new ContentMock("ceshiaa", "ddddddddddddddd");
                contentMocks.add(contentMock);

                //contentMocks.clear();

                endLoading(true, true, contentMocks);
            }
        }, 2000);
    }

    @Override
    protected void onItemChildClick(View clickedItemView, int position) {
        super.onItemChildClick(clickedItemView, position);
        Toast.makeText(getContext(), "Click Title: " + getItem(position).title, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onItemClick(View clickedView, int position) {
        super.onItemClick(clickedView, position);
        Toast.makeText(getContext(), getItem(position).title, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onItemLongClick(View clickedView, int position) {
        Toast.makeText(getContext(), "Long Click: " + getItem(position).title, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onItemChildLongClick(View clickedItemView, int position) {
        super.onItemChildLongClick(clickedItemView, position);
        Toast.makeText(getContext(), "Long Click Title: " + getItem(position).title, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected int getPageSize() {
        return 50;
    }
}