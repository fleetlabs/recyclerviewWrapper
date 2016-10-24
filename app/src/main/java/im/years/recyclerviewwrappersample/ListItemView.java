package im.years.recyclerviewwrappersample;

/**
 * Created by alvinzeng on 19/10/2016.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by alvinzeng on 5/26/16.
 */
public class ListItemView extends LinearLayout {

    TextView textView;

    public ListItemView(Context context) {
        super(context);
        initViews();
    }

    public ListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public ListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    public void initViews() {
        inflate(getContext(), R.layout.item_hello_list, this);
        textView = (TextView) findViewById(R.id.textView);
    }

    public void setText(String text) {
        textView.setText(text);
    }
}