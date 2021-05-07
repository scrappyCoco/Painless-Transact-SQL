package ru.coding4fun.tsql.test

import junit.framework.TestCase
import org.locationtech.jts.util.Assert
import ru.coding4fun.tsql.utils.RegexUtils

class RegexUtilsTest: TestCase() {
    fun testGetGroupNames() {
        val groups = RegexUtils.getGroupNames("PK_\\k<schema>.+?)_\\k<table>.+?")
        Assert.equals("schema", groups[0])
        Assert.equals("table", groups[1])
    }

    fun testGetGroupNames2() {
        val groups = RegexUtils.getGroupNames("^(?<unique>unique)?\\s+((?<clustered>clustered)|(?<nonClustered>nonclustered))?\\s+IX(?(clustered)C|.{0})(?(unique)U|.{0})_.+\$")
        Assert.equals("clustered", groups[0])
        Assert.equals("unique", groups[1])
    }

    fun testMyRegex() {
        val regex = Regex("((?<clustered>clustered)|(?<nonclustered>nonclustered))?\\s+((?!\\k<clustered>)C)?IX")
        val input = "nonclustered CIX"
        Assert.isTrue(regex.matches(input))
    }
}