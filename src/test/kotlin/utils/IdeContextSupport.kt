package utils

import com.intellij.driver.client.Driver
import com.intellij.driver.sdk.singleProject
import com.intellij.driver.sdk.waitForIndicators
import com.intellij.driver.sdk.waitForProjectOpen
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.project.LocalProjectInfo
import com.intellij.ide.starter.runner.Starter
import org.junit.jupiter.api.TestInfo
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path
import kotlin.time.Duration.Companion.minutes

const val KOTLIN_SOURCE_DIR = "src/main/kotlin"

private const val IDE_VERSION = "2024.3"
private const val SAMPLE_PROJECT_NAME = "kotlin-template-project"
private const val SAMPLE_PROJECT_PATH = "src/test/resources/sampleProjects/$SAMPLE_PROJECT_NAME"
private const val TEST_PROJECTS_DIR = "build/test-projects"

fun runIdeTest(
    testInfo: TestInfo,
    testBody: (driver: Driver, projectDir: Path) -> Unit,
): Path {
    val testName = testInfo.testMethod.orElseThrow().name
    val sampleProjectDir = copySampleProject()

    Starter.newContext(
        testName = testName,
        TestCase(
            IdeProductProvider.IC,
            LocalProjectInfo(projectDir = sampleProjectDir),
        ).withVersion(IDE_VERSION),
    ).runIdeWithDriver().useDriverAndCloseIde {
        waitForProjectOpen(2.minutes)
        val project = singleProject()
        waitForIndicators(project, 5.minutes)

        testBody(this, sampleProjectDir)
    }

    return sampleProjectDir
}

fun Path.kotlinFile(className: String): Path = resolve("$KOTLIN_SOURCE_DIR/$className.kt")

private fun copySampleProject(): Path {
    val source = Path(SAMPLE_PROJECT_PATH).toAbsolutePath()
    val target = Path("$TEST_PROJECTS_DIR/$SAMPLE_PROJECT_NAME-${System.nanoTime()}").toAbsolutePath()

    Files.createDirectories(target.parent)
    Files.walk(source).use { paths ->
        paths.forEach { path ->
            val relativePath = source.relativize(path)
            val targetPath = target.resolve(relativePath)
            if (Files.isDirectory(path)) {
                Files.createDirectories(targetPath)
            } else {
                Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    return target
}
