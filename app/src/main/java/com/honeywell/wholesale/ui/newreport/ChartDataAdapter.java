package com.honeywell.wholesale.ui.newreport;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.honeywell.wholesale.R;

import java.util.ArrayList;


/**
 * Created by H154326 on 16/12/29.
 * Email: yang.liu6@honeywell.com
 */

public class ChartDataAdapter extends RecyclerView.Adapter<ChartDataAdapter.ChatDataViewHolder> {

    private View mHeaderView;
    private View mFooterView;

    public static final int HEADER = 1;
    public static final int NORMAL = 2;
    public static final int FOOTER = 3;

    private LayoutInflater mInflater;
    private ArrayList<ChartDataModel> mChatDataList;
    private ChartAssistDataModel mChartAssistDataModel;
    private MyItemClickListener mItemClickListener;
    private ChatDataViewHolder chatDataViewHolder;
    private int selectedPosition = -1;

    private String mChartKind = "";

    public void setData(ArrayList<ChartDataModel> mChatDataList,
                        ChartAssistDataModel mChartAssistDataModel,
                        String mChartKind){
        this.mChatDataList = mChatDataList;
        this.mChartAssistDataModel = mChartAssistDataModel;
        this.mChartKind = mChartKind;
    }

    public void clearData(){
        this.mChatDataList.clear();
        notifyDataSetChanged();
    }

    public void addHeaderView(View headView) {
        this.mHeaderView = headView;
        notifyItemInserted(0);
    }

    public void addFooterView(View footView) {
        this.mFooterView = footView;
        notifyItemInserted(getItemCount()-1);
    }

    public int getHeadViewCount() {
        return mHeaderView == null ? 0 : 1;
    }

    public int getFootViewCount() {
        return mFooterView == null ? 0 : 1;
    }

    public ChartDataAdapter(Context context,
                            ArrayList<ChartDataModel> mChatDataList,
                            ChartAssistDataModel mChartAssistDataModel,
                            String mChartKind) {
        mInflater = LayoutInflater.from(context);
        this.mChatDataList = mChatDataList;
        this.mChartAssistDataModel = mChartAssistDataModel;
        this.mChartKind = mChartKind;
    }


    public void setRectSelected(int position){
        selectedPosition = position + 1;
    }

    @Override
    public ChatDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mHeaderView != null && viewType == HEADER) {
            return new ChatDataViewHolder(mHeaderView, mItemClickListener);
        }
        if(mFooterView != null && viewType == FOOTER){
            if (viewType == FOOTER) return new ChatDataViewHolder(mFooterView, mItemClickListener);
        }
//        if (viewType == HEADER) return new ChatDataViewHolder(mHeaderView, mItemClickListener);
//        if (viewType == FOOTER) return new ChatDataViewHolder(mFooterView, mItemClickListener);
        View view = mInflater.inflate(R.layout.item_chart_item, parent, false);
        chatDataViewHolder = new ChatDataViewHolder(view, mItemClickListener);
        chatDataViewHolder.chartView = (ChartView) view.findViewById(R.id.chat_view);
        chatDataViewHolder.dateTextview = (TextView) view.findViewById(R.id.date_textview);
        return chatDataViewHolder;
    }

    @Override
    public void onBindViewHolder(ChatDataViewHolder holder, int position) {

        if(getItemViewType(position) == NORMAL){
            if(holder instanceof ChatDataViewHolder) {
                ChartDataModel chartDataModel1 = mChatDataList.get(position-1);
                if (selectedPosition == position){
                    holder.chartView.setRectSelected(true);
                } else {
                    holder.chartView.setRectSelected(false);
                }
                holder.chartView.setChatAssistData(mChartAssistDataModel);
                holder.chartView.setChatData(chartDataModel1);
                holder.chartView.setmChartKind(mChartKind);
                holder.dateTextview.setText(chartDataModel1.getmAxisDate());

                //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了
                //((ChatDataViewHolder) holder).tv.setText(mDatas.get(position-1));
                return;
            }
            return;
        }else if(getItemViewType(position) == HEADER){
            return;
        }else{
            return;
        }
    }

    @Override
    public int getItemCount() {
        if(mHeaderView == null && mFooterView == null){
            return mChatDataList.size();
        }else if(mHeaderView == null && mFooterView != null){
            return mChatDataList.size() + 1;
        }else if (mHeaderView != null && mFooterView == null){
            return mChatDataList.size() + 1;
        }else {
            return mChatDataList.size() + 2;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null && mFooterView == null){
            return NORMAL;
        }
        if (position == 0){
            //第一个item应该加载Header
            return HEADER;
        }
        if (position == getItemCount()-1){
            //最后一个,应该加载Footer
            return FOOTER;
        }
        return NORMAL;
    }

        /**
         * 设置Item点击监听
         * @param listener
         */
    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public class ChatDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ChartView chartView;
        TextView dateTextview;

        private MyItemClickListener mListener;

//        public ChatDataViewHolder(View itemView) {
//            super(itemView);
//            if (itemView == mHeaderView) {
//                return;
//            }
//            if (itemView == mFooterView) {
//                return;
//            }
//        }

        public ChatDataViewHolder(View itemView, MyItemClickListener listener) {
            super(itemView);
            if (itemView == mHeaderView) {
                return;
            }
            if (itemView == mFooterView) {
                return;
            }
            this.mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onItemClick(v,getPosition());
            }
        }
    }

    public interface MyItemClickListener {
        public void onItemClick(View view, int position);
    }
}
