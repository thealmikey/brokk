package ai.brokk.analyzer;

import ai.brokk.project.IProject;
import java.util.Set;

public class CppLanguage implements Language {
    private final Set<String> extensions = Set.of("c", "h", "cpp", "hpp", "cc", "hh", "cxx", "hxx");

    public CppLanguage() {}

    @Override
    public Set<String> getExtensions() {
        return extensions;
    }

    @Override
    public String name() {
        return "C/C++";
    }

    @Override
    public String internalName() {
        return "C_CPP";
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public IAnalyzer createAnalyzer(IProject project, IAnalyzer.ProgressListener listener) {
        return new CppAnalyzer(project, listener);
    }

    @Override
    public IAnalyzer loadAnalyzer(IProject project, IAnalyzer.ProgressListener listener) {
        var storage = getStoragePath(project);
        return TreeSitterStateIO.load(storage)
                .map(state -> (IAnalyzer) CppAnalyzer.fromState(project, state, listener))
                .orElseGet(() -> createAnalyzer(project, listener));
    }
}
