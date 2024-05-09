package ru.pussia.endpoints.service.analyze.acyclicDependency;

import ru.pussia.endpoints.entity.details.FileDetails;

import java.util.HashSet;
import java.util.List;

/**
 * Service that checks the project violates acyclic dependency principle (ADP).
 * Violates ADP is when the first component depends on the second component
 * and the second component depends on the first component.
 */
public interface AcyclicDependencyService {

    /**
     * Returns list of cyclic dependencies if project violates ADP otherwise empty list
     * ADP violation can be checked by prove that graph of the dependencies in the project is a tree
     * @param  fileDetails details of all the files in the project
     * @return list of cyclic dependencies
     */
    HashSet<HashSet<String>> getADPFiles(List<FileDetails> fileDetails);
}