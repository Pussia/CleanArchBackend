package ru.pussia.endpoints.service.analyze.acyclicDependency;

import org.springframework.stereotype.Service;
import ru.pussia.endpoints.entity.details.FileDetails;
import ru.pussia.endpoints.entity.details.ImportDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
public class PyAcyclicDependencyService implements AcyclicDependencyService {

    private final HashMap<FileDetails, Integer> indexedFiles = new HashMap<>();
    private final List<List<FileDetails>> cyclicDependencies = new ArrayList<>();
    private final HashSet<HashSet<String>> uniqueCyclicDependencies = new HashSet<>();

    public void dfs(
            FileDetails fileDetails,
            List<Integer> used,
            List<FileDetails> path
    ) {
        int idx = indexedFiles.get(fileDetails);

        used.set(idx, 1);
        path.add(fileDetails);

        for (ImportDetails importDetails : fileDetails.getImportDetails()) {
            for (FileDetails importedFile : importDetails.getImportedFiles()) {
                int innerIdx = indexedFiles.getOrDefault(importedFile, -1);

                if (innerIdx == -1) { // Import from library or framework
                    continue;
                }

                int state = used.get(innerIdx);

                if (state == 0) {
                    dfs(importedFile, used, path);
                } else if (state == 1) {
                    List<FileDetails> cycledPath = new ArrayList<>();
                    HashSet<FileDetails> cycledPathForUniqueCheck = new HashSet<>();

                    cycledPath.add(importedFile);
                    cycledPathForUniqueCheck.add(importedFile);

                    int i = path.size() - 1;
                    while (path.get(i) != importedFile) {
                        cycledPath.add(path.get(i));
                        cycledPathForUniqueCheck.add(path.get(i));
                        i--;
                    }

                    int sz = uniqueCyclicDependencies.size();
                    HashSet<String> paths = new HashSet<>();
                    for (FileDetails details : cycledPathForUniqueCheck) {
                        paths.add(details.getRelativePath());
                    }

                    uniqueCyclicDependencies.add(paths);

                    if (sz != uniqueCyclicDependencies.size()) {
                        cyclicDependencies.add(cycledPath);
                    }
                }
            }
        }

        used.set(idx, 0);
        path.remove(path.size() - 1);
    }

    @Override
    public HashSet<HashSet<String>> getADPFiles(List<FileDetails> fileDetails) {
        List<Integer> used = new ArrayList<>();
        List<FileDetails> par = new ArrayList<>();

        for (int i = 0; i < fileDetails.size(); i++) {
            used.add(0);
            par.add(new FileDetails());

            indexedFiles.put(fileDetails.get(i), i);
        }

        for (int i = 0; i < fileDetails.size(); i++) {
            if (used.get(i) == 0) {
                dfs(fileDetails.get(i), used, par);
            }
        }

        return uniqueCyclicDependencies;
    }
}
