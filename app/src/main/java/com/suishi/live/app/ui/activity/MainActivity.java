package com.suishi.live.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suishi.live.app.R;
import com.swbyte.chat.runtimepermissions.PermissionsManager;
import com.swbyte.chat.runtimepermissions.PermissionsResultAction;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.navigation)
    BottomNavigationView mBottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(String permission) {

            }
        });

        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                return true;
            case R.id.navigation_dashboard:
                return true;
            case R.id.navigation_notifications:
        }

        return false;
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

}
