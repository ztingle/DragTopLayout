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

package github.chenupt.dragtoplayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by chenupt@gmail.com on 2015/1/18.
 * Description : Drag down to show a menu panel on the top.
 */
public class DragTopLayout extends FrameLayout {

    private static SetupWizard wizard;
    private ViewDragHelper dragHelper;
    private int dragRange;
    private View dragContentView;
    private View topView;

    private int contentTop;
    private int topViewHeight;
    private boolean isRefreshing;
    private boolean shouldUpdateContentHeight;

    // Used for scrolling
    private float lastSlidingRatio = 0;
    private boolean dispatchingChildrenDownFaked = false;
    private boolean dispatchingChildrenContentView = false;
    private float dispatchingChildrenStartedAtY = Float.MAX_VALUE;
    private float dispatchingChildrenAtRatio = 0f;

    public static enum PanelState {

        COLLAPSED(0),
        EXPANDED(1),
        SLIDING(2);

        private int asInt;

      PanelState(int i){
        this.asInt = i;
      }

      static PanelState fromInt(int i){
        switch (i){
          case 0 : return COLLAPSED;
          case 2 : return SLIDING;
          default:
          case 1 : return EXPANDED;
        }
      }

      public int toInt(){
        return asInt;
      }
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
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        wizard = new SetupWizard();
        dragHelper = ViewDragHelper.create(this, 1.0f, callback);

        // init from attrs
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DragTopLayout);
        wizard.setCollapseOffset(a.getDimensionPixelSize(R.styleable.DragTopLayout_dtlCollapseOffset,
                wizard.collapseOffset));
        wizard.setOverDrag(a.getBoolean(R.styleable.DragTopLayout_dtlOverDrag, wizard.overDrag));
        wizard.initOpen = a.getBoolean(R.styleable.DragTopLayout_dtlOpen, wizard.initOpen);
        wizard.dragContentViewId = a.getResourceId(R.styleable.DragTopLayout_dtlDragContentView, -1);
        wizard.topViewId = a.getResourceId(R.styleable.DragTopLayout_dtlTopView, -1);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() < 2) {
          throw new RuntimeException("Content view must contains two child views at least.");
        }

        if (wizard.topViewId != -1 && wizard.dragContentViewId == -1){
            throw new IllegalArgumentException("You have set \"dtlTopView\" but not \"dtlDragContentView\". Both are required!");
        }

        if (wizard.dragContentViewId != -1 && wizard.topViewId == -1){
            throw new IllegalArgumentException("You have set \"dtlDragContentView\" but not \"dtlTopView\". Both are required!");
        }

        if (wizard.dragContentViewId != -1 && wizard.topViewId != -1){
          topView = findViewById(wizard.topViewId);
          dragContentView = findViewById(wizard.dragContentViewId);

          if (topView == null){
            throw new IllegalArgumentException("\"dtlTopView\" with id = \"@id/"
                + getResources().getResourceEntryName(wizard.topViewId)
                + "\" has NOT been found. Is a child with that id in this "+getClass().getSimpleName()+"?");
          }


          if (dragContentView == null) {
            throw new IllegalArgumentException("\"dtlDragContentView\" with id = \"@id/"
                + getResources().getResourceEntryName(wizard.dragContentViewId)
                + "\" has NOT been found. Is a child with that id in this "
                + getClass().getSimpleName()
                + "?");
          }

        } else {
          topView = getChildAt(0);
          dragContentView = getChildAt(1);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        dragRange = getHeight();

        // In case of resetting the content top to target position before sliding.
        int contentTopTemp = contentTop;
        resetTopViewHeight();
        resetContentHeight();

        topView.layout(left, Math.min(topView.getPaddingTop(), contentTop - topViewHeight), right,
                contentTop);
        dragContentView.layout(left, contentTopTemp, right,
                contentTopTemp + dragContentView.getHeight());
    }

    private void resetTopViewHeight() {
        int newTopHeight = topView.getHeight();
        // Top layout is changed
        if (topViewHeight != newTopHeight) {
            if (panelState == PanelState.EXPANDED) {
                contentTop = newTopHeight;
                handleSlide(newTopHeight);
            }
            topViewHeight = newTopHeight;
        }
    }

    private void resetContentHeight(){
        if (shouldUpdateContentHeight) {
            ViewGroup.LayoutParams layoutParams = dragContentView.getLayoutParams();
            layoutParams.height = getHeight() - wizard.collapseOffset;
            dragContentView.setLayoutParams(layoutParams);
            shouldUpdateContentHeight = false;
        }
    }

    private void setDispatchingChildrenAtRatio() {
        dispatchingChildrenAtRatio = ((float) wizard.collapseOffset) / ((float) topViewHeight);
    }

    private void handleSlide(final int top) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                dragHelper.smoothSlideViewTo(dragContentView, getPaddingLeft(), top);
                postInvalidate();
            }
        });
    }

    public void openTopView(boolean anim) {
        resetDragContent(anim, topViewHeight);
    }

    public void closeTopView(boolean anim) {
        resetDragContent(anim, 0);
    }

    public void toggleTopView() {
        toggleTopView(false);
    }

    public void toggleTopView(boolean touchMode) {
        switch (panelState) {
            case COLLAPSED:
                openTopView(true);
                if (touchMode) {
                    setTouchMode(true);
                }
                break;
            case EXPANDED:
                closeTopView(true);
                if (touchMode) {
                    setTouchMode(false);
                }
                break;
        }
    }

    private void resetDragContent(boolean anim, int top) {
        contentTop = top;
        if (anim) {
            dragHelper.smoothSlideViewTo(dragContentView, getPaddingLeft(), contentTop);
            postInvalidate();
        } else {
            requestLayout();
        }
    }

    public void setOverDrag(boolean overDrag) {
        wizard.overDrag = overDrag;
    }

    public boolean isOverDrag() {
        return wizard.overDrag;
    }

    /**
     * Get refresh state
     */
    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void setRefreshing(boolean isRefreshing) {
        this.isRefreshing = isRefreshing;
    }

    /**
     * Complete refresh and reset the refresh state.
     */
    public void onRefreshComplete() {
        isRefreshing = false;
    }

    public void setCollapseOffset(int px) {
        wizard.collapseOffset = px;
        shouldUpdateContentHeight = true;
        resetContentHeight();
        setDispatchingChildrenAtRatio();
    }

    public int getCollapseOffset() {
        return wizard.collapseOffset;
    }

    private void calculateRatio(float top) {

        float ratio = top / topViewHeight;
        lastSlidingRatio = ratio;
        if (dispatchingChildrenContentView && ratio > dispatchingChildrenAtRatio) {
            resetDispatchingContentView();
        }

        if (wizard.panelListener != null) {
            // Calculate the ratio while dragging.
            wizard.panelListener.onSliding(ratio);
            if (ratio > wizard.refreshRatio && !isRefreshing) {
                wizard.panelListener.onRefresh();
                isRefreshing = true;
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {

      Parcelable superState = super.onSaveInstanceState();
      SavedState state = new SavedState(superState);
      state.panelState = panelState.toInt();

      return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

      if(!(state instanceof SavedState)) {
        super.onRestoreInstanceState(state);
        return;
      }

      SavedState s = (SavedState)state;
      super.onRestoreInstanceState(s.getSuperState());

      this.panelState = PanelState.fromInt(s.panelState);
      if (panelState == PanelState.COLLAPSED){
        closeTopView(false);
      } else {
        openTopView(false);
      }
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (child == topView) {
                dragHelper.captureChildView(dragContentView, pointerId);
                return false;
            }
            return child == dragContentView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            contentTop = top;
            requestLayout();
            calculateRatio(contentTop);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return dragRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (wizard.overDrag) {
                // Drag over the top view height.
                return Math.max(top, getPaddingTop() + wizard.collapseOffset);
            } else {
                return Math.min(topViewHeight, Math.max(top, getPaddingTop() + wizard.collapseOffset));
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            // yvel > 0 Fling down || yvel < 0 Fling up
            int top;
            if (yvel > 0 || contentTop > topViewHeight) {
                top = topViewHeight + getPaddingTop();
            } else {
                top = getPaddingTop() + wizard.collapseOffset;
            }
            dragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
            postInvalidate();
        }

        @Override
        public void onViewDragStateChanged(int state) {
            // 1 -> 2 -> 0
            if (state == ViewDragHelper.STATE_IDLE) {
                // Change the panel state while the drag content view is idle.
                if (contentTop > getPaddingTop()) {
                    panelState = PanelState.EXPANDED;
                } else {
                    panelState = PanelState.COLLAPSED;
                }
            } else {
                panelState = PanelState.SLIDING;
            }
            if (wizard.panelListener != null) {
                wizard.panelListener.onPanelStateChanged(panelState);
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
        try {

            boolean intercept = shouldIntercept && dragHelper.shouldInterceptTouchEvent(ev);
            // Log.d("Drag", "intercept " + intercept + " " + dispatchingChildrenDownFaked);
            // java.lang.NullPointerException: Attempt to read from null array
            // at android.support.v4.widget.ViewDragHelper.shouldInterceptTouchEvent(ViewDragHelper.java:1011)


          /*
          if (lastSlidingRatio == 0 && dispatchingChildrenDownFaked){
            return false;
          }
          */

            return intercept;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int action = MotionEventCompat.getActionMasked(event);

        if (!dispatchingChildrenContentView) {
          try {
            // There seems to be a bug on certain devices: "pointerindex out of range" in viewdraghelper
            // https://github.com/umano/AndroidSlidingUpPanel/issues/351
            dragHelper.processTouchEvent(event);
          } catch (Exception e){
            e.printStackTrace();
          }
        }

        if (action == MotionEvent.ACTION_MOVE && lastSlidingRatio == dispatchingChildrenAtRatio) {
            dispatchingChildrenContentView = true;
            if (!dispatchingChildrenDownFaked) {
                dispatchingChildrenStartedAtY = event.getY();
                event.setAction(MotionEvent.ACTION_DOWN);
                dispatchingChildrenDownFaked = true;
            }
            dragContentView.dispatchTouchEvent(event);
        }

        if (dispatchingChildrenContentView && dispatchingChildrenStartedAtY < event.getY()) {
            resetDispatchingContentView();
        }

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            resetDispatchingContentView();
        }

        return true;
    }

    private void resetDispatchingContentView() {
        dispatchingChildrenDownFaked = false;
        dispatchingChildrenContentView = false;
        dispatchingChildrenStartedAtY = Float.MAX_VALUE;
    }

    public void setTouchMode(boolean shouldIntercept) {
        this.shouldIntercept = shouldIntercept;
    }

    private void setupWizard() {
        // fix the content height with collapse offset
        if(wizard.collapseOffset != 0){
            shouldUpdateContentHeight = true;
        }

        // init panel state
        if (wizard.initOpen) {
            panelState = PanelState.EXPANDED;
            if (wizard.panelListener != null) {
                wizard.panelListener.onSliding(1.0f);
            }
        } else {
            panelState = PanelState.COLLAPSED;
            if (wizard.panelListener != null) {
                wizard.panelListener.onSliding(0f);
            }
        }
    }

    public interface PanelListener {
        /**
         * Called while the panel state is changed.
         */
        public void onPanelStateChanged(PanelState panelState);

        /**
         * Called while dragging.
         * ratio >= 0.
         */
        public void onSliding(float ratio);

        /**
         * Called while the ratio over refreshRatio.
         */
        public void onRefresh();
    }

    public static class SimplePanelListener implements PanelListener {

        @Override
        public void onPanelStateChanged(PanelState panelState) {

        }

        @Override
        public void onSliding(float ratio) {

        }

        @Override
        public void onRefresh() {

        }
    }

    // -----------------

    public static SetupWizard from(Context context) {
        return wizard;
    }

    public static final class SetupWizard {
        private PanelListener panelListener;
        private boolean initOpen;
        private float refreshRatio = 1.5f;
        private boolean overDrag = true;
        private int collapseOffset;
        private int topViewId = -1;
        private int dragContentViewId = -1;

        public SetupWizard() {

        }

        /**
         * Setup the drag listener.
         *
         * @return SetupWizard
         */
        public SetupWizard listener(PanelListener panelListener) {
            this.panelListener = panelListener;
            return this;
        }

        /**
         * Open the top view after the drag layout is created.
         * The default value is false.
         *
         * @return SetupWizard
         */
        public SetupWizard open() {
            initOpen = true;
            return this;
        }

        /**
         * Set the refresh position while dragging you want.
         * The default value is 1.5f.
         *
         * @return SetupWizard
         */
        public SetupWizard setRefreshRatio(float ratio) {
            this.refreshRatio = ratio;
            return this;
        }

        /**
         * Set enable drag over.
         * The default value is true.
         *
         * @return SetupWizard
         */
        public SetupWizard setOverDrag(boolean overDrag) {
            this.overDrag = overDrag;
            return this;
        }

        /**
         * Set the collapse offset
         *
         * @return SetupWizard
         */
        public SetupWizard setCollapseOffset(int px) {
            this.collapseOffset = px;
            return this;
        }

      /**
       * Set the content view. Pass the id of the view (R.id.xxxxx).
       * This one will be set as the content view and will be dragged together with the topView
       * @param id The id (R.id.xxxxx) of the content view.
       * @return
       */
        public SetupWizard setDragContentViewId(int id){
          this.dragContentViewId = id;
          return this;
        }

      /**
       * Set the top view. The top view is the header view that will be dragged out.
       * Pass the id of the view (R.id.xxxxx)
       * @param id The id (R.id.xxxxx) of the top view
       * @return
       */
        public SetupWizard setTopViewId(int id){
          this.dragContentViewId = id;
          return this;
        }

        public void setup(DragTopLayout dragTopLayout) {
            dragTopLayout.setupWizard();
        }
    }

  /**
   * Save the instance state
   */
  private static class SavedState  extends BaseSavedState {

    int panelState;

    SavedState(Parcelable superState) {
      super(superState);
    }

  }
}
