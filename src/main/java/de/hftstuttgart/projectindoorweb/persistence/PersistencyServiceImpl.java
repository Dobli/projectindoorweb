package de.hftstuttgart.projectindoorweb.persistence;

import de.hftstuttgart.projectindoorweb.application.internal.AssertParam;
import de.hftstuttgart.projectindoorweb.persistence.entities.*;
import de.hftstuttgart.projectindoorweb.persistence.repositories.BuildingRepository;
import de.hftstuttgart.projectindoorweb.persistence.repositories.EvaalFileRepository;
import de.hftstuttgart.projectindoorweb.persistence.repositories.ProjectRepository;
import de.hftstuttgart.projectindoorweb.positionCalculator.CalculationAlgorithm;
import de.hftstuttgart.projectindoorweb.web.internal.requests.building.AddNewBuildingPositionAnchor;
import de.hftstuttgart.projectindoorweb.web.internal.requests.project.SaveNewProjectParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PersistencyServiceImpl implements PersistencyService {

    @Override
    public long createNewProject(String projectName, String algorithmType, Set<SaveNewProjectParameters> saveNewProjectParamaters) {

        AssertParam.throwIfNullOrEmpty(projectName,"projectName");
        AssertParam.throwIfNullOrEmpty(algorithmType,"algorithmType");
        AssertParam.throwIfNull(saveNewProjectParamaters,"saveNewProjectParamaters");

        CalculationAlgorithm calculationAlgorithm = getAlgorithmFromText(algorithmType);

        if(calculationAlgorithm==null){
            return -1;
        }

        List<Parameter> parametersAsList = convertToEntityParameters(saveNewProjectParamaters);

        Project newProject = new Project(projectName, calculationAlgorithm, parametersAsList);

        ProjectRepository projectRepository = (ProjectRepository) RepositoryRegistry.getRepositoryByEntityName(Project.class.getName());
        newProject = projectRepository.save(newProject);

        return newProject.getId();

    }

    @Override
    public boolean updateProject(long projectId, String newProjectName, String newAlgorithmType, Set<SaveNewProjectParameters> newSaveNewProjectParamaters) {

        AssertParam.throwIfNull(projectId,"projectId");
        AssertParam.throwIfNullOrEmpty(newProjectName,"newProjectName");
        AssertParam.throwIfNullOrEmpty(newAlgorithmType,"newAlgorithmType");
        AssertParam.throwIfNull(newSaveNewProjectParamaters,"newSaveNewProjectParamaters");

        ProjectRepository projectRepository = (ProjectRepository) RepositoryRegistry.getRepositoryByEntityName(Project.class.getName());

        Project project = projectRepository.findOne(projectId);
        CalculationAlgorithm requestedAlgorithm = getAlgorithmFromText(newAlgorithmType);

        if(project != null && requestedAlgorithm != null){
            project.setProjectName(newProjectName);
            project.setCalculationAlgorithm(requestedAlgorithm);
            project.setProjectParameters(convertToEntityParameters(newSaveNewProjectParamaters));
            projectRepository.save(project);
            return true;
        }

        return false;

    }

    @Override
    public boolean updateProject(Project project) {

        AssertParam.throwIfNull(project,"project");

        ProjectRepository projectRepository = (ProjectRepository) RepositoryRegistry.getRepositoryByEntityName(Project.class.getName());

        Project fromDatabase = projectRepository.findOne(project.getId());

        if(project != null){
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

        AssertParam.throwIfNull(projectId,"projectId");

        ProjectRepository projectRepository = (ProjectRepository) RepositoryRegistry.getRepositoryByEntityName(Project.class.getName());

        projectRepository.delete(projectId);
        Project project = projectRepository.findOne(projectId);

        return project == null;

    }

    @Override
    public boolean addNewBuilding(String buildingName, int numberOfFloors, int imagePixelWidth, int imagePixelHeight,
                                  AddNewBuildingPositionAnchor southEastAnchor, AddNewBuildingPositionAnchor southWestAnchor,
                                  AddNewBuildingPositionAnchor northEastAnchor, AddNewBuildingPositionAnchor northWestAnchor) {

        AssertParam.throwIfNullOrEmpty(buildingName,"buildingName");
        AssertParam.throwIfNull(numberOfFloors,"numberOfFloors");
        AssertParam.throwIfNull(imagePixelWidth, "imagePixelWidth");
        AssertParam.throwIfNull(imagePixelHeight, "imagePixelHeigth");
        AssertParam.throwIfNull(southEastAnchor,"southEastAnchor");
        AssertParam.throwIfNull(southWestAnchor,"southWestAnchor");
        AssertParam.throwIfNull(northEastAnchor,"northEastAnchor");
        AssertParam.throwIfNull(northWestAnchor,"northWestAnchor");

        Position northWestPosition = new Position(northWestAnchor.getLatitude(), northWestAnchor.getLongitude(), 0.0, true);
        Position northEastPosition = new Position(northEastAnchor.getLatitude(), northEastAnchor.getLongitude(), 0.0, true);
        Position southEastPosition = new Position(southEastAnchor.getLatitude(), southEastAnchor.getLongitude(), 0.0, true);
        Position southWestPosition = new Position(southWestAnchor.getLatitude(), southWestAnchor.getLongitude(), 0.0, true);

        Building buildingToBeSaved = new Building(buildingName, numberOfFloors, imagePixelWidth, imagePixelHeight, northWestPosition,
                northEastPosition, southEastPosition, southWestPosition);

        BuildingRepository buildingRepository = (BuildingRepository) RepositoryRegistry.getRepositoryByEntityName(Building.class.getName());
        buildingToBeSaved = buildingRepository.save(buildingToBeSaved);

        return buildingToBeSaved != null;

    }

    @Override
    public Project getProjectById(long projectId) {

        AssertParam.throwIfNull(projectId,"projectId");

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

        AssertParam.throwIfNull(buildingId,"buildingId");

        BuildingRepository buildingRepository = (BuildingRepository)RepositoryRegistry.getRepositoryByEntityName(Building.class.getName());

        return buildingRepository.findOne(buildingId);

    }

    @Override
    public boolean saveEvaalFiles(List<EvaalFile> evaalFiles) {

        AssertParam.throwIfNull(evaalFiles,"evaalFiles");

        EvaalFileRepository evaalFileRepository = (EvaalFileRepository) RepositoryRegistry.getRepositoryByEntityName(EvaalFile.class.getName());

        Iterable<EvaalFile> saved = evaalFileRepository.save(evaalFiles);

        return saved != null;

    }

    @Override
    public EvaalFile getEvaalFileForId(long evaalFileId) {

        AssertParam.throwIfNull(evaalFileId,"evaalFileId");

        EvaalFileRepository evaalFileRepository = (EvaalFileRepository) RepositoryRegistry.getRepositoryByEntityName(EvaalFile.class.getName());

        return evaalFileRepository.findOne(evaalFileId);
    }


    @Override
    public List<EvaalFile> getEvaluationFilesForBuilding(Building building) {

        AssertParam.throwIfNull(building,"building");

        EvaalFileRepository evaalFileRepository = (EvaalFileRepository) RepositoryRegistry.getRepositoryByEntityName(EvaalFile.class.getName());

        return evaalFileRepository.findByRecordedInBuildingAndAndEvaluationFileTrue(building);

    }

    @Override
    public List<EvaalFile> getRadioMapFilesForBuiling(Building building) {

        AssertParam.throwIfNull(building,"building");

        EvaalFileRepository evaalFileRepository = (EvaalFileRepository) RepositoryRegistry.getRepositoryByEntityName(EvaalFile.class.getName());

        return evaalFileRepository.findByRecordedInBuildingAndEvaluationFileFalse(building);

    }


    private List<Parameter> convertToEntityParameters(Set<SaveNewProjectParameters> saveNewProjectParamaters){

        List<Parameter> parametersAsList = new ArrayList<>();

        for (SaveNewProjectParameters parameter:
                saveNewProjectParamaters) {
            parametersAsList.add(new Parameter(parameter.getName(), parameter.getValue()));
        }

        return parametersAsList;


    }

    private CalculationAlgorithm getAlgorithmFromText(String text){

        CalculationAlgorithm calculationAlgorithm = null;
        if(text.equals("WIFI")){
            calculationAlgorithm = CalculationAlgorithm.WIFI;
        }

        return calculationAlgorithm;

    }


}
