package github.chenupt.toolbardemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import github.chenupt.multiplemodel.viewpager.ModelPagerAdapter;
import github.chenupt.multiplemodel.viewpager.PagerModelManager;


public class MainActivity extends FragmentActivity {

//    private Toolbar toolbar;
    private DragTopLayout dragLayout;
    private ModelPagerAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dragLayout = (DragTopLayout) findViewById(R.id.drag_layout);

//        toolbar.setTitle("ToolBar");
//        setSupportActionBar(toolbar);


        // init DragTopLayout
        DragTopLayout.from(this)
                .open()
                .listener(new DragTopLayout.SimplePanelListener() {
                    @Override
                    public void onPanelStateChanged(DragTopLayout.PanelState panelState) {

                    }

                    @Override
                    public void onSliding(float radio) {

                    }
                }).setup(dragLayout);

        PagerModelManager factory = new PagerModelManager();
        factory.addCommonFragment(TestListFragment.class, getTitles());
        adapter = new ModelPagerAdapter(getSupportFragmentManager(), factory);
        viewPager.setAdapter(adapter);
    }

    private List<String> getTitles(){
        List<String> list = new ArrayList<>();
        list.add("dd");
        list.add("dd");
        return list;
    }



    public void onEvent(Boolean b){
        dragLayout.setTouchMode(b);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
