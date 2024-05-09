package ru.pussia.endpoints.entity.details;

import ru.pussia.endpoints.entity.general.ClassPointer;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;

public class ClassDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 123L;
    private String name; // Class' name
    private String filePath; // File's path
    private boolean isAbstract; // Is class abstract
    private HashSet<VariableDetails> variableDetails; // The variable's details in the class
    private HashSet<FunctionDetails> functionDetails; // The function's details in the class
    private HashSet<String> parentDetails; // The parents' details in the class
    private HashSet<ClassDetails> innerClassesDetails; //  The inner classes' details in the class
    private ClassPointer pointer; // The pointer to the class

    public ClassDetails() {
    }

    public ClassDetails(String name) {
        this.name = name;
    }

    public ClassDetails(String name, HashSet<String> parentDetails) {
        this.name = name;
        this.parentDetails = parentDetails;
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

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public HashSet<VariableDetails> getVariableDetails() {
        return variableDetails;
    }

    public void setVariableDetails(HashSet<VariableDetails> variableDetails) {
        this.variableDetails = variableDetails;
    }

    public void addVariableDetails(VariableDetails variableDetails) {
        if (this.variableDetails == null) {
            this.variableDetails = new HashSet<>();
        }

        this.variableDetails.add(variableDetails);
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

    public HashSet<String> getParentDetails() {
        return parentDetails == null ? new HashSet<>() : parentDetails;
    }

    public void setParentDetails(HashSet<String> parentDetails) {
        this.parentDetails = parentDetails;
    }

    public HashSet<ClassDetails> getInnerClassesDetails() {
        return innerClassesDetails;
    }

    public void setInnerClassesDetails(HashSet<ClassDetails> innerClassesDetails) {
        this.innerClassesDetails = innerClassesDetails;
    }

    public void addInnerClassesDetails(ClassDetails innerClassDetails) {
        if (this.innerClassesDetails == null) {
            this.innerClassesDetails = new HashSet<>();
        }

        this.innerClassesDetails.add(innerClassDetails);
    }

    public ClassPointer getPointer() {
        return pointer;
    }

    public void setPointer(ClassPointer pointer) {
        this.pointer = pointer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassDetails that = (ClassDetails) o;

        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(filePath, that.filePath)) return false;
        if (isAbstract != that.isAbstract) return false;
        if (!Objects.equals(variableDetails, that.variableDetails))
            return false;
        if (!Objects.equals(functionDetails, that.functionDetails))
            return false;
        if (!Objects.equals(parentDetails, that.parentDetails))
            return false;
        return Objects.equals(innerClassesDetails, that.innerClassesDetails);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (isAbstract ? 1 : 0);
        result = 31 * result + (variableDetails != null ? variableDetails.hashCode() : 0);
        result = 31 * result + (functionDetails != null ? functionDetails.hashCode() : 0);
        result = 31 * result + (parentDetails != null ? parentDetails.hashCode() : 0);
        result = 31 * result + (innerClassesDetails != null ? innerClassesDetails.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClassDetails{" +
                "name='" + name + '\'' +
                ", isAbstract=" + isAbstract +
                ", variableDetails=" + variableDetails +
                ", functionDetails=" + functionDetails +
                ", parentDetails=" + parentDetails +
                ", innerClassesDetails=" + innerClassesDetails +
                '}';
    }
}
