package com.zhong.mzglass.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhong.mzglass.R;
import com.zhong.mzglass.base.BaseFragment;
import com.zhong.mzglass.utils.IconObject;

import java.util.ArrayList;

public class FragmentServices extends BaseFragment {

    private View sevicesView;
    private GridView gv;
    private FgGVAdapter fgGVAdapter;
    private ArrayList<IconObject> iconList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        sevicesView = inflater.inflate(R.layout.fragment_services,null);
        initBind();
        initView();
        return sevicesView;
    }

    private void initBind() {
        gv = (GridView) sevicesView.findViewById(R.id.gv_services);

    }

    private void initView() {
        iconList = new ArrayList<IconObject>();
        iconList.add(new IconObject("no","weather"));
        iconList.add(new IconObject("no","navigation"));
        iconList.add(new IconObject("no","unknown"));
        iconList.add(new IconObject("no","unknown"));

        fgGVAdapter = new FgGVAdapter(iconList,getActivity());
        gv.setAdapter(fgGVAdapter);

    }
}
