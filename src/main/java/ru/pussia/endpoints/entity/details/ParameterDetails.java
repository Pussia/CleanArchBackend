package ru.pussia.endpoints.entity.details;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ParameterDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 128L;
    private String name; // Parameter's name
    private String type; // Parameter's type

    public ParameterDetails() {
    }

    public ParameterDetails(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParameterDetails that = (ParameterDetails) o;

        if (!Objects.equals(name, that.name)) return false;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ParameterDetails{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
