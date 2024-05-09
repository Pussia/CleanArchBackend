package ru.pussia.endpoints.entity.general;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DividedFile implements Serializable {

    @Serial
    private static final long serialVersionUID = 111L;
    private String fileName; // File's name
    private String absolutePath; // File's absolute path
    private String relativePath; // File's relative path
    private List<ImportPointer> importPointers; // Pointers to from and import statements in the file
    private List<VariablePointer> variablePointers; // Pointer to the variables in the file
    private List<ClassPointer> classPointers; // Pointers to class declarations in the file
    private List<FunctionPointer> funPointers; // Pointers to function declarations in the file

    public DividedFile() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public List<ImportPointer> getImportPointers() {
        return importPointers == null ? List.of() : importPointers;
    }

    public void setImportPointers(List<ImportPointer> importPointers) {
        this.importPointers = importPointers;
    }

    public void addImportPointer(ImportPointer importPointer) {
        if (this.importPointers == null) {
            this.importPointers = new ArrayList<>();
        }

        this.importPointers.add(importPointer);
    }

    public List<VariablePointer> getVariablePointers() {
        return this.variablePointers == null ? new ArrayList<>() : this.variablePointers;
    }

    public void setVariablePointers(List<VariablePointer> variablePointers) {
        this.variablePointers = variablePointers;
    }

    public void addVariablePointer(VariablePointer variablePointer) {
        if (this.variablePointers == null) {
            this.variablePointers = new ArrayList<>();
        }

        this.variablePointers.add(variablePointer);
    }

    public List<ClassPointer> getClassPointers() {
        return classPointers == null ? List.of() : classPointers;
    }

    public void setClassPointers(List<ClassPointer> classPointers) {
        this.classPointers = classPointers;
    }

    public void addClassPointer(ClassPointer classPointer) {
        if (this.classPointers == null) {
            this.classPointers = new ArrayList<>();
        }

        this.classPointers.add(classPointer);
    }

    public List<FunctionPointer> getFunPointers() {
        return funPointers == null ? List.of() : funPointers;
    }

    public void setFunPointers(List<FunctionPointer> funPointers) {
        this.funPointers = funPointers;
    }

    public void addFunPointer(FunctionPointer funPointer) {
        if (this.funPointers == null) {
            this.funPointers = new ArrayList<>();
        }

        this.funPointers.add(funPointer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DividedFile that = (DividedFile) o;

        if (!Objects.equals(fileName, that.fileName)) return false;
        if (!Objects.equals(absolutePath, that.absolutePath)) return false;
        if (!Objects.equals(relativePath, that.relativePath)) return false;
        if (!Objects.equals(importPointers, that.importPointers))
            return false;
        if (!Objects.equals(variablePointers, that.variablePointers))
            return false;
        if (!Objects.equals(classPointers, that.classPointers))
            return false;
        return Objects.equals(funPointers, that.funPointers);
    }

    @Override
    public int hashCode() {
        int result = fileName != null ? fileName.hashCode() : 0;
        result = 31 * result + (absolutePath != null ? absolutePath.hashCode() : 0);
        result = 31 * result + (relativePath != null ? relativePath.hashCode() : 0);
        result = 31 * result + (importPointers != null ? importPointers.hashCode() : 0);
        result = 31 * result + (variablePointers != null ? variablePointers.hashCode() : 0);
        result = 31 * result + (classPointers != null ? classPointers.hashCode() : 0);
        result = 31 * result + (funPointers != null ? funPointers.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DividedFile{" +
                "fileName='" + fileName + '\'' +
                ", absolutePath='" + absolutePath +
                ", relativePath='" + relativePath +
                ", importPointers=" + importPointers +
                ", variablePointers=" + variablePointers +
                ", classPointers=" + classPointers +
                ", funPointers=" + funPointers +
                '}';
    }
}
