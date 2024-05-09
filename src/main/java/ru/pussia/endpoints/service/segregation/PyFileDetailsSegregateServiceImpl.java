package ru.pussia.endpoints.service.segregation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.pussia.endpoints.entity.details.*;
import ru.pussia.endpoints.entity.general.*;
import ru.pussia.endpoints.exception.OutOfBoundReadingException;
import ru.pussia.endpoints.utils.structures.Pair;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static ru.pussia.endpoints.service.segregation.PyFileDetailsSegregateServiceImpl.KeyLetter.BACK_SLASH;
import static ru.pussia.endpoints.service.segregation.PyFileDetailsSegregateServiceImpl.KeyLetter.CLOSE_BRACKET;
import static ru.pussia.endpoints.service.segregation.PyFileDetailsSegregateServiceImpl.KeyLetter.COLON;
import static ru.pussia.endpoints.service.segregation.PyFileDetailsSegregateServiceImpl.KeyLetter.COMMA;
import static ru.pussia.endpoints.service.segregation.PyFileDetailsSegregateServiceImpl.KeyLetter.COMMENT;
import static ru.pussia.endpoints.service.segregation.PyFileDetailsSegregateServiceImpl.KeyLetter.DOUBLE_QUOTE;
import static ru.pussia.endpoints.service.segregation.PyFileDetailsSegregateServiceImpl.KeyLetter.EQUAL;
import static ru.pussia.endpoints.service.segregation.PyFileDetailsSegregateServiceImpl.KeyLetter.OPEN_BRACKET;
import static ru.pussia.endpoints.service.segregation.PyFileDetailsSegregateServiceImpl.KeyLetter.SINGLE_QUOTE;
import static ru.pussia.endpoints.service.segregation.PyFileDetailsSegregateServiceImpl.KeyLetter.SPACE;
import static ru.pussia.endpoints.service.segregation.PyFileDetailsSegregateServiceImpl.KeyLetter.CLOSE_SQUARE_BRACKET;
import static ru.pussia.endpoints.service.segregation.PyFileDetailsSegregateServiceImpl.KeyLetter.OPEN_SQUARE_BRACKET;

/**
 * Service divides file into small parts
 * Works only with .py (python) files
 * Gives one public method: divide()
 * @author Pussia
 * @see FileDetailsSegregateService
 */
@Service
public class PyFileDetailsSegregateServiceImpl implements FileDetailsSegregateService {

    private static final Logger logger = LoggerFactory.getLogger(PyFileDetailsSegregateServiceImpl.class);

    private LineReader reader;

    public PyFileDetailsSegregateServiceImpl() {
    }

    enum Keywords {
        AS("as");

        private final String name;

        Keywords(String name) {
            this.name = name;
        }
    }

    enum KeyLetter {
        COMMA(','),
        SPACE(' '),
        OPEN_BRACKET('('),
        CLOSE_BRACKET(')'),
        OPEN_SQUARE_BRACKET('['),
        CLOSE_SQUARE_BRACKET(']'),
        COLON(':'),
        END_OF_LINE(' '),
        LEFT_CHEVRON('>'),
        EQUAL('='),
        COMMENT('#'),
        SINGLE_QUOTE('\''),
        DOUBLE_QUOTE('"'),
        BACK_SLASH('\\'),
        NOT_FOUND(' ');

        private final char name;

        KeyLetter(char name) {
            this.name = name;
        }
    }

    /**
     * Returns the component's name that imported to the file
     * @param  line line with from keyword
     * @return name of component
     */
    private String getFromName(String line) {
        StringBuilder string = new StringBuilder();

        char ch;
        int p = 0;
        while (p < line.length() && (ch=line.charAt(p)) != SPACE.name) {
            string.append(ch);

            p++;
        }

        return string.toString();
    }

    /**
     * Returns an import name
     * @param  line line with as keyword
     * @return import name
     */
    private String getImportedName(String line) {
        StringBuilder string = new StringBuilder();

        int p = 0;
        char ch;
        while (p < line.length() && (ch=line.charAt(p)) != COMMA.name && ch != SPACE.name) {
            string.append(ch);
            p++;
        }

        return string.toString();
    }

    /**
     * Helper method for the getImportNames
     * Returns an import's name
     * @param  line line with import keyword
     * @param  p pointer to the name of module or class
     * @return import's name
     */
    private String getImportedName(String line, int p) {
        StringBuilder string = new StringBuilder();

        char ch;
        while (p < line.length() && (ch=line.charAt(p)) != COMMA.name && ch != SPACE.name) {
            string.append(ch);
            p++;
        }

        return string.toString();
    }

    /**
     * Returns the function or class' names that imported to the file
     * @param  line line with import keyword
     * @return names
     */
    private HashSet<String> getImportedNames(String line) {
        HashSet<String> names = new HashSet<>();

        int p = 0;
        while (p < line.length()) {
            String name = getImportedName(line, p);

            p += name.length();

            if (name.equals(Keywords.AS.name)) {
                break;
            }

            names.add(name);

            char ch = 0;
            while (p < line.length() && ((ch=line.charAt(p)) == COMMA.name || ch == SPACE.name)) {
                p++;
            }

            if (ch == COMMENT.name) break;
        }

        return names;
    }

    /**
     * Returns an import details in the file
     * @param  pointer pointer to the end of the "from" or "import" keyword
     * @return import's details
     * @see    ImportPointer
     * @see    ImportDetails
     */
    private ImportDetails getImportDetails(ImportPointer pointer) throws OutOfBoundReadingException, IOException {
        ImportDetails importDetails = new ImportDetails();
        String line;

        if (pointer.getFromPointer() != -1) {
            int p = pointer.getFromPointer();

            line = reader.readLine(p);

            String from = getFromName(line.trim());
            importDetails.setFromName(from);
        }

        if (pointer.getAsPointer() != -1) {
            int p = pointer.getAsPointer();

            line = reader.readLine(p);

            String name = getImportedName(line.trim());
            importDetails.setImportName(name);
        }

        int p = pointer.getImportPointer();

        line = reader.readLine(p);
        HashSet<String> importedClasses = getImportedNames(line.trim());

        importDetails.setImportedNames(importedClasses);
        importDetails.setPointer(pointer);

        return importDetails;
    }

    /**
     * Returns a variable's name
     * @param  line line with the variable declaration
     * @return variable's name
     */
    private String getVariableName(String line) {
        StringBuilder name = new StringBuilder();
        int p = 0;
        char ch;

        while (p < line.length() && (ch=line.charAt(p)) != EQUAL.name) {
            if (ch != SPACE.name) {
                name.append(ch);
            }

            if (name.toString().equals("self.")) { // Class' variable
                name = new StringBuilder();
            }

            p++;
        }

        return name.toString();
    }

    /**
     * Returns a variable details in the file
     * @param  pointer pointer to the start of the variable
     * @return variable details
     * @see VariablePointer
     * @see VariableDetails
     */
    private VariableDetails getVariableDetails(String path, VariablePointer pointer)
            throws OutOfBoundReadingException, IOException {
        VariableDetails variableDetails = new VariableDetails();
        String line = reader.readLine(pointer.getVariablePointer());

        String name = getVariableName(line);
        variableDetails.setName(name);
        variableDetails.setGlobal(pointer.isGlobal());
        variableDetails.setFilePath(path);

        variableDetails.setPointer(pointer);

        return variableDetails;
    }

    /**
     * Returns a list of all parameters in the function
     * @param  pointer pointer to the open bracket
     * @return list of all parameters
     */
    private List<Pair<String, String>> getParameters(int pointer) throws OutOfBoundReadingException, IOException {
        String inlineParameters = inlineParameters(pointer);
        if (inlineParameters.isBlank()) {
            return List.of();
        }

        List<Pair<String, String>> parameters = new ArrayList<>();

        String[] parameterDetails = inlineParameters.split(",");
        for (String parameter : parameterDetails) {
            Pair<String, String> nameAndType = new Pair<>("", "");
            String[] nameAndTypeArr = parameter.split(":");
            nameAndType.setFirst(nameAndTypeArr[0].split("=")[0]);

            if (nameAndTypeArr.length > 1) {
                nameAndType.setSecond(nameAndTypeArr[1].split("=")[0]); // skip def value
            }

            parameters.add(nameAndType);
        }

        return parameters;
    }

    /**
     * Returns the parameter details in the function
     * @param  p pointer to the start of the parameters declarations
     * @return parameter's details
     * @see    ParameterDetails
     */
    private HashSet<ParameterDetails> getFunctionParameterDetails(int p)
            throws OutOfBoundReadingException, IOException {
        HashSet<ParameterDetails> parameterDetailsList = new HashSet<>();

        List<Pair<String, String>> parameters = getParameters(p);
        for (Pair<String, String> parameter : parameters) {
            ParameterDetails parameterDetails = new ParameterDetails();

            parameterDetails.setName(parameter.getFirst());
            parameterDetails.setType(parameter.getSecond());

            parameterDetailsList.add(parameterDetails);
        }

        return parameterDetailsList;
    }

    /**
     * Returns the function name
     * @param  line line with the function
     * @return function name
     */
    private String getFunctionName(String line) {
        StringBuilder string = new StringBuilder();

        char ch;
        int p = 0;
        while (p < line.length() && (ch=line.charAt(p)) != OPEN_BRACKET.name) {
            if (ch != SPACE.name) {
                string.append(ch);
            }

            p++;
        }

        return string.toString();
    }

    /**
     * Returns the function details in the file
     * @param  pointer pointer to the "def", parameters and "return" statements
     * @param  fileDetails the main class with all the details
     * @return function details
     * @see    FunctionDetails
     * @see    FunctionPointer
     * @see    ClassPointer
     */
    private FunctionDetails getFunctionDetails(FunctionPointer pointer, FileDetails fileDetails)
            throws OutOfBoundReadingException, IOException {
        FunctionDetails functionDetails = new FunctionDetails();

        String line;

        line = reader.readLine(pointer.getFunPointer());
        String name = getFunctionName(line);
        functionDetails.setFunName(name);

        HashSet<ParameterDetails> parameterDetails = getFunctionParameterDetails(pointer.getParamPointer());
        functionDetails.setParameterDetails(parameterDetails);

        for (VariablePointer variablePointer : pointer.getVariablePointers()) {
            VariableDetails details = getVariableDetails(fileDetails.getRelativePath(), variablePointer);

            functionDetails.addVariableDetails(details);
            if (details.isGlobal()) {
                fileDetails.addGlobalVariableDetails(details);
            }
        }

        functionDetails.setPointer(pointer);

        return functionDetails;
    }

    /**
     * Helper method to get more readable code
     * Returns true if three chars in row == quotes (triplet quotes) otherwise false
     * @param line      line with quote
     * @param pointer   pointer to the quote
     * @param quoteType quote type (single quote or double quote)
     * @return          has triplet quote
     */
    private boolean hasTripletQuote(String line, int pointer, char quoteType) {
        return line.charAt(pointer) == quoteType
                && line.charAt(pointer + 1) == quoteType
                && line.charAt(pointer + 2) == quoteType;
    }

    /**
     * Helper method to get rid of multiple repeated code snippets
     * @param line iterating line
     * @param p    pointer to the char in the line
     * @return     new pointer
     */
    private int next(String line, int p) {
        if (line.charAt(p) == BACK_SLASH.name) {
            p++;
        }

        return ++p;
    }

    /**
     * Returns a pointer that points out of quotes
     * @param  pointer pointer to the start of a line
     * @param  p       pointer to the char in the line
     * @param  ch      type of the quotes
     * @return pointer out of quotes
     */
    private int skipQuotes(int pointer, int p, char ch) throws OutOfBoundReadingException, IOException {
        String line = reader.readLine(pointer);

        if (p + 2 < line.length() && hasTripletQuote(line, p, ch)) {
            p += 2; // skip open quotes

            do {
                p = next(line, p);

                while (p + 2 >= line.length()) { // triplet quotes on the next line
                    pointer += p + 1;
                    line = reader.readLine(pointer);
                    p = 0;
                }
            } while (!hasTripletQuote(line, p, ch));

            p += 2;
        } else {
            do {
                p = next(line, p);
            } while (p < line.length() && line.charAt(p) != ch);
        }

        return p;
    }

    /**
     * Returns a pointer that points out of braces
     * @param  pointer pointer to the start of a line
     * @param  p       pointer to the char in the line
     * @param  open    type of the open braces
     * @param  close   type of the close braces
     * @return pointer out of braces
     */
    private int skipBraces(int pointer, int p, char open, char close) throws OutOfBoundReadingException, IOException {
        String line = reader.readLine(pointer);

        int openCnt = 1;
        while (openCnt > 0) {
            if (line.charAt(p) == close) {
                openCnt--;
            }

            if (line.charAt(p) == DOUBLE_QUOTE.name) {
                p = skipQuotes(pointer, p, DOUBLE_QUOTE.name);
                line = reader.readLine(pointer);
            }

            if (line.charAt(p) == SINGLE_QUOTE.name) {
                p = skipQuotes(pointer, p, SINGLE_QUOTE.name);
                line = reader.readLine(pointer);
            }

            if (openCnt == 0) break;

            p++;

            while (p >= line.length()) {
                pointer += p + 1;
                line = reader.readLine(pointer);
                p = 0;
            }

            if (line.charAt(p) == open) {
                openCnt++;
            }
        }

        return p;
    }

    /**
     * Returns a parameter line without spaces
     * @param  pointer pointer to the open bracket
     * @return parameter line
     */
    private String inlineParameters(int pointer) throws OutOfBoundReadingException, IOException {
        String line = reader.readLine(pointer);

        StringBuilder parameters = new StringBuilder();

        int p = -1;
        char ch = SPACE.name;

        while (ch != CLOSE_BRACKET.name) {
            if (ch != SPACE.name) {
                parameters.append(ch);
            }

            if (ch == DOUBLE_QUOTE.name) {
                p = skipQuotes(pointer, p, DOUBLE_QUOTE.name);
                line = reader.readLine(pointer);
            } else if (ch == SINGLE_QUOTE.name) {
                p = skipQuotes(pointer, p, SINGLE_QUOTE.name);
                line = reader.readLine(pointer);
            } else if (ch == OPEN_BRACKET.name) {
                p = skipBraces(pointer, p, OPEN_BRACKET.name, CLOSE_BRACKET.name);
                line = reader.readLine(pointer);
            } else if (ch == OPEN_SQUARE_BRACKET.name) {
                p = skipBraces(pointer, p, OPEN_SQUARE_BRACKET.name, CLOSE_SQUARE_BRACKET.name);
                line = reader.readLine(pointer);
            }

            p++;

            while (p >= line.length()) {
                pointer += p + 1;
                line = reader.readLine(pointer);
                p = 0;
            }

            ch = line.charAt(p);
        }

        return parameters.toString();
    }

    /**
     * Returns a class' name
     * @param  line line with the class declaration
     * @return name
     */
    private String getClassName(String line) {
        StringBuilder string = new StringBuilder();

        int p = 0;
        char ch;
        while (p < line.length() && ((ch=line.charAt(p)) != COLON.name && ch != OPEN_BRACKET.name)) {
            if (ch != SPACE.name) {
                string.append(ch);
            }

            p++;
        }

        return string.toString();
    }

    /**
     * Returns a list of all parent classes' names in the class
     * @param  pointer pointer to the open bracket
     * @return all names
     */
    private HashSet<String> getParentClassNames(int pointer) throws OutOfBoundReadingException, IOException {
        String inlineNames = inlineParameters(pointer);
        if (inlineNames.isBlank()) {
            return new HashSet<>();
        }

        HashSet<String> names = new HashSet<>();
        String[] allNames = inlineNames.split(",");

        for (String name : allNames) {
            if (!name.contains(String.valueOf(EQUAL.name))) {
                names.add(name);
            }
        }

        return names;
    }

    /**
     * Returns details about class' name, inherited classes' names and function details
     * declared in the class
     * @param  pointer class pointer
     * @param  fileDetails the main class with all details
     * @return class details
     * @see    ClassDetails
     * @see    ClassPointer
     * @see    FileDetails
     */
    private ClassDetails getClassDetails(ClassPointer pointer, FileDetails fileDetails)
            throws OutOfBoundReadingException, IOException {
        ClassDetails classDetails = new ClassDetails();

        String line = reader.readLine(pointer.getClassPointer());

        String name = getClassName(line);
        classDetails.setName(name);
        classDetails.setFilePath(fileDetails.getRelativePath());

        if (pointer.getParentPointer() != - 1) {
            String inlineParentClasses = inlineParameters(pointer.getParentPointer());
            if (!inlineParentClasses.isBlank()) {
                HashSet<String> parentClasses = getParentClassNames(pointer.getParentPointer());
                classDetails.setParentDetails(parentClasses);
            }
        }

        for (VariablePointer variablePointer : pointer.getVariablePointers()) {
            VariableDetails details = getVariableDetails(fileDetails.getRelativePath(), variablePointer);

            classDetails.addVariableDetails(details);
            if (details.isGlobal()) {
                fileDetails.addGlobalVariableDetails(details);
            }
        }

        for (FunctionPointer functionPointer : pointer.getFunPointers()) {
            FunctionDetails functionDetails = getFunctionDetails(functionPointer, fileDetails);

            classDetails.addFunctionDetails(functionDetails);
        }

        for (ClassPointer classPointer : pointer.getInnerClassPointers()) {
            ClassDetails innerClassDetails = getClassDetails(classPointer, fileDetails);

            classDetails.addInnerClassesDetails(innerClassDetails);
        }

        classDetails.setPointer(pointer);

        return classDetails;
    }

    /**
     * Returns an ABC class' name.
     * The name can be default (ABC)
     * or can be changed via "as" keyword in the import
     * @param  fileDetails file
     * @return ABC class' name
     */
    private String getABCClassName(FileDetails fileDetails) {
        String abcModuleName = "abc";
        String defaultABCClassName = "ABC";

        for (ImportDetails importDetails : fileDetails.getImportDetails()) {
            if (importDetails.getFromName() != null && importDetails.getFromName().equals(abcModuleName)) {
                String name = importDetails.getImportName();

                return name == null ? defaultABCClassName : name;
            }
        }

        return defaultABCClassName;
    }

    /**
     * Collects a details about the divided parts of the file into file details variable
     * @param fileDetails file details variable
     * @param dividedFile divided file
     */
    private void collectInfo(FileDetails fileDetails, DividedFile dividedFile)
            throws OutOfBoundReadingException, IOException {
        for (ImportPointer pointer : dividedFile.getImportPointers()) {
            ImportDetails importDetails = getImportDetails(pointer);

            fileDetails.addImportDetails(importDetails);
        }

        for (VariablePointer pointer : dividedFile.getVariablePointers()) {
            VariableDetails details = getVariableDetails(fileDetails.getRelativePath(), pointer);

            fileDetails.addGlobalVariableDetails(details);
        }

        for (FunctionPointer pointer : dividedFile.getFunPointers()) {
            FunctionDetails details = getFunctionDetails(pointer, fileDetails);

            fileDetails.addFunctionDetails(details);

            fileDetails.addFunctionName(details.getFunName());
        }

        for (ClassPointer pointer : dividedFile.getClassPointers()) {
            ClassDetails classDetails = getClassDetails(pointer, fileDetails);

            fileDetails.addClassDetails(classDetails);

            fileDetails.addClassName(classDetails.getName());
        }

        String ABCClassName = getABCClassName(fileDetails);
        for (ClassDetails classDetails : fileDetails.getClassDetails()) {
            classDetails.setAbstract(false);

            if (classDetails.getParentDetails().contains(ABCClassName)) {
                classDetails.setAbstract(true);
            }
        }

        fileDetails.setPointer(dividedFile);
    }

    /**
     * Returns details about imports, functions, classes in the file
     * @param  dividedFile - divided file in file divider service
     * @return file details
     * @see FileDetails
     * @see FileDividerService
     */
    @Override
    public FileDetails segregate(DividedFile dividedFile) throws IOException, OutOfBoundReadingException {
        String path = dividedFile.getAbsolutePath();

        logger.info(
                String.format("Python file segmentation service with path %s started successfully", path)
        );

        reader = new FileLineReader();
        reader.setFile(Paths.get(path).toFile());
        reader.open();

        FileDetails fileDetails = new FileDetails();
        fileDetails.setAbsolutePath(path);
        fileDetails.setRelativePath(dividedFile.getRelativePath());
        fileDetails.setName(dividedFile.getFileName());
        collectInfo(fileDetails, dividedFile);

        reader.close();

        logger.info(
                String.format("Python file segmentation service with path %s stopped successfully", path)
        );

        return fileDetails;
    }
}
