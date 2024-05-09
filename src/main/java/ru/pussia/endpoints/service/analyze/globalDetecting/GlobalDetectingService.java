package ru.pussia.endpoints.service.analyze.globalDetecting;

import ru.pussia.endpoints.entity.details.FileDetails;
import ru.pussia.endpoints.entity.details.VariableDetails;

import java.util.List;

/**
 * Service that finds global variables (globals).
 * Globals can be defined in the global scope and with the "global" keyword.
 */
public interface GlobalDetectingService {

    /**
     * Returns a list with all the global in the project
     * @param  fileDetails list of all the files
     * @return global variables
     */
    List<VariableDetails> getGlobals(List<FileDetails> fileDetails);
}
