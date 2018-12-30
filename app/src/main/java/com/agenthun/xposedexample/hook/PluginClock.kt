package com.agenthun.xposedexample.hook

import android.graphics.Color
import android.widget.TextView
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * @project XposedExample
 * @authors agenthun
 * @date    2018/12/30 15:54.
 */
class PluginClock : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (HOOK_PKG_NAME != lpparam?.packageName) {
            return
        }
        XposedBridge.log("$TAG, pkgName=${lpparam.packageName}")
        XposedHelpers.findAndHookMethod(
            "$HOOK_PKG_NAME.statusbar.policy.Clock",
            lpparam.classLoader,
            "updateClock",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    param?.let {
                        (it.thisObject as TextView).apply {
                            XposedBridge.log("$TAG, text=$text")
                            text = "$text, hook"
                            setTextColor(Color.RED)
                        }
                    }
                }

                override fun beforeHookedMethod(param: MethodHookParam?) {
                    super.beforeHookedMethod(param)
                }
            })
    }

    companion object {
        private val TAG = PluginClock::class.java.simpleName
        private const val HOOK_PKG_NAME = "com.android.systemui"
    }
}