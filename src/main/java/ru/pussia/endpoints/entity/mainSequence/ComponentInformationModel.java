package ru.pussia.endpoints.entity.mainSequence;

import ru.pussia.endpoints.entity.details.FileDetails;

import java.util.Objects;

public class ComponentInformationModel {

    private int fanIn;
    private int fanOut;
    private int numberOfClasses;
    private int numberOfConcreteClasses;
    private int numberOfAbstractClasses;
    private double abstractness;
    private double instability;
    private double distance;
    private final String component;

    public ComponentInformationModel(String component) {
        this.component = component;
    }

    public int getFanIn() {
        return fanIn;
    }

    public void setFanIn(int fanIn) {
        this.fanIn = fanIn;
    }

    public int getFanOut() {
        return fanOut;
    }

    public void setFanOut(int fanOut) {
        this.fanOut = fanOut;
    }

    public int getNumberOfClasses() {
        return numberOfClasses;
    }

    public void setNumberOfClasses(int numberOfClasses) {
        this.numberOfClasses = numberOfClasses;
    }

    public int getNumberOfConcreteClasses() {
        return numberOfConcreteClasses;
    }

    public void setNumberOfConcreteClasses(int numberOfConcreteClasses) {
        this.numberOfConcreteClasses = numberOfConcreteClasses;
    }

    public int getNumberOfAbstractClasses() {
        return numberOfAbstractClasses;
    }

    public void setNumberOfAbstractClasses(int numberOfAbstractClasses) {
        this.numberOfAbstractClasses = numberOfAbstractClasses;
    }

    public double getAbstractness() {
        return abstractness;
    }

    public void setAbstractness(double abstractness) {
        this.abstractness = abstractness;
    }

    public double getInstability() {
        return instability;
    }

    public void setInstability(double instability) {
        this.instability = instability;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getComponent() {
        return component;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentInformationModel that = (ComponentInformationModel) o;

        if (fanIn != that.fanIn) return false;
        if (fanOut != that.fanOut) return false;
        if (numberOfClasses != that.numberOfClasses) return false;
        if (numberOfConcreteClasses != that.numberOfConcreteClasses) return false;
        if (numberOfAbstractClasses != that.numberOfAbstractClasses) return false;
        return Objects.equals(component, that.component);
    }

    @Override
    public int hashCode() {
        int result;
        result = fanIn;
        result = 31 * result + fanOut;
        result = 31 * result + numberOfClasses;
        result = 31 * result + numberOfConcreteClasses;
        result = 31 * result + numberOfAbstractClasses;
        result = 31 * result + (component != null ? component.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ComponentInformationModel{" +
                "fanIn=" + fanIn +
                ", fanOut=" + fanOut +
                ", numberOfClasses=" + numberOfClasses +
                ", numberOfConcreteClasses=" + numberOfConcreteClasses +
                ", numberOfAbstractClasses=" + numberOfAbstractClasses +
                ", abstractness=" + abstractness +
                ", instability=" + instability +
                ", distance=" + distance +
                ", component=" + component +
                '}';
    }
}
