package com.agenthun.xposedexample.hook

import android.app.Notification
import android.os.Build
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * @project XposedExample
 * @authors agenthun
 * @date    2018/12/30 15:54.
 */
class PluginNotification : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam == null) {
            return
        }
        Log.d(TAG, "hook: ${lpparam.packageName}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            XposedHelpers.findAndHookMethod(
                "android.app.NotificationManager",
                lpparam.classLoader,
                "notify",
                Int::class.java,
                Notification::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        super.beforeHookedMethod(param)
                        (param.args[1] as Notification).apply {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                val bundle = extras
                                val title = bundle.get("android.title")
                                val text = bundle.get("android.text")
                                if (HOOK_PKG_NAME == contentView?.`package`) {
                                    Log.d(TAG, "notify, is AliPay~")
                                    param.result = null
                                }
                                Log.d(TAG, "notify, title=$title, text=$text, bundle=$bundle")
                            }
                        }
                    }
                })
        }
    }

    companion object {
        private val TAG = PluginNotification::class.java.simpleName
        private const val HOOK_PKG_NAME = "com.eg.android.AlipayGphone"
    }
}