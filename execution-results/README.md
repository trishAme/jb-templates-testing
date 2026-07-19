# Execution Results

Latest run: July 19, 2026

Command:

```bash
JAVA_HOME=<path-to-JDK-21> GRADLE_USER_HOME=<gradle-cache> ./gradlew test --no-daemon
```

IDE under test: IntelliJ IDEA Community 2024.3

## Summary

| Tests | Skipped | Failures | Errors | Duration | Result |
| ---: | ---: | ---: | ---: | ---: | --- |
| 3 | 0 | 0 | 0 | 5m03.22s | 100% successful |

## Test Cases

| Test | Method name | Duration | Result |
| --- | --- | ---: | --- |
| Edit Kotlin Class template | `editKotlinClassTemplateAndCreateClass(TestInfo)` | 3m11.26s | passed |
| Reset Kotlin Class template | `resetKotlinClassTemplateToDefault(TestInfo)` | 1m13.18s | passed |
| Reject duplicate Template name | `duplicateTemplateNameShowsValidationError(TestInfo)` | 38.77s | passed |

## Report Files

- [Sanitized HTML report](index.html)
- [Sanitized JUnit XML](TEST-FileAndCodeTemplatesTest.xml)
