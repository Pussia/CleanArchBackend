package ru.pussia.endpoints.service.analyze.mainSequence;

import org.springframework.stereotype.Service;
import ru.pussia.endpoints.entity.details.ClassDetails;
import ru.pussia.endpoints.entity.details.FileDetails;
import ru.pussia.endpoints.entity.details.ImportDetails;
import ru.pussia.endpoints.entity.mainSequence.ComponentInformationModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
public class PyMainSequenceService implements MainSequenceService {

    private final HashMap<FileDetails, Integer> numberOfFunInForEachClass = new HashMap<>();

    /**
     * Finds a fan out for all the components in the project and saves it to the variable
     * @param  fileDetails all the component in the project
     */
    private void getFanInForEachClasses(List<FileDetails> fileDetails) {
        for (FileDetails file : fileDetails) {
            for (ImportDetails importDetails : file.getImportDetails()) {
                for (FileDetails imported : importDetails.getImportedFiles()) {
                    if (imported.getName().equals("__init__")) {
                        if (!(imported.getClassDetails().isEmpty() && imported.getFunctionDetails().isEmpty())) {
                            continue;
                        }
                    }

                    numberOfFunInForEachClass.put(
                            imported,
                            numberOfFunInForEachClass.getOrDefault(imported, 0) + 1
                    );
                }
            }
        }
    }

    /**
     * Returns a fan out for current component
     * @param  file component
     * @return fan out
     */
    private int getFanOut(FileDetails file) {
        int fanOut = 0;

        for (ImportDetails importDetails : file.getImportDetails()) {
            fanOut += importDetails.getImportedFiles().size() == 0 ? 1 : importDetails.getImportedFiles().size();
        }

        return fanOut;
    }

    /**
     * Returns a fan in for given component
     * @param  file component
     * @return fan in
     */
    private int getFanIn(FileDetails file) {
        return this.numberOfFunInForEachClass.getOrDefault(file, 0);
    }

    /**
     * Returns a number of the classes in the given component
     * @param  file component
     * @return number of the classes
     */
    private int getNumberOfClasses(FileDetails file) {
        return file.getClassDetails().size();
    }

    /**
     * Returns a number of the concrete classes in the given component
     * @param  file component
     * @return number of the concrete classes
     */
    private int getNumberOfAbstractClasses(FileDetails file) {
        int cnt = 0;

        for (ClassDetails classDetails : file.getClassDetails()) {
            if (classDetails.isAbstract()) {
                cnt++;
            }
        }

        return cnt;
    }

    /**
     * Returns an abstractness that calculated using the formula A = Na/Nc
     * @param  numberOfAbsClasses number of the abstract classes in the component (Na)
     * @param  numberOfClasses number of all the classes in the component (Nc)
     * @return A (abstractness)
     */
    private double calculateAbstractness(int numberOfAbsClasses, int numberOfClasses) {
        if (numberOfClasses == 0) {
            return 0;
        }

        return numberOfAbsClasses / (double) numberOfClasses;
    }

    /**
     * Returns an instability that calculated using the formula I = fanOut/(fanOut + fanIn)
     * @param  fanIn  number of imported components
     * @param  fanOut number of components that uses given component
     * @return I (instability)
     */
    private double calculateInstability(int fanIn, int fanOut) {
        if (fanOut + fanIn == 0) {
            return 0;
        }

        return fanOut / ((double) (fanOut + fanIn));
    }

    /**
     * Returns a distance from the given component to the main sequence using formula d = |A + I - 1|
     * @param  A given components' abstractness
     * @param  I given components' instability
     * @return d (distance)
     */
    private double calculateDistanceFromMainSequence(double A, double I) {
        return Math.abs(A + I - 1);
    }

    @Override
    public List<ComponentInformationModel> getComponentsInformation(List<FileDetails> fileDetails) {
        List<ComponentInformationModel> componentInformationModels = new ArrayList<>();
        getFanInForEachClasses(fileDetails);

        for (FileDetails file : fileDetails) {
            ComponentInformationModel componentInformationModel = new ComponentInformationModel(file.getRelativePath());

            int fanIn = getFanIn(file);
            int fanOut = getFanOut(file);
            int numberOfClasses = getNumberOfClasses(file);
            int numberOfAbsClasses = getNumberOfAbstractClasses(file);

            componentInformationModel.setFanIn(fanIn);
            componentInformationModel.setFanOut(fanOut);
            componentInformationModel.setNumberOfClasses(numberOfClasses);
            componentInformationModel.setNumberOfAbstractClasses(numberOfAbsClasses);
            componentInformationModel.setNumberOfConcreteClasses(numberOfClasses - numberOfAbsClasses);
            componentInformationModel.setAbstractness(calculateAbstractness(numberOfAbsClasses, numberOfClasses));
            componentInformationModel.setInstability(calculateInstability(fanIn, fanOut));
            componentInformationModel.setDistance(calculateDistanceFromMainSequence(
                    componentInformationModel.getAbstractness(), componentInformationModel.getInstability())
            );

            componentInformationModels.add(componentInformationModel);
        }

        return componentInformationModels;
    }
}
