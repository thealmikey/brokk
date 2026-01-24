package ai.brokk.analyzer;

import ai.brokk.project.IProject;
import java.util.Set;

public class SqlLanguage implements Language {
    private final Set<String> extensions = Set.of("sql");

    public SqlLanguage() {}

    @Override
    public Set<String> getExtensions() {
        return extensions;
    }

    @Override
    public String name() {
        return "SQL";
    }

    @Override
    public String internalName() {
        return "SQL";
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public IAnalyzer createAnalyzer(IProject project, IAnalyzer.ProgressListener listener) {
        return new SqlAnalyzer(project);
    }

    @Override
    public IAnalyzer loadAnalyzer(IProject project, IAnalyzer.ProgressListener listener) {
        return createAnalyzer(project, listener);
    }
}
