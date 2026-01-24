package ai.brokk.analyzer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Languages {
    public static final Language C_SHARP;
    public static final Language JAVA;
    public static final Language JAVASCRIPT;
    public static final Language PYTHON;
    public static final Language C_CPP;
    public static final Language GO;
    public static final Language RUST;
    public static final Language NONE;
    public static final Language PHP;
    public static final Language SQL;
    public static final Language TYPESCRIPT;
    public static final Language SCALA;

    public static final List<Language> ALL_LANGUAGES;

    private static final Map<String, Language> BY_INTERNAL_NAME;

    static {
        List<Language> discovered = ServiceLoader.load(Language.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .toList();

        BY_INTERNAL_NAME = discovered.stream()
                .collect(Collectors.toMap(
                        Language::internalName,
                        Function.identity(),
                        (a, b) -> a,
                        LinkedHashMap::new));

        Language none = BY_INTERNAL_NAME.get("NONE");
        if (none == null) {
            none = new NoneLanguage();
        }

        List<Language> ordered = new ArrayList<>(discovered.size() + 1);
        for (Language lang : discovered) {
            if (lang != none) {
                ordered.add(lang);
            }
        }
        ordered.add(none);
        ALL_LANGUAGES = List.copyOf(ordered);

        NONE = none;

        JAVA = byInternalNameOrNone("JAVA");
        PYTHON = byInternalNameOrNone("PYTHON");
        C_SHARP = byInternalNameOrNone("C_SHARP");
        JAVASCRIPT = byInternalNameOrNone("JAVASCRIPT");
        C_CPP = byInternalNameOrNone("C_CPP");
        GO = byInternalNameOrNone("GO");
        RUST = byInternalNameOrNone("RUST");
        PHP = byInternalNameOrNone("PHP");
        SQL = byInternalNameOrNone("SQL");
        TYPESCRIPT = byInternalNameOrNone("TYPESCRIPT");
        SCALA = byInternalNameOrNone("SCALA");
    }

    private static Language byInternalNameOrNone(String internalName) {
        Language lang = BY_INTERNAL_NAME.get(internalName);
        return lang != null ? lang : NONE;
    }

    /**
     * Returns the Language constant corresponding to the given file extension. Comparison is case-insensitive.
     *
     * @param extension The file extension (e.g., "java", "py").
     * @return The matching Language, or NONE if no match is found or the extension is empty.
     */
    public static Language fromExtension(String extension) {
        if (extension.isEmpty()) {
            return NONE;
        }
        String lowerExt = extension.toLowerCase(Locale.ROOT);
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
     * Returns an array containing all discovered Language implementations.
     *
     * @return an array containing all discovered Language implementations.
     */
    public static Language[] values() {
        return ALL_LANGUAGES.toArray(new Language[0]);
    }

    /**
     * Returns the Language constant with the specified name. The string must match either {@link Language#name()} or
     * {@link Language#internalName()}.
     *
     * @param name the name of the Language constant to be returned.
     * @return the Language constant with the specified name.
     * @throws IllegalArgumentException if this language type has no constant with the specified name.
     * @throws NullPointerException if name is null.
     */
    public static Language valueOf(String name) {
        for (Language lang : ALL_LANGUAGES) {
            if (lang.name().equals(name) || lang.internalName().equals(name)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("No language constant " + Language.class.getCanonicalName() + "." + name);
    }
}
