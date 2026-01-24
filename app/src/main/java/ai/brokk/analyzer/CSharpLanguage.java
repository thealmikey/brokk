package ai.brokk.analyzer;

import ai.brokk.project.IProject;
import java.util.Set;

public class CSharpLanguage implements Language {
    private final Set<String> extensions = Set.of("cs");

    public CSharpLanguage() {}

    @Override
    public Set<String> getExtensions() {
        return extensions;
    }

    @Override
    public String name() {
        return "C#";
    }

    @Override
    public String internalName() {
        return "C_SHARP";
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public IAnalyzer createAnalyzer(IProject project, IAnalyzer.ProgressListener listener) {
        return new CSharpAnalyzer(project, listener);
    }

    @Override
    public IAnalyzer loadAnalyzer(IProject project, IAnalyzer.ProgressListener listener) {
        var storage = getStoragePath(project);
        return TreeSitterStateIO.load(storage)
                .map(state -> (IAnalyzer) CSharpAnalyzer.fromState(project, state, listener))
                .orElseGet(() -> createAnalyzer(project, listener));
    }
}
