package de.hft_stuttgart.projectindoorweb.parser.internal;

import java.util.ArrayList;
import java.util.List;

public abstract class SensorDataParser<T> implements Runnable{


    protected List parsedDataList;
    protected String filePath;


    public SensorDataParser(String filePath) {
        this.parsedDataList = new ArrayList<>();

        this.filePath=filePath;
    }

    public List getParsedDataList(){
        return parsedDataList;
    }

    protected abstract boolean isValidLine(String line);

}
