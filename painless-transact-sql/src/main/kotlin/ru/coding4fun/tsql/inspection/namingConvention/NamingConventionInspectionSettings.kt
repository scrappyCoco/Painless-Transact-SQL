package ru.coding4fun.tsql.inspection.namingConvention

// Original source is https://github.com/JetBrains/kotlin/blob/master/idea/src/org/jetbrains/kotlin/idea/inspections/NamingConventionInspections.kt

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.ui.EditorTextField
import com.intellij.util.ui.FormBuilder
import org.intellij.lang.regexp.RegExpFileType
import ru.coding4fun.tsql.MsInspectionMessages
import ru.coding4fun.tsql.utils.RegexUtils
import java.util.regex.PatternSyntaxException
import javax.swing.JPanel

class NamingConventionInspectionSettings(
    private val regexPrefix: String,
    defaultNamePattern: String,
    defaultDescription: String,
    private val setNamePatternCallback: (value: String) -> Unit,
    private val setDescriptionCallback: (value: String) -> Unit
) {
    private var nameRegex: Regex? = null
    var contextRegexGroups: Set<String> = emptySet()

    var namePattern: String = ""
        set(value) {
            field = value
            setNamePatternCallback.invoke(value)
            nameRegex = try {
                (regexPrefix + value).toRegex()
            } catch (e: PatternSyntaxException) {
                null
            }
            contextRegexGroups = emptySet()
            if (nameRegex != null) contextRegexGroups = RegexUtils.getGroupNames(value).toHashSet()
        }

    var description: String = ""
        set(value) {
            field = value
            setDescriptionCallback.invoke(value)
        }

    init {
        namePattern = defaultNamePattern
        description = defaultDescription
    }

    fun isValidName(formattedName: String, additionalCheck: (MatchResult) -> Boolean): Boolean {
        if (nameRegex == null) return true;
        val matchResult = nameRegex!!.matchEntire(formattedName) ?: return false
        return additionalCheck.invoke(matchResult)
    }

    fun createOptionsPanel(): JPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(
            MsInspectionMessages.message("name.convention.settings.pattern"),
            EditorTextField(namePattern, null, RegExpFileType.INSTANCE).also { regexField ->
                regexField.setOneLineMode(true)
                regexField.document.addDocumentListener(object : DocumentListener {
                    override fun documentChanged(e: DocumentEvent) {
                        namePattern = regexField.text
                    }
                })
            }
        )
        .addLabeledComponent(
            MsInspectionMessages.message("name.convention.settings.description"),
            EditorTextField(description).also { descriptionField ->
                descriptionField.setOneLineMode(true)
                descriptionField.document.addDocumentListener(object : DocumentListener {
                    override fun documentChanged(e: DocumentEvent) {
                        description = descriptionField.text
                    }
                })
            })
        .panel
}