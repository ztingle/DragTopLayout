package github.chenupt.toolbardemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import github.chenupt.multiplemodel.BaseItemModel;

/**
 * Created by chenupt@gmail.com on 2015/1/18.
 * Description TODO
 */
public class CustomView extends BaseItemModel<String> {

    private TextView textView;

    public CustomView(Context context) {
        super(context);
        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.view_item_custom, this, true);
        textView = (TextView) findViewById(R.id.text);
    }

    @Override
    public void bindView() {
        textView.setText("item:" + viewPosition);
    }
}
