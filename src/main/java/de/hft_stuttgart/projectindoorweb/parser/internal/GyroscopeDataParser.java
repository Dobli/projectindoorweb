package de.hft_stuttgart.projectindoorweb.parser.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class GyroscopeDataParser extends  SensorDataParser{
    public GyroscopeDataParser(String filePath) {
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
                float gyrX = Float.valueOf(elements[3]);
                float gyrY = Float.valueOf(elements[4]);
                float gyrZ = Float.valueOf(elements[5]);
                String dataUnit = "rad/s";
                int accuracy = Integer.valueOf(elements[6]);

                return elements;// TODO: Adapt to the database

            }).collect(Collectors.toCollection(() -> super.parsedDataList));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected boolean isValidLine(String line) {
        return !line.contains("%")
                && !line.isEmpty()
                && line.split(";")[0].equals("GYRO")
                && line.split(";").length == 7;
    }
}
