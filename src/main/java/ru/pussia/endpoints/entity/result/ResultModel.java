package ru.pussia.endpoints.entity.result;

import ru.pussia.endpoints.entity.details.ClassDetails;
import ru.pussia.endpoints.entity.details.FileDetails;
import ru.pussia.endpoints.entity.details.VariableDetails;
import ru.pussia.endpoints.entity.mainSequence.ComponentInformationModel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class ResultModel implements Serializable {

    private HashSet<HashSet<String>> adpFiles;
    private List<VariableDetails> globals;
    private List<ComponentInformationModel> componentsInformation;
    private List<MultipleInheritanceResultModel> multipleInheritance;

    public ResultModel() {
    }

    public ResultModel(
            HashSet<HashSet<String>> adpFiles,
            List<VariableDetails> globals,
            List<ComponentInformationModel> componentsInformation,
            List<MultipleInheritanceResultModel> multipleInheritance
    ) {
        this.adpFiles = adpFiles;
        this.globals = globals;
        this.componentsInformation = componentsInformation;
        this.multipleInheritance = multipleInheritance;
    }

    public HashSet<HashSet<String>> getAdpFiles() {
        return adpFiles;
    }

    public void setAdpFiles(HashSet<HashSet<String>> adpFiles) {
        this.adpFiles = adpFiles;
    }

    public List<VariableDetails> getGlobals() {
        return globals;
    }

    public void setGlobals(List<VariableDetails> globals) {
        this.globals = globals;
    }

    public List<ComponentInformationModel> getComponentsInformation() {
        return componentsInformation;
    }

    public void setComponentsInformation(List<ComponentInformationModel> componentsInformation) {
        this.componentsInformation = componentsInformation;
    }

    public List<MultipleInheritanceResultModel> getMultipleInheritance() {
        return multipleInheritance;
    }

    public void setMultipleInheritance(List<MultipleInheritanceResultModel> multipleInheritance) {
        this.multipleInheritance = multipleInheritance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResultModel that = (ResultModel) o;

        if (!Objects.equals(adpFiles, that.adpFiles)) return false;
        if (!Objects.equals(globals, that.globals)) return false;
        if (!Objects.equals(componentsInformation, that.componentsInformation))
            return false;
        return Objects.equals(multipleInheritance, that.multipleInheritance);
    }

    @Override
    public int hashCode() {
        int result = adpFiles != null ? adpFiles.hashCode() : 0;
        result = 31 * result + (globals != null ? globals.hashCode() : 0);
        result = 31 * result + (componentsInformation != null ? componentsInformation.hashCode() : 0);
        result = 31 * result + (multipleInheritance != null ? multipleInheritance.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ResultModel{" +
                "adpFiles=" + adpFiles +
                ", globals=" + globals +
                ", componentsInformation=" + componentsInformation +
                ", multipleInheritance=" + multipleInheritance +
                '}';
    }
}
