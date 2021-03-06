package de.hftstuttgart.projectindoorweb.web.internal.requests.positioning;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ReferencePosition {

    private int positionId;

    private double x;
    private double y;
    private double z;

    private boolean wgs84;

    @JsonCreator
    public ReferencePosition(@JsonProperty("positionId") int positionId,
                             @JsonProperty("x") double x,
                             @JsonProperty("y") double y,
                             @JsonProperty("z") double z,
                             @JsonProperty("wgs84") boolean wgs84) {

        this.positionId = positionId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.wgs84 = wgs84;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public boolean isWgs84() {
        return wgs84;
    }

    public void setWgs84(boolean wgs84) {
        this.wgs84 = wgs84;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReferencePosition that = (ReferencePosition) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.z, z) == 0 &&
                wgs84 == that.wgs84;
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y, z, wgs84);
    }
}
