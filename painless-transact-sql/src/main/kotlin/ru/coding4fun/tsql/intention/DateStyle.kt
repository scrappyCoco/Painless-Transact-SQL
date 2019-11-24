/*
 * Copyright [2019] Coding4fun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.coding4fun.tsql.intention

object DateStyle {
    class MsDateFormat(val id: String, val title: String, val format: String)

    val STYLES = mapOf(
            "100" to MsDateFormat("100", "Default", "mon dd yyyy hh:miAM"),
            "101" to MsDateFormat("101", "U.S.", "mm/dd/yyyy"),
            "102" to MsDateFormat("102", "ANSI", "yyyy.mm.dd"),
            "103" to MsDateFormat("103", "British/French", "dd/mm/yyyy"),
            "104" to MsDateFormat("104", "German", "dd.mm.yyyy"),
            "105" to MsDateFormat("105", "Italian", "dd-mm-yyyy"),
            "106" to MsDateFormat("106", "-", "dd mon yyyy"),
            "107" to MsDateFormat("107", "-", "Mon dd, yyyy"),
            "108" to MsDateFormat("108", "-", "hh:mi:ss"),
            "109" to MsDateFormat("109", "-", "mon dd yyyy hh:mi:ss:mmmAM"),
            "110" to MsDateFormat("110", "USA", "mm-dd-yyyy"),
            "111" to MsDateFormat("111", "JAPAN", "yyyy/mm/dd"),
            "112" to MsDateFormat("112", "ISO", "yyyymmdd"),
            "113" to MsDateFormat("113", "Europe default (with ms)", "dd mon yyyy hh:mi:ss:mmm (24h)"),
            "114" to MsDateFormat("114", "-", "hh:mi:ss:mmm (24h)"),
            "120" to MsDateFormat("120", "ODBC canonical", "yyyy-mm-dd hh:mi:ss (24h)"),
            "121" to MsDateFormat("121", "ODBC canonical (with ms)", "yyyy-mm-dd hh:mi:ss.mmm (24h)"),
            "22" to MsDateFormat("22", "U.S.", "mm/dd/yy hh:mi:ss AM"),
            "23" to MsDateFormat("23", "ISO8601", "yyyy-mm-dd"),
            "126" to MsDateFormat("126", "ISO8601", "yyyy-mm-ddThh:mi:ss.mmm"),
            "127" to MsDateFormat("127", "ISO8601 with time zone Z", "yyyy-mm-ddThh:mi:ss.mmmZ"),
            "130" to MsDateFormat("130", "Hijri", "dd mon yyyy hh:mi:ss:mmmAM"),
            "131" to MsDateFormat("131", "Hijri", "dd/mm/yyyy hh:mi:ss:mmmAM")
    )
}