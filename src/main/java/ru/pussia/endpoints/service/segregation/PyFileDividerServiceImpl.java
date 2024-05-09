package ru.pussia.endpoints.service.segregation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.pussia.endpoints.entity.general.*;
import ru.pussia.endpoints.exception.OutOfBoundReadingException;
import ru.pussia.endpoints.exception.UnknownKeywordException;
import ru.pussia.endpoints.utils.structures.HierarchyDeque;
import ru.pussia.endpoints.utils.structures.Pair;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Service that divides file into small parts and saves information to the DividedFile class
 * Works only with .py (python) files
 * Gives one public method: divide()
 * @author Pussia
 * @see FileDividerService
 */

@Service
public class PyFileDividerServiceImpl implements FileDividerService {

    private static final Logger logger = LoggerFactory.getLogger(PyFileDividerServiceImpl.class);

    private LineReader reader;
    private final HierarchyDeque<ClassPointer> classHierarchy;
    private final HierarchyDeque<FunctionPointer> funHierarchy;
    private final Utils utils;

    private int p;
    private Pair<Utils.SearchableItems, Integer> foundUnnecessaryPart;

    public PyFileDividerServiceImpl() {
        this.classHierarchy = new HierarchyDeque<>();
        this.funHierarchy = new HierarchyDeque<>();
        this.utils = new Utils();
    }

    /**
     * Class with utility functions that helps divide file
     */
    private class Utils {

        public enum SpecialChars {
            COMMENT("#"),
            SPACE(" "),
            TAB("\t"),
            DOUBLE_QUOTES("\""),
            SINGLE_QUOTES("'"),
            BACK_SLASH("\\"),
            EXCEPT_NOTHING_OR_SPACE_OR_TAB("\u0000"),
            EXCEPT_SPACE_OR_TAB("\u0001"),
            NOTHING("\u0002");

            private final String name;

            SpecialChars(String name) {
                this.name = name;
            }
        }

        public enum SearchableItems {
            OPEN_BRACKET("(", new HashSet<>(), new HashSet<>()),
            CLOSE_BRACKET(")", new HashSet<>(), new HashSet<>()),
            ANY_QUOTE("\"", new HashSet<>(), new HashSet<>()),

            VARIABLE_EQUAL(
                    "=",
                    new HashSet<>(
                            Set.of("+", "-", "*", "/", "%", ">", "<", "!", "=")
                    ),
                    new HashSet<>(
                            Set.of("=")
                    )
            ),
            IMPORT(
                    "import",
                    new HashSet<>(Set.of(SpecialChars.EXCEPT_NOTHING_OR_SPACE_OR_TAB.name)),
                    new HashSet<>(Set.of(SpecialChars.EXCEPT_SPACE_OR_TAB.name))
            ),
            FROM(
                    "from",
                    new HashSet<>(Set.of(SpecialChars.EXCEPT_NOTHING_OR_SPACE_OR_TAB.name)),
                    new HashSet<>(Set.of(SpecialChars.EXCEPT_SPACE_OR_TAB.name))
            ),
            AS(
                    "as",
                    new HashSet<>(Set.of(SpecialChars.EXCEPT_NOTHING_OR_SPACE_OR_TAB.name)),
                    new HashSet<>(Set.of(SpecialChars.EXCEPT_SPACE_OR_TAB.name))
            ),
            CLASS(
                    "class",
                    new HashSet<>(Set.of(SpecialChars.EXCEPT_NOTHING_OR_SPACE_OR_TAB.name)),
                    new HashSet<>(Set.of(SpecialChars.EXCEPT_SPACE_OR_TAB.name))
            ),
            FUNCTION(
                    "def",
                    new HashSet<>(Set.of(SpecialChars.EXCEPT_NOTHING_OR_SPACE_OR_TAB.name)),
                    new HashSet<>(Set.of(SpecialChars.EXCEPT_SPACE_OR_TAB.name))
            ),
            GLOBAL(
                    "global",
                    new HashSet<>(Set.of(SpecialChars.EXCEPT_NOTHING_OR_SPACE_OR_TAB.name)),
                    new HashSet<>(Set.of(SpecialChars.EXCEPT_SPACE_OR_TAB.name))
            ),
            SELF(
                    "self.",
                    new HashSet<>(Set.of(SpecialChars.EXCEPT_NOTHING_OR_SPACE_OR_TAB.name)),
                    new HashSet<>(Set.of(SpecialChars.SPACE.name))
            ),
            NONE("", new HashSet<>(), new HashSet<>()),
            END("", new HashSet<>(), new HashSet<>());

            private final String name;
            private final HashSet<String> prefixExceptions;
            private final HashSet<String> suffixExceptions;
            SearchableItems(String name, HashSet<String> prefixExceptions, HashSet<String> suffixExceptions) {
                this.name = name;
                this.prefixExceptions = prefixExceptions;
                this.suffixExceptions = suffixExceptions;
            }
            
            public static SearchableItems findByName(String name, String suffix) {
                for (SearchableItems value : SearchableItems.values()) {
                    if (!name.endsWith(value.name)) {
                        continue;
                    }

                    int prefixIdx = name.length() - (value.name.length() + 1);
                    String prefix = SpecialChars.NOTHING.name;
                    if (prefixIdx >= 0) {
                        prefix = String.valueOf(name.charAt(prefixIdx));
                    }

                    if (value.prefixExceptions.contains(SpecialChars.EXCEPT_NOTHING_OR_SPACE_OR_TAB.name)) {
                        if (!(prefix.equals(SpecialChars.NOTHING.name) ||
                                prefix.equals(SpecialChars.SPACE.name) ||
                                prefix.equals(SpecialChars.TAB.name))) {
                            return SearchableItems.NONE;
                        }
                    }

                    if (value.suffixExceptions.contains(SpecialChars.EXCEPT_SPACE_OR_TAB.name)) {
                        if (!(suffix.equals(SpecialChars.SPACE.name) ||
                                suffix.equals(SpecialChars.TAB.name))) {
                            return SearchableItems.NONE;
                        }
                    }

                    if (value.prefixExceptions.contains(prefix) || value.suffixExceptions.contains(suffix)) {
                        return SearchableItems.NONE;
                    }

                    return value;
                }

                return SearchableItems.NONE;
            }
        }

        /**
         * Returns a quantity of the spaces in the start of the line
         * @param  line initial line
         * @return quantity if the spaces
         */
        private int countSpaces(String line) {
            int cnt = 0;
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                    break;
                }

                cnt++;
            }

            return cnt;
        }

        /**
         * Returns a hashmap with found keyword in the line
         * @param line     initial line
         * @param keywords keywords to find
         * @return         hashmap with found keywords
         */
        private HashMap<SearchableItems, Integer> searchKeyWordsInLine(
                String line, HashSet<SearchableItems> keywords
        ) throws OutOfBoundReadingException, IOException {
            HashMap<SearchableItems, Integer> res = new HashMap<>();
            StringBuilder expression = new StringBuilder();

            for (int i = 0; i < line.length(); i++) {
                if (!keywords.contains(SearchableItems.OPEN_BRACKET)) {
                    int bracesEnd = skipBraces(line, i, true); // Skip braces if pointer already inside the braces expression

                    if (bracesEnd == -1) { // the whole line has been traversed
                        res.put(SearchableItems.OPEN_BRACKET, i);
                        break;
                    }

                    i = bracesEnd;
                }

                int quotesEnd = skipQuotes(line, i, true);

                line = reader.readLine(p);

                if (quotesEnd >= line.length()) { // the whole line has been traversed
                    res.put(SearchableItems.ANY_QUOTE, i);
                    break;
                }

                i = quotesEnd;

                String ch = String.valueOf(line.charAt(i));

                if (ch.equals(SpecialChars.COMMENT.name)) { // Skip comments
                    break;
                }

                expression.append(ch);

                String next = SpecialChars.NOTHING.name;
                if (i + 1 < line.length()) {
                    next = String.valueOf(line.charAt(i + 1));
                }

                SearchableItems keyword = SearchableItems.findByName(expression.toString(), next);
                
                if (keywords.contains(keyword)) {
                    res.put(keyword, p + i + 1);
                }
            }

            return res;
        }

        /**
         * Returns the file's name by the path
         * @param  path file's absolute path
         * @return file's name
         */
        public String getFileNameByPath(String path) {
            int size = path.length() - 1;
            StringBuilder fileName = new StringBuilder();
            for (int i = size - 3; i >= 0; i--) {
                char ch = path.charAt(i);

                if (ch == '\\' || ch == '/') break;

                fileName.append(ch);
            }

            return fileName.reverse().toString();
        }
    }

    /**
     * Helper method to get more readable code
     * Returns true if character at the pointer in line == char otherwise false
     * @param line     line with char
     * @param pointer  pointer to the char
     * @param charType char
     * @return         has char
     */
    private boolean hasChar(String line, int pointer, Utils.SpecialChars charType) {
        return String.valueOf(line.charAt(pointer)).equals(charType.name);
    }

    /**
     * Helper method to get more readable code
     * Returns true if three chars in row == quotes (triplet quotes) otherwise false
     * @param line      line with quote
     * @param pointer   pointer to the quote
     * @param quoteType quote type (single quote or double quote)
     * @return          has triplet quote
     */
    private boolean hasTripletQuote(String line, int pointer, Utils.SpecialChars quoteType) {
        return  hasChar(line, pointer, quoteType)
                && hasChar(line, pointer + 1, quoteType)
                && hasChar(line, pointer + 2, quoteType);
    }

    /**
     * Skips quotes on a line
     * @param  pointer pointer to the char in the line
     * @param  line    line with quotes
     * @return         is whole line traversed, pointer to the end of the quotes
     */
    private int skipQuotesOnLine(int pointer, String line) {
        if (pointer + 2 < line.length() && hasTripletQuote(line, pointer, Utils.SpecialChars.DOUBLE_QUOTES)) {
            pointer += 2; // skip open quotes

            do {
                if (hasChar(line, pointer, Utils.SpecialChars.BACK_SLASH)) {
                    pointer++; // skip backslash
                }

                pointer++;

                if (pointer + 2 >= line.length()) { // triplet quotes on the next line
                    break;
                }
            } while (!hasTripletQuote(line, pointer, Utils.SpecialChars.DOUBLE_QUOTES));

            pointer += 3;
        } else if (hasChar(line, pointer, Utils.SpecialChars.DOUBLE_QUOTES)) {
            do {
                if (hasChar(line, pointer, Utils.SpecialChars.BACK_SLASH)) {
                    pointer++; // skip backslash
                }

                pointer++;
            } while (pointer < line.length() && !hasChar(line, pointer, Utils.SpecialChars.DOUBLE_QUOTES));

            pointer++;
        } else if (pointer + 2 < line.length() && hasTripletQuote(line, pointer, Utils.SpecialChars.SINGLE_QUOTES)) {
            pointer += 2; // skip open quotes

            do {
                if (hasChar(line, pointer, Utils.SpecialChars.BACK_SLASH)) {
                    pointer++; // skip backslash
                }

                pointer++;

                if (pointer + 2 >= line.length()) { // triplet quotes on the next line
                    break;
                }
            } while (!hasTripletQuote(line, pointer, Utils.SpecialChars.SINGLE_QUOTES));

            pointer += 3;
        } else if (hasChar(line, pointer, Utils.SpecialChars.SINGLE_QUOTES)) {
            do {
                if (hasChar(line, pointer, Utils.SpecialChars.BACK_SLASH)) {
                    pointer++; // skip backslash
                }

                pointer++;
            } while (pointer < line.length() && !hasChar(line, pointer, Utils.SpecialChars.SINGLE_QUOTES));

            pointer++;
        }

        return pointer;
    }

    /**
     * Skips quotes on all the lines
     * @param  pointer pointer to the char in the line
     * @param  line    line with quotes
     * @return         is whole line traversed, pointer to the end of the quotes
     */
    private int skipQuotesOnAllLines(int pointer, String line) throws OutOfBoundReadingException, IOException {
        while (pointer < line.length()) {
            if (pointer + 2 < line.length() && hasTripletQuote(line, pointer, Utils.SpecialChars.DOUBLE_QUOTES)) {
                pointer += 2; // skip open quotes

                do {
                    if (hasChar(line, pointer, Utils.SpecialChars.BACK_SLASH)) {
                        pointer++; // skip backslash
                    }

                    pointer++;

                    while (pointer + 2 >= line.length()) { // triplet quotes on the next line
                        p += line.length() + 1;
                        line = reader.readLine(p);
                        pointer = 0;
                    }
                } while (!hasTripletQuote(line, pointer, Utils.SpecialChars.DOUBLE_QUOTES));

                pointer += 3;
            } else if (hasChar(line, pointer, Utils.SpecialChars.DOUBLE_QUOTES)) {
                do {
                    if (hasChar(line, pointer, Utils.SpecialChars.BACK_SLASH)) {
                        pointer++; // skip backslash
                    }

                    pointer++;
                } while (pointer < line.length() && !hasChar(line, pointer, Utils.SpecialChars.DOUBLE_QUOTES));

                pointer++;
            } else if (pointer + 2 < line.length() && hasTripletQuote(line, pointer, Utils.SpecialChars.SINGLE_QUOTES)) {
                pointer += 2; // skip open quotes

                do {
                    if (hasChar(line, pointer, Utils.SpecialChars.BACK_SLASH)) {
                        pointer++; // skip backslash
                    }

                    pointer++;

                    while (pointer + 2 >= line.length()) { // triplet quotes on the next line
                        p += line.length() + 1;
                        line = reader.readLine(p);
                        pointer = 0;
                    }
                } while (!hasTripletQuote(line, pointer, Utils.SpecialChars.SINGLE_QUOTES));

                pointer += 3;
            } else if (hasChar(line, pointer, Utils.SpecialChars.SINGLE_QUOTES)) {
                do {
                    if (hasChar(line, pointer, Utils.SpecialChars.BACK_SLASH)) {
                        pointer++; // skip backslash
                    }

                    pointer++;
                } while (pointer < line.length() && !hasChar(line, pointer, Utils.SpecialChars.SINGLE_QUOTES));

                pointer++;
            }

            pointer++;
        }

        p += line.length() - 1;

        return pointer;
    }

    /**
     * Skips all types of quotes in python and return pointer to the next character after the close quote
     * @param  line    line to check on quotes
     * @param  pointer pointer to the character
     * @return pointer to the end of the quotes
     */
    private int skipQuotes(String line, int pointer, boolean breakOnNewLine)
            throws OutOfBoundReadingException, IOException {
        if (pointer > 0 && hasChar(line, pointer - 1, Utils.SpecialChars.BACK_SLASH)) {
            return pointer;
        }

        if (breakOnNewLine) {
            return skipQuotesOnLine(pointer, line);
        }

        return skipQuotesOnAllLines(pointer, line);
    }

    /**
     * Skips the content inside the braces
     * @param line    line with open brace
     * @param pointer pointer to the open brace
     * @return        pointer to the next char after the close brace
     */
    private int skipBraces(String line, int pointer, boolean breakOnNewLine)
            throws OutOfBoundReadingException, IOException {
        boolean isWholeLineTraversed = false;

        if (pointer < line.length() &&
                String.valueOf(line.charAt(pointer)).equals(Utils.SearchableItems.OPEN_BRACKET.name)) {
            int openCnt = 1;

            while (openCnt > 0) {
                if (String.valueOf(line.charAt(pointer)).equals(Utils.SearchableItems.CLOSE_BRACKET.name)) {
                    openCnt--;
                }

                if (String.valueOf(line.charAt(pointer)).equals(Utils.SpecialChars.DOUBLE_QUOTES.name) ||
                        String.valueOf(line.charAt(pointer)).equals(Utils.SpecialChars.SINGLE_QUOTES.name)) {
                    int tmp = skipQuotes(line, pointer, true);

                    if (tmp >= line.length()) {
                        pointer = skipQuotes(line, pointer, false);
                    } else {
                        pointer = tmp;
                    }

                    line = reader.readLine(p);

                    if (pointer > 0) {
                        pointer--;
                    }

                    if (pointer >= line.length()) {
                        line = reader.readLine(p + 1);
                    }
                }

                if (openCnt == 0) break;

                pointer++;

                if (breakOnNewLine && pointer >= line.length()) {
                    isWholeLineTraversed = true;

                    break;
                }

                while (pointer >= line.length()) {
                    p += line.length() + 1;
                    line = reader.readLine(p);
                    pointer = 0;
                }

                if (String.valueOf(line.charAt(pointer)).equals(Utils.SearchableItems.OPEN_BRACKET.name)) {
                    openCnt++;
                }
            }
        }

        if (isWholeLineTraversed || pointer >= line.length()) {
            if (pointer >= line.length() && !breakOnNewLine) {
                p += line.length() - 1;
            }

            return -1;
        }

        return pointer;
    }

    /**
     * Shifting pointer to the non-empty string or to the end of the file
     */
    private void shiftToOperableLine() throws IOException, OutOfBoundReadingException {
        if (reader.isEnd(p)) {
            return;
        }

        String line = reader.readLine(p);
        p += line.length() + 1;
        while (!reader.isEnd(p) && (line=reader.readLine(p)) != null && line.trim().equals("")) {
            p += line.length() + 1;
        }
    }

    /**
     * Collects information about the import that starts with the "import" keyword
     * pointer shifts to the new line after the end of the import
     * @return the ImportPointer with all needed information
     * @see    ImportPointer
     */
    private ImportPointer traverseImport() throws OutOfBoundReadingException, IOException {
        ImportPointer importPointer = new ImportPointer();
        importPointer.setFromPointer(-1);

        String line = reader.readLine(p);
        HashSet<Utils.SearchableItems> keywords = new HashSet<>();
        keywords.add(Utils.SearchableItems.IMPORT);
        keywords.add(Utils.SearchableItems.AS);

        HashMap<Utils.SearchableItems, Integer> foundKeywords =
                utils.searchKeyWordsInLine(line, keywords);

        importPointer.setImportPointer(foundKeywords.get(Utils.SearchableItems.IMPORT));
        importPointer.setAsPointer(foundKeywords.getOrDefault(Utils.SearchableItems.AS, -1));

        return importPointer;
    }

    /**
     * Collects information about the import that starts with the "from" keyword
     * pointer shifts to the new line after the end of the import
     * @return the ImportPointer with all needed information
     * @see    ImportPointer
     */
    private ImportPointer traverseFrom() throws OutOfBoundReadingException, IOException {
        ImportPointer pointer = new ImportPointer();

        String line = reader.readLine(p);

        HashSet<Utils.SearchableItems> keywords = new HashSet<>();
        keywords.add(Utils.SearchableItems.IMPORT);
        keywords.add(Utils.SearchableItems.FROM);
        keywords.add(Utils.SearchableItems.AS);

        HashMap<Utils.SearchableItems, Integer> foundKeywords =
                utils.searchKeyWordsInLine(line, keywords);

        pointer.setFromPointer(foundKeywords.get(Utils.SearchableItems.FROM));
        pointer.setImportPointer(foundKeywords.get(Utils.SearchableItems.IMPORT));
        pointer.setAsPointer(foundKeywords.getOrDefault(Utils.SearchableItems.AS, -1));

        return pointer;
    }

    /**
     * Collects information about the function that starts form the pointer.
     * pointer shifts to the new line after the end of the function
     * @return the FunctionPointer with all needed information started from the p
     * @see    FunctionPointer
     */
    private FunctionPointer traverseFunction() throws OutOfBoundReadingException, IOException {
        FunctionPointer pointer = new FunctionPointer();

        // Searching for "def" and "(" in the function declaration
        String line = reader.readLine(p);

        HashSet<Utils.SearchableItems> funDeclrKeywords = new HashSet<>();
        funDeclrKeywords.add(Utils.SearchableItems.FUNCTION);
        funDeclrKeywords.add(Utils.SearchableItems.OPEN_BRACKET);

        HashMap<Utils.SearchableItems, Integer> funDeclrFoundKeywords =
                utils.searchKeyWordsInLine(line, funDeclrKeywords);

        pointer.setFunPointer(funDeclrFoundKeywords.get(Utils.SearchableItems.FUNCTION));
        pointer.setParamPointer(funDeclrFoundKeywords.get(Utils.SearchableItems.OPEN_BRACKET));

        return pointer;
    }

    /**
     * Collects information about the class that starts from the pointer p.
     * pointer shifts to the new line after the end of the class
     * @return the ClassPointer with all needed information started from the p
     * @see    ClassPointer
     */
    private ClassPointer traverseClass() throws OutOfBoundReadingException, IOException {
        ClassPointer pointer = new ClassPointer();
        pointer.setParentPointer(-1); // If class is basic

        // Searching for "class" and "(" in the function declaration
        String line = reader.readLine(p);

        HashSet<Utils.SearchableItems> classDeclrKeywords = new HashSet<>();
        classDeclrKeywords.add(Utils.SearchableItems.CLASS);
        classDeclrKeywords.add(Utils.SearchableItems.OPEN_BRACKET);

        HashMap<Utils.SearchableItems, Integer> classDeclrFoundKeywords =
                utils.searchKeyWordsInLine(line, classDeclrKeywords);

        pointer.setClassPointer(classDeclrFoundKeywords.get(Utils.SearchableItems.CLASS));
        if (classDeclrFoundKeywords.containsKey(Utils.SearchableItems.OPEN_BRACKET)) {
            pointer.setParentPointer(classDeclrFoundKeywords.get(Utils.SearchableItems.OPEN_BRACKET));
        }

        return pointer;
    }

    /**
     * Collects the information about the variable that starts from the pointer p.
     * pointer shifts to the new line after the end of the variable declaration
     * @return pair of variable pointer and is variable belongs to a class
     */

    private Pair<VariablePointer, Boolean> traverseVariable() throws OutOfBoundReadingException, IOException {
        VariablePointer variablePointer = new VariablePointer();
        variablePointer.setVariablePointer(p);
        variablePointer.setGlobal(false);
        String line = reader.readLine(p);

        HashSet<Utils.SearchableItems> variableDeclrKeywords = new HashSet<>();
        variableDeclrKeywords.add(Utils.SearchableItems.GLOBAL);
        variableDeclrKeywords.add(Utils.SearchableItems.SELF);

        HashMap<Utils.SearchableItems, Integer> variableDeclrFoundKeywords =
                utils.searchKeyWordsInLine(line, variableDeclrKeywords);

        if (variableDeclrFoundKeywords.containsKey(Utils.SearchableItems.GLOBAL)) {
            variablePointer.setVariablePointer(variableDeclrFoundKeywords.get(Utils.SearchableItems.GLOBAL));
            variablePointer.setGlobal(true);
        }

        if (utils.countSpaces(line) == 0) {
            variablePointer.setGlobal(true);
        }

        return new Pair<>(variablePointer, variableDeclrFoundKeywords.containsKey(Utils.SearchableItems.SELF));
    }

    /**
     * Shifting p until reach import, class or function declaration
     * "p" field shifts to the start of shifted line
     * @return the name of expression shifted in the method
     * @see    Utils.SearchableItems
     */
    private Utils.SearchableItems shiftNext() throws OutOfBoundReadingException, IOException {
        String line;
        while (!reader.isEnd(p)) {
            line = reader.readLine(p);

            HashSet<Utils.SearchableItems> keywords = new HashSet<>();
            keywords.add(Utils.SearchableItems.IMPORT);
            keywords.add(Utils.SearchableItems.FROM);
            keywords.add(Utils.SearchableItems.CLASS);
            keywords.add(Utils.SearchableItems.FUNCTION);
            keywords.add(Utils.SearchableItems.VARIABLE_EQUAL);

            HashMap<Utils.SearchableItems, Integer> foundKeywords =
                    utils.searchKeyWordsInLine(line, keywords);

            if (foundKeywords.containsKey(Utils.SearchableItems.ANY_QUOTE)) {
                foundUnnecessaryPart = new Pair<>(
                        Utils.SearchableItems.ANY_QUOTE,
                        foundKeywords.get(Utils.SearchableItems.ANY_QUOTE)
                );

                foundKeywords.remove(Utils.SearchableItems.ANY_QUOTE);
            } else if (foundKeywords.containsKey(Utils.SearchableItems.OPEN_BRACKET)) {
                foundUnnecessaryPart = new Pair<>(
                        Utils.SearchableItems.OPEN_BRACKET,
                        foundKeywords.get(Utils.SearchableItems.OPEN_BRACKET)
                );

                foundKeywords.remove(Utils.SearchableItems.OPEN_BRACKET);
            }

            if (foundKeywords.containsKey(Utils.SearchableItems.FROM) &&
                    foundKeywords.containsKey(Utils.SearchableItems.IMPORT)
            ) {
                return Utils.SearchableItems.FROM;
            }

            // if this code reached means that "from" using with yield, so it's neccesary to delete "from"
            foundKeywords.remove(Utils.SearchableItems.FROM);

            for (Utils.SearchableItems keyword : foundKeywords.keySet()) {
                return keyword;
            }

            skipUnnecessaryPartIfExists();
            shiftToOperableLine();
        }

        return Utils.SearchableItems.END;
    }

    /**
     * Executes method to skip quotes or braces
     */
    private void skipUnnecessaryPartIfExists() throws OutOfBoundReadingException, IOException {
        if (foundUnnecessaryPart != null) {
            switch (foundUnnecessaryPart.getFirst()) {
                case ANY_QUOTE -> {
                    skipQuotes(reader.readLine(p), foundUnnecessaryPart.getSecond(), false);
                }
                case OPEN_BRACKET -> {
                    skipBraces(reader.readLine(p), foundUnnecessaryPart.getSecond(), false);
                }
            }

            foundUnnecessaryPart = null;
        }
    }

    /**
     * Collects information about divided parts of the file into a divided file variable
     * @param dividedFile divided file variable
     */
    private void collectInfo(DividedFile dividedFile)
            throws OutOfBoundReadingException, IOException, UnknownKeywordException {
        Utils.SearchableItems pointerExpression;
        while ((pointerExpression=shiftNext()) != Utils.SearchableItems.END) {
            // If new line start with fewer spaces means that class ended
            int spaces = utils.countSpaces(reader.readLine(p));
            classHierarchy.popIfSpacesDecrease(spaces);
            funHierarchy.popIfSpacesDecrease(spaces);

            switch (pointerExpression) {
                case CLASS -> {
                    ClassPointer classPointer = traverseClass();

                    if (!classHierarchy.isEmpty()) {
                        ClassPointer parent = classHierarchy.last().getSecond();

                        parent.addInnerClassPointer(classPointer);
                    } else {
                        dividedFile.addClassPointer(classPointer);
                    }

                    classHierarchy.push(spaces, classPointer);
                }
                case FUNCTION -> {
                    FunctionPointer functionPointer = traverseFunction();

                    if (classHierarchy.isEmpty()) {
                        dividedFile.addFunPointer(functionPointer);
                    } else {
                        classHierarchy.last().getSecond().addFunPointer(functionPointer);
                    }

                    funHierarchy.push(spaces, functionPointer);
                }
                case IMPORT -> {
                    ImportPointer importPointer = traverseImport();
                    dividedFile.addImportPointer(importPointer);
                }
                case FROM -> {
                    ImportPointer importPointer = traverseFrom();
                    dividedFile.addImportPointer(importPointer);
                }
                case VARIABLE_EQUAL -> {
                    var pair = traverseVariable();

                    VariablePointer variablePointer = pair.getFirst();
                    boolean isVarBelongToClass = pair.getSecond();

                    if (isVarBelongToClass) {
                        classHierarchy.last().getSecond().addVariablePointer(variablePointer);
                    } else if (!funHierarchy.isEmpty()) {
                        funHierarchy.last().getSecond().addVariablePointer(variablePointer);
                    } else if (!classHierarchy.isEmpty()) {
                        classHierarchy.last().getSecond().addVariablePointer(variablePointer);
                    } else {
                        dividedFile.addVariablePointer(variablePointer);
                    }
                }
                default -> throw new UnknownKeywordException(String.format(
                                    "Got unknown type (%s) while dividing file on path %s",
                                    pointerExpression,
                                    dividedFile.getAbsolutePath()
                            ));
            }

            skipUnnecessaryPartIfExists();
            shiftToOperableLine();
        }
    }

    /**
     * Divides the file to the small parts and saves to the DividedFile class
     * @param  path path to the initial file
     * @return DividedFile with the all information
     * @see    DividedFile
     */
    @Override
    public DividedFile divide(String path, int offset) throws IOException, OutOfBoundReadingException, UnknownKeywordException {
        logger.info(
                String.format("Python file divider service with path %s started successfully", path)
        );

        this.p = 0;
        this.classHierarchy.clear();
        this.funHierarchy.clear();
        this.foundUnnecessaryPart = null;

        reader = new FileLineReader();
        reader.setFile(Paths.get(path).toFile());
        reader.open();

        DividedFile dividedFile = new DividedFile();

        dividedFile.setAbsolutePath(path);
        dividedFile.setRelativePath(path.substring(offset));
        dividedFile.setFileName(utils.getFileNameByPath(path));

        collectInfo(dividedFile);

        reader.close();

        logger.info(
                String.format("Python file divider service with path %s stopped successfully", path)
        );

        return dividedFile;
    }
}
