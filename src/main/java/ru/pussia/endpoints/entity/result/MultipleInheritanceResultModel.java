package ru.pussia.endpoints.entity.result;

import java.util.Objects;

public class MultipleInheritanceResultModel {

    private String className;
    private String filePath;

    public MultipleInheritanceResultModel() {
    }

    public MultipleInheritanceResultModel(String className, String filePath) {
        this.className = className;
        this.filePath = filePath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultipleInheritanceResultModel that = (MultipleInheritanceResultModel) o;

        if (!Objects.equals(className, that.className)) return false;
        return Objects.equals(filePath, that.filePath);
    }

    @Override
    public int hashCode() {
        int result = className != null ? className.hashCode() : 0;
        result = 31 * result + (filePath != null ? filePath.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MultipleInheritanceResultModel{" +
                "className='" + className + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
