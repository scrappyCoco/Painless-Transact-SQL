package ru.coding4fun.tsql.usages

import com.intellij.database.dataSource.srcStorage.DbSrcMapping
import com.intellij.psi.PsiElement
import com.intellij.sql.psi.SqlCreateStatement

class MsUsageElement(val createStatement: SqlCreateStatement, val refElement: PsiElement? = null) {
    val dbCreateStatement: PsiElement?

    init {
        val dbSrcMapping = DbSrcMapping.getInstance()
        val dbElement = dbSrcMapping.getDbElement(createStatement)
        dbCreateStatement = dbElement?.declaration
    }

    val target get() = dbCreateStatement ?: createStatement
    val occurrence get() = refElement ?: createStatement

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MsUsageElement

        if (dbCreateStatement != null && other.dbCreateStatement != null &&
                dbCreateStatement != other.dbCreateStatement) return false
        if (createStatement != other.createStatement) return false

        return true
    }


    override fun hashCode(): Int {
        if (dbCreateStatement != null) return dbCreateStatement.hashCode()
        return createStatement.hashCode()
    }
}