package ru.coding4fun.tsql.contains

import com.intellij.AbstractBundle
import org.jetbrains.annotations.PropertyKey

object ContainsIntentionMessages : AbstractBundle("messages/containsIntention") {
    fun message(@PropertyKey(resourceBundle = "messages.containsIntention") key: String, vararg params: String): String {
        return getMessage(key, *params)
    }
}