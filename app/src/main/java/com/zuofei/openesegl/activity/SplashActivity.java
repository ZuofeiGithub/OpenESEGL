package com.zuofei.openesegl.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.zhengsr.viewpagerlib.bean.PagerBean;
import com.zhengsr.viewpagerlib.callback.PagerHelperListener;
import com.zhengsr.viewpagerlib.indicator.ZoomIndicator;
import com.zhengsr.viewpagerlib.view.GlideViewPager;
import com.zuofei.openesegl.R;
import com.zuofei.openesegl.activity.login.LoginActivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private static final Integer[] RES = {R.drawable.banner1,R.drawable.banner2,R.drawable.banner3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        GlideViewPager viewPager =  findViewById(R.id.splase_viewpager);
        ZoomIndicator zoomIndicator =  findViewById(R.id.splase_bottom_layout);
        Button button =  findViewById(R.id.splase_start_btn);


        //先把本地的图片 id 装进 list 容器中
        List<Integer> images = new ArrayList<>();
        for (int i = 0; i < RES.length; i++) {
            images.add(RES[i]);
        }
        //配置pagerbean，这里主要是为了viewpager的指示器的作用，然后把最后一页的button也添加进来，注意记得写上泛型
        PagerBean bean = new PagerBean.Builder<Integer>()
                .setDataObjects(images)
                .setIndicator(zoomIndicator)
                .setOpenView(button)
                .builder();

        // 把数据添加到 viewpager中，并把view提供出来，这样除了方便调试，也不会出现一个view，多个
        // parent的问题，这里在轮播图比较明显
        viewPager.setPageListener(bean, R.layout.image_layout, new PagerHelperListener<Integer>() {
            @Override
            public void getItemView(View view, Integer data) {
                //通过获取到这个view，你可以随意定制你的内容
                ImageView imageView = view.findViewById(R.id.icon);
                imageView.setImageResource(data);
                Bitmap bm = readBitMap(SplashActivity.this,data);
                imageView.setImageBitmap(bm);
                if(bm.isRecycled()){
                    bm.recycle();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
