package ru.pussia.endpoints.service.segregation;

import ru.pussia.endpoints.entity.details.FileDetails;
import ru.pussia.endpoints.entity.general.DividedFile;
import ru.pussia.endpoints.exception.OutOfBoundReadingException;

import java.io.IOException;

public interface FileDetailsSegregateService {

    /**
     * Returns details about imports, functions, classes in the file
     * @param  dividedFile - divided file in file divider service
     * @return file details
     * @see FileDetails
     */
    FileDetails segregate(DividedFile dividedFile) throws IOException, OutOfBoundReadingException;
}
