package ru.coding4fun.tsql.contains.intention

class ContainsReplaceToInflectional : ContainsReplaceTermBaseIntention() {
    override fun getNewText(simpleTermText: String): String = "FORMSOF(INFLECTIONAL, $simpleTermText))"
}