package com.zhengsr.viewpagerlib.callback;

import android.view.View;

/**
 * Created by Administrator on 2017/11/9.
 */

public interface PagerHelperListener<T> {
    void getItemView(View view, T data);
}
