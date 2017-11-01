package de.hft_stuttgart.projectindoorweb.parser;

import de.hft_stuttgart.projectindoorweb.parser.internal.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class SensorFileReaderServiceImpl implements SensorFileReaderService {

    private final static String FILEPATH="";//TODO find out how to get the filepath. Local constant? Through another service? As a parameter?
    private final static int TIMEOUT=10000;

    private ExecutorService executorService;

    public SensorFileReaderServiceImpl(ExecutorService executorService){
        this.executorService = executorService;
    }

    @Override
    public List parseWifiData() {
        WifiDataParser wifiDataParser = new WifiDataParser(FILEPATH);
        return parseData(wifiDataParser);
    }

    @Override
    public List parsePositionData() {
        PositionDataParser positionDataParser = new PositionDataParser(FILEPATH);
        return parseData(positionDataParser);
    }

    @Override
    public List parseGyroscopeData() {
        GyroscopeDataParser gyroscopeDataParser = new GyroscopeDataParser(FILEPATH);
        return parseData(gyroscopeDataParser);
    }

    @Override
    public List parseAccelerationData() {
        AccelerationDataParser accelerationDataParser = new AccelerationDataParser(FILEPATH);
        return parseData(accelerationDataParser);
    }

    @Override
    public List parseMagnetometerData() {
        MagnetometerDataParser magnetometerDataParser = new MagnetometerDataParser(FILEPATH);
        return parseData(magnetometerDataParser);
    }

    @Override
    public List parseGnssData() {
        GnssDataParser gnssDataParser = new GnssDataParser(FILEPATH);
        return parseData(gnssDataParser);
    }

    @Override
    public List parseLightData() {
        LightDataParser lightDataParser = new LightDataParser(FILEPATH);
        return parseData(lightDataParser);
    }

    //TODO Shutdown may shut all threads down, not just the one who calls it. Check if viable through testing.
    private List parseData(SensorDataParser sensorDataParser){

        executorService.execute(sensorDataParser);

        try {
            executorService.shutdown();
            executorService.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return sensorDataParser.getParsedDataList();

    }
}
