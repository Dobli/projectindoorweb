package de.hftstuttgart.projectindoorweb.persistence;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.hftstuttgart.projectindoorweb.application.internal.AssertParam;
import de.hftstuttgart.projectindoorweb.geoCalculator.internal.LatLongCoord;
import de.hftstuttgart.projectindoorweb.geoCalculator.transformation.TransformationHelper;
import de.hftstuttgart.projectindoorweb.persistence.entities.*;
import de.hftstuttgart.projectindoorweb.persistence.repositories.BuildingRepository;
import de.hftstuttgart.projectindoorweb.persistence.repositories.EvaalFileRepository;
import de.hftstuttgart.projectindoorweb.persistence.repositories.ProjectRepository;
import de.hftstuttgart.projectindoorweb.persistence.repositories.RadioMapRepository;
import de.hftstuttgart.projectindoorweb.positionCalculator.CalculationAlgorithm;
import de.hftstuttgart.projectindoorweb.web.internal.requests.building.BuildingPositionAnchor;
import de.hftstuttgart.projectindoorweb.web.internal.requests.project.SaveNewProjectParameters;
import de.hftstuttgart.projectindoorweb.web.internal.util.TransmissionHelper;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PersistencyServiceImpl implements PersistencyService {

    @Override
    public long createNewProject(String projectName, String algorithmType, Set<SaveNewProjectParameters> saveNewProjectParameters,
                                 long buildingIdentifier, long evalFileIdentifier, long[] radioMapFileIdentifiers) {

        AssertParam.throwIfNullOrEmpty(projectName, "projectName");
        AssertParam.throwIfNullOrEmpty(algorithmType, "algorithmType");
        AssertParam.throwIfNull(saveNewProjectParameters, "saveNewProjectParameters");
        AssertParam.throwIfNull(buildingIdentifier, "buildingIdentifier");
        AssertParam.throwIfNull(evalFileIdentifier, "evalFileIdentifier");
        AssertParam.throwIfNull(radioMapFileIdentifiers, "radioMapFileIdentifiers");

        BuildingRepository buildingRepository = (BuildingRepository) RepositoryRegistry.getRepositoryByEntityName(Building.class.getName());
        EvaalFileRepository evaalFileRepository = (EvaalFileRepository) RepositoryRegistry.getRepositoryByEntityName(EvaalFile.class.getName());
        RadioMapRepository radioMapRepository = (RadioMapRepository) RepositoryRegistry.getRepositoryByEntityName(RadioMap.class.getName());

        CalculationAlgorithm calculationAlgorithm = getAlgorithmFromText(algorithmType);

        Building building = buildingRepository.findOne(buildingIdentifier);
        EvaalFile evaalFile = evaalFileRepository.findOne(evalFileIdentifier);
        List<RadioMap> radioMaps = getAllRadioMaps(radioMapRepository, radioMapFileIdentifiers);

        if (calculationAlgorithm == null || building == null || evaalFile == null ) {// TODO check if radio map files should always be filled with radio maps or if it is ok if the list is empty
            return -1;
        }

        List evaalFileList = new ArrayList<>(); //TODO Check if really a list of files is necessary or if implementation is not correct
        evaalFileList.add(evaalFile);

        List<Parameter> parametersAsList = convertToEntityParameters(saveNewProjectParameters);

        Project newProject = new Project(projectName, calculationAlgorithm, parametersAsList, evaalFileList, building, radioMaps );

        ProjectRepository projectRepository = (ProjectRepository) RepositoryRegistry.getRepositoryByEntityName(Project.class.getName());
        newProject = projectRepository.save(newProject);

        return newProject.getId();

    }

    @Override
    public boolean updateProject(long projectId, String newProjectName, String newAlgorithmType, Set<SaveNewProjectParameters> newSaveNewProjectParameters,
                                 long buildingIdentifier, long evalFileIdentifier, long[] radioMapFileIdentifiers) {

        AssertParam.throwIfNull(projectId, "projectId");
        AssertParam.throwIfNullOrEmpty(newProjectName, "newProjectName");
        AssertParam.throwIfNullOrEmpty(newAlgorithmType, "newAlgorithmType");
        AssertParam.throwIfNull(newSaveNewProjectParameters, "newSaveNewProjectParameters");
        AssertParam.throwIfNull(buildingIdentifier, "buildingIdentifier");
        AssertParam.throwIfNull(evalFileIdentifier, "evalFileIdentifier");
        AssertParam.throwIfNull(radioMapFileIdentifiers, "radioMapFileIdentifiers");

        ProjectRepository projectRepository = (ProjectRepository) RepositoryRegistry.getRepositoryByEntityName(Project.class.getName());
        BuildingRepository buildingRepository = (BuildingRepository) RepositoryRegistry.getRepositoryByEntityName(Building.class.getName());
        EvaalFileRepository evaalFileRepository = (EvaalFileRepository) RepositoryRegistry.getRepositoryByEntityName(EvaalFile.class.getName());
        RadioMapRepository radioMapRepository = (RadioMapRepository) RepositoryRegistry.getRepositoryByEntityName(RadioMap.class.getName());

        Project project = projectRepository.findOne(projectId);
        Building building = buildingRepository.findOne(buildingIdentifier);
        EvaalFile evaalFile = evaalFileRepository.findOne(evalFileIdentifier);
        List<RadioMap> radioMaps = getAllRadioMaps(radioMapRepository, radioMapFileIdentifiers);

        CalculationAlgorithm requestedAlgorithm = getAlgorithmFromText(newAlgorithmType);

        if (project != null && requestedAlgorithm != null && building != null && evaalFile != null) {// TODO check if radio map files should always be filled with radio maps or if it is ok if the list is empty

            List evaalFileList = new ArrayList<>(); //TODO Check if really a list of files is necessary or if implementation is not correct
            evaalFileList.add(evaalFile);

            project.setProjectName(newProjectName);
            project.setCalculationAlgorithm(requestedAlgorithm);
            project.setProjectParameters(convertToEntityParameters(newSaveNewProjectParameters));
            project.setProjectParameters(convertToEntityParameters(newSaveNewProjectParameters));
            project.setBuilding(building);
            project.setEvaalFiles(evaalFileList);
            project.setRadioMaps(radioMaps);
            projectRepository.save(project);
            return true;
        }

        return false;

    }

    @Override
    public boolean updateProject(Project project) { //TODO Redundant? Parameters are just wrapped inside an object but data is still the same as above

        AssertParam.throwIfNull(project, "project");

        ProjectRepository projectRepository = (ProjectRepository) RepositoryRegistry.getRepositoryByEntityName(Project.class.getName());

        Project fromDatabase = projectRepository.findOne(project.getId());

        if (project != null) {
            fromDatabase.setProjectName(project.getProjectName());
            fromDatabase.setProjectParameters(project.getProjectParameters());
            fromDatabase.setCalculationAlgorithm(project.getCalculationAlgorithm());
            fromDatabase.setEvaalFiles(project.getEvaalFiles());
            projectRepository.save(fromDatabase);
            return true;
        }

        return false;

    }


    @Override
    public boolean deleteProject(long projectId) {

        AssertParam.throwIfNull(projectId, "projectId");

        ProjectRepository projectRepository = (ProjectRepository) RepositoryRegistry.getRepositoryByEntityName(Project.class.getName());

        projectRepository.delete(projectId);
        Project project = projectRepository.findOne(projectId);

        return project == null;

    }

    @Override
    public boolean addNewBuilding(String buildingName, int numberOfFloors, int imagePixelWidth, int imagePixelHeight,
                                  BuildingPositionAnchor southEastAnchor, BuildingPositionAnchor southWestAnchor,
                                  BuildingPositionAnchor northEastAnchor, BuildingPositionAnchor northWestAnchor,
                                  BuildingPositionAnchor buildingCenterPoint, double rotationAngle, double metersPerPixel) {

        AssertParam.throwIfNullOrEmpty(buildingName, "buildingName");
        AssertParam.throwIfNull(numberOfFloors, "numberOfFloors");
        AssertParam.throwIfNull(imagePixelWidth, "imagePixelWidth");
        AssertParam.throwIfNull(imagePixelHeight, "imagePixelHeigth");

        if ((southEastAnchor == null || southWestAnchor == null || northEastAnchor == null || northWestAnchor == null)
                && (buildingCenterPoint == null)) {

            AssertParam.throwIfNull(northWestAnchor, "northWestAnchor");
            AssertParam.throwIfNull(northEastAnchor, "northEastAnchor");
            AssertParam.throwIfNull(southEastAnchor, "southEastAnchor");
            AssertParam.throwIfNull(southWestAnchor, "southWestAnchor");
            AssertParam.throwIfNull(buildingCenterPoint, "buildingCenterPoint");

        }


        if(northWestAnchor == null){
            northWestAnchor = retrievePositionAnchor(buildingCenterPoint, rotationAngle, metersPerPixel,
                    imagePixelWidth, imagePixelHeight, "northWest");
        }

        if(northEastAnchor == null){
            northEastAnchor = retrievePositionAnchor(buildingCenterPoint, rotationAngle, metersPerPixel,
                    imagePixelWidth, imagePixelHeight, "northEast");
        }

        if(southEastAnchor == null){
            southEastAnchor = retrievePositionAnchor(buildingCenterPoint, rotationAngle, metersPerPixel,
                    imagePixelWidth, imagePixelHeight, "southEast");
        }

        if(southWestAnchor == null){
            southWestAnchor = retrievePositionAnchor(buildingCenterPoint, rotationAngle, metersPerPixel,
                    imagePixelWidth, imagePixelHeight, "southWest");
        }

        Position northWestPosition = TransmissionHelper.convertPositionAnchorToPosition(northWestAnchor);
        Position northEastPosition = TransmissionHelper.convertPositionAnchorToPosition(northEastAnchor);
        Position southEastPosition = TransmissionHelper.convertPositionAnchorToPosition(southEastAnchor);
        Position southWestPosition = TransmissionHelper.convertPositionAnchorToPosition(southWestAnchor);

        Position buildingCenterPosition = null;
        if(buildingCenterPoint != null){
            buildingCenterPosition = new Position(buildingCenterPoint.getLatitude(), buildingCenterPoint.getLongitude(), 0.0, true);
        }

        Building buildingToBeSaved = new Building(buildingName, numberOfFloors, imagePixelWidth, imagePixelHeight,
                rotationAngle, metersPerPixel, northWestPosition, northEastPosition, southEastPosition,
                southWestPosition, buildingCenterPosition);

        BuildingRepository buildingRepository = (BuildingRepository) RepositoryRegistry.getRepositoryByEntityName(Building.class.getName());
        buildingToBeSaved = buildingRepository.save(buildingToBeSaved);

        return buildingToBeSaved != null;
    }


    @Override
    public Project getProjectById(long projectId) {

        AssertParam.throwIfNull(projectId, "projectId");

        ProjectRepository projectRepository = (ProjectRepository) RepositoryRegistry.getRepositoryByEntityName(Project.class.getName());

        return projectRepository.findOne(projectId);

    }

    @Override
    public List<Project> getAllProjects() {

        ProjectRepository projectRepository = (ProjectRepository) RepositoryRegistry.getRepositoryByEntityName(Project.class.getName());

        return (List<Project>) projectRepository.findAll();

    }

    @Override
    public List<Building> getAllBuildings() {

        BuildingRepository buildingRepository = (BuildingRepository) RepositoryRegistry.getRepositoryByEntityName(Building.class.getName());

        return (List<Building>) buildingRepository.findAll();


    }

    @Override
    public Building getBuildingById(long buildingId) {

        AssertParam.throwIfNull(buildingId, "buildingId");

        BuildingRepository buildingRepository = (BuildingRepository) RepositoryRegistry.getRepositoryByEntityName(Building.class.getName());

        return buildingRepository.findOne(buildingId);

    }

    @Override
    public boolean updateBuilding(long buildingId, String buildingName, int imagePixelWidth, int imagePixelHeight,
                                  Position northWest, Position northEast, Position southEast, Position southWest, Position buildingCenterPoint,
                                  double rotationAngle, double metersPerPixel) {

        AssertParam.throwIfNull(buildingId, "buildingId");
        AssertParam.throwIfNullOrEmpty(buildingName, "buildingName");
        AssertParam.throwIfNull(imagePixelWidth, "imagePixelWidth");
        AssertParam.throwIfNull(imagePixelHeight, "imagePixelHeight");
        AssertParam.throwIfNull(northWest, "northWest");
        AssertParam.throwIfNull(northEast, "northEast");
        AssertParam.throwIfNull(southEast, "southEast");
        AssertParam.throwIfNull(southWest, "southWest");
        AssertParam.throwIfNull(buildingCenterPoint, "buildingCenterPoint");
        AssertParam.throwIfNull(rotationAngle, "rotationAngle");
        AssertParam.throwIfNull(metersPerPixel, "metersPerPixel");

        Building buildingToBeUpdated = this.getBuildingById(buildingId);

        if(buildingToBeUpdated != null){
            buildingToBeUpdated.setBuildingName(buildingName);
            buildingToBeUpdated.setImagePixelWidth(imagePixelWidth);
            buildingToBeUpdated.setImagePixelHeight(imagePixelHeight);
            buildingToBeUpdated.setNorthWest(northWest);
            buildingToBeUpdated.setNorthEast(northEast);
            buildingToBeUpdated.setSouthEast(southEast);
            buildingToBeUpdated.setSouthWest(southWest);
            buildingToBeUpdated.setCenterPoint(buildingCenterPoint);
            buildingToBeUpdated.setRotationAngle(rotationAngle);
            buildingToBeUpdated.setMetersPerPixel(metersPerPixel);

            BuildingRepository buildingRepository = (BuildingRepository) RepositoryRegistry.getRepositoryByEntityName(Building.class.getName());
            return buildingRepository.save(buildingToBeUpdated) != null;
        }

        return false;


    }

    @Override
    public boolean saveEvaalFiles(List<EvaalFile> evaalFiles) {

        AssertParam.throwIfNull(evaalFiles, "evaalFiles");

        EvaalFileRepository evaalFileRepository = (EvaalFileRepository) RepositoryRegistry.getRepositoryByEntityName(EvaalFile.class.getName());

        Iterable<EvaalFile> saved = evaalFileRepository.save(evaalFiles);

        return saved != null;

    }

    @Override
    public EvaalFile getEvaalFileForId(long evaalFileId) {

        AssertParam.throwIfNull(evaalFileId, "evaalFileId");

        EvaalFileRepository evaalFileRepository = (EvaalFileRepository) RepositoryRegistry.getRepositoryByEntityName(EvaalFile.class.getName());

        return evaalFileRepository.findOne(evaalFileId);
    }


    @Override
    public List<EvaalFile> getEvaluationFilesForBuilding(Building building) {

        AssertParam.throwIfNull(building, "building");

        EvaalFileRepository evaalFileRepository = (EvaalFileRepository) RepositoryRegistry.getRepositoryByEntityName(EvaalFile.class.getName());

        return evaalFileRepository.findByRecordedInBuildingAndAndEvaluationFileTrue(building);

    }

    @Override
    public List<EvaalFile> getRadioMapFilesForBuiling(Building building) {

        AssertParam.throwIfNull(building, "building");

        EvaalFileRepository evaalFileRepository = (EvaalFileRepository) RepositoryRegistry.getRepositoryByEntityName(EvaalFile.class.getName());

        return evaalFileRepository.findByRecordedInBuildingAndEvaluationFileFalse(building);

    }


    private List<Parameter> convertToEntityParameters(Set<SaveNewProjectParameters> saveNewProjectParamaters) {

        List<Parameter> parametersAsList = new ArrayList<>();

        for (SaveNewProjectParameters parameter :
                saveNewProjectParamaters) {
            parametersAsList.add(new Parameter(parameter.getName(), parameter.getValue()));
        }

        return parametersAsList;


    }

    private BuildingPositionAnchor retrievePositionAnchor(BuildingPositionAnchor buildingCenterPoint,
                                            double rotationAngle, double metersPerPixel,
                                            int imagePixelWidth, int imagePixelHeight,
                                            String corner){

        String capitalizedCorner = corner.toUpperCase();

        double[] calculatedLatLong;
        switch(capitalizedCorner){
            case "NORTHWEST":
                calculatedLatLong = TransformationHelper.calculateNorthWestCorner(new LatLongCoord(buildingCenterPoint.getLatitude(),
                        buildingCenterPoint.getLongitude()), rotationAngle, metersPerPixel, imagePixelWidth, imagePixelHeight);
                break;
            case "NORTHEAST":
                calculatedLatLong = TransformationHelper.calculateNorthEastCorner(new LatLongCoord(buildingCenterPoint.getLatitude(),
                        buildingCenterPoint.getLongitude()), rotationAngle, metersPerPixel, imagePixelWidth, imagePixelHeight);
                break;
            case "SOUTHEAST":
                calculatedLatLong = TransformationHelper.calculateSouthEastCorner(new LatLongCoord(buildingCenterPoint.getLatitude(),
                        buildingCenterPoint.getLongitude()), rotationAngle, metersPerPixel, imagePixelWidth, imagePixelHeight);
                break;
            case "SOUTHWEST":
                calculatedLatLong = TransformationHelper.calculateSouthWestCorner(new LatLongCoord(buildingCenterPoint.getLatitude(),
                        buildingCenterPoint.getLongitude()), rotationAngle, metersPerPixel, imagePixelWidth, imagePixelHeight);
                break;
                default:
                    calculatedLatLong = null;
        }

        return new BuildingPositionAnchor(calculatedLatLong[0], calculatedLatLong[1]);

    }



    private CalculationAlgorithm getAlgorithmFromText(String text) {

        CalculationAlgorithm calculationAlgorithm = null;
        if (text.equals("WIFI")) {
            calculationAlgorithm = CalculationAlgorithm.WIFI;
        }

        return calculationAlgorithm;

    }


    private List<RadioMap> getAllRadioMaps(RadioMapRepository radioMapRepository, long[] radioMapFileIdentifiers) {
        List<RadioMap> radioMaps = new ArrayList<>();

        for(long radioMapFileIdentifier : radioMapFileIdentifiers){
            RadioMap radioMap = radioMapRepository.findOne(radioMapFileIdentifier);
            if(radioMap != null){
                radioMaps.add(radioMap);
            }
        }

        return radioMaps;
    }


}
