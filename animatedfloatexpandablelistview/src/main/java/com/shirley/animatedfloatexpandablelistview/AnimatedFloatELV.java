package com.shirley.animatedfloatexpandablelistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shirley.animatedfloatexpandablelistview.entity.GroupData;

import java.util.List;

/**
 * 带伸展动画以及group项悬停效果的ExpandableListView
 * <br>
 * Created by ZLJ on 2018/5/31
 */
public class AnimatedFloatELV extends RelativeLayout {

    private Context mContext;
    /**
     * 悬停View
     */
    private View floatView;
    /**
     * 悬停View上的TextView
     */
    private TextView tvFloat;
    /**
     * floatView的高度
     */
    private int floatViewHeight = 0;
    /**
     * floatView所显示内容对应ExpandableListView中group的position
     */
    private int floatViewGroupPosition = AbsListView.INVALID_POSITION;
    private int tempY = 0;
    /**
     * 自定义带动画的二级列表控件
     */
    private AnimatedELV mAnimatedELV;
    private RelativeLayout rlContainer;
    private List<GroupData> mData;

    public AnimatedFloatELV(Context context) {
        this(context, null);
    }

    public AnimatedFloatELV(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedFloatELV(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_animated_float_elv, this);
        rlContainer = view.findViewById(R.id.rl_container);
        mAnimatedELV = view.findViewById(R.id.animated_elv);
        initFloatView();
        initListener();
    }

    /**
     * 对外方法，设置数据
     *
     * @param data
     */
    public void setData(List<GroupData> data) {
        mData = data;
        mAnimatedELV.setData(mData);
    }

    /**
     * 初始化浮动层视图
     */
    private void initFloatView() {
        floatView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_group, null);
        // 设置浮动层的图标是展开的，不设置的话，图标是折叠的，因为布局文件中的默认图标是折叠的
        ImageView ivSwitch = floatView.findViewById(R.id.iv_switch);
        ivSwitch.setImageResource(R.drawable.expand);
        tvFloat = floatView.findViewById(R.id.tv_content);
        rlContainer.addView(floatView);
        setFloatViewClickListener();
    }

    /**
     * 设置FloatView的点击事件，是FloatView兼具GroupItem的点击效果
     */
    private void setFloatViewClickListener() {
        if (null == floatView) {
            return;
        }
        floatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAnimatedELV.isGroupExpanded(floatViewGroupPosition)) {
                    mAnimatedELV.collapseGroupWithAnimation(floatViewGroupPosition);
                } else {
                    mAnimatedELV.expandGroupWithAnimation(floatViewGroupPosition);
                }
            }
        });
    }

    /**
     * 为二级列表添加滚动监听事件，以控制悬停View的改变
     */
    public void initListener() {
        mAnimatedELV.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (null == floatView) {
                    return;
                }
                int firstPos = view.pointToPosition(0, 0);// 其实就是firstVisibleItem
                if (firstPos == AdapterView.INVALID_POSITION) // 如果第一个位置值无效
                    return;

                long firstPackedPos = mAnimatedELV.getExpandableListPosition(firstPos);
                int firstChildPos = ExpandableListView.getPackedPositionChild(firstPackedPos);// 获取第一行child的id
                int firstGroupPos = ExpandableListView.getPackedPositionGroup(firstPackedPos);// 获取第一行group的id
                if ((firstGroupPos == AdapterView.INVALID_POSITION || !mAnimatedELV.isGroupExpanded(firstGroupPos))
                        && firstChildPos == AdapterView.INVALID_POSITION) {
                    /*
                     * 显示出来的第一个item既不是group也不是child，这时应该是header，隐藏indicatorGroup
                     */
                    floatView.setVisibility(View.GONE);
                    return;
                } else if (firstChildPos == AdapterView.INVALID_POSITION) {
                    /* 显示出来的第一个item是group, 取其高度，用来初始化indicatorGroupHeight */
                    floatViewHeight = mAnimatedELV.getChildAt(0).getHeight();
                    floatView.setVisibility(View.VISIBLE);
                } else {
                    floatView.setVisibility(View.VISIBLE);
                }

                if (floatViewHeight == 0) {
                    return;
                }

                if (firstGroupPos != AdapterView.INVALID_POSITION) {
                    if (firstGroupPos != floatViewGroupPosition) {// 如果指示器显示的不是当前group
                        updateFloatViewText(firstGroupPos);
                        floatViewGroupPosition = firstGroupPos;
                    }
                }

                /**
                 * calculate point (0,indicatorGroupHeight) 下面是形成往上推出的效果
                 */
                int topOffset = floatViewHeight;
                // 第二个item的位置
                int secondPos = mAnimatedELV.pointToPosition(0, floatViewHeight + mAnimatedELV.getDividerHeight());
                if (secondPos == AdapterView.INVALID_POSITION) // 如果无效直接返回
                    return;
                long secondPackedPos = mAnimatedELV.getExpandableListPosition(secondPos);
                // 获取第二个group的id
                int secondGroupPos = ExpandableListView.getPackedPositionGroup(secondPackedPos);
                // 如果不等于指示器当前的group
                if (secondGroupPos != AdapterView.INVALID_POSITION && secondGroupPos != floatViewGroupPosition) {
                    topOffset = mAnimatedELV.getChildAt(secondPos - mAnimatedELV.getFirstVisiblePosition()).getTop();
                }
                tempY = floatViewHeight - topOffset;
                if (tempY < 0) {
                    tempY = 0;
                }
                MarginLayoutParams layoutParams = (MarginLayoutParams) floatView.getLayoutParams();
                layoutParams.topMargin = -tempY;
                floatView.setLayoutParams(layoutParams);
            }
        });
    }

    /**
     * 更新悬停View的文字
     * @param position
     */
    private void updateFloatViewText(int position) {
        GroupData model = mData.get(position);
        tvFloat.setText(model.getName());
    }

}
