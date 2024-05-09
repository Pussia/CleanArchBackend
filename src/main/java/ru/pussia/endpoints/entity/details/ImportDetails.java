package ru.pussia.endpoints.entity.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.pussia.endpoints.entity.general.ImportPointer;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;

public class ImportDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 127L;
    private String fromName; // The file's name where classes imported
    private HashSet<String> importedNames; // The imported classes' names
    private String importName; // The import's name in the file
    private HashSet<FileDetails> importedFiles; // The files from things imported
    private ImportPointer pointer; // The pointer to the import

    public ImportDetails() {
    }

    public ImportDetails(String fromName, HashSet<String> importedNames) {
        this.fromName = fromName;
        this.importedNames = importedNames;
    }

    public ImportDetails(String fromName, HashSet<String> importedNames, String importName) {
        this.fromName = fromName;
        this.importedNames = importedNames;
        this.importName = importName;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getImportName() {
        return importName;
    }

    public void setImportName(String importName) {
        this.importName = importName;
    }

    public HashSet<String> getImportedNames() {
        return importedNames == null ? new HashSet<>() : importedNames;
    }

    public void setImportedNames(HashSet<String> importedNames) {
        this.importedNames = importedNames;
    }

    @JsonIgnore
    public HashSet<FileDetails> getImportedFiles() {
        return this.importedFiles == null ? new HashSet<>() : this.importedFiles;
    }

    public void setImportedFiles(HashSet<FileDetails> importedFiles) {
        this.importedFiles = importedFiles;
    }

    public void addImportedFile(FileDetails importedFile) {
        if (this.importedFiles == null) {
            this.importedFiles = new HashSet<>();
        }

        this.importedFiles.add(importedFile);
    }

    public ImportPointer getPointer() {
        return pointer;
    }

    public void setPointer(ImportPointer pointer) {
        this.pointer = pointer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImportDetails that = (ImportDetails) o;

        if (!Objects.equals(fromName, that.fromName)) return false;
        if (!Objects.equals(importName, that.importName)) return false;
        return Objects.equals(importedNames, that.importedNames);
    }

    @Override
    public int hashCode() {
        int result = fromName != null ? fromName.hashCode() : 0;
        result = 31 * result + (importedNames != null ? importedNames.hashCode() : 0);
        result = 31 * result + (importName != null ? importName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ImportDetails{" +
                "fromName='" + fromName + '\'' +
                ", importedNames=" + importedNames +
                ", importName=" + importName +
                '}';
    }
}
