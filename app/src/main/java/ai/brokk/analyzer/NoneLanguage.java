package ai.brokk.analyzer;

import ai.brokk.project.IProject;
import java.util.Collections;
import java.util.Set;

public class NoneLanguage implements Language {
    private final Set<String> extensions = Collections.emptySet();

    NoneLanguage() {}

    @Override
    public Set<String> getExtensions() {
        return extensions;
    }

    @Override
    public String name() {
        return "None";
    }

    @Override
    public String internalName() {
        return "NONE";
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public IAnalyzer createAnalyzer(IProject project, IAnalyzer.ProgressListener listener) {
        return new DisabledAnalyzer(project);
    }

    @Override
    public IAnalyzer loadAnalyzer(IProject project, IAnalyzer.ProgressListener listener) {
        return createAnalyzer(project, listener);
    }
}
