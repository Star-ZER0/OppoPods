package moe.chenxy.oppopods.hook

import android.util.Log


object SystemUIPluginHook : HookContext() {
    override fun onHook() {
        var pluginLoaderClassLoader: ClassLoader? = null

        fun loadPluginHooker(hooker: HookContext) {
            val classLoader = pluginLoaderClassLoader ?: return
            hooker.module = module
            hooker.appClassLoader = classLoader
            hooker.prefs = prefs
            hooker.onHook()
        }

        fun initPluginHook() {
            loadPluginHooker(DeviceCardHook)
        }

        // Load plugin hooker
        // get Classloader for plugin on Android U
        hookAfter(findMethodByParamCount("com.android.systemui.shared.plugins.PluginInstance", "loadPlugin", 0)) {
            val pkgName = callMethod(instance, "getPackage")
            if (pkgName == "miui.systemui.plugin") {
                val factory = getObjectField(instance, "mPluginFactory")
                val clsLoader = callMethod(getObjectField(factory, "mClassLoaderFactory"), "get") as ClassLoader
                if (pluginLoaderClassLoader != clsLoader) {
                    Log.i(
                        "OppoPods",
                        "[loadPlugin] initPluginHook"
                    )
                    pluginLoaderClassLoader = clsLoader
                    initPluginHook()
                }
            }
        }
    }
}
