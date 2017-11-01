package de.hft_stuttgart.projectindoorweb.parser.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class LightDataParser extends SensorDataParser{
    public LightDataParser(String filePath) {
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
                float light = Float.valueOf(elements[3]);
                String dataUnit = "lux";
                int accuracy = Integer.valueOf(elements[4]);

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
                && line.split(";")[0].equals("LIGH")
                && line.split(";").length == 5;
    }
}