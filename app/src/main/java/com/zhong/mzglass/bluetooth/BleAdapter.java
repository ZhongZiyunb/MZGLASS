package com.zhong.mzglass.bluetooth;

import android.content.Context;
import android.os.ParcelUuid;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zhong.mzglass.R;
import com.zhong.mzglass.ui.FgGVAdapter;
import com.zhong.mzglass.utils.BleDeviceInfo;
import com.zhong.mzglass.utils.BleObject;

import java.util.ArrayList;

public class BleAdapter extends BaseAdapter {

    private ArrayList<String> uuids;
    private Context mContext;

    BleAdapter(BleDeviceInfo d, Context c) {
        uuids = d.uuids;
        mContext = c;
    }

    @Override
    public int getCount() {
        return uuids.size();
    }

    @Override
    public Object getItem(int i) {
        return uuids.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        BleAdapter.viewHolder vHolder;
        if (view == null) {
            vHolder = new BleAdapter.viewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.uuid_structure,null);

            vHolder.txtv = (TextView) view.findViewById(R.id.uuid_txt);

            view.setTag(vHolder);

        } else {
            vHolder = (BleAdapter.viewHolder) view.getTag();
        }

        vHolder.txtv.setText(uuids.get(i).toString());

        return view;
    }

    public static class viewHolder {
        TextView txtv;
    }

}
