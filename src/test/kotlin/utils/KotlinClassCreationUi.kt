package utils

import com.intellij.driver.client.Driver
import com.intellij.driver.sdk.ui.Finder
import com.intellij.driver.sdk.ui.components.JTreeUiComponent
import com.intellij.driver.sdk.ui.components.ideFrame
import com.intellij.driver.sdk.ui.components.list
import com.intellij.driver.sdk.ui.components.openProjectViewToolWindow
import com.intellij.driver.sdk.ui.components.popupMenu
import com.intellij.driver.sdk.ui.components.textField
import com.intellij.driver.sdk.waitFor
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.Duration.Companion.seconds

private const val PROJECT_VIEW_TREE_CLASS = "com.intellij.ide.projectView.impl.ProjectViewTree"
private const val SRC_NODE = "src"
private const val MAIN_NODE = "main"
private const val KOTLIN_NODE = "kotlin"

private const val NEW_MENU_ITEM = "New"
private const val KOTLIN_CLASS_OR_FILE_MENU_ITEM = "Kotlin Class/File"
private const val CLASS_KIND = "Class"
private const val POPUP_MENU_XPATH = "//div[@class='MyMenu']"
private const val NEW_KOTLIN_CLASS_POPUP_XPATH = "//div[@javaclass='com.intellij.ide.actions.newclass.CreateWithTemplatesDialogPanel']"

fun Driver.createKotlinClass(projectDir: Path, className: String) {
    ideFrame {
        openNewKotlinClassPopup()

        x(NEW_KOTLIN_CLASS_POPUP_XPATH).apply {
            textField { byAccessibleName("Name") }.text = className
            list { byAccessibleName("Templates") }.clickItem(CLASS_KIND, fullMatch = true)
            keyboard { enter() }
        }
    }

    waitFor(
        message = "$className.kt is created",
        timeout = 10.seconds,
    ) {
        Files.exists(projectDir.kotlinFile(className))
    }
}

private fun Finder.openNewKotlinClassPopup() {
    openProjectViewToolWindow()
    val projectTree = x(JTreeUiComponent::class.java) { byType(PROJECT_VIEW_TREE_CLASS) }

    waitFor(
        message = "Kotlin source root is available in Project View",
        timeout = 10.seconds,
    ) {
        runCatching {
            projectTree.clickPath(SRC_NODE, MAIN_NODE, KOTLIN_NODE, fullMatch = false)
        }.isSuccess
    }

    waitFor(
        message = "Project context menu is opened",
        timeout = 10.seconds,
    ) {
        runCatching {
            projectTree.rightClickPath(SRC_NODE, MAIN_NODE, KOTLIN_NODE, fullMatch = false)
            isPopupMenuOpened()
        }.getOrDefault(false)
    }

    popupMenu().select(NEW_MENU_ITEM)
    popupMenu().select(KOTLIN_CLASS_OR_FILE_MENU_ITEM)
}

private fun Finder.isPopupMenuOpened(): Boolean =
    xx(POPUP_MENU_XPATH).list().isNotEmpty()
