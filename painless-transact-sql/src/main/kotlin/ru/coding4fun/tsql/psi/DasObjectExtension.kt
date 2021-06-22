package ru.coding4fun.tsql.psi

import com.intellij.database.model.DasObject

private val tv = arrayOf('#', '@')

fun DasObject.isTempOrVariable(): Boolean = tv.contains(this.name[0])