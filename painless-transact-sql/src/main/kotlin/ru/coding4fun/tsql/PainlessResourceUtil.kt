package ru.coding4fun.tsql

import com.intellij.util.ResourceUtil
import com.intellij.util.io.URLUtil;

object PainlessResourceUtil {
    fun readContent(resourcePath: String): String {
        val resourceUrl = PainlessResourceUtil::class.java.classLoader.getResource(resourcePath)!!
        return ResourceUtil.loadText(URLUtil.openStream(resourceUrl))
    }
}