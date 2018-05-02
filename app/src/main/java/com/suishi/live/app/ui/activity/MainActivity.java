package com.suishi.live.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suishi.live.app.R;
import com.suishi.live.app.ui.fragment.PlayFragment;
import com.suishi.live.app.ui.fragment.PushFragment;
import com.suishi.live.app.ui.fragment.ShortVideoFragment;
import com.swbyte.chat.runtimepermissions.PermissionsManager;
import com.swbyte.chat.runtimepermissions.PermissionsResultAction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.navigation)
    BottomNavigationView mBottomNavigationView;

    Fragment mCurrent=null;

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder=ButterKnife.bind(this);

        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(String permission) {

            }
        });

        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
       // switchContent(PlayFragment.newInstance());
        mCurrent=PlayFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction().add(R.id.content,mCurrent).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                //播放
                switchContent(PlayFragment.newInstance());
                return true;
            case R.id.navigation_dashboard:
                //推流
                switchContent(PushFragment.newInstance());
                return true;
            case R.id.navigation_notifications:
                //小视频
                switchContent(ShortVideoFragment.newInstance());
                break;
        }
        return true;
    }

    /** 修改显示的内容 不会重新加载 **/
    public void switchContent(Fragment to) {
        if (mCurrent != to) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(mCurrent).add(R.id.content, to).commit();
            } else {
                transaction.hide(mCurrent).show(to).commit();
            }
            mCurrent = to;
        }
    }

    public class HoloTilesAdapter extends BaseAdapter {

        private static final int TILES_COUNT = 2;

        private final int[] DRAWABLES = {
                R.drawable.blue_tile,
                R.drawable.green_tile,
        };

        @Override
        public int getCount() {
            return TILES_COUNT;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            RelativeLayout v;
            if (convertView == null) {
                v = (RelativeLayout) getLayoutInflater().inflate(R.layout.grid_item, parent, false);
            } else {
                v = (RelativeLayout) convertView;
            }
            v.setBackgroundResource(DRAWABLES[position % 2]);

            TextView textView1 = v.findViewById(R.id.textView1);
            TextView textView2 = v.findViewById(R.id.textView2);

            String string1 = "", string2 = "";
            if (position == 0) {
                string1 = "Portrait";
                string2 = "Flv + Local";
            } else if (position == 1) {
                string1 = "Landscape";
                string2 = "Rtmp";
            } else if (position == 2) {
                string1 = "Portrait";
                string2 = "Part";
            } else if (position == 3) {
                string1 = "Portrait";
                string2 = "Screen + Rtmp";
            }
            textView1.setText(string1);
            textView2.setText(string2);

            final int currentPosition = position;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentPosition == 0) {
                        goPortraitAndLocal();
                    } else if (currentPosition == 1) {
                        goPart();
                    } else if (currentPosition == 2) {

                    } else if (currentPosition == 3) {
                    }
                }
            });
            return v;
        }
    }

    private void goPortraitAndLocal() {
        Intent intent = new Intent(this, VideoActivity.class);
        startActivity(intent);
    }

    private void goPart() {
        Intent intent = new Intent(this, PortraitActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
