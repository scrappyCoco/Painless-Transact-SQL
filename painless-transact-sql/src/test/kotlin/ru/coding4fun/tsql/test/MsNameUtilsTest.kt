package ru.coding4fun.tsql.test

import junit.framework.TestCase
import org.locationtech.jts.util.Assert
import ru.coding4fun.tsql.utils.MsNameUtils

class MsNameUtilsTest: TestCase() {
    fun testUnquote() {
        Assert.equals("MyObject", MsNameUtils.unquote("[MyObject]"))
        Assert.equals("MyObject", MsNameUtils.unquote("MyObject"))
    }
}