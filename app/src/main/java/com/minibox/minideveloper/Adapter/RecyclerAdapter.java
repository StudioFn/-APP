package com.minibox.minideveloper.Adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.minibox.minideveloper.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private Context context;
    //adapter传递过来的数据集合
    private List<String> list = new ArrayList<>();
    //需要改变颜色的text
    private String text;

    private Animator animator;

    /**
     * Recyclerview的点击监听接口
     */
    public interface onItemClickListener {
        void onLongClick(View view, String pos, int size);
        void onItemClick(View view, String pos, int size);
    }

    private onItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(RecyclerAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // 在MainActivity中设置text

    public void setText(String text) {
        this.text = text;
    }

    public RecyclerAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        /**如果没有进行搜索操作或者搜索之后点击了删除按钮 会在Sesrch_ID中把text置空并传递过来*/
        if (text != null) {
            //设置span
            SpannableString string = matcherSearchText(Color.rgb(255, 0, 0), list.get(position), text);
            holder.mTvText.setText(string);
        } else {
            holder.mTvText.setText(list.get(position));
        }
        //属性动画
        animator = AnimatorInflater.loadAnimator(context, R.animator.anim_set);
        animator.setTarget(holder.mLlItem);
        animator.start();
        //长按监听
        holder.mLlItem.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View p1) {
                try {
                    String pos = list.get(position);
                    onItemClickListener.onLongClick(holder.itemView, pos, position);
                }catch (Exception e){
                    Log.e("在DynamicAdapter","错误"+e);}

                return false;
            }
        });
        holder.mLlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLlItem;
        private TextView mTvText;

        public MyViewHolder(View v) {
            super(v);
            mLlItem = v.findViewById(R.id.ll_item);
            mTvText = v.findViewById(R.id.tv_text);
        }
    }


    //正则匹配
    private SpannableString matcherSearchText(int color, String text, String keyword) {
        SpannableString spannableString = new SpannableString(text);
        //条件 keyword
        Pattern pattern = Pattern.compile(keyword);
        //匹配
        Matcher matcher = pattern.matcher(spannableString);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            //ForegroundColorSpan 需要new 不然也只能是部分变色
            spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //返回变色处理的结果
        return spannableString;
    }


}
