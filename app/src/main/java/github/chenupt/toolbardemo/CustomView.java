package github.chenupt.toolbardemo;

import android.content.Context;
import android.view.LayoutInflater;

import github.chenupt.multiplemodel.BaseItemModel;

/**
 * Created by chenupt@gmail.com on 2015/1/18.
 * Description TODO
 */
public class CustomView extends BaseItemModel<String> {


    public CustomView(Context context) {
        super(context);
        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.view_item_custom, this, true);
    }

    @Override
    public void bindView() {

    }
}
