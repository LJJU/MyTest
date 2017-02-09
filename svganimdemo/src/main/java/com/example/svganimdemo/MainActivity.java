package com.example.svganimdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    WowView wv;
    WowSplashView wsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wv = (WowView) findViewById(R.id.wv);
        wsv = (WowSplashView) findViewById(R.id.wsv);

        wsv.setDrawingCacheEnabled(true);   //设置能否缓存图片信息（drawing cache）
        wsv.buildDrawingCache();            //如果能够缓存图片，则创建图片缓存
        wsv.setOnAnimFinishedListener(new WowSplashView.OnAnimFinishedListener() {
            @Override
            public void onAnimFinished() {
                wsv.setVisibility(View.GONE);
                wv.setVisibility(View.VISIBLE);
                wv.startAnimator(wsv.getDrawingCache());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wsv.destroyDrawingCache();
    }
}
