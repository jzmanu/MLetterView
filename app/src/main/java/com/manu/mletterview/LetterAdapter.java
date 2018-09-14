package com.manu.mletterview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Powered by jzman.
 * Created on 2018/8/21 0021.
 */
public class LetterAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<DataBean> mDataList;

    LetterAdapter(Context mContext) {
        this.mContext = mContext;
        this.mDataList = getData();
    }

    ArrayList<DataBean> getDataList() {
        return mDataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_letter_layout, null);
            vh = new ViewHolder();
            vh.tvLetter = convertView.findViewById(R.id.tvLetter);
            vh.tvData = convertView.findViewById(R.id.tvData);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        DataBean dataBean = mDataList.get(position);

        //特殊处理
        vh.tvLetter.setText("~".equals(dataBean.getNameHeader()) ? "#" : dataBean.getNameHeader());
        vh.tvData.setText(dataBean.getNameChinese());

        if (position == 0) {
            vh.tvLetter.setVisibility(View.VISIBLE);
        } else {
            String currentNameHeader = mDataList.get(position).getNameHeader();
            String nextNameHeader = mDataList.get(position - 1).getNameHeader();
            if (currentNameHeader.equals(nextNameHeader)) {
                vh.tvLetter.setVisibility(View.GONE);
            } else {
                vh.tvLetter.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    static class ViewHolder {
        TextView tvLetter;
        TextView tvData;
    }

    private String[] names = {
            "宋江", "卢俊义 ", "吴用 ", "公孙胜 ",
            "关胜 ", "林冲 ", "秦明 ", "呼延灼 ",
            "花荣 ", "柴进", "李应 ", "朱仝 ",
            "鲁智深 ", "武松 ", "董平", "张清 ",
            "杨志 ", "徐宁 ", "索超 ", "戴宗 ",
            "刘唐 ", "李逵 ", "史进 ", "穆弘 ",
            "雷横 ", "李俊 ", "阮小二 ", "张横 ",
            "阮小五 ", "张顺 ", "阮小七 ", "杨雄 ",
            "石秀 ", "解珍 ", "解宝 ", "燕青", "0", ".", "2", "["};

    private ArrayList<DataBean> getData() {
        ArrayList<DataBean> dataBeans = new ArrayList<>();
        for (String name : names) {
            DataBean dataBean = new DataBean();
            dataBean.setNameChinese(name);
            dataBeans.add(dataBean);
        }

        Collections.sort(dataBeans, new Comparator<DataBean>() {
            @Override
            public int compare(DataBean o1, DataBean o2) {
                return o1.getNameHeader().compareTo(o2.getNameHeader());
            }
        });
        return dataBeans;
    }
}
