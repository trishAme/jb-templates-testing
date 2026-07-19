package utils

import com.intellij.driver.client.Driver
import com.intellij.driver.sdk.ui.components.DialogUiComponent
import com.intellij.driver.sdk.ui.components.JEditorUiComponent
import com.intellij.driver.sdk.ui.components.actionButton
import com.intellij.driver.sdk.ui.components.dialog
import com.intellij.driver.sdk.ui.components.editor
import com.intellij.driver.sdk.ui.components.ideFrame
import com.intellij.driver.sdk.ui.components.list
import com.intellij.driver.sdk.ui.components.textField
import com.intellij.driver.sdk.ui.components.tree
import com.intellij.driver.sdk.waitFor
import kotlin.time.Duration.Companion.seconds

private const val SETTINGS_DIALOG_TITLE = "Settings"
private const val EDITOR_SETTINGS_NODE = "Editor"
private const val FILE_AND_CODE_TEMPLATES_NODE = "File and Code Templates"
private const val TEMPLATE_EDITOR_XPATH = "//div[@javaclass='com.intellij.openapi.editor.impl.EditorComponentImpl']"
private const val KOTLIN_CLASS_TEMPLATE_NAME = "Kotlin Class"
private const val KOTLIN_TEMPLATE_EXTENSION = "kt"
private const val CREATE_TEMPLATE_ACCESSIBLE_NAME = "Create Template"
private const val REVERT_TO_ORIGINAL_TEMPLATE_ACCESSIBLE_NAME = "Revert to Original Template"
private const val RESET_TEMPLATE_DIALOG_TITLE = "Reset Template"
private const val RESET_BUTTON = "Reset"
private const val OK_BUTTON = "OK"
private const val CANCEL_BUTTON = "Cancel"
private const val DUPLICATE_TEMPLATE_NAME_ERROR =
    "Cannot Save Settings: Template with name 'Kotlin Class' already exists. Please specify a different template name"

fun Driver.editKotlinClassTemplate(updateText: (String) -> String) {
    ideFrame {
        openSettingsDialog()

        dialog(title = SETTINGS_DIALOG_TITLE) {
            selectKotlinClassTemplate()

            val templateEditor = templateEditor()
            templateEditor.text = updateText(templateEditor.text)

            pressButton(OK_BUTTON)
        }
    }
}

fun Driver.resetKotlinClassTemplate(removedText: String) {
    ideFrame {
        openSettingsDialog()

        dialog(title = SETTINGS_DIALOG_TITLE) {
            selectKotlinClassTemplate()
            actionButton { byAccessibleName(REVERT_TO_ORIGINAL_TEMPLATE_ACCESSIBLE_NAME) }.click()
        }

        dialog(title = RESET_TEMPLATE_DIALOG_TITLE) {
            pressButton(RESET_BUTTON)
        }

        dialog(title = SETTINGS_DIALOG_TITLE) {
            val templateEditor = templateEditor()
            waitFor(
                message = "$KOTLIN_CLASS_TEMPLATE_NAME template is reverted to original",
                timeout = 10.seconds,
            ) {
                !templateEditor.text.contains(removedText)
            }
            pressButton(OK_BUTTON)
        }
    }
}

fun Driver.tryCreateDuplicateKotlinClassTemplate(): Boolean {
    var validationShown = false

    ideFrame {
        openSettingsDialog()

        dialog(title = SETTINGS_DIALOG_TITLE) {
            openFileAndCodeTemplatesSettings()
            actionButton { byAccessibleName(CREATE_TEMPLATE_ACCESSIBLE_NAME) }.click()
            templateNameField().text = KOTLIN_CLASS_TEMPLATE_NAME
            templateExtensionField().text = KOTLIN_TEMPLATE_EXTENSION

            pressButton(OK_BUTTON)
            waitFor(
                message = "Duplicate template name validation is shown",
                timeout = 5.seconds,
            ) {
                isDuplicateTemplateNameErrorShown()
            }
            validationShown = isDuplicateTemplateNameErrorShown()
            pressButton(CANCEL_BUTTON)
        }
    }

    return validationShown
}

private fun DialogUiComponent.selectKotlinClassTemplate() {
    openFileAndCodeTemplatesSettings()
    list().clickItem(KOTLIN_CLASS_TEMPLATE_NAME, fullMatch = true)
}

private fun DialogUiComponent.openFileAndCodeTemplatesSettings() {
    tree().clickPath(EDITOR_SETTINGS_NODE, FILE_AND_CODE_TEMPLATES_NODE)
}

private fun DialogUiComponent.templateEditor(): JEditorUiComponent =
    editor(TEMPLATE_EDITOR_XPATH)

private fun DialogUiComponent.templateNameField() =
    textField("//div[@javaclass='javax.swing.JTextField' and @accessiblename='Name:']")

private fun DialogUiComponent.templateExtensionField() =
    textField("//div[@javaclass='javax.swing.JTextField' and @accessiblename='Extension:']")

private fun DialogUiComponent.isDuplicateTemplateNameErrorShown(): Boolean =
    xx { byAccessibleName(DUPLICATE_TEMPLATE_NAME_ERROR) }.list().isNotEmpty()
