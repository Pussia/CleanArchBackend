package ru.pussia.endpoints.service.analyze.mainSequence;

import ru.pussia.endpoints.entity.details.FileDetails;
import ru.pussia.endpoints.entity.mainSequence.ComponentInformationModel;

import java.util.List;

/**
 * Service that finds component's information on main sequence graphic
 */
public interface MainSequenceService {

    /**
     * Returns a list with all the components' information in the project
     * @param  fileDetails all the files (components) in the project
     * @return components' information
     */
    List<ComponentInformationModel> getComponentsInformation(List<FileDetails> fileDetails);
}
