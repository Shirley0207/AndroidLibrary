package com.shirley.animatedfloatexpandablelistview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;

import com.shirley.animatedfloatexpandablelistview.adapter.AnimatedELVAdapter;
import com.shirley.animatedfloatexpandablelistview.entity.GroupData;

import java.util.List;

/**
 * Created by ZLJ on 2018/6/8
 * 带伸展动画的ExpandableListView
 * 别人的类
 */
public class AnimatedELV extends ExpandableListView {

    /*
     * 关于这个类如何工作的详细解释：
     *
     * 设置ExpandableListView动画并非易事。这个类做的方式是利用ExpandableListView的工作方式
     *
     * 通常当调用{@link ExpandableListView＃collapseGroup（int）}或
     * {@link ExpandableListView＃expandGroup（int）}时，该视图切换group的标志并调用
     * notifyDataSetChanged以使ListView刷新其全部视图。 但是，这一次，依据某个group是展开还是折叠，
     * 某些childViews将被忽略或添加到列表中。
     *
     * 知道这一点，我们可以想出一种方法来为我们的view添加动画。例如，对于group扩展，我们告诉适配器
     * 为某个group的child设置动画。然后我们展开导致ExpandableListView刷新屏幕上所有视图的group。
     * ExpandableListView这样做的方式是通过调用适配器中的getView()。但是，由于适配器知道我们正在
     * 为某个group制作动画，所以它会返回一个虚拟视图，而不是返回为正在动画的group的children真实视图。
     * 这个虚拟视图会在其dispatchDraw函数中绘制真实的子视图。我们这样做的原因是，我们可以通过
     * 动画制作虚拟视图来为其所有children制作动画。完成动画之后，我们告诉适配器停止对group进行动画
     * 并调用notifyDataSetChanged。现在ExpandableListView被迫再次刷新它的视图，除了这次，它将获得扩展组的真实视图。
     *
     * 因此，要列出这一切，当{@link #expandGroupWithAnimation（int）}被调用时，会发生以下情况：
     *
     * 1. ExpandableListView通知适配器为某个group设置动画。
     * 2. ExpandableListView调用expandGroup。
     * 3. ExpandGroup调用notifyDataSetChanged。
     * 4. 结果，为展开组调用getChildView。
     * 5. 由于适配器处于“动画模式”，它将返回一个虚拟视图。
     * 6. 这个虚拟视图绘制了扩展组的children的实际视图。
     * 7. 该虚拟视图的高度从0扩展到其展开高度。
     * 8. 一旦动画完成，通知适配器停止对group进行动画处理，并再次调用notifyDataSetChanged。
     * 9. 这迫使ExpandableListView再次刷新其所有视图。
     * 10.这次调用getChildView时，它将返回实际的子视图。
     *
     * 对于group的折叠动画有点困难，因为我们无法从一开始就调用collapseGroup，它只会忽略子项目，因此
     * 不会去做任何动画。相反，我们必须首先播放动画，并在动画完成后调用collapseGroup。
     *
     * 所以，要全部列出，{@link #collapseGroupWithAnimation（int）}会发生以下情况：
     *
     * 1. ExpandableListView通知适配器为某个group设置动画。
     * 2. ExpandableListView调用notifyDataSetChanged。
     * 3. 结果，为展开的group调用getChildView。
     * 4. 由于适配器处于“动画模式”，它将返回一个虚拟视图。
     * 5. 这个虚拟视图绘制了扩展group的children的实际视图。
     * 6. 这个虚拟视图的高度从当前高度变化到0。
     * 7. 一旦动画完成，通知适配器停止对组进行动画处理，并再次调用notifyDataSetChanged。
     * 8. collapseGroup最终被调用。
     * 9. 这迫使ExpandableListView再次刷新其所有视图。
     * 10.这次ListView不会得到折叠的group的任何child视图。
     */

    private static final int ANIMATION_DURATION = 300;
    private AnimatedELVAdapter mAdapter;
    private Context mContext;

    public AnimatedELV(Context context) {
        this(context, null);
    }

    public AnimatedELV(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedELV(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    /**
     * 对外设置数据的方法
     * @param data 要显示的列表数据
     */
    public void setData(List<GroupData> data){
        // 在第一个位置加上全部分类
        GroupData allKinds = new GroupData();
        allKinds.setName("全部分类");
        allKinds.setChildren(null);
        data.add(0, allKinds);
        mAdapter = new AnimatedELVAdapter(mContext, this, data);
        setAdapter(mAdapter);
    }

    /**
     * 用动画扩展指定的group
     * @param groupPos 要扩展的group的位置
     * @return 如果group扩展了（从折叠到扩展），则返回true。 如果该group已经扩展，则为false。
     */
    public boolean expandGroupWithAnimation(int groupPos){
        // 以下这段的作用：当点击最后一行时（此时item是填满屏幕的），使最后一个item的子元素能显示
        // 如果去掉这段，这最后一个item的子元素不会自动显示，需要滑动才能显示
        boolean lastGroup = groupPos == mAdapter.getGroupCount() - 1;
        // 最后一组且Android版本大于等于4.0
        if (lastGroup && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            return expandGroup(groupPos, true);
        }
        // 感觉以下这段并没有什么用
        // groupFlatPos指的是当前group在整个list中的实际位置
        int groupFlatPos = getFlatListPosition(getPackedPositionForGroup(groupPos));
        if (groupFlatPos != -1){
            // getFirstVisiblePosition()获取的是屏幕上第一个可见的group的位置
            // 实际位置与第一个可见group的位置的差值可以表示当前group在可见列表中的索引位置
            int childIndex = groupFlatPos - getFirstVisiblePosition();
            // getChildCount()获取的是屏幕上可见的group的数量
            if (childIndex < getChildCount()){
                // 获得groupPos指向的group
                View v = getChildAt(childIndex);
                // v.getBottom()表示当前view的bottom到第一个可见view的top的距离
                // getBottom()大概表示整个ListView的高度（最后一个可见view的bottom到第一个可见view的top的距离）
                if (v.getBottom() >= getBottom()){
                    // 此段判断表示group已超出可见区域范围，还未调试到此种情况
                    // 如果用户无法看到动画，我们只需在没有动画的情况下展开该组。
                    // 这解决了如果group的children不在屏幕上，getChildView将不会被调用的情况
                    mAdapter.notifyGroupExpanded(groupPos);
                    return expandGroup(groupPos);
                }
            }
        }
        // 通知适配器为某个group设置扩展动画
        mAdapter.startExpandAnimation(groupPos, 0);
        // 最后调用expandGroup(注意我们不必调用notifyDataSetChanged，expandGroup会调用)
        // expandGroup()同时会进行展开调用notifyDataSetChanged和返回group的状态
        return expandGroup(groupPos);
    }

    /**
     * 用动画折叠指定的group
     * @param groupPos 要折叠的group的位置
     * @return 如果group折叠了（从扩展到折叠），返回true。如果group本来就是折叠状态，则返回false
     */
    public boolean collapseGroupWithAnimation(int groupPos){
        // groupFlatPos指的是当前group在整个list中的实际位置
        int groupFlatPos = getFlatListPosition(getPackedPositionForGroup(groupPos));
        if (groupFlatPos != -1){
            // getFirstVisiblePosition()获取的是屏幕上第一个可见的group的位置
            // 实际位置与第一个可见group的位置的差值可以表示当前group在可见列表中的索引位置
            int firstVisiblePos = getFirstVisiblePosition();
            View v = getChildAt(0);
            v.getBottom();
            int childIndex = groupFlatPos - firstVisiblePos;
            if (childIndex >= 0 && childIndex < getChildCount()){
                // 获得groupPos指向的group
                // getChildAt()获取的是页面上可见的视图
                View view = getChildAt(childIndex);
                if (view.getBottom() >= getBottom()){
                    // 如果用户无法看到动画，我们只需在没有动画的情况下折叠该组
                    // 这解决了如果group的children不在屏幕上，getChildView将不会被调用的情况
                    return collapseGroup(groupPos);
                }
            } else {
                // 如果该group在屏幕之外，我们可以在没有动画的情况下折叠它
                return collapseGroup(groupPos);
            }
        }

        // 获取屏幕上第一个可见item的位置信息
        long packedPos = getExpandableListPosition(getFirstVisiblePosition());
        int firstChildPos = getPackedPositionChild(packedPos);
        int firstGroupPos = getPackedPositionGroup(packedPos);

        // 如果屏幕上的第一个可见视图是子视图，并且它是我们尝试折叠的组的子项，
        // 则将其设置为组的第一个子视图位置。
        firstChildPos = firstChildPos == -1 || firstGroupPos != groupPos ? 0 : firstChildPos;
        // 通知适配器为某个group设置折叠动画
        mAdapter.startCollapseAnimation(groupPos, firstChildPos);
        // 此处为什么不直接调用collapseGroup()，因为调试的时候发现collapseGroup()好像不会调用notifyDataSetChanged()
        // 而且就算强制调用notifyDataSetChanged(),再调用collapseGroup()，也不会执行到getChildView
        // 只有先强制调用notifyDataSetChanged(),再调用isGroupExpanded()才有效果
        // 原因可能在于，expandGroup()和collapseGroup()返回的结果是相反的，而isGroupExpanded()
        // 和expandGroup()返回的结果是相同的
        // 强制刷新
        mAdapter.notifyDataSetChanged();
        // 返回group的状态（扩展或折叠）
        return isGroupExpanded(groupPos);
    }

    /**
     * 返回动画持续的时间
     * @return
     */
    public int getAnimationDuration(){
        return ANIMATION_DURATION;
    }
}
