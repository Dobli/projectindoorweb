package de.hft_stuttgart.projectindoorweb.parser.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WifiDataParser extends  SensorDataParser {
    public WifiDataParser(String filePath) {
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
                String name = elements[3];
                String mac = elements[4];
                float rssi = Float.valueOf(elements[5]);
                String dataUnit = "dBm";

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
                && line.split(";")[0].equals("WIFI")
                && line.split(";").length == 6
                && isValidMac(line);
    }

    protected boolean isValidMac(String line) {
        String[] elements = line.split(";");
        Pattern pattern = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
        Matcher matcher = pattern.matcher(elements[4]);

        return matcher.matches();
    }
}