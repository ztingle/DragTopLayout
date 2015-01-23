
/*
 * Copyright 2015 chenupt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * imitations under the License.
 */

package github.chenupt.toolbardemo;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;


/**
 * Created by chenupt@gmail.com on 2015/1/18.
 * Description : Drag down to show a menu panel on the top like Google Calendar.
 */
public class DragTopLayout extends FrameLayout {

    private SetupWizard wizard;
    private ViewDragHelper dragHelper;
    private int dragRange;
    private View dragContentView;
    private View menuView;

    private int contentTop;
    private int menuHeight;

    public static enum PanelState {
        EXPANDED,
        COLLAPSED
    }

    private PanelState panelState = PanelState.COLLAPSED;
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
        dragRange = getHeight();

        if (menuHeight == 0) {
            menuHeight = menuView.getHeight();
        }
        menuView.layout(left, Math.min(0, contentTop - menuHeight), right, contentTop);
        dragContentView.layout(
                left,
                contentTop,
                right,
                contentTop + dragContentView.getHeight());
    }

    private void openMenu() {
        ViewTreeObserver vto = menuView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                menuView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                contentTop = menuHeight;
                requestLayout();
            }
        });
    }

    public void openMenu(boolean anim) {
        resetMenu(anim, menuHeight);
    }

    public void closeMenu(boolean anim) {
        resetMenu(anim, 0);
    }

    public void resetMenu(boolean anim, int top){
        contentTop = top;
        if (anim) {
            dragHelper.smoothSlideViewTo(dragContentView, 0, contentTop);
            postInvalidate();
        } else {
            requestLayout();
        }
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
            if (wizard.panelSlideListener != null){
                float radio = (float)contentTop / menuHeight;
                wizard.panelSlideListener.onSliding(radio);
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
            DebugLog.d("top:" + top + ", dy:" + dy + ", menuSize:" + menuHeight);
//            return Math.min(menuHeight, Math.max(top, getPaddingTop()));
            return Math.max(top, getPaddingTop());
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            //yvel Fling产生的值，yvel > 0 则是快速往下Fling || yvel < 0 则是快速往上Fling
            int top;
            if (yvel > 0 || contentTop > menuHeight) {
                top = menuHeight + getPaddingTop();
            } else {
                top = getPaddingTop();
            }
            dragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
            postInvalidate();
        }

        @Override
        public void onViewDragStateChanged(int state) {
            // 1 -> 2 -> 0
            if (state == ViewDragHelper.STATE_IDLE) {
                if (wizard.panelSlideListener != null) {
                    switch (panelState) {
                        case COLLAPSED:
                            panelState = PanelState.EXPANDED;
                            break;
                        case EXPANDED:
                            panelState = PanelState.COLLAPSED;
                            break;
                    }
                    wizard.panelSlideListener.onPanelStateChanged(panelState);
                }
            }
            super.onViewDragStateChanged(state);
        }
    };


    @Override
    public void computeScroll() {
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

    private void setWizard(SetupWizard setupWizard){
        this.wizard = setupWizard;
    }

    public interface PanelSlideListener {
        public void onPanelStateChanged(PanelState panelState);
        public void onSliding(float radio);
    }

    // -----------------

    public static SetupWizard from(Context context) {
        return new SetupWizard(context);
    }

    public static final class SetupWizard {
        private Context context;
        private PanelSlideListener panelSlideListener;
        private boolean initOpen;

        public SetupWizard(Context context) {
            this.context = context;
        }

        public SetupWizard listener(PanelSlideListener panelSlideListener) {
            this.panelSlideListener = panelSlideListener;
            return this;
        }

        public SetupWizard open(){
            initOpen = true;
            return this;
        }

        public void setup(DragTopLayout dragTopLayout){
            dragTopLayout.setWizard(this);
            if(initOpen){
                dragTopLayout.openMenu();
            }
        }
    }


}
