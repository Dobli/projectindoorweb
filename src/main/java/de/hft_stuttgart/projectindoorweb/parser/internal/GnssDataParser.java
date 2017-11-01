package de.hft_stuttgart.projectindoorweb.parser.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class GnssDataParser extends SensorDataParser{

    public GnssDataParser(String filePath) {
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
                float lat = Float.valueOf(elements[3]);
                float lon = Float.valueOf(elements[4]);
                float alt = Float.valueOf(elements[5]);
                float bearing = Float.valueOf(elements[6]);
                float accuracy = Float.valueOf(elements[7]);
                float speed = Float.valueOf(elements[8]);
                int satView = Integer.valueOf(elements[9]);
                int satUse = Integer.valueOf(elements[10]);

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
                && line.split(";")[0].equals("GNSS")
                && line.split(";").length == 11;
    }
}
