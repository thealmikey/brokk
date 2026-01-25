package ai.brokk.analyzer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

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
        List<ServiceLoader.Provider<Language>> providers =
                ServiceLoader.load(Language.class).stream().toList();

        Map<String, ServiceLoader.Provider<Language>> providersByInternalName = new LinkedHashMap<>();
        for (ServiceLoader.Provider<Language> provider : providers) {
            String inferredInternalName = inferInternalName(provider.type());
            String internalName = inferredInternalName != null
                    ? inferredInternalName
                    : provider.get().internalName();
            providersByInternalName.putIfAbsent(internalName, provider);
        }

        ServiceLoader.Provider<Language> noneProvider = providersByInternalName.get("NONE");
        Language none = noneProvider != null ? new LazyLanguage("NONE", noneProvider) : new NoneLanguage();

        Map<String, Language> byInternalName = new LinkedHashMap<>();
        byInternalName.put("NONE", none);
        for (Map.Entry<String, ServiceLoader.Provider<Language>> entry : providersByInternalName.entrySet()) {
            String internalName = entry.getKey();
            if (!internalName.equals("NONE")) {
                byInternalName.putIfAbsent(internalName, new LazyLanguage(internalName, entry.getValue()));
            }
        }
        BY_INTERNAL_NAME = Map.copyOf(byInternalName);

        List<Language> ordered = new ArrayList<>(BY_INTERNAL_NAME.size());
        addIfPresent(ordered, "JAVA");
        addIfPresent(ordered, "PYTHON");
        addIfPresent(ordered, "C_SHARP");
        addIfPresent(ordered, "JAVASCRIPT");
        addIfPresent(ordered, "TYPESCRIPT");
        addIfPresent(ordered, "C_CPP");
        addIfPresent(ordered, "GO");
        addIfPresent(ordered, "RUST");
        addIfPresent(ordered, "PHP");
        addIfPresent(ordered, "SQL");
        addIfPresent(ordered, "SCALA");

        for (Map.Entry<String, Language> entry : BY_INTERNAL_NAME.entrySet()) {
            String internalName = entry.getKey();
            if (!internalName.equals("NONE")
                    && ordered.stream().noneMatch(l -> l.internalName().equals(internalName))) {
                ordered.add(entry.getValue());
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

    private static void addIfPresent(List<Language> ordered, String internalName) {
        Language lang = BY_INTERNAL_NAME.get(internalName);
        if (lang != null && !lang.internalName().equals("NONE")) {
            ordered.add(lang);
        }
    }

    private static Language byInternalNameOrNone(String internalName) {
        Language lang = BY_INTERNAL_NAME.get(internalName);
        return lang != null ? lang : NONE;
    }

    private static @Nullable String inferInternalName(Class<? extends Language> type) {
        return switch (type.getSimpleName()) {
            case "JavaLanguage" -> "JAVA";
            case "PythonLanguage" -> "PYTHON";
            case "CSharpLanguage" -> "C_SHARP";
            case "JavaScriptLanguage" -> "JAVASCRIPT";
            case "TypeScriptLanguage" -> "TYPESCRIPT";
            case "GoLanguage" -> "GO";
            case "RustLanguage" -> "RUST";
            case "PhpLanguage" -> "PHP";
            case "SqlLanguage" -> "SQL";
            case "ScalaLanguage" -> "SCALA";
            case "CppLanguage", "CppTreeSitterLanguage" -> "C_CPP";
            case "NoneLanguage" -> "NONE";
            default -> null;
        };
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
        Language byInternalName = BY_INTERNAL_NAME.get(name);
        if (byInternalName != null) {
            return byInternalName;
        }

        for (Language lang : ALL_LANGUAGES) {
            if (lang.name().equals(name) || lang.internalName().equals(name)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("No language constant " + Language.class.getCanonicalName() + "." + name);
    }

    private static final class LazyLanguage implements Language {
        private final String internalName;
        private final ServiceLoader.Provider<Language> provider;
        private volatile @Nullable Language delegate;

        private LazyLanguage(String internalName, ServiceLoader.Provider<Language> provider) {
            this.internalName = internalName;
            this.provider = provider;
        }

        private Language delegate() {
            Language d = delegate;
            if (d != null) {
                return d;
            }
            synchronized (this) {
                if (delegate == null) {
                    delegate = provider.get();
                }
                return delegate;
            }
        }

        @Override
        public Set<String> getExtensions() {
            return delegate().getExtensions();
        }

        @Override
        public String name() {
            return delegate().name();
        }

        @Override
        public String internalName() {
            return internalName;
        }

        @Override
        public IAnalyzer createAnalyzer(ai.brokk.project.IProject project, IAnalyzer.ProgressListener listener) {
            return delegate().createAnalyzer(project, listener);
        }

        @Override
        public IAnalyzer loadAnalyzer(ai.brokk.project.IProject project, IAnalyzer.ProgressListener listener) {
            return delegate().loadAnalyzer(project, listener);
        }
    }
}
