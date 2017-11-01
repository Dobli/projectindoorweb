package de.hft_stuttgart.projectindoorweb.parser;

import java.util.List;

public interface SensorFileReaderService {

    List parseWifiData();

    List parsePositionData();

    List parseGyroscopeData();

    List parseAccelerationData();

    List parseMagnetometerData();

    List parseGnssData();

    List parseLightData();
}
