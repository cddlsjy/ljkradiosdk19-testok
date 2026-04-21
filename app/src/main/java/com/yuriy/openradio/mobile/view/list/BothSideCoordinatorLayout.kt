package com.yuriy.openradio.mobile.view.list

import android.content.Context
import android.view.View
import com.xenione.libs.swipemaker.AbsCoordinatorLayout

class BothSideCoordinatorLayout(context: Context) : AbsCoordinatorLayout(context) {
    override fun doInitialViewsLocation() {
        // 实现初始视图位置逻辑
    }
    
    override fun onTranslateChange(globalPercent: Float, index: Int, relativePercent: Float) {
        // 实现翻译变化逻辑
    }
}
