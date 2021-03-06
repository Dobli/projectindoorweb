package de.hftstuttgart.projectindoorweb.web.internal.requests.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GetAlgorithmParameters {

    private String name;
    private String prettyName;
    private String defaultValue;
    private String valueDataType;
    private String description;
    private String[] applicableForAlgorithms;

    @JsonCreator
    public GetAlgorithmParameters(@JsonProperty("name") String name,
                                  @JsonProperty("prettyName") String prettyName,
                                  @JsonProperty("defaultValue") String defaultValue,
                                  @JsonProperty("valueDataType") String valueDataType,
                                  @JsonProperty("description") String description,
                                  @JsonProperty("applicableForAlgorithms") String[] applicableForAlgorithms) {

        this.name = name;
        this.prettyName = prettyName;
        this.defaultValue = defaultValue;
        this.valueDataType = valueDataType;
        this.description = description;
        this.applicableForAlgorithms = applicableForAlgorithms;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValueDataType() {
        return valueDataType;
    }

    public void setValueDataType(String valueDataType) {
        this.valueDataType = valueDataType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getApplicableForAlgorithms() {
        return applicableForAlgorithms;
    }

    public void setApplicableForAlgorithms(String[] applicableForAlgorithms) {
        this.applicableForAlgorithms = applicableForAlgorithms;
    }
}
