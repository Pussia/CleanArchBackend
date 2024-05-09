package ru.pussia.endpoints.service.segregation;

import org.springframework.stereotype.Component;
import ru.pussia.endpoints.exception.OutOfBoundReadingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

@Component
public class FileLineReader implements LineReader {

    private RandomAccessFile reader;
    private File file;

    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Init random access file
     * @see RandomAccessFile
     */
    @Override
    public void open() throws FileNotFoundException {
        this.reader = new RandomAccessFile(file, "r");
    }

    /**
     * Returns true if p is bigger than actual file size otherwise false
     * @param  p pointer to check
     * @return is the end
     * @throws IOException unknown io exception while getting
     */
    @Override
    public boolean isEnd(int p) throws IOException {
        return p >= reader.length();
    }

    /**
     * Returns a read line from the file
     * @return read line
     * @see    RandomAccessFile
     */
    @Override
    public String readLine(int p) throws IOException, OutOfBoundReadingException {
        reader.seek(p);

        if (p >= reader.length()) {
            throw new OutOfBoundReadingException(
                    String.format(
                            "Pointer got out of bounds (pointer = %s, length = %s) in file on path %s",
                            p, reader.length(), file.getPath()
                    )
            );
        }

        return reader.readLine();
    }

    /**
     * Closes random access file
     * @see RandomAccessFile
     */
    @Override
    public void close() throws IOException {
        reader.close();
    }
}
