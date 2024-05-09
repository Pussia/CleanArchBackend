package ru.pussia.endpoints.service.segregation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.pussia.endpoints.entity.details.FileDetails;
import ru.pussia.endpoints.entity.details.ImportDetails;
import ru.pussia.endpoints.entity.general.DividedFile;
import ru.pussia.endpoints.exception.OutOfBoundReadingException;
import ru.pussia.endpoints.exception.UnknownKeywordException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Component
public class PyFileGetInformationFacade {

    private final Logger logger = LoggerFactory.getLogger(PyFileGetInformationFacade.class);
    private final FileDividerService fileDividerService = new PyFileDividerServiceImpl();
    private final FileDetailsSegregateService fileDetailsSegregateService =
            new PyFileDetailsSegregateServiceImpl();

    private StringBuilder collectFromPath(StringBuilder path, String from) {
        boolean pathStarted = false;
        for (int i = 0; i < from.length(); i++) {
            char ch = from.charAt(i);

            if (!pathStarted && ch == '.') continue;

            pathStarted = true;

            if (ch != '.') {
                path.append(ch);
            } else {
                path.append("/");
            }
        }

        return path;
    }


    private HashSet<FileDetails> findImportedFiles(
            List<FileDetails> fileDetails,
            StringBuilder path,
            HashSet<String> importedNames
    ) {
        HashSet<FileDetails> importedFiles = new HashSet<>();

        // 1. Path includes the file

        String file = path.append(".py").toString();

        for (FileDetails details : fileDetails) {
            if (details.getRelativePath().contains(file)) {
                importedFiles.add(details);

                return importedFiles;
            }
        }

        // 2. Imported file hide in the __init__.py file

        String initPy = path.append("/__init__.py").toString();

        for (FileDetails details : fileDetails) {
            if (details.getRelativePath().contains(initPy)) {
                if (importedNames.contains("*")) {
                    for (ImportDetails importDetails : details.getImportDetails()) {
                        String from = importDetails.getFromName();
                        String importPath = collectFromPath(path, from).append(".py").toString();

                        for (FileDetails importedDetails : fileDetails) {
                            if (importedDetails.getRelativePath().contains(importPath)) {
                                importedFiles.add(importedDetails);
                                break;
                            }
                        }
                    }

                    return importedFiles;
                }

                HashSet<String> tmp = importedNames;

                for (ImportDetails importDetails : details.getImportDetails()) {
                    int sz = tmp.size();
                    tmp.removeAll(importDetails.getImportedNames());

                    if (tmp.size() < sz) {
                        importedFiles.add(details);
                    }

                    if (tmp.size() == 0) {
                        return importedFiles;
                    }
                }

                return importedFiles;
            }
        }

        return importedFiles;
    }

    /**
     * Finds files that imported in another file
     * @param fileDetails all files' details
     */
    private void findImportedFile(List<FileDetails> fileDetails) {
        for (FileDetails details : fileDetails) {
            for (ImportDetails importDetails : details.getImportDetails()) {
                if (importDetails.getImportedNames().contains(details.getName())) continue;

                if (importDetails.getFromName() != null) {
                    String from = importDetails.getFromName();
                    StringBuilder path = collectFromPath(new StringBuilder("/"), from);

                    importDetails.setImportedFiles(
                            findImportedFiles(fileDetails, path, importDetails.getImportedNames())
                    );
                } else {
                    for (String importName : importDetails.getImportedNames()) {
                        for (FileDetails anotherDetails: fileDetails) {
                            boolean isFunctionImported = anotherDetails.getFunctionNames().contains(importName);
                            boolean isClassImported = anotherDetails.getClassNames().contains(importName);
                            boolean isFile = anotherDetails.getName().equals(importName);

                            if (isFunctionImported || isClassImported || isFile) {
                                importDetails.addImportedFile(anotherDetails);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns a list of the python files
     * @param  root the root of the project
     * @return python files
     */
    private List<File> findPythonFiles(File root) {
        logger.info("Searching for all the python files");

        // Traverse files' tree (bfs)
        List<File> pythonFiles = new ArrayList<>();
        Deque<File> queue = new ArrayDeque<>();
        queue.push(root);

        while (!queue.isEmpty()) {
            File curr = queue.peek();
            queue.poll();

            if (curr.listFiles() == null) {
                String name = curr.getName();

                StringBuilder extension = new StringBuilder();
                int i = name.length() - 1;
                while (name.length() - i <= 3) {
                    extension.append(name.charAt(i));
                    i--;
                }

                if (extension.toString().equals("yp.")) { // .py reversed
                    pythonFiles.add(curr);
                }

                continue;
            }

            for (File inner : Objects.requireNonNull(curr.listFiles())) {
                queue.push(inner);
            }
        }

        logger.info(String.format("All python files found (%s files)", pythonFiles.size()));

        return pythonFiles;
    }

    /**
     * Returns a list of divided files
     * @param  root the root of the project
     * @return divided files
     */
    private List<DividedFile> divideFiles(File root, int offset)
            throws OutOfBoundReadingException, IOException, UnknownKeywordException {
        logger.info("Dividing files started");

        List<DividedFile> dividedFiles = new ArrayList<>();

        List<File> pythonFiles = findPythonFiles(root);

        for (File pythonFile : pythonFiles) {
            dividedFiles.add(fileDividerService.divide(pythonFile.getPath(), offset));
        }

        logger.info(String.format("Files divided successfully (%s files)", dividedFiles.size()));

        return dividedFiles;
    }

    /**
     * Returns a list of file details
     * @param  dividedFiles all divided files
     * @return file details
     */
    private List<FileDetails> segregateFiles(List<DividedFile> dividedFiles)
            throws OutOfBoundReadingException, IOException {
        logger.info("Segregation files started");

        List<FileDetails> fileDetails = new ArrayList<>();

        for (DividedFile dividedFile : dividedFiles) {
            fileDetails.add(fileDetailsSegregateService.segregate(dividedFile));
        }

        logger.info(String.format("Files successfully segregated (%s files)", fileDetails.size()));

        return fileDetails;
    }

    /**
     * Returns an information about python file
     * @param  root the root of the project
     * @return list of file detail
     */
    public List<FileDetails> getInformation(File root, int offset)
            throws OutOfBoundReadingException, IOException, UnknownKeywordException {
        logger.info(String.format("Collection information started successfully on path %s", root.getPath()));

        List<DividedFile> dividedFiles = divideFiles(root, offset);
        List<FileDetails> fileDetails = segregateFiles(dividedFiles);

        findImportedFile(fileDetails);

        logger.info(
                String.format("Additional details about file on path %s collected successfully", root.getPath())
        );
        logger.info("Collection information has finished successfully");

        return fileDetails;
    }
}
