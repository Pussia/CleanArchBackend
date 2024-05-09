package ru.pussia.endpoints.entity.details;

import ru.pussia.endpoints.entity.general.FunctionPointer;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;

public class FunctionDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 126L;
    private String funName; // The name of the function
    private HashSet<VariableDetails> variableDetails; // The variable's details in the function
    private HashSet<ParameterDetails> parameterDetails; // The function's parameters details
    private FunctionPointer pointer; // The pointer to the function

    public FunctionDetails() {
    }

    public FunctionDetails(String funName) {
        this.funName = funName;
    }

    public String getFunName() {
        return funName;
    }

    public void setFunName(String funName) {
        this.funName = funName;
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

    public HashSet<ParameterDetails> getParameterDetails() {
        return parameterDetails;
    }

    public void setParameterDetails(HashSet<ParameterDetails> parameterDetails) {
        this.parameterDetails = parameterDetails;
    }

    public FunctionPointer getPointer() {
        return pointer;
    }

    public void setPointer(FunctionPointer pointer) {
        this.pointer = pointer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionDetails that = (FunctionDetails) o;

        if (!Objects.equals(funName, that.funName)) return false;
        if (!Objects.equals(variableDetails, that.variableDetails)) return false;
        return Objects.equals(parameterDetails, that.parameterDetails);
    }

    @Override
    public int hashCode() {
        int result = funName != null ? funName.hashCode() : 0;
        result = 31 * result + (variableDetails != null ? variableDetails.hashCode() : 0);
        result = 31 * result + (parameterDetails != null ? parameterDetails.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FunctionDetails{" +
                "funName='" + funName + '\'' +
                ", variableDetails='" + variableDetails +
                ", parameterDetails=" + parameterDetails +
                '}';
    }
}
