package ai.brokk.analyzer;

import ai.brokk.project.IProject;
import java.nio.file.Path;
import java.util.Set;

public class PhpLanguage implements Language {
    private final Set<String> extensions = Set.of("php", "phtml", "php3", "php4", "php5", "phps");

    PhpLanguage() {}

    @Override
    public Set<String> getExtensions() {
        return extensions;
    }

    @Override
    public String name() {
        return "PHP";
    }

    @Override
    public String internalName() {
        return "PHP";
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public IAnalyzer createAnalyzer(IProject project, IAnalyzer.ProgressListener listener) {
        return new PhpAnalyzer(project, listener);
    }

    @Override
    public IAnalyzer loadAnalyzer(IProject project, IAnalyzer.ProgressListener listener) {
        var storage = getStoragePath(project);
        return TreeSitterStateIO.load(storage)
                .map(state -> (IAnalyzer) PhpAnalyzer.fromState(project, state, listener))
                .orElseGet(() -> createAnalyzer(project, listener));
    }

    @Override
    public boolean isAnalyzed(IProject project, Path pathToImport) {
        assert pathToImport.isAbsolute() : "Path must be absolute for isAnalyzed check: " + pathToImport;
        Path projectRoot = project.getRoot();
        Path normalizedPathToImport = pathToImport.normalize();

        if (!normalizedPathToImport.startsWith(projectRoot)) {
            return false;
        }

        Path vendorDir = projectRoot.resolve("vendor");
        return !normalizedPathToImport.startsWith(vendorDir);
    }
}
