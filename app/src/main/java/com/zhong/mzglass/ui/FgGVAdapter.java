package com.zhong.mzglass.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhong.mzglass.R;
import com.zhong.mzglass.utils.IconObject;

import java.util.ArrayList;

public class FgGVAdapter extends BaseAdapter {

    private ArrayList<IconObject> mList;
    private Context mContext;

    FgGVAdapter(ArrayList<IconObject> List, Context context) {
        mList = List;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        viewHolder vHolder;
        if (view == null) {
            vHolder = new viewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.icon_structure,null);

            vHolder.imgv = (ImageView) view.findViewById(R.id.icon_img);
            vHolder.txtv = (TextView) view.findViewById(R.id.icon_txt);

            view.setTag(vHolder);

        } else {
            vHolder = (viewHolder) view.getTag();
        }

//        vHolder.imgv.setImageResource();
        vHolder.txtv.setText(mList.get(i).mtxt);

        return view;
    }

    public static class viewHolder {
        ImageView imgv;
        TextView txtv;
    }
}
