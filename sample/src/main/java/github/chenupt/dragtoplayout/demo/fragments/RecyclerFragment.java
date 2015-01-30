package github.chenupt.dragtoplayout.demo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import github.chenupt.dragtoplayout.demo.CustomView;
import github.chenupt.dragtoplayout.demo.R;
import github.chenupt.multiplemodel.ItemEntity;
import github.chenupt.multiplemodel.ItemEntityCreator;
import github.chenupt.multiplemodel.ModelManager;
import github.chenupt.multiplemodel.ModelManagerBuilder;
import github.chenupt.multiplemodel.recycler.ModelRecyclerAdapter;

/**
 * Created by chenupt@gmail.com on 1/30/15.
 * Description :
 */
public class RecyclerFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews(){
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);

        // init recycler view
        ModelRecyclerAdapter adapter = new ModelRecyclerAdapter(getActivity(), getModelManager());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView.getChildCount() > 0) {
                    if (recyclerView.getChildAt(0).getTop() >= 0) {
                        EventBus.getDefault().post(true);
                    } else {
                        EventBus.getDefault().post(false);
                    }
                }else{
                    EventBus.getDefault().post(true);
                }
            }
        });

        // set data source
        adapter.setList(getList());
        adapter.notifyDataSetChanged();
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

}


