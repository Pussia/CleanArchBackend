package ru.pussia.endpoints.service.analyze.globalDetecting;

import org.springframework.stereotype.Service;
import ru.pussia.endpoints.entity.details.FileDetails;
import ru.pussia.endpoints.entity.details.VariableDetails;

import java.util.ArrayList;
import java.util.List;

@Service
public class PyGlobalDetectingService implements GlobalDetectingService {

    @Override
    public List<VariableDetails> getGlobals(List<FileDetails> fileDetails) {
        List<VariableDetails> globals = new ArrayList<>();

        for (FileDetails file : fileDetails) {
            globals.addAll(file.getGlobalVariableDetails());
        }

        return globals;
    }
}
