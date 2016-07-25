package wq.refreshlistview_demo;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import wq.refreshlistview_library.RefreshListView;
/*1.下拉刷新  2.加载更多数据*/
public class MainActivity extends AppCompatActivity {
    private RefreshListView rlv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rlv = (RefreshListView) findViewById(R.id.rlv_test);
        rlv.setAdapter(new MyAdapter());

        refreshPullDownUse(); //1.下拉刷新头的用法

        loadingMoreDataUse(); //2.加载更多数据的用法
    }

    /**
     * 2.加载更过数据
     */
    private void loadingMoreDataUse() {
        //2.1 设置可以加载更多数据
        rlv.setIsRefreshFoot(true);
        //2.2 设置加加载更多数据的监听
        rlv.setOnRefreshDataListener(new RefreshListView.OnRefreshDataListener() {

            @Override
            public void refresdData() {
            }
            @Override
            public void loadingMore() { //覆盖此方法，添加刷新数据的代码
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);//模拟耗时操作(3秒)
                        runOnUiThread(new Runnable() {//假设3秒后数据刷新成功
                            @Override
                            public void run() {
                                rlv.refreshStateFinish();//调用listVew的这个方法处理刷新结果状态改变，显示视图
                                Toast.makeText(MainActivity.this, "加载成功！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    /**
     * 1.下拉刷新
     */
    private void refreshPullDownUse() {
        //1.1 设置可以下拉刷新
        rlv.setIsRefreshHead(true);
        //1.2 设置下拉刷新数据的监听器:OnRefreshDataListener
        rlv.setOnRefreshDataListener(new RefreshListView.OnRefreshDataListener() {

            @Override
            public void refresdData() {//覆盖此方法，刷新数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(2000);//模拟耗时操作(3秒)
                        runOnUiThread(new Runnable() {//假设3秒后数据刷新成功
                            @Override
                            public void run() {
                                rlv.refreshStateFinish();//调用listVew的这个方法处理刷新结果状态改变，显示视图
                                Toast.makeText(MainActivity.this, "刷新成功！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void loadingMore() {
            }
        });
    }

    /**
     * ListView适配器
     */
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 100;//模拟100条数据
        }
        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getApplicationContext());
            tv.setGravity(Gravity.LEFT);
            tv.setTextSize(22);
            tv.setText("测试数据------" + position);
            return tv;
        }

    }

}
