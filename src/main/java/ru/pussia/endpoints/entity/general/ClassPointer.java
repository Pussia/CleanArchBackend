package ru.pussia.endpoints.entity.general;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassPointer implements Serializable {

    @Serial
    private static final long serialVersionUID = 110L;
    private int classPointer; // The pointer to the class declarations in the file
    private int parentPointer; // The pointer to the parent classes in the class
    private List<VariablePointer> variablePointers; // List of the pointers to the variables in the class
    private List<FunctionPointer> funPointers; // List of the pointers to the function declarations in the class
    private List<ClassPointer> innerClassPointers; // List of the pointer to the class declarations in the class

    public ClassPointer() {
    }

    public ClassPointer(int classPointer, int parentPointer, List<FunctionPointer> funPointers) {
        this.classPointer = classPointer;
        this.parentPointer = parentPointer;
        this.funPointers = funPointers;
    }

    public int getClassPointer() {
        return classPointer;
    }

    public void setClassPointer(int classPointer) {
        this.classPointer = classPointer;
    }

    public int getParentPointer() {
        return parentPointer;
    }

    public void setParentPointer(int parentPointer) {
        this.parentPointer = parentPointer;
    }

    public List<VariablePointer> getVariablePointers() {
        return variablePointers == null ? List.of() : variablePointers;
    }

    public void addVariablePointer(VariablePointer variablePointers) {
        if (this.variablePointers == null) {
            this.variablePointers = new ArrayList<>();
        }

        this.variablePointers.add(variablePointers);
    }

    public List<FunctionPointer> getFunPointers() {
        return funPointers == null ? List.of() : funPointers;
    }

    public void addFunPointer(FunctionPointer funPointer) {
        if (this.funPointers == null) {
            this.funPointers = new ArrayList<>();
        }

        this.funPointers.add(funPointer);
    }

    public List<ClassPointer> getInnerClassPointers() {
        return innerClassPointers == null ? List.of() : innerClassPointers;
    }

    public void addInnerClassPointer(ClassPointer innerClassPointer) {
        if (this.innerClassPointers == null) {
            this.innerClassPointers = new ArrayList<>();
        }

        this.innerClassPointers.add(innerClassPointer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassPointer that = (ClassPointer) o;

        if (classPointer != that.classPointer) return false;
        if (parentPointer != that.parentPointer) return false;
        if (!Objects.equals(variablePointers, that.variablePointers)) return false;
        if (!Objects.equals(funPointers, that.funPointers)) return false;
        return Objects.equals(innerClassPointers, that.innerClassPointers);
    }

    @Override
    public int hashCode() {
        int result = classPointer;
        result = 31 * result + parentPointer;
        result = 31 * result + (variablePointers != null ? variablePointers.hashCode() : 0);
        result = 31 * result + (funPointers != null ? funPointers.hashCode() : 0);
        result = 31 * result + (innerClassPointers != null ? innerClassPointers.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClassPointer{" +
                "classPointer=" + classPointer +
                ", parentPointer=" + parentPointer +
                ", variablePointers=" + variablePointers +
                ", funPointers=" + funPointers +
                ", innerClassPointers=" + innerClassPointers +
                '}';
    }
}
