package ru.pussia.endpoints.service.segregation;

import ru.pussia.endpoints.exception.OutOfBoundReadingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface LineReader {

    void setFile(File file);

    /**
     * Opens working stream
     */
    void open() throws FileNotFoundException;

    boolean isEnd(int p) throws IOException;

    /**
     * Returns a line from some resource (file or string) that starts in the "p" position
     * @param  p pointer to the start position
     * @return line
     */
    String readLine(int p) throws IOException, OutOfBoundReadingException;

    /**
     * Closes working stream
     */
    void close() throws IOException;
}
