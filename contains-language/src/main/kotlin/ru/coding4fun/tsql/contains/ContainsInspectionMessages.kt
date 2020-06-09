package ru.coding4fun.tsql.contains

import com.intellij.AbstractBundle
import org.jetbrains.annotations.PropertyKey

object ContainsInspectionMessages : AbstractBundle("messages/containsInspection") {
    fun message(@PropertyKey(resourceBundle = "messages.containsInspection") key: String, vararg params: String): String {
        return ContainsIntentionMessages.getMessage(key, *params)
    }
}