package ru.coding4fun.tsql

import com.intellij.util.ResourceUtil
import com.intellij.util.io.URLUtil;

object PainlessResourceUtil {
    fun readQuery(resourcePath: String): String {
        val resourceUrl = ResourceUtil.getResource(PainlessResourceUtil::class.java.classLoader, "/", resourcePath)
        return ResourceUtil.loadText(URLUtil.openStream(resourceUrl))
    }
}