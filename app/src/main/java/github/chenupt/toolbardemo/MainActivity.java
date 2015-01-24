package github.chenupt.toolbardemo;

import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.common.collect.Lists;

import java.util.List;

import de.greenrobot.event.EventBus;
import github.chenupt.multiplemodel.viewpager.ModelPagerAdapter;
import github.chenupt.multiplemodel.viewpager.PagerModelManager;


public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private DragTopLayout dragLayout;
    private ModelPagerAdapter adapter;
    private ViewPager viewPager;
    private ImageView imageView;
    private PagerTabStrip pagerTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dragLayout = (DragTopLayout) findViewById(R.id.drag_layout);
        imageView = (ImageView) findViewById(R.id.image_view);
        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);

        toolbar.setTitle("ToolBar");
        setSupportActionBar(toolbar);


        // init DragTopLayout
        DragTopLayout.from(this)
                .open()
                .setRefreshRadio(1.4f)
                .listener(new DragTopLayout.SimplePanelListener() {
                    @Override
                    public void onSliding(float radio) {

                    }
                }).setup(dragLayout);

        // init pager
        PagerModelManager factory = new PagerModelManager();
        factory.addCommonFragment(TestListFragment.class, getTitles(), getTitles());
        adapter = new ModelPagerAdapter(getSupportFragmentManager(), factory);
        viewPager.setAdapter(adapter);
    }

    private List<String> getTitles(){
        return Lists.newArrayList("TAB 1", "TAB 2", "TAB 3", "TAB 4");
    }

    // Handle scroll event from fragments
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_menu_icon) {
            if(imageView.getVisibility() == View.GONE){
                imageView.setVisibility(View.VISIBLE);
            }else{
                imageView.setVisibility(View.GONE);
            }
            return true;
        } else if(id == R.id.action_toggle){
            dragLayout.toggleMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
