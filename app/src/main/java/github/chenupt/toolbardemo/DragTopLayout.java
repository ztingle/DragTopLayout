package github.chenupt.toolbardemo;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
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
    private float radio;

    public enum PanelState {
        EXPANDED,
        COLLAPSED
    }

    // 默认panel显示
    private PanelState panelState = PanelState.EXPANDED;
    private boolean shouldIntercept = true;

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

    private void init() {
        dragHelper = ViewDragHelper.create(this, 1.0f, callback);
        // 设置滚动速度
        dragHelper.setMinVelocity(80);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        dragContentView = findViewById(R.id.drag_content_view);
        menuView = findViewById(R.id.menu_view);
    }


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

    public void openMenu() {
        ViewTreeObserver vto = menuView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                menuView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                contentTop = menuView.getHeight();
                requestLayout();
            }
        });
    }

    public void openMenu(boolean anim) {
        contentTop = menuView.getHeight();
        if (anim) {
            dragHelper.smoothSlideViewTo(dragContentView, 0, contentTop);
            postInvalidate();
        } else {
            requestLayout();
        }
    }

    public void toggleMenu(){
        // TODO
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
            radio = (float)contentTop / menuView.getHeight();
            if (panelSlideListener != null) {
                DebugLog.d("radio:" + radio);
                panelSlideListener.onSliding(radio);
            }
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
            int top;
            if (yvel > 0) {
                top = menuView.getHeight() + getPaddingTop();
            } else {
                top = getPaddingTop();
            }
//            // 设置捕获的drag view 的位置
            DebugLog.d("top:" + top + ", left:" + releasedChild.getLeft());
            dragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);     // 有滑动的效果
            postInvalidate();
        }

        @Override
        public void onViewDragStateChanged(int state) {
            // 1 -> 2 -> 0
            Log.d(TAG, "onViewDragStateChanged:" + state);

            if (state == ViewDragHelper.STATE_IDLE) {
                setTouchMode(true);
                if (radio == 0){
                    panelState = PanelState.COLLAPSED;
                    setTouchMode(false);
                } else if(radio == 1.0f){
                    panelState = PanelState.EXPANDED;
                }
                if (panelSlideListener != null) {
                    panelSlideListener.onPanelCollapsed();  // 当panel收起时回调
                }
            }
            super.onViewDragStateChanged(state);
        }
    };


    @Override
    public void computeScroll() {
        // 滑动
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (shouldIntercept) {
            return dragHelper.shouldInterceptTouchEvent(ev);
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }


    public void setTouchMode(boolean shouldIntercept) {
        this.shouldIntercept = shouldIntercept;
    }

    public void setPanelState(PanelState panelState) {
        this.panelState = panelState;
    }

    // 设置回调接口
    private PanelSlideListener panelSlideListener;

    public interface PanelSlideListener {
        public void onPanelCollapsed();
        public void onSliding(float radio);
    }

    public void setPanelSlideListener(PanelSlideListener panelSlideListener) {
        this.panelSlideListener = panelSlideListener;
    }


}
