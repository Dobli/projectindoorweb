package de.hft_stuttgart.projectindoorweb.parser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVFormat;

//TODO delete class when ready
public class SensorFileReader {
    public static final String WIFI_FIELDNAME = "WIFI";
    public static final String POSITION_FIELDNAME = "POSI";
    public static final String ACCELERATION_FIELDNAME = "ACCE";
    public static final String GYROSCOPE_FIELDNAME = "GYRO";
    private String filePath;

    public SensorFileReader(String filePath){
        this.filePath=filePath;
    }

    public List readPositionFromFile() throws IOException{
        return readValuesFromFile(POSITION_FIELDNAME);
    }

    public List readAccelerationFromFile() throws IOException{
        return readValuesFromFile(ACCELERATION_FIELDNAME);
    }

    public List readGyroscopeFromFile() throws IOException{
        return readValuesFromFile(GYROSCOPE_FIELDNAME);
    }

    public List readWifiDataFromFile() throws IOException{
        return readValuesFromFile();
    }

    private List readValuesFromFile(String fieldName) throws IOException{
        List result= new ArrayList<String>();
        FileReader in = new FileReader(this.filePath);
        Iterable<CSVRecord> records=CSVFormat.EXCEL.withDelimiter(';').withRecordSeparator("\n").parse(in);
        for(CSVRecord record: records){
            result.add(record.get(fieldName));
        }

        return result;
    }

    private List readValuesFromFile() throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));

        List result = new ArrayList<String>();

        String line;
        while ((line = br.readLine()) != null) {
            // skip empty lines
            if (line.length() == 0) {  continue; }
            // skip comments
            if (line.substring(0,1).equals("%")) {  continue; }
            // split data
            result.add(line.split(Pattern.quote(";")));
        }
        br.close();

        return result;
    }
}
