package ru.pussia.endpoints.entity.general;

import java.io.Serial;
import java.io.Serializable;

public class ImportPointer implements Serializable {

    @Serial
    private static final long serialVersionUID = 113L;
    private int fromPointer; // Pointer to the from statement in the file
    private int importPointer; // Pointer to the import statement in the file
    private int asPointer; // Pointer to the as statement in the file

    public ImportPointer() {
    }

    public ImportPointer(int fromPointer, int importPointer, int asPointer) {
        this.fromPointer = fromPointer;
        this.importPointer = importPointer;
        this.asPointer = asPointer;
    }

    public int getFromPointer() {
        return fromPointer;
    }

    public void setFromPointer(int fromPointer) {
        this.fromPointer = fromPointer;
    }

    public int getImportPointer() {
        return importPointer;
    }

    public void setImportPointer(int importPointer) {
        this.importPointer = importPointer;
    }

    public int getAsPointer() {
        return asPointer;
    }

    public void setAsPointer(int asPointer) {
        this.asPointer = asPointer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImportPointer pointer = (ImportPointer) o;

        if (fromPointer != pointer.fromPointer) return false;
        if (asPointer != pointer.asPointer) return false;
        return importPointer == pointer.importPointer;
    }

    @Override
    public int hashCode() {
        int result = fromPointer;
        result = 31 * result + importPointer;
        result = 31 * result + asPointer;
        return result;
    }

    @Override
    public String toString() {
        return "ImportPointer{" +
                "fromPointer=" + fromPointer +
                ", importPointer=" + importPointer +
                ", asPointer=" + asPointer +
                '}';
    }
}
