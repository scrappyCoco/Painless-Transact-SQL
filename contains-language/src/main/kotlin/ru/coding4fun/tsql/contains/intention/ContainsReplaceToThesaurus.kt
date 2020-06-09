package ru.coding4fun.tsql.contains.intention

class ContainsReplaceToThesaurus : ContainsReplaceTermBaseIntention() {
    override fun getNewText(simpleTermText: String): String = "FORMSOF(THESAURUS, $simpleTermText))"
}