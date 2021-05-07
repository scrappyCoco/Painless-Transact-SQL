package ru.coding4fun.tsql.inspection.namingConvention

// Original source is https://github.com/JetBrains/kotlin/blob/master/idea/src/org/jetbrains/kotlin/idea/inspections/NamingConventionInspections.kt

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.components.State
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.SqlDefinition
import org.jdom.Element
import org.jetbrains.annotations.PropertyKey
import ru.coding4fun.tsql.MsInspectionMessages
import ru.coding4fun.tsql.utils.MsNameUtils
import ru.coding4fun.tsql.utils.RegexUtils
import javax.swing.JPanel

@State(name = "MsNameConventionInspection")
abstract class MsNamingInspectionBase(
    @PropertyKey(resourceBundle = "messages.inspection") defaultNamePatternPropertyKey: String,
    @PropertyKey(resourceBundle = "messages.inspection") defaultDescriptionPropertyKey: String,
    vararg availableContexts: String
) : SqlInspectionBase() {
    private val separator = " "
    private val regexPrefix: String
    private val availableContextVariables: List<String> = availableContexts.asList()

    init {
        val regexPrefixSb = StringBuilder(512)
        for (context in availableContexts) {
            regexPrefixSb
                .append("(?<")
                .append(context)
                .append(">(\\S+|.{0}))")
                .append(separator)
        }
        regexPrefix = regexPrefixSb.toString()
    }

    private fun formatName(names: List<String>): String {
        val sb = StringBuilder(512)
        for ((number, name) in names.withIndex()) {
            if (number > 0) sb.append(separator)
            sb.append(MsNameUtils.unquote(name))
        }
        return sb.toString()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    var namePattern: String = MsInspectionMessages.message(defaultNamePatternPropertyKey)

    @Suppress("MemberVisibilityCanBePrivate")
    var description: String = MsInspectionMessages.message(defaultDescriptionPropertyKey)

    private var requestedRegexGroups = emptySet<String>()

    private val namingSettings = NamingConventionInspectionSettings(regexPrefix, namePattern, description,
        { newPattern ->
            namePattern = newPattern
            requestedRegexGroups = RegexUtils.getGroupNames(newPattern).toHashSet()
        }, { newDescription ->
            description = newDescription
        })

    override fun createOptionsPanel(): JPanel = namingSettings.createOptionsPanel()

    override fun readSettings(node: Element) {
        super.readSettings(node)
        namingSettings.namePattern = namePattern
        namingSettings.description = description
    }

    protected fun validateNameAndReport(
        names: List<String>,
        sqlDefinition: SqlDefinition,
        manager: InspectionManager,
        problems: MutableList<ProblemDescriptor>,
        onTheFly: Boolean,
        contextResolver: ((contextVariable: String) -> String?)? = null
    ) {
        val formattedName = formatName(names)
        val isValidName = namingSettings.isValidName(formattedName) { regexMatchResult ->
            var isVariableValid = true
            for (contextVariable in availableContextVariables) {
                if (!requestedRegexGroups.contains(contextVariable)) continue
                val actualValue = contextResolver!!.invoke(contextVariable)
                if (regexMatchResult.groups[contextVariable]?.value != actualValue) {
                    isVariableValid = false
                    break
                }
            }
            isVariableValid
        }
        if (!isValidName) {
            val message = description.ifBlank { MsInspectionMessages.message("name.convention.problem", namePattern) }
            val problemDescriptor = manager.createProblemDescriptor(
                sqlDefinition.nameElement!!,
                message,
                onTheFly,
                emptyArray<LocalQuickFix>(),
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING
            )
            problems.add(problemDescriptor)
        }
    }
}