package ru.coding4fun.tsql

import com.intellij.util.ResourceUtil

object PainlessResourceUtil {
    fun readQuery(resourcePath: String): String {
        val resourceUrl = ResourceUtil.getResource(PainlessResourceUtil::class.java, "/", resourcePath)
        return ResourceUtil.loadText(resourceUrl)
    }
}