package ru.pussia.endpoints.entity.details;

import ru.pussia.endpoints.entity.general.VariablePointer;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class VariableDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 124L;
    private String name; // Variable's name
    private String filePath; // File's relative path
    private boolean isGlobal; // True if this variable is global, otherwise false
    private VariablePointer pointer; // The pointer to the variable

    public VariableDetails() {
    }

    public VariableDetails(
            String name,
            boolean isGlobal
    ) {
        this.name = name;
        this.isGlobal = isGlobal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public VariablePointer getPointer() {
        return pointer;
    }

    public void setPointer(VariablePointer pointer) {
        this.pointer = pointer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariableDetails that = (VariableDetails) o;

        if (isGlobal != that.isGlobal) return false;
        if (!Objects.equals(name, that.name)) return false;
        return Objects.equals(filePath, that.filePath);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (filePath != null ? filePath.hashCode() : 0);
        result = 31 * result + (isGlobal ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VariableDetails{" +
                "name='" + name + '\'' +
                "filePath='" + name + '\'' +
                ", isGlobal=" + isGlobal +
                '}';
    }
}
