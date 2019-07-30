package com.example.popwindowsdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Context mContext;
    private List<String> list;
    private HashMap<Integer, Boolean> maps = new HashMap<Integer, Boolean>();//多选
    public MyAdapter(Context mContext, List<String> list) {
        this.mContext = mContext;
        this.list = list;
        initMap();
    }

    private void initMap() {
        for (int i = 0; i <list.size() ; i++) {
            maps.put(i,false);   //每一次进入列表页面  都是未选中状态
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_provice_chose, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.province_name.setText(list.get(i).toString());
        viewHolder.cbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                maps.put(i, isChecked);
            }
        });

        if (maps.get(i) == null) {
            maps.put(i, false);
        }
        //没有设置tag之前会有item重复选框出现，设置tag之后，此问题解决
        viewHolder.cbox.setChecked(maps.get(i));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    //获取最终的map存储数据
    public Map<Integer, Boolean> getMap() {
        return maps;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerViewOnItemClickListener mListener;//接口
        private CheckBox cbox;
        private TextView province_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbox = itemView.findViewById(R.id.cbox);
            province_name = itemView.findViewById(R.id.province_name);
        }
    }

    public RecyclerViewOnItemClickListener onItemClickListener;

    //回调的接口
    public void setItemClickListener(RecyclerViewOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //接口回调设置点击事件
    public interface RecyclerViewOnItemClickListener {
        //点击事件
        void onItemClickListener(View view, int position);
    }
}
