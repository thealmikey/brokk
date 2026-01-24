package ai.brokk.analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.brokk.testutil.TestProject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LanguageStoragePathTest {

    @Test
    void languagesValues_discoversAllExpectedLanguagesViaSpi() {
        Set<String> expectedInternalNames = Set.of(
                "C_SHARP",
                "JAVA",
                "JAVASCRIPT",
                "PYTHON",
                "C_CPP",
                "GO",
                "RUST",
                "PHP",
                "SQL",
                "TYPESCRIPT",
                "SCALA",
                "NONE");

        Language[] values = Languages.values();
        Set<String> discoveredInternalNames = Arrays.stream(values)
                .map(Language::internalName)
                .collect(Collectors.toUnmodifiableSet());

        assertEquals(expectedInternalNames, discoveredInternalNames);

        long noneCount = Arrays.stream(values)
                .filter(l -> l.internalName().equals("NONE"))
                .count();
        assertEquals(1L, noneCount);

        for (String internalName : expectedInternalNames) {
            Language resolved = Languages.valueOf(internalName);
            assertNotNull(resolved);
            assertEquals(internalName, resolved.internalName());
        }
    }

    @Test
    void languagesValueOf_javaResolvesToJavaConstant() {
        assertSame(Languages.JAVA, Languages.valueOf("JAVA"));
        assertNotEquals(Languages.NONE, Languages.JAVA);
        assertEquals("JAVA", Languages.JAVA.internalName());
    }

    @Test
    void languagesFromExtension_pyResolvesToPythonConstant() {
        assertSame(Languages.PYTHON, Languages.fromExtension("py"));
        assertNotEquals(Languages.NONE, Languages.PYTHON);
        assertEquals("PYTHON", Languages.PYTHON.internalName());
    }

    @Test
    void languagesNone_isAlwaysAvailable() {
        assertNotNull(Languages.NONE);
        assertEquals("NONE", Languages.NONE.internalName());
        assertTrue(Arrays.stream(Languages.values()).anyMatch(l -> l.internalName().equals("NONE")));
    }

    @Test
    void javaLanguageStoragePathDiffersPerProjectRoot(@TempDir Path tempDir) throws Exception {
        Path root1 = tempDir.resolve("main");
        Path root2 = tempDir.resolve("worktree");
        Files.createDirectories(root1);
        Files.createDirectories(root2);

        Language lang = new JavaLanguage();

        TestProject project1 = new TestProject(root1, lang);
        TestProject project2 = new TestProject(root2, lang);

        Path storage1 = lang.getStoragePath(project1).toAbsolutePath().normalize();
        Path storage2 = lang.getStoragePath(project2).toAbsolutePath().normalize();

        assertNotEquals(storage1, storage2, "JavaLanguage storage paths must differ for distinct project roots");
    }

    @Test
    void pythonLanguageStoragePathDiffersPerProjectRoot(@TempDir Path tempDir) throws Exception {
        Path root1 = tempDir.resolve("main");
        Path root2 = tempDir.resolve("worktree");
        Files.createDirectories(root1);
        Files.createDirectories(root2);

        Language lang = new PythonLanguage();

        TestProject project1 = new TestProject(root1, lang);
        TestProject project2 = new TestProject(root2, lang);

        Path storage1 = lang.getStoragePath(project1).toAbsolutePath().normalize();
        Path storage2 = lang.getStoragePath(project2).toAbsolutePath().normalize();

        assertNotEquals(storage1, storage2, "PythonLanguage storage paths must differ for distinct project roots");
    }
}
