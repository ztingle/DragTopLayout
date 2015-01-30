package github.chenupt.dragtoplayout.demo.utils;

import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;

/**
 * Created by chenupt@gmail.com on 1/30/15.
 * Description :
 */
public class AttachUtil {

    public static boolean isListViewAttach(AbsListView listView){
        if (listView != null && listView.getChildCount() > 0) {
            if (listView.getChildAt(0).getTop() < 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isRecyclerView(RecyclerView recyclerView){
        if (recyclerView != null && recyclerView.getChildCount() > 0) {
            if (recyclerView.getChildAt(0).getTop() < 0) {
                return false;
            }
        }
        return true;
    }
}
