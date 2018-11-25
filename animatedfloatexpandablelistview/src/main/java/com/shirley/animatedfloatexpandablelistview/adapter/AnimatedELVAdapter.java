package com.shirley.animatedfloatexpandablelistview.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shirley.animatedfloatexpandablelistview.AnimatedELV;
import com.shirley.animatedfloatexpandablelistview.R;
import com.shirley.animatedfloatexpandablelistview.animation.ExpandAnimation;
import com.shirley.animatedfloatexpandablelistview.entity.GroupData;
import com.shirley.animatedfloatexpandablelistview.entity.GroupInfo;
import com.shirley.animatedfloatexpandablelistview.view.DummyView;

import java.util.List;

/**
 * 用于AnimatedELV的专用适配器<br>
 * Created by ZLJ on 2018/3/1.
 */
public class AnimatedELVAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    /**
     * 要显示的数据
     */
    private List<GroupData> mData;

    // 闲置状态
    private static final int STATE_IDLE = 0;
    private static final int STATE_EXPANDING = 1;
    private static final int STATE_COLLAPSING = 2;
    // ChildView的类型
    private static final int DUMMY_VIEW = 0;
    private static final int REAL_VIEW = 1;

    private AnimatedELV animatedELV;

    /**
     * 保存每个group的信息的集合
     */
    private SparseArray<GroupInfo> groupInfo = new SparseArray<>();
    /**
     * 存储上一个被选中的ViewHolder
     */
    private ViewHolder mLastChosenViewHolder;
    /**
     * 选中的group的Id
     */
    private static int chosenGroupId = -1;
    /**
     * 选中的child的Id
     */
    private static int chosenChildId = -1;
    /**
     * 选中的项，默认为“全部分类”
     */
    private static String chosen = "全部分类";

    public AnimatedELVAdapter(Context context, AnimatedELV animatedELV, List<GroupData> data) {
        this.mContext = context;
        this.animatedELV = animatedELV;
        this.mData = data;
    }

   @Override
    public int getGroupCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        GroupInfo info = getGroupInfo(groupPosition);
        if (info.animating) {
            // 需要执行动画
            // 如果需要执行动画，则只需要执行一遍getChildView就行了，因此需要返回1，
            // 否则还会加载一遍childView并且一闪而过（还未发现firstChildPosition不为0的情况）
            return info.firstChildPosition + 1;
        } else {
            // 不需要执行动画，则返回实际的child的个数
            return getRealChildrenCount(groupPosition);
        }
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_group, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.tvContent = convertView.findViewById(R.id.tv_content);
            viewHolder.layout = convertView.findViewById(R.id.layout);
            viewHolder.ivSwitch = convertView.findViewById(R.id.iv_switch);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvContent.setText(mData.get(groupPosition).getName());
        if (groupPosition == 0) {
            // 全部分类，没有子元素，不能收缩伸展，只能选中与否
            if (chosen.equals(mData.get(groupPosition).getName())) {
                // 选中状态
                viewHolder.ivSwitch.setImageResource(R.drawable.yes);
                // 将当前ViewHolder(全部分类)保存起来
                mLastChosenViewHolder = viewHolder;
            } else {
                // 未选中状态
                viewHolder.ivSwitch.setVisibility(View.INVISIBLE);
            }
        } else {
            // 除全部分类外的项，有子元素，点击收缩或伸展
            viewHolder.ivSwitch.setVisibility(View.VISIBLE);
            if (isExpanded) {
                viewHolder.ivSwitch.setImageResource(R.drawable.expand);
            } else {
                viewHolder.ivSwitch.setImageResource(R.drawable.collapse);
            }
        }
        // 点击事件
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupPosition == 0) {
                    // 点击全部分类，没有子类，直接传值退出
                    // 先清除上一个ViewHolder的选中痕迹
                    mLastChosenViewHolder.ivSwitch.setVisibility(View.INVISIBLE);
                    mLastChosenViewHolder.tvContent.setTextColor(
                            mContext.getResources().getColor(R.color.grey));
                    // 记录当前选中信息，用于在下一次进来时可以知道被选中的是哪一项
                    chosenGroupId = groupPosition;
                    chosenChildId = -1;
                    chosen = mData.get(groupPosition).getName();
                    // 如果不手动更新下，点击事件有时候会没反应
                    notifyDataSetChanged();
                    // 设置当前项为选中样式
                    ((ViewHolder) v.getTag()).ivSwitch.setVisibility(View.VISIBLE);
                    ((ViewHolder) v.getTag()).ivSwitch.setImageResource(R.drawable.yes);
                    // 重新赋值
                    mLastChosenViewHolder = (ViewHolder) v.getTag();
                } else {
                    // 点击除全部分类之外的group，则进行扩展或折叠操作
                    if(animatedELV.isGroupExpanded(groupPosition)){
                        animatedELV.collapseGroupWithAnimation(groupPosition);
                    }else{
                        animatedELV.expandGroupWithAnimation(groupPosition);
                    }
                }
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // 先获取当前group的信息
        final GroupInfo info = getGroupInfo(groupPosition);
        // 判断是否有动画效果
        if (info.animating){
            // 如果此group正在动画，请返回DummyView并执行动画，执行完后调用notifyDataSetChanged
            // 强制刷新，此时animating为false,则会根据child的个数返回实际的child的视图，也就是说
            // 当前这段if内的语句只会执行一次
            if (!(convertView instanceof DummyView)){
                convertView = new DummyView(parent.getContext());
                convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 0));
            }

            // 不懂为什么要这一段？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
            if (childPosition < info.firstChildPosition){
                // 我们之所以这样做是为了支持在group视图不可见但它的子元素可见的情况下支持这个group
                // 的折叠。当调用notifyDataSetChanged时，ExpandableListView尝试通过保存第一个可见项
                // 并在刷新视图后跳回到该项来保持列表位置相同。现在的问题是，如果一个group有2个child，
                // 并且第一个可见child是该group的第二个child，并且该group已被折叠，则虚拟视图将用于
                // 该group。但是现在该group只有1个child是虚拟视图，因此当ListView试图恢复滚动位置时，
                // 它将尝试跳转到group的第二个child。但这个group不再有第二个child，所以它被迫跳到下
                // 一个group。这将导致一个非常丑陋的视觉故障。所以我们抵消这个的方式是创建尽可能多
                // 的虚拟视图，因为我们需要在notifyDataSetChanged被调用后维护ListView的滚动位置。
                convertView.getLayoutParams().height = 0;
                return convertView;
            }

            final ExpandableListView listView = (ExpandableListView) parent;
            final DummyView dummyView = (DummyView) convertView;
            // 清除所有DummyView绘制的view
            dummyView.clearViews();
            // 确定测量child的测量规格
            final int measureSpecW = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
            final int measureSpecH = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            // 用于记录所有child的height的总和
            int totalHeight = 0;
            // ExpandableListView的高度（XML文件中设置为match_parent，因此就是ExpandableList在屏幕上的可见高度
            int clipHeight = parent.getHeight();
            // child的个数
            final int len = getRealChildrenCount(groupPosition);
            // 循环添加子ChildView
            for (int i = info.firstChildPosition; i < len; i++){
                // 先构造一个childView
                View childView = getRealChildView(groupPosition, i, null, parent);
                AbsListView.LayoutParams params = (AbsListView.LayoutParams) childView.getLayoutParams();
                if (params == null){
                    params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                    childView.setLayoutParams(params);
                }
                int lpHeight = params.height;
                int childHeightSpec;
                if (lpHeight > 0){
                    childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
                } else {
                    // 调试发现lpHeight==-2，可能没有measure过才会出现负值
                    childHeightSpec = measureSpecH;
                }
                childView.measure(measureSpecW, childHeightSpec);
                // 叠加childView的height
                totalHeight += childView.getMeasuredHeight();

                // 将childView添加到dummyView中
                if (totalHeight < clipHeight){
                    // 当前要添加的childView还未超出屏幕可见范围
                    // 我们只需要绘制足够的视图来欺骗用户...
                    dummyView.addFakeView(childView);
                } else {
                    // 当前要添加的childView超出了屏幕可见范围，之后的childView没有必要再添加了
                    // 如果这个group的视图太多，我们不想计算一切的高度......只是做一个轻微的近似并中断此循环
                    dummyView.addFakeView(childView);
                    int averageHeight = totalHeight / (i + 1);
                    totalHeight += (len - i - 1) * averageHeight;
                    break;
                }
            }

            Object object;
            // 初始状态总为STATE_IDLE
            int state = (object = dummyView.getTag()) == null ? STATE_IDLE : (Integer) object;
            if (info.expanding && state != STATE_EXPANDING){
                // 要执行的动作是扩展且当前状态不是已扩展（当前状态是折叠，要执行的动作是扩展）
                // 开始扩展动画
                ExpandAnimation animation = new ExpandAnimation(dummyView, 0, totalHeight, info);
                // 设置持续时间
                animation.setDuration(animatedELV.getAnimationDuration());
                // 设置动画监听器
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // 设置停止动画的属性
                        stopAnimation(groupPosition);
                        // 通知更新
                        notifyDataSetChanged();
                        // 设置当前的dummyView的状态为闲置状态
                        dummyView.setTag(STATE_IDLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                });
                // 开始执行动画
                dummyView.startAnimation(animation);
                dummyView.setTag(STATE_EXPANDING);
            } else if (!info.expanding && state != STATE_COLLAPSING){
                // 要执行的动作不是扩展且当前状态不是折叠（当前状态是扩展，要执行的动作是折叠）
                if (info.dummyHeight == -1){
                    info.dummyHeight = totalHeight;
                }
                // 进行收缩动画
                ExpandAnimation animation = new ExpandAnimation(dummyView, info.dummyHeight, 0, info);
                // 设置持续时间
                animation.setDuration(animatedELV.getAnimationDuration());
                // 设置动画监听器
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // 设置停止动画的属性
                        stopAnimation(groupPosition);
                        // 收缩group
                        listView.collapseGroup(groupPosition);
                        notifyDataSetChanged();
                        info.dummyHeight = -1;
                        // 动画结束，设置dummyView为闲置状态
                        dummyView.setTag(STATE_IDLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                });
                dummyView.startAnimation(animation);
                // 设置dummyView为收缩状态
                dummyView.setTag(STATE_COLLAPSING);
            }
            return convertView;
        } else {
            // 没有动画效果，则返回普通的ChildView
            return getRealChildView(groupPosition, childPosition, convertView, parent);
        }
    }

    /**
     * 返回普通的真实的ChildView
     * @param groupPosition
     * @param childPosition
     * @param convertView
     * @param parent
     * @return
     */
    private View getRealChildView(final int groupPosition, final int childPosition, View convertView, ViewGroup parent){
        final ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_child, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvContent = convertView.findViewById(R.id.tv_content);
            viewHolder.layout = convertView.findViewById(R.id.layout);
            viewHolder.ivSwitch = convertView.findViewById(R.id.iv_switch);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvContent.setText(mData.get(groupPosition).getChildren().get(childPosition).getName());
        viewHolder.tvContent.setTextColor(mContext.getResources().getColor(R.color.grey));
        viewHolder.ivSwitch.setVisibility(View.INVISIBLE);
        // 为了使下一次进来时能实现上一次退出的样子（选中的为红色）
        if (chosenGroupId == groupPosition && chosenChildId == childPosition) {
            viewHolder.ivSwitch.setVisibility(View.VISIBLE);
            viewHolder.tvContent.setTextColor(mContext.getResources().getColor(R.color.indian_red));
        }
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 先清除上一个ViewHolder的选中痕迹
                if (mLastChosenViewHolder != null){
                    mLastChosenViewHolder.ivSwitch.setVisibility(View.INVISIBLE);
                    mLastChosenViewHolder.tvContent.setTextColor(mContext.getResources().getColor(R.color.grey));
                }
                // 记录当前选中信息，用于在下一次进来时可以知道被选中的是哪一项
                chosenGroupId = groupPosition;
                chosenChildId = childPosition;
                chosen = mData.get(groupPosition).getChildren().get(childPosition).getName();
                // 如果不手动更新下，点击事件有时候会没反应
                notifyDataSetChanged();
                viewHolder.ivSwitch.setVisibility(View.VISIBLE);
                viewHolder.tvContent.setTextColor(mContext.getResources().getColor(R.color.indian_red));
                // 重新赋值
                mLastChosenViewHolder = viewHolder;
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        // 因为有动画和没有动画，要加载是两种不同的view，因此需要依据情况返回不同的类型
        // 先获取指定位置的group的信息对象
        GroupInfo info = getGroupInfo(groupPosition);
        if (info.animating){
            // 如果我们为这个group制作动画，那么它所有的孩子都将成为虚拟视图，我们将会说它是类型DUMMY_VIEW
            return DUMMY_VIEW;
        } else {
            // 如果我们没有为这个group创建动画，那么我们将它的类型REAL_VIEW
            return REAL_VIEW;
        }
    }

    // 在执行getGroupView()之前就会执行该方法
    @Override
    public int getChildTypeCount() {
        return getRealChildTypeCount() + 1;
    }

    public int getRealChildTypeCount(){
        return 1;
    }

    /**
     * 返回实际的child的个数
     * @param groupPosition
     * @return
     */
    public int getRealChildrenCount(int groupPosition){
        List<GroupData> children = mData.get(groupPosition).getChildren();
        return children == null ? 0 : children.size();
    }

    class ViewHolder{
        TextView tvContent;
        LinearLayout layout;
        ImageView ivSwitch;
    }

    public void notifyGroupExpanded(int groupPosition){
        // 先获取指定位置的group的信息对象
        GroupInfo info = getGroupInfo(groupPosition);
        // ??????????????????
        info.dummyHeight = -1;
    }

    /**
     * 从集合中获取指定位置的group的信息，如果为null，则新建一个对象存进集合并返回该对象
     * @param groupPosition group的位置索引值
     * @return 返回指定位置的group的信息对象
     */
    private GroupInfo getGroupInfo(int groupPosition){
        GroupInfo info = groupInfo.get(groupPosition);
        if (info == null){
            info = new GroupInfo();
            groupInfo.put(groupPosition, info);
        }
        return info;
    }

    /**
     * 表示要开始伸展动画了
     * @param groupPosition group的位置
     * @param firstChildPosition 第一个子元素的位置
     */
    public void startExpandAnimation(int groupPosition, int firstChildPosition){
        // 先获取当前group的信息
        GroupInfo groupInfo = getGroupInfo(groupPosition);
        // 设置属性
        groupInfo.animating = true;
        groupInfo.firstChildPosition = firstChildPosition;
        groupInfo.expanding = true;
    }

    /**
     * 表示要开始收缩动画了
     * @param groupPosition group的位置
     * @param firstChildPosition 第一个子元素的位置
     */
    public void startCollapseAnimation(int groupPosition, int firstChildPosition){
        GroupInfo groupInfo = getGroupInfo(groupPosition);
        groupInfo.animating = true;
        groupInfo.firstChildPosition = firstChildPosition;
        groupInfo.expanding = false;
    }

    /**
     * 设置停止动画的属性（设置当前group的animating为false3）
     * @param groupPosition group的位置
     */
    private void stopAnimation(int groupPosition){
        // 先获取当前group的信息
        GroupInfo info = getGroupInfo(groupPosition);
        // 设置属性
        info.animating = false;
    }

}
