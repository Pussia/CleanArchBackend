package ru.pussia.endpoints.entity.details;

import ru.pussia.endpoints.entity.general.DividedFile;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;

public class FileDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 125L;
    private String name; // File's name
    private String absolutePath; // File's absolute path
    private String relativePath; // File's relative path
    private HashSet<ImportDetails> importDetails; // The imported classes' details
    private HashSet<VariableDetails> globalVariableDetails; // The global variables' details in the file
    private HashSet<ClassDetails> classDetails; // The classes' details in the file
    private HashSet<FunctionDetails> functionDetails; // The functions' details in the file
    private HashSet<String> classNames; // The classes' names in the file
    private HashSet<String> functionNames; // The names of the functions in the file
    private DividedFile pointer; // The pointer to the divided file

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public HashSet<ImportDetails> getImportDetails() {
        return importDetails == null ? new HashSet<>() : importDetails;
    }

    public void setImportDetails(HashSet<ImportDetails> importDetails) {
        this.importDetails = importDetails;
    }

    public void addImportDetails(ImportDetails importDetails) {
        if (this.importDetails == null) {
            this.importDetails = new HashSet<>();
        }

        this.importDetails.add(importDetails);
    }

    public HashSet<VariableDetails> getGlobalVariableDetails() {
        return this.globalVariableDetails == null ? new HashSet<>() : this.globalVariableDetails;
    }

    public void setGlobalVariableDetails(HashSet<VariableDetails> globalVariableDetails) {
        this.globalVariableDetails = globalVariableDetails;
    }

    public void addGlobalVariableDetails(VariableDetails variableDetails) {
        if (this.globalVariableDetails == null) {
            this.globalVariableDetails = new HashSet<>();
        }

        this.globalVariableDetails.add(variableDetails);
    }

    public HashSet<ClassDetails> getClassDetails() {
        return classDetails == null ? new HashSet<>() : classDetails;
    }

    public void setClassDetails(HashSet<ClassDetails> classDetails) {
        this.classDetails = classDetails;
    }

    public void addClassDetails(ClassDetails classDetails) {
        if (this.classDetails == null) {
            this.classDetails = new HashSet<>();
        }

        this.classDetails.add(classDetails);
    }

    public HashSet<FunctionDetails> getFunctionDetails() {
        return functionDetails;
    }

    public void setFunctionDetails(HashSet<FunctionDetails> functionDetails) {
        this.functionDetails = functionDetails;
    }

    public void addFunctionDetails(FunctionDetails functionDetails) {
        if (this.functionDetails == null) {
            this.functionDetails = new HashSet<>();
        }

        this.functionDetails.add(functionDetails);
    }

    public HashSet<String> getClassNames() {
        return this.classNames == null ? new HashSet<>() : this.classNames;
    }

    public void setClassNames(HashSet<String> classNames) {
        this.classNames = classNames;
    }

    public void addClassName(String className) {
        if (this.classNames == null) {
            this.classNames = new HashSet<>();
        }

        this.classNames.add(className);
    }

    public HashSet<String> getFunctionNames() {
        return this.functionNames == null ? new HashSet<>() : this.functionNames;
    }

    public void setFunctionNames(HashSet<String> functionNames) {
        this.functionNames = functionNames;
    }

    public void addFunctionName(String functionName) {
        if (this.functionNames == null) {
            this.functionNames = new HashSet<>();
        }

        this.functionNames.add(functionName);
    }

    public DividedFile getPointer() {
        return pointer;
    }

    public void setPointer(DividedFile pointer) {
        this.pointer = pointer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileDetails that = (FileDetails) o;

        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(absolutePath, that.absolutePath)) return false;
        if (!Objects.equals(relativePath, that.relativePath)) return false;
        if (!Objects.equals(importDetails, that.importDetails))
            return false;
        if (!Objects.equals(globalVariableDetails, that.globalVariableDetails))
            return false;
        if (!Objects.equals(classDetails, that.classDetails)) return false;
        if (!Objects.equals(functionDetails, that.functionDetails))
            return false;
        if (!Objects.equals(classNames, that.classNames)) return false;
        return Objects.equals(functionNames, that.functionNames);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (absolutePath != null ? absolutePath.hashCode() : 0);
        result = 31 * result + (relativePath != null ? relativePath.hashCode() : 0);
        result = 31 * result + (importDetails != null ? importDetails.hashCode() : 0);
        result = 31 * result + (globalVariableDetails != null ? globalVariableDetails.hashCode() : 0);
        result = 31 * result + (classDetails != null ? classDetails.hashCode() : 0);
        result = 31 * result + (functionDetails != null ? functionDetails.hashCode() : 0);
        result = 31 * result + (classNames != null ? classNames.hashCode() : 0);
        result = 31 * result + (functionNames != null ? functionNames.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FileDetails{" +
                "name='" + name + '\'' +
                ", absolutePath='" + absolutePath +
                ", relativePath='" + relativePath +
                ", importDetails=" + importDetails +
                ", variableDetails=" + globalVariableDetails +
                ", classDetails=" + classDetails +
                ", functionDetails=" + functionDetails +
                ", classNames=" + classNames +
                ", functionNames=" + functionNames +
                '}';
    }
}
