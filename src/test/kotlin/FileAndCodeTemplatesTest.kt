import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.Timeout
import utils.*
import java.nio.file.Files
import java.util.concurrent.TimeUnit

private const val TEST_COMMENT = "// This is test class"
private const val RESET_TEST_COMMENT = "// This comment should disappear after reset"

class FileAndCodeTemplatesTest {

    /**
     * Opens File and Code Templates settings, adds a test comment to Kotlin Class template,
     * creates a Kotlin class, and checks that the generated file contains the comment.
     */
    @DisplayName("Edit Kotlin Class template")
    @Test
    @Timeout(value = 10, unit = TimeUnit.MINUTES)
    fun editKotlinClassTemplateAndCreateClass(testInfo: TestInfo) {
        val className = "GeneratedTemplateClass"
        val projectDir = runIdeTest(testInfo) { driver, projectDir ->
            driver.editKotlinClassTemplate { templateText ->
                "$TEST_COMMENT\n$templateText"
            }

            driver.createKotlinClass(projectDir, className)
        }
        val generatedClass = projectDir.kotlinFile(className)

        assertTrue(
            Files.readString(generatedClass).contains(TEST_COMMENT),
            "Generated Kotlin class should contain the comment from the Kotlin Class template",
        )
    }

    /**
     * Changes Kotlin Class template, creates a class with the custom comment,
     * reverts the template to original, and checks that the next class is created without it.
     */
    @DisplayName("Reset Kotlin Class template")
    @Test
    @Timeout(value = 10, unit = TimeUnit.MINUTES)
    fun resetKotlinClassTemplateToDefault(testInfo: TestInfo) {
        val beforeResetClassName = "BeforeResetClass"
        val afterResetClassName = "AfterResetClass"
        val projectDir = runIdeTest(testInfo) { driver, projectDir ->
            driver.editKotlinClassTemplate { templateText ->
                "$RESET_TEST_COMMENT\n$templateText"
            }

            driver.createKotlinClass(projectDir, beforeResetClassName)
            driver.resetKotlinClassTemplate(RESET_TEST_COMMENT)
            driver.createKotlinClass(projectDir, afterResetClassName)
        }
        val beforeResetClass = projectDir.kotlinFile(beforeResetClassName)
        val afterResetClass = projectDir.kotlinFile(afterResetClassName)

        assertTrue(
            Files.readString(beforeResetClass).contains(RESET_TEST_COMMENT),
            "Kotlin class created before reset should contain the custom template comment",
        )
        assertFalse(
            Files.readString(afterResetClass).contains(RESET_TEST_COMMENT),
            "Kotlin class created after reset should not contain the custom template comment",
        )
    }

    /**
     * Creates a new file template with the existing Kotlin Class name
     * and checks that Settings shows duplicate template name validation.
     */
    @DisplayName("Reject duplicate Template name")
    @Test
    @Timeout(value = 10, unit = TimeUnit.MINUTES)
    fun duplicateTemplateNameShowsValidationError(testInfo: TestInfo) {
        runIdeTest(testInfo) { driver, _ ->
            assertTrue(
                driver.tryCreateDuplicateKotlinClassTemplate(),
                "IDE should show an error when a template named Kotlin Class already exists",
            )
        }
    }
}
