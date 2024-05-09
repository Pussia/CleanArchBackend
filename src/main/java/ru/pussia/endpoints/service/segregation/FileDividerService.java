package ru.pussia.endpoints.service.segregation;

import ru.pussia.endpoints.entity.general.DividedFile;
import ru.pussia.endpoints.exception.OutOfBoundReadingException;
import ru.pussia.endpoints.exception.UnknownKeywordException;

import java.io.IOException;

public interface FileDividerService {

    /**
     * Divides the file to the small parts and saves to the DividedFile class
     * @param  path path to the initial file
     * @param  offset length of absolute path - length of relative path
     * @return DividedFile with the all information
     * @see    DividedFile
     */
    DividedFile divide(String path, int offset) throws IOException, OutOfBoundReadingException, UnknownKeywordException;
}
