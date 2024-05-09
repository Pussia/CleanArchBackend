package ru.pussia.endpoints.entity.general;

import java.io.Serial;
import java.io.Serializable;

public class VariablePointer implements Serializable {

    @Serial
    private static final long serialVersionUID = 114L;

    private int variablePointer; // Pointer to the start of variable declaration
    private boolean isGlobal; // True of this variable is global, otherwise false

    public VariablePointer() {
    }

    public VariablePointer(int variablePointer, boolean isGlobal) {
        this.variablePointer = variablePointer;
        this.isGlobal = isGlobal;
    }

    public int getVariablePointer() {
        return variablePointer;
    }

    public void setVariablePointer(int variablePointer) {
        this.variablePointer = variablePointer;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariablePointer that = (VariablePointer) o;

        if (variablePointer != that.variablePointer) return false;
        return isGlobal == that.isGlobal;
    }

    @Override
    public int hashCode() {
        int result = variablePointer;
        result = 31 * result + (isGlobal ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VariablePointer{" +
                "variablePointer=" + variablePointer +
                ", isGlobal=" + isGlobal +
                '}';
    }
}
