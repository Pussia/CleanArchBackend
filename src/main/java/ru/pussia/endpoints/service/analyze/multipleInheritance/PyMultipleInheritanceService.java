package ru.pussia.endpoints.service.analyze.multipleInheritance;

import org.springframework.stereotype.Service;
import ru.pussia.endpoints.entity.details.ClassDetails;
import ru.pussia.endpoints.entity.details.FileDetails;
import ru.pussia.endpoints.entity.result.MultipleInheritanceResultModel;

import java.util.ArrayList;
import java.util.List;

@Service
public class PyMultipleInheritanceService implements MultipleInheritanceService {

    @Override
    public List<MultipleInheritanceResultModel> getMultipleInheritance(List<FileDetails> fileDetails) {
        List<MultipleInheritanceResultModel> multipleInheritance = new ArrayList<>();

        for (FileDetails file : fileDetails) {
            for (ClassDetails classDetails : file.getClassDetails()) {
                int numberOfParents = classDetails.getParentDetails().size();

                if (classDetails.isAbstract()) { // Remove if ABC is parent too
                    numberOfParents--;
                }

                if (numberOfParents > 1) {
                    multipleInheritance.add(
                            new MultipleInheritanceResultModel(classDetails.getName(), classDetails.getFilePath())
                    );
                }
            }
        }

        return multipleInheritance;
    }
}
