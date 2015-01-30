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

package github.chenupt.dragtoplayout.demo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import github.chenupt.dragtoplayout.demo.CustomView;
import github.chenupt.dragtoplayout.demo.R;
import github.chenupt.multiplemodel.ItemEntity;
import github.chenupt.multiplemodel.ItemEntityCreator;
import github.chenupt.multiplemodel.ModelListAdapter;
import github.chenupt.multiplemodel.ModelManager;
import github.chenupt.multiplemodel.ModelManagerBuilder;

/**
 * Created by chenupt@gmail.com on 1/23/15.
 * Description :
 */
public class ListViewFragment extends Fragment {

    private ListView listView;
    private ModelListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView(){
        listView = (ListView) getView().findViewById(R.id.list_view);

        adapter = new ModelListAdapter(getActivity(), getModelManager());
        listView.setAdapter(adapter);

        adapter.setList(getList());
        adapter.notifyDataSetChanged();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (listView.getChildCount() > 0) {
                    if (listView.getChildAt(0).getTop() >= 0) {
                        EventBus.getDefault().post(true);
                    } else {
                        EventBus.getDefault().post(false);
                    }
                }else{
                    EventBus.getDefault().post(true);
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
}
