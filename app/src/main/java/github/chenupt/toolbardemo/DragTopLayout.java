package github.chenupt.toolbardemo;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by chenupt@gmail.com on 2015/1/18.
 * Description : Drag down to show a menu panel on the top.
 */
public class DragTopLayout extends FrameLayout {

    public static final String TAG = "DragTopLayout";

    private ViewDragHelper dragHelper;
    private int dragRange;  // 拖动范围
    private View dragContentView;
    private View menuView;

    private int contentTop;

    public enum PanelState {
        EXPANDED,
        COLLAPSED
    }

    // 默认panel显示
    private PanelState panelState = PanelState.EXPANDED;

    public DragTopLayout(Context context) {
        this(context, null);
    }

    public DragTopLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragTopLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        dragHelper = ViewDragHelper.create(this, 1.0f, callback);
        // 设置滚动速度
        dragHelper.setMinVelocity(1);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        dragContentView = findViewById(R.id.drag_content_view);
        menuView = findViewById(R.id.menu_view);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.d(TAG, "onMeasure" + widthMeasureSpec + "+" + heightMeasureSpec);
//        measureChildren(widthMeasureSpec, heightMeasureSpec);
//
//        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
//        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
//
//
//        // 设置要显示的大小返回给父view，确定view的大小
//        setMeasuredDimension(ViewCompat.resolveSizeAndState(maxWidth, widthMeasureSpec, 0), ViewCompat.resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        dragRange = getHeight();    // FIXME set menu size


        menuView.setTop(contentTop - menuView.getHeight());
        menuView.setBottom(contentTop);

        // 根据手势的top, 设置子控件的位置，这里只有一个ViewGroup的继承类
        dragContentView.layout(
                left,
                contentTop,
                right,
                contentTop + dragContentView.getHeight());
    }

    private void openMenu(){
        contentTop = menuView.getHeight();
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == dragContentView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            contentTop = top;
            // 重新布局
            requestLayout();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return dragRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            DebugLog.d("top:" + top + ", dy:" + dy + ", menuSize:" + menuView.getHeight());
            int menuSize = menuView.getHeight();
            return Math.min(menuSize, Math.max(top, getPaddingTop()));
//            return Math.max(top, getPaddingTop());
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            Log.i(TAG, "onViewReleased:" + "xvel:" + xvel + ",yvel:" + yvel);
            //yvel Fling产生的值，yvel > 0 则是快速往下Fling || yvel < 0 则是快速往上Fling
            int top = getPaddingTop();
//            int top = getPaddingTop() - dragLayout.getHeight();
//            panelState = PanelState.COLLAPSED;
//            if (yvel > 0 || (yvel == 0 && dragOffset > -0.2f)/* 后面这个小括号里判断处理拖动之后停下来但是未松手的情况 */) {
//                panelState = PanelState.EXPANDED;
//                top = getPaddingTop();
//            }
//            // 设置捕获的drag view 的位置
            DebugLog.d("top:" + top + ", left:" + releasedChild.getLeft());
            dragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);     // 有滑动的效果
            postInvalidate();
        }

        @Override
        public void onViewDragStateChanged(int state) {
            // 1 -> 2 -> 0
            Log.d(TAG, "onViewDragStateChanged:" + state);
            // 停止的状态
            if(state == ViewDragHelper.STATE_IDLE){
                // TODO call listener
            }
            super.onViewDragStateChanged(state);
        }
    };


    @Override
    public void computeScroll() {
        // 滑动
        if(dragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (shouldIntercept) {
            return dragHelper.shouldInterceptTouchEvent(ev);
        }else{
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }

    private boolean shouldIntercept = true;

    public void setTouchMode(boolean shouldIntercept){
        this.shouldIntercept = shouldIntercept;
    }

}
