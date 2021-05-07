package ru.coding4fun.tsql.utils

import com.intellij.database.model.serialization.startsWith

object MsNameUtils {
    fun unquote(name: String?): String? {
        return when {
            name != null && name.startsWith('[') && name.endsWith(']') -> name.substring(1, name.length - 1)
            else -> name
        }
    }
}