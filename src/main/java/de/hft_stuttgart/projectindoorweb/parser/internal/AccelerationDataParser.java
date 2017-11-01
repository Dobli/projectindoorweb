package de.hft_stuttgart.projectindoorweb.parser.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class AccelerationDataParser extends SensorDataParser {

    public AccelerationDataParser(String filePath) {
        super(filePath);
    }

    @Override
    public void run() {
        try {
            Files.readAllLines(Paths.get(super.filePath)).stream().filter(s -> {

                return isValidLine(s);

            }).map(s -> {

                String[] elements = s.split(";");
                String rawName = elements[0];
                long appTimestamp = Long.valueOf(elements[1].replace(".", ""));
                long sensorTimestamp = Long.valueOf(elements[2].replace(".", ""));
                float accX = Float.valueOf(elements[3]);
                float accY = Float.valueOf(elements[4]);
                float accZ = Float.valueOf(elements[5]);
                String dataUnit = "m/s^²";
                int accuracy = Integer.valueOf(elements[6]);

                return elements;// TODO: Adapt to the database. new AccelerometerData(super.logFile.getId(), rawName, appTimestamp, sensorTimestamp, accX, accY, accZ, dataUnit, accuracy);


            }).collect(Collectors.toCollection(() -> super.parsedDataList));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected boolean isValidLine(String line) {
        return !line.contains("%")
                && !line.isEmpty()
                && line.split(";")[0].equals("ACCE")
                && line.split(";").length == 7;
    }
}
