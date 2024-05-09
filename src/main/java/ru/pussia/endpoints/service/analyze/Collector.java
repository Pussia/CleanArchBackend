package ru.pussia.endpoints.service.analyze;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.pussia.endpoints.entity.details.FileDetails;
import ru.pussia.endpoints.entity.details.VariableDetails;
import ru.pussia.endpoints.entity.mainSequence.ComponentInformationModel;
import ru.pussia.endpoints.entity.result.MultipleInheritanceResultModel;
import ru.pussia.endpoints.entity.result.ResultModel;
import ru.pussia.endpoints.service.analyze.acyclicDependency.PyAcyclicDependencyService;
import ru.pussia.endpoints.service.analyze.globalDetecting.PyGlobalDetectingService;
import ru.pussia.endpoints.service.analyze.mainSequence.PyMainSequenceService;
import ru.pussia.endpoints.service.analyze.multipleInheritance.PyMultipleInheritanceService;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Collector {

    private final Logger logger = LoggerFactory.getLogger(Collector.class);

    private final PyAcyclicDependencyService pyAcyclicDependencyService;
    private final PyGlobalDetectingService pyGlobalDetectingService;
    private final PyMainSequenceService pyMainSequenceService;
    private final PyMultipleInheritanceService pyMultipleInheritanceService;

    public ResultModel collect(List<FileDetails> fileDetails) {
        logger.info("Start collection information");

        logger.info("Start searching for the cyclic dependencies");
        HashSet<HashSet<String>> adpFiles = pyAcyclicDependencyService.getADPFiles(fileDetails);
        logger.info(
                String.format("Stop searching for the cyclic dependencies (%s found)", adpFiles.size())
        );

        logger.info("Start searching for the global variables");
        List<VariableDetails> globals = pyGlobalDetectingService.getGlobals(fileDetails);
        logger.info(String.format("Stopped searching for the global variable (found %s)", globals.size()));

        logger.info("Start collecting components information");
        List<ComponentInformationModel> componentsInformation =
                pyMainSequenceService.getComponentsInformation(fileDetails);
        logger.info("Stop collecting components information");

        logger.info("Start searching for the multiple inheritance");
        List<MultipleInheritanceResultModel> multipleInheritance =
                pyMultipleInheritanceService.getMultipleInheritance(fileDetails);
        logger.info(
                String.format("Stop searching for the multiple inheritance (found %s)", multipleInheritance.size())
        );

        logger.info("Stop collection information");

        return new ResultModel(adpFiles, globals, componentsInformation, multipleInheritance);
    }
}
