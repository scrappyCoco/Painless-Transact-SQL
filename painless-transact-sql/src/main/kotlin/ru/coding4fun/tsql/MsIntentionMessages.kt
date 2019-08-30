package ru.coding4fun.tsql

import com.intellij.AbstractBundle
import org.jetbrains.annotations.PropertyKey

object MsIntentionMessages : AbstractBundle("messages/intention") {
    fun message(@PropertyKey(resourceBundle = "messages.intention") key: String, vararg params: String): String {
        return getMessage(key, *params)
    }
}