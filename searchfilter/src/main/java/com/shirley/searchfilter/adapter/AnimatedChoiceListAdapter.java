package com.shirley.searchfilter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shirley.searchfilter.R;
import com.shirley.searchfilter.entity.SingleChoiceData;
import com.shirley.searchfilter.widget.AnimatedExpandableListView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by ZLJ on 2018/5/17
 * 单选二级列表适配器
 */
public class AnimatedChoiceListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private AnimatedExpandableListView mListView;
    private List<SingleChoiceData> mData;
    // 选中的项，默认为“全部分类”
    private static String chosen = "全部分类";
    private static int chosenGroupId = -1;
    private static int chosenChildId = -1;
    private Context mContext;
    // 存储上一个被选中的ViewHolder
    private ViewHolder preChosenViewHolder;

    private ExpandableListView.OnGroupClickListener onGroupClickListener;

    public AnimatedChoiceListAdapter(Context context, AnimatedExpandableListView listView, List<SingleChoiceData> data) {
        this.mContext = context;
        mListView = listView;
        mData = data;
    }

    @Override
    public View getRealChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choice_list_child, parent, false);
            childViewHolder = new ViewHolder();
            childViewHolder.tvContent = convertView.findViewById(R.id.tv_content);
            childViewHolder.ivSwitch = convertView.findViewById(R.id.iv_switch);
            childViewHolder.layout = convertView.findViewById(R.id.layout);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ViewHolder) convertView.getTag();
        }
        childViewHolder.tvContent.setText(mData.get(groupPosition).getChildren().get(childPosition));
        childViewHolder.tvContent.setTextColor(mContext.getResources().getColor(R.color.grey));
        childViewHolder.ivSwitch.setVisibility(View.INVISIBLE);
        // 为了使下一次进来时能实现上一次退出的样子（选中的为红色）
        if (chosenGroupId == groupPosition && chosenChildId == childPosition) {
            childViewHolder.ivSwitch.setVisibility(View.VISIBLE);
            childViewHolder.tvContent.setTextColor(mContext.getResources().getColor(R.color.indian_red));
        }
        childViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 先清除上一个ViewHolder的选中痕迹
                preChosenViewHolder.ivSwitch.setVisibility(View.INVISIBLE);
                preChosenViewHolder.tvContent.setTextColor(mContext.getResources().getColor(R.color.grey));
                // 记录当前选中信息，用于在下一次进来时可以知道被选中的是哪一项
                chosenGroupId = groupPosition;
                chosenChildId = childPosition;
                chosen = mData.get(groupPosition).getChildren().get(childPosition);
                // 如果不手动更新下，点击事件有时候会没反应
                notifyDataSetChanged();
                childViewHolder.ivSwitch.setVisibility(View.VISIBLE);
                childViewHolder.tvContent.setTextColor(mContext.getResources().getColor(R.color.indian_red));
                EventBus.getDefault().post(chosen);
                // 重新赋值
                preChosenViewHolder = childViewHolder;
            }
        });
        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        List<String> children = mData.get(groupPosition).getChildren();
        return children == null ? 0 : children.size();
    }

    @Override
    public int getGroupCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mData.get(groupPosition).getChildren().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final ViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choice_list_group, parent, false);
            groupViewHolder = new ViewHolder();
            groupViewHolder.tvContent = convertView.findViewById(R.id.tv_content);
            groupViewHolder.ivSwitch = convertView.findViewById(R.id.iv_switch);
            groupViewHolder.layout = convertView.findViewById(R.id.layout);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (ViewHolder) convertView.getTag();
        }
        groupViewHolder.tvContent.setText(mData.get(groupPosition).getGroup());
        groupViewHolder.ivSwitch.setVisibility(View.VISIBLE);
        if (groupPosition == 0) {
            // 全部分类，没有子元素，不能收缩伸展，只能选中与否
            if (chosen.equals(mData.get(groupPosition).getGroup())) {
                // 选中状态
                groupViewHolder.ivSwitch.setImageResource(R.drawable.ok);
                // 将当前ViewHolder(全部分类)保存起来
                preChosenViewHolder = groupViewHolder;
            } else {
                // 未选中状态
                groupViewHolder.ivSwitch.setVisibility(View.INVISIBLE);
            }
        } else {
            // 除全部分类外的项，有子元素，点击收缩或伸展
            if (isExpanded) {
                groupViewHolder.ivSwitch.setImageResource(R.drawable.expand);
            } else {
                groupViewHolder.ivSwitch.setImageResource(R.drawable.collapse);
            }
        }
        groupViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupPosition == 0) {
                    // 点击全部分类，没有子类，直接传值退出
                    // 先清除上一个ViewHolder的选中痕迹
                    preChosenViewHolder.ivSwitch.setVisibility(View.INVISIBLE);
                    preChosenViewHolder.tvContent.setTextColor(mContext.getResources().getColor(R.color.grey));
                    // 记录当前选中信息，用于在下一次进来时可以知道被选中的是哪一项
                    chosenGroupId = groupPosition;
                    chosenChildId = -1;
                    chosen = mData.get(groupPosition).getGroup();
                    // 如果不手动更新下，点击事件有时候会没反应
                    notifyDataSetChanged();
                    // 设置当前项为选中样式
                    ((ViewHolder) v.getTag()).ivSwitch.setVisibility(View.VISIBLE);
                    ((ViewHolder) v.getTag()).ivSwitch.setImageResource(R.drawable.ok);
                    // 重新赋值
                    preChosenViewHolder = (ViewHolder) v.getTag();
                    EventBus.getDefault().post("全部");
                } else {
                    // 点击除全部分类之外的group，则进行伸展或收缩
                    if(mListView.isGroupExpanded(groupPosition)){
                        mListView.collapseGroupWithAnimation(groupPosition);
                    }else{
                        mListView.expandGroupWithAnimation(groupPosition);
                    }
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolder {
        LinearLayout layout;
        TextView tvContent;
        ImageView ivSwitch;
    }

}
