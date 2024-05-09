package ru.pussia.endpoints.entity.general;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FunctionPointer implements Serializable {

    @Serial
    private static final long serialVersionUID = 112L;
    private int funPointer; // The pointer to the beginning the function declarations in the file
    private int paramPointer; // The pointer to the beginning of parameters declarations in the function
    private List<VariablePointer> variablePointers; // The pointers to the variable in the function

    public FunctionPointer() {
    }

    public FunctionPointer(int funPointer, int paramPointer) {
        this.funPointer = funPointer;
        this.paramPointer = paramPointer;
    }

    public int getFunPointer() {
        return funPointer;
    }

    public void setFunPointer(int funPointer) {
        this.funPointer = funPointer;
    }

    public int getParamPointer() {
        return paramPointer;
    }

    public void setParamPointer(int paramPointer) {
        this.paramPointer = paramPointer;
    }

    public List<VariablePointer> getVariablePointers() {
        return variablePointers == null ? new ArrayList<>() : variablePointers;
    }

    public void addVariablePointer(VariablePointer variablePointers) {
        if (this.variablePointers == null) {
            this.variablePointers = new ArrayList<>();
        }

        this.variablePointers.add(variablePointers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionPointer that = (FunctionPointer) o;

        if (funPointer != that.funPointer) return false;
        if (!Objects.equals(variablePointers, that.variablePointers)) return false;
        return paramPointer == that.paramPointer;
    }

    @Override
    public int hashCode() {
        int result = funPointer;
        result = 31 * result + paramPointer;
        result = 31 * result + (variablePointers != null ? variablePointers.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FunctionPointer{" +
                "funPointer=" + funPointer +
                ", paramPointer=" + paramPointer +
                ", variablePointers=" + variablePointers +
                '}';
    }
}
