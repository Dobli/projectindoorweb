package de.hft_stuttgart.projectindoorweb.parser;

import java.util.concurrent.ExecutorService;

/**
 *  Sensor file reader component class to initialize and dispose of the sensor file reader service.
 *  Do not use the <class>{@link SensorFileReaderServiceImpl}</class> class implementation directly. Only initialize
 *  this class once through @initComponent on startup and access the object through the @getDeviceAccessor method.
 */
public class SensorFileReaderComponent {

    private static SensorFileReaderService instance;

    public static void initComponent(ExecutorService executorService)
    {
        instance = new SensorFileReaderServiceImpl(executorService);
    }

    public static void disposeComponent()
    {
        if (instance != null)
        {
            instance = null;
        }
        else
        {
            System.out.print("Sensor file reader service was not initialised when trying to dispose component.");
        }

    }

    public static SensorFileReaderService getDeviceAccessor()
    {
        if (instance != null)
        {
            return instance;
        }

        throw new IllegalStateException ("Sensor file reader service is not initialized.");
    }
}
