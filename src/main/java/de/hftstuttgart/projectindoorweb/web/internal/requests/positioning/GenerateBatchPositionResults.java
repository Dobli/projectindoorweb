package de.hftstuttgart.projectindoorweb.web.internal.requests.positioning;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.hftstuttgart.projectindoorweb.web.internal.requests.project.SaveNewProjectParameters;

import java.util.Set;

public class GenerateBatchPositionResults {

    private Long buildingIdentifier;
    private Long evalFileIdentifier;
    private long projectIdentifier;
    private long[] radioMapFileIdentifiers;
    private String algorithmType;
    private Set<SaveNewProjectParameters> projectParameters;
    private boolean withPixelPosition;

    @JsonCreator
    public GenerateBatchPositionResults(@JsonProperty("buildingIdentifier") Long buildingIdentifier,
                                        @JsonProperty("evaluationFile") Long evalFileIdentifier,
                                        @JsonProperty(value = "projectIdentifier", required = false) long projectIdentifier,
                                        @JsonProperty("radioMapFiles") long[] radioMapFileIdentifiers,
                                        @JsonProperty("algorithmType") String algorithmType,
                                        @JsonProperty("projectParameters") Set<SaveNewProjectParameters> projectParameters,
                                        @JsonProperty("withPixelPosition") boolean withPixelPosition) {

        this.buildingIdentifier = buildingIdentifier;
        this.evalFileIdentifier = evalFileIdentifier;
        this.projectIdentifier = projectIdentifier;
        this.radioMapFileIdentifiers = radioMapFileIdentifiers;
        this.algorithmType = algorithmType;
        this.projectParameters = projectParameters;
        this.withPixelPosition = withPixelPosition;
    }

    public Long getBuildingIdentifier() {
        return buildingIdentifier;
    }

    public void setBuildingIdentifier(Long buildingIdentifier) {
        this.buildingIdentifier = buildingIdentifier;
    }

    public Long getProjectIdentifier() {
        return projectIdentifier;
    }

    public void setProjectIdentifier(long projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }

    public Long getEvalFileIdentifier() {
        return evalFileIdentifier;
    }

    public void setEvalFileIdentifier(Long evalFileIdentifier) {
        this.evalFileIdentifier = evalFileIdentifier;
    }

    public long[] getRadioMapFileIdentifiers() {
        return radioMapFileIdentifiers;
    }

    public void setRadioMapFileIdentifiers(long[] radioMapFileIdentifiers) {
        this.radioMapFileIdentifiers = radioMapFileIdentifiers;
    }

    public String getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(String algorithmType) {
        this.algorithmType = algorithmType;
    }

    public Set<SaveNewProjectParameters> getProjectParameters() {
        return projectParameters;
    }

    public void setProjectParameters(Set<SaveNewProjectParameters> projectParameters) {
        this.projectParameters = projectParameters;
    }

    public boolean isWithPixelPosition() {
        return withPixelPosition;
    }

    public void setWithPixelPosition(boolean withPixelPosition) {
        this.withPixelPosition = withPixelPosition;
    }
}
