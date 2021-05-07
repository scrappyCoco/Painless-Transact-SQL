package ru.coding4fun.tsql.utils

import org.intellij.lang.annotations.RegExp

object RegexUtils {
    private val groupNameRegex = Regex("(\\\\k[<](?<group1>[^>]+)[>])|([?][(](?<group2>[^)]+)[)])")

    fun getGroupNames(@RegExp regexPatter: String?): List<String> {
        if (regexPatter.isNullOrBlank()) return emptyList()

        val groupNames = groupNameRegex.findAll(regexPatter)
            .map { it.groups["group1"]?.value ?: it.groups["group2"]!!.value }
            .toList()

        return groupNames
    }
}