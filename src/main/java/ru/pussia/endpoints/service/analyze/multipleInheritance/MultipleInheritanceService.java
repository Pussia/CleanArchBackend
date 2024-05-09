package ru.pussia.endpoints.service.analyze.multipleInheritance;

import ru.pussia.endpoints.entity.details.ClassDetails;
import ru.pussia.endpoints.entity.details.FileDetails;
import ru.pussia.endpoints.entity.result.MultipleInheritanceResultModel;

import java.util.List;

/**
 * Service that finds classes with multiple inheritance.
 * Multiple inheritance is class, that inherits two or more classes
 */
public interface MultipleInheritanceService {

    /**
     * Returns a list with all the classes with multiple inheritance in the project
     * @param  fileDetails all the files in the project
     * @return list of the classes with multiple inheritance
     */
    List<MultipleInheritanceResultModel> getMultipleInheritance(List<FileDetails> fileDetails);
}
