package ai.brokk.analyzer;

import ai.brokk.gui.Chrome;
import ai.brokk.gui.dependencies.DependenciesPanel;
import ai.brokk.project.IProject;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class TypeScriptLanguage implements Language {
    private final Set<String> extensions = Set.of("ts", "tsx");

    public TypeScriptLanguage() {}

    @Override
    public Set<String> getExtensions() {
        return extensions;
    }

    @Override
    public String name() {
        return "Typescript";
    }

    @Override
    public String internalName() {
        return "TYPESCRIPT";
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public IAnalyzer createAnalyzer(IProject project, IAnalyzer.ProgressListener listener) {
        return new TypescriptAnalyzer(project, listener);
    }

    @Override
    public IAnalyzer loadAnalyzer(IProject project, IAnalyzer.ProgressListener listener) {
        var storage = getStoragePath(project);
        return TreeSitterStateIO.load(storage)
                .map(state -> (IAnalyzer) TypescriptAnalyzer.fromState(project, state, listener))
                .orElseGet(() -> createAnalyzer(project, listener));
    }

    @Override
    public List<Path> getDependencyCandidates(IProject project) {
        return NodeJsDependencyHelper.getDependencyCandidates(project);
    }

    @Override
    public List<Language.DependencyCandidate> listDependencyPackages(IProject project) {
        return NodeJsDependencyHelper.listDependencyPackages(project);
    }

    @Override
    public boolean importDependency(
            Chrome chrome,
            Language.DependencyCandidate pkg,
            @Nullable DependenciesPanel.DependencyLifecycleListener lifecycle) {
        return NodeJsDependencyHelper.importDependency(chrome, pkg, lifecycle);
    }

    @Override
    public Language.ImportSupport getDependencyImportSupport() {
        return Language.ImportSupport.FINE_GRAINED;
    }

    @Override
    public boolean isAnalyzed(IProject project, Path pathToImport) {
        return NodeJsDependencyHelper.isAnalyzed(project, pathToImport);
    }
}
