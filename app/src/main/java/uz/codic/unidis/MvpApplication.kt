package uz.codic.unidis

import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.gw.swipeback.tools.WxSwipeBackActivityManager

class MvpApplication : MultiDexApplication(){

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        WxSwipeBackActivityManager.getInstance().init(this)
    }
}