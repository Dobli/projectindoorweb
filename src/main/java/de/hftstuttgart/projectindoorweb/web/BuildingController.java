package de.hftstuttgart.projectindoorweb.web;

import de.hftstuttgart.projectindoorweb.web.internal.HttpResultHandler;
import de.hftstuttgart.projectindoorweb.web.internal.ResponseWrapper;
import de.hftstuttgart.projectindoorweb.web.internal.requests.building.AddNewBuilding;
import de.hftstuttgart.projectindoorweb.web.internal.requests.building.GetAllBuildings;
import de.hftstuttgart.projectindoorweb.web.internal.requests.building.GetSingleBuilding;
import de.hftstuttgart.projectindoorweb.web.internal.requests.building.UpdateBuilding;
import de.hftstuttgart.projectindoorweb.web.internal.util.TransmissionConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * REST resource handling building operations. Has access to RestTransmissionService. Do not put any logic in here!
 */
@Controller
@RestController
@Api(value = "Building", description = "Provides operations for interaction with Buildings.", tags = "Building")
@RequestMapping("/building")
public class BuildingController {

    @Autowired
    private RestTransmissionService restTransmissionService;

    @ApiOperation(value = "Add a new building", nickname = "building/addNewBuilding",
            notes = TransmissionConstants.ADD_NEW_BUILDING_NOTE)
    @RequestMapping(path = "/addNewBuilding", method = POST)
    public ResponseEntity addNewBuilding(@RequestBody AddNewBuilding buildingJsonWrapper) {

        ResponseWrapper result = restTransmissionService.addNewBuilding(buildingJsonWrapper);
        return HttpResultHandler.getInstance().handleLongBuildingResult(result);
    }

    @ApiOperation(value = "Get all buildings", nickname = "building/getAllBuildings", notes = TransmissionConstants.GET_ALL_BUILDINGS_NOTE)
    @RequestMapping(path = "/getAllBuildings", method = GET)
    public ResponseEntity<List<GetAllBuildings>> getAllBuildings() {
        List<GetAllBuildings> result = restTransmissionService.getAllBuildings();
        return new ResponseEntity<List<GetAllBuildings>>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieves detailed information about a single building", nickname = "building/getBuildingByBuildingId",
            notes = TransmissionConstants.GET_SINGLE_BUILDING_NOTE)
    @RequestMapping(path = "/getBuildingByBuildingId", method = GET)
    public ResponseEntity<GetSingleBuilding> getBuildingByBuildingId(
            @RequestParam(value = TransmissionConstants.BUILDING_IDENTIFIER_PARAM,
                    defaultValue = TransmissionConstants.EMPTY_STRING_VALUE)
                    String buildingIdentifier) {
        GetSingleBuilding result = restTransmissionService.getSingleBuilding(buildingIdentifier);
        return new ResponseEntity<GetSingleBuilding>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Updates a given building by the given JSON body.", nickname = "building/updateBuilding",
            notes = TransmissionConstants.UPDATE_BUILDING_NOTE)
    @RequestMapping(path = "/updateBuilding", method = POST)
    public ResponseEntity updateBuilding(@RequestBody UpdateBuilding updateBuilding) {

        String operationResult = restTransmissionService.updateBuilding(updateBuilding);
        return HttpResultHandler.getInstance().handleSimpleBuildingResult(operationResult);

    }

    @ApiOperation(value = "Retrieves the floor image file of the given floor identifier", nickname = "building/getFloorMap",
            notes = TransmissionConstants.GET_FLOOR_MAP_NOTE)
    @RequestMapping(path = TransmissionConstants.GET_FLOOR_MAP_REST_URL,
            method = GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<InputStreamResource> getFloorMap(
            @RequestParam(value = TransmissionConstants.FLOOR_IDENTIFIER_PARAM,
                    defaultValue = TransmissionConstants.EMPTY_STRING_VALUE)
                    String floorIdentifier) {

        File result = restTransmissionService.getFloorMap(floorIdentifier);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        try {

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(new InputStreamResource(new FileInputStream(result)));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

    }

    @ApiOperation(value = "Deletes a selected building with a given building identifier",
            nickname = "project/deleteBuilding", notes = TransmissionConstants.DELETE_BUILDING_NOTE)
    @RequestMapping(path = "/deleteSelectedBuilding", method = DELETE)
    public ResponseEntity deleteSelectedBuilding(@RequestParam(value = TransmissionConstants.BUILDING_IDENTIFIER_PARAM,
            defaultValue = TransmissionConstants.EMPTY_STRING_VALUE)
                                                         String buildingIdentifier) {
        String operationResult = restTransmissionService.deleteBuilding(buildingIdentifier);
        return HttpResultHandler.getInstance().handleSimpleBuildingResult(operationResult);
    }

    @ApiOperation(value = "Add a new floor to a given building", nickname = "building/addFloorToBuilding",
            notes = TransmissionConstants.ADD_FLOOR_TO_BUILDING_NOTE)
    @RequestMapping(path = "/addFloorToBuilding", method = POST)
    public ResponseEntity addFloorToBuilding(@RequestParam(value = TransmissionConstants.BUILDING_IDENTIFIER_PARAM,
            defaultValue = TransmissionConstants.EMPTY_STRING_VALUE)
                                                     String buildingIdentifier,
                                             @RequestParam(value = TransmissionConstants.FLOOR_IDENTIFIER_PARAM,
                                                     defaultValue = TransmissionConstants.EMPTY_STRING_VALUE)
                                                     String floorIdentifier,
                                             @RequestParam(value = TransmissionConstants.FLOOR_NAME_PARAM,
                                                     defaultValue = TransmissionConstants.EMPTY_STRING_VALUE)
                                                     String floorName,
                                             @RequestParam(value = TransmissionConstants.FLOOR_MAP_FILE_PARAM)
                                                     MultipartFile floorMapFile) {
        String operationResult = restTransmissionService.addFloorToBuilding(buildingIdentifier, floorIdentifier, floorName, floorMapFile);
        return HttpResultHandler.getInstance().handleSimpleBuildingResult(operationResult);
    }
}
