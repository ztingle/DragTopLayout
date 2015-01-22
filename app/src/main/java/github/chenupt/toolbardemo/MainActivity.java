package github.chenupt.toolbardemo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import github.chenupt.multiplemodel.ItemEntity;
import github.chenupt.multiplemodel.ItemEntityCreator;
import github.chenupt.multiplemodel.ModelListAdapter;
import github.chenupt.multiplemodel.ModelManager;
import github.chenupt.multiplemodel.ModelManagerBuilder;


public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private ListView listView;
    private ModelListAdapter adapter;
    private DragTopLayout dragLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        listView = (ListView) findViewById(R.id.list_view);
        dragLayout = (DragTopLayout) findViewById(R.id.drag_layout);

        toolbar.setTitle("ToolBar");
        setSupportActionBar(toolbar);

        dragLayout.openMenu();
        dragLayout.setPanelSlideListener(new DragTopLayout.PanelSlideListener() {
            @Override
            public void onPanelCollapsed() {

            }

            @Override
            public void onSliding(float radio) {

            }
        });

        adapter = new ModelListAdapter(this, getModelManager());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dragLayout.openMenu(true);
            }
        });

        adapter.setList(getList());
        adapter.notifyDataSetChanged();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getChildCount() > 0) {
                    DebugLog.d("onScroll:" + view.getChildAt(0).getTop());
                    if (view.getChildAt(0).getTop() >= 0) {
                        dragLayout.setTouchMode(true);
                    } else {
                        dragLayout.setTouchMode(false);
                    }
                }
            }
        });
    }

    public ModelManager getModelManager() {
        return ModelManagerBuilder.begin().addModel(CustomView.class).build(ModelManager.class);
    }

    public List<ItemEntity> getList() {
        List<ItemEntity> resultList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ItemEntityCreator.create("").setModelView(CustomView.class).attach(resultList);
        }
        return resultList;
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
