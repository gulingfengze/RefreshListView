

package wq.refreshlistview_library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Lenovo on 2016/7/25.
 * Description 自定义刷新头和加载数据尾的ListView.
 */
public class RefreshListView extends ListView {

    private View foot;// listView加载更多数据的尾部组件
    private LinearLayout head;// listView刷新数据的头部组件
    private LinearLayout ll_refresh_head_root;
    private int ll_refresh_head_root_Height;
    private int ll_refresh_foot_Height;
    private float downY = -1;
    private final int PULL_DOWN = 0;// 下拉刷新状态
    private final int RELEASE_STATE = 1;// 松开刷新
    private final int REFRESHING = 2; // 正在刷新
    private int currentState = PULL_DOWN; // 当前的状态
    private OnRefreshDataListener listener;//刷新数据的监听回调
    private boolean isEnablePullRefresh;//下拉刷新是否可用
    private boolean isLoadingMore;//是否是加载更多数据
    private boolean isEnableLoadingMore;
    private View lunbotu;
    private int listViewOnScreanY;// listView在屏幕中的y轴坐标位置
    private TextView tv_state;
    private TextView tv_time;
    private ImageView iv_arrow;
    private ProgressBar pb_loading;
    private RotateAnimation up_ra;
    private RotateAnimation down_ra;

    public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
        initAnimation();
        initEvent();
    }

    private void initEvent() {
        /*添加当前ListView的滑动事件*/
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (!isEnableLoadingMore) {
                    return;  //不启用加载更多数据的功能
                }
                if (getLastVisiblePosition() == getAdapter().getCount() - 1 && !isLoadingMore) {//如果是最后一条数据,显示加载更多的组件
                    foot.setPadding(0, 0, 0, 0);//显示加载更多
                    setSelection(getAdapter().getCount());
                    isLoadingMore = true;  //加载更多数据
                    if (listener != null) {
                        listener.loadingMore();//实现该接口的组件取完成数据的加载
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListView(Context context) {
        this(context, null);
    }

    private void initView() {
        initFoot();
        initHead();
    }

    /*
     * 覆盖该方法完成自己的事件处理
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
       /*下拉拖动（当listView显示第一个条数据）*/
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:// 按下
                downY = ev.getY();// 按下时Y轴坐标
                break;
            case MotionEvent.ACTION_MOVE:// 移动
                if (!isEnablePullRefresh) {
                    break; //没有启用下拉刷新
                }
                if (currentState == REFRESHING) {  //现在是否处于刷新数据的状态
                    break;//正在刷新
                }
                if (!isLunboFullShow()) {
                    break;  // 轮播图没有完全显示
                }
                if (downY == -1) {
                    downY = ev.getY();// 按下的时候没有获取坐标
                }
                float moveY = ev.getY(); // 获取移动位置的坐标
                float dy = moveY - downY;  // 移动的位置间距

                if (dy > 0 && getFirstVisiblePosition() == 0) { // 下拉拖动（当listView显示第一个条数据）处理自己的事件，不让listView原生的拖动事件生效

                    float scrollYDis = -ll_refresh_head_root_Height + dy;  // 当前padding top 的参数值

                    if (scrollYDis < 0 && currentState != PULL_DOWN) {//判断刷新头状态(未完全显示)
                        currentState = PULL_DOWN;
                        refreshState();
                    } else if (scrollYDis >= 0 && currentState != RELEASE_STATE) {
                        currentState = RELEASE_STATE;// 记录松开刷新
                        refreshState();
                    }
                    ll_refresh_head_root.setPadding(0, (int) scrollYDis, 0, 0);
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:// 松开
                downY = -1;
                if (currentState == PULL_DOWN) {//如果是PULL_DOWN状态,松开恢复原状
                    ll_refresh_head_root.setPadding(0, -ll_refresh_head_root_Height, 0, 0);
                } else if (currentState == RELEASE_STATE) {

                    ll_refresh_head_root.setPadding(0, 0, 0, 0);  //刷新数据

                    currentState = REFRESHING;//改变状态为正在刷新数据的状态
                    refreshState();//刷新界面

                    if (listener != null) {
                        listener.refresdData();//刷新数据
                    }
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setOnRefreshDataListener(OnRefreshDataListener listener) {
        this.listener = listener;
    }

    public interface OnRefreshDataListener {
        void refresdData();
        void loadingMore();
    }

    private void initAnimation() {
        up_ra = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        up_ra.setDuration(500);
        up_ra.setFillAfter(true);//停留在动画结束的状态
        down_ra = new RotateAnimation(-180, -360,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        down_ra.setDuration(500);
        down_ra.setFillAfter(true);//停留在动画结束的状态
    }

    private void refreshState() {
        switch (currentState) {
            case PULL_DOWN:// 下拉刷新
                tv_state.setText("下拉刷新");
                iv_arrow.startAnimation(down_ra);
                break;
            case RELEASE_STATE:// 松开刷新
                tv_state.setText("松开刷新");
                iv_arrow.startAnimation(up_ra);
                break;
            case REFRESHING://正在刷新状态
                iv_arrow.clearAnimation();//清除所有动画
                iv_arrow.setVisibility(View.GONE);//隐藏箭头
                pb_loading.setVisibility(View.VISIBLE);//显示进度条
                tv_state.setText("正在刷新数据");
            default:
                break;
        }

    }

    /**
     * 刷新数据成功,处理结果
     */
    public void refreshStateFinish() {
        if (isLoadingMore) {
            isLoadingMore = false;
            foot.setPadding(0, -ll_refresh_foot_Height, 0, 0);
        } else {
            tv_state.setText("下拉刷新");
            iv_arrow.setVisibility(View.VISIBLE);//显示箭头
            pb_loading.setVisibility(View.INVISIBLE);//隐藏进度条
            tv_time.setText(getCurrentFormatDate());//设置刷新时间为当前时间
            ll_refresh_head_root.setPadding(0, -ll_refresh_head_root_Height, 0, 0); //隐藏刷新的头布局
            currentState = PULL_DOWN;//初始化为下拉刷新的状态
        }
    }

    private String getCurrentFormatDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    /**
     * @return 轮播图是否完全显示
     */
    private boolean isLunboFullShow() {
        if (lunbotu == null) { // 判断轮播图是否完全显示
            return true;
        }
        int[] location = new int[2];
        /*取listView在屏幕中坐标和轮播图在屏幕中的坐标*/
        if (listViewOnScreanY == 0) {
            this.getLocationOnScreen(location);
            listViewOnScreanY = location[1]; // 获取listView在屏幕中的Y轴坐标
        }
        lunbotu.getLocationOnScreen(location);// 轮播图在屏幕中的坐标
        if (location[1] < listViewOnScreanY) {  // 轮播图没有完全显示,响应listView的事件
            return false;
        }
        return true;

    }

    /**
     * 初始化尾部组件
     */
    private void initFoot() {
        foot = View.inflate(getContext(), R.layout.listview_refresh_foot, null);
        foot.measure(0, 0);// 测量尾部组件的高度
        ll_refresh_foot_Height = foot.getMeasuredHeight();  // listView尾部组件的高度
        foot.setPadding(0, -ll_refresh_foot_Height, 0, 0);
        addFooterView(foot);
    }

    /**
     * 用户自己选择是否启用下拉刷新头的功能
     *
     * @param isPullrefresh true 启用下拉刷新 false 不用下拉刷新
     */
    public void setIsRefreshHead(boolean isPullrefresh) {
        isEnablePullRefresh = isPullrefresh;
    }

    /**
     * 用户自己选择是否启用加载更多数据的功能
     *
     * @param isLoadingMore true 启用下拉刷新 false 不用下拉刷新
     */
    public void setIsRefreshFoot(boolean isLoadingMore) {
        isEnableLoadingMore = isLoadingMore;
    }

    /**
     * @param view 轮播图view
     */
    @Override
    public void addHeaderView(View view) {
       /* 如果使用下拉刷新,把头布局加到下拉刷新的容器中，否则加载原生ListView*/
        if (isEnablePullRefresh) {//启用下拉刷新
            lunbotu = view; // 轮播图的组件
            head.addView(view);
        } else {
            super.addHeaderView(view); //使用原生的ListView
        }

    }

    /**
     * 初始化头部组件
     */
    private void initHead() {
        head = (LinearLayout) View.inflate(getContext(), R.layout.listview_head_container, null);
        ll_refresh_head_root = (LinearLayout) head.findViewById(R.id.ll_listview_head_root);
        tv_state = (TextView) head.findViewById(R.id.tv_listview_head_state_dec);
        tv_time = (TextView) head.findViewById(R.id.tv_listview_head_refresh_time);
        iv_arrow = (ImageView) head.findViewById(R.id.iv_listview_head_arrow);
        pb_loading = (ProgressBar) head.findViewById(R.id.pb_listview_head_loading);
        ll_refresh_head_root.measure(0, 0);
        ll_refresh_head_root_Height = ll_refresh_head_root.getMeasuredHeight();
        ll_refresh_head_root.setPadding(0, -ll_refresh_head_root_Height, 0, 0);
        addHeaderView(head);
    }

}
