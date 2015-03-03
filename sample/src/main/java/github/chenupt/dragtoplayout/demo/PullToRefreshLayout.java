package github.chenupt.dragtoplayout.demo;

import android.content.Context;
import android.util.AttributeSet;

import com.handmark.pulltorefresh.library.PullToRefreshBase;

import github.chenupt.dragtoplayout.DragTopLayout;

/**
 * Created by chenupt@gmail.com on 3/3/15.
 * Description :
 */
public class PullToRefreshLayout extends PullToRefreshBase<DragTopLayout> {

    public PullToRefreshLayout(Context context) {
        super(context);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getRefreshableView().onFinishInflate();
    }

    @Override
    protected DragTopLayout createRefreshableView(Context context, AttributeSet attrs) {
        DragTopLayout dragTopLayout = new DragTopLayout(context, attrs);
        return dragTopLayout;
    }

    @Override
    protected boolean isReadyForPullStart() {
        DragTopLayout refreshableView = getRefreshableView();
        return refreshableView.getState() == DragTopLayout.PanelState.EXPANDED;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        return false;
    }
}

