package im.years.recyclerviewwrappersample;

import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import im.years.recyclerviewwrapper.RecyclerViewHelper;

/**
 * Created by alvinzeng on 13/01/2017.
 */

public class TestHelperFragment extends BaseListFragment implements RecyclerViewHelper.SimpleList<MyViewHolder, ContentMock> {

    @Override
    protected void initViews() {
        super.initViews();
        ArrayList<ContentMock> contentMocks = new ArrayList<>();

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

        recyclerViewHelper.refresh(contentMocks);
        recyclerViewHelper.enableRefresh();
        recyclerViewHelper.enableLoadMore();

        onLoadData(1);
    }

    @Override
    public int itemViewRes() {
        return R.layout.item_hello_list;
    }

    @Override
    public int itemViewBackground() {
        return 0;
    }

    @Override
    public void onBindViewItemHolder(MyViewHolder holder, ContentMock item, int position) {
        holder.addOnClickListener(R.id.button);
        holder.setText(R.id.textView, item.title);
    }

    @Override
    protected void onLoadData(final int page) {
        super.onLoadData(page);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ArrayList<ContentMock> contentMocks = new ArrayList<ContentMock>();

                ContentMock contentMock = new ContentMock("未命名"+page, "ddddddddddd");
                contentMocks.add(contentMock);
                contentMock = new ContentMock("ceshiaa"+page, "ddddddddddddddd");
                contentMocks.add(contentMock);
                contentMock = new ContentMock("ceshiaa"+page, "ddddddddddddddd");
                contentMocks.add(contentMock);
                contentMock = new ContentMock("ceshiaa"+page, "ddddddddddddddd");
                contentMocks.add(contentMock);


                recyclerViewHelper.endLoading(true, page, contentMocks);
            }
        }, 2000);
    }

    @Override
    public void onItemClick(View view, ContentMock item, int position) {
        Toast.makeText(getContext(), "onItemClick: " + item.title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View view, ContentMock item, int position) {
        Toast.makeText(getContext(), "onItemLongClick: " + item.title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemChildClick(View view, ContentMock item, int position) {
        Toast.makeText(getContext(), "onItemChildClick: " + item.title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemChildLongClick(View view, ContentMock item, int position) {
        Toast.makeText(getContext(), "onItemChildLongClick: " + item.title, Toast.LENGTH_SHORT).show();
    }
}
