package ai.brokk.analyzer;

import ai.brokk.project.IProject;
import java.util.Set;

public class CppTreeSitterLanguage implements Language {
    private final Set<String> extensions = Set.of("c", "cpp", "hpp", "cc", "hh", "cxx", "hxx", "c++", "h++", "h");

    CppTreeSitterLanguage() {}

    @Override
    public Set<String> getExtensions() {
        return extensions;
    }

    @Override
    public String name() {
        return "C++ (TreeSitter)";
    }

    @Override
    public String internalName() {
        return "CPP_TREESITTER";
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
