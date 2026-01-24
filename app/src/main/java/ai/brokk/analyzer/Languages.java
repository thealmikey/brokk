package ai.brokk.analyzer;

import ai.brokk.analyzer.scala.ScalaLanguage;
import java.util.List;
import java.util.Locale;

public class Languages {
    public static final Language C_SHARP = new CSharpLanguage();
    public static final Language JAVA = new JavaLanguage();
    public static final Language JAVASCRIPT = new JavaScriptLanguage();
    public static final Language PYTHON = new PythonLanguage();
    public static final Language C_CPP = new CppLanguage();
    public static final Language GO = new GoLanguage();
    public static final Language CPP_TREESITTER = new CppTreeSitterLanguage();
    public static final Language RUST = new RustLanguage();
    public static final Language NONE = new NoneLanguage();
    public static final Language PHP = new PhpLanguage();
    public static final Language SQL = new SqlLanguage();
    public static final Language TYPESCRIPT = new TypeScriptLanguage();

    public static final Language SCALA = new ScalaLanguage();

    public static final List<Language> ALL_LANGUAGES = List.of(
            C_SHARP,
            JAVA,
            JAVASCRIPT,
            PYTHON,
            C_CPP,
            CPP_TREESITTER,
            GO,
            RUST,
            PHP,
            TYPESCRIPT,
            SCALA,
            SQL,
            NONE);

    /**
     * Returns the Language constant corresponding to the given file extension. Comparison is case-insensitive.
     *
     * @param extension The file extension (e.g., "java", "py").
     * @return The matching Language, or NONE if no match is found or the extension is null/empty.
     */
    public static Language fromExtension(String extension) {
        if (extension.isEmpty()) {
            return NONE;
        }
        String lowerExt = extension.toLowerCase(Locale.ROOT);
        // Ensure the extension does not start with a dot for consistent matching.
        String normalizedExt = lowerExt.startsWith(".") ? lowerExt.substring(1) : lowerExt;

        for (Language lang : ALL_LANGUAGES) {
            for (String langExt : lang.getExtensions()) {
                if (langExt.equals(normalizedExt)) {
                    return lang;
                }
            }
        }
        return NONE;
    }

    /**
     * Returns an array containing all the defined Language constants, in the order they are declared. This method is
     * provided for compatibility with Enum.values().
     *
     * @return an array containing all the defined Language constants.
     */
    public static Language[] values() {
        return ALL_LANGUAGES.toArray(new Language[0]);
    }

    /**
     * Returns the Language constant with the specified name. The string must match exactly an identifier used to
     * declare a Language constant. (Extraneous whitespace characters are not permitted.) This method is provided for
     * compatibility with Enum.valueOf(String).
     *
     * @param name the name of the Language constant to be returned.
     * @return the Language constant with the specified name.
     * @throws IllegalArgumentException if this language type has no constant with the specified name.
     * @throws NullPointerException if name is null.
     */
    public static Language valueOf(String name) {
        for (Language lang : ALL_LANGUAGES) {
            // Check current human-friendly name first, then old programmatic name for backward compatibility.
            if (lang.name().equals(name) || lang.internalName().equals(name)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("No language constant " + Language.class.getCanonicalName() + "." + name);
    }
}
