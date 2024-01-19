package fer.proinz.prijave.controller;

import fer.proinz.prijave.exception.NonExistingCityDeptException;
import fer.proinz.prijave.exception.NonExistingUserException;
import fer.proinz.prijave.model.CityDept;
import fer.proinz.prijave.service.CityDeptService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CityDeptController {

    private final CityDeptService cityDeptService;

    @Operation(summary = "Get all city departments")
    @GetMapping("/public/cityDept/getAll")
    public List<CityDept> getAllCityDepts() {
        return cityDeptService.getAllCityDepts();
    }

    @Operation(summary = "Get a city department by its id")
    @GetMapping("/public/cityDept/{cityDeptId}")
    public ResponseEntity<CityDept> getCityDeptById(@PathVariable("cityDeptId") int cityDeptId)
            throws NonExistingCityDeptException {
        return ResponseEntity.ok(cityDeptService
                .getCityDeptById(cityDeptId)
                .orElseThrow(NonExistingCityDeptException::new)
        );
    }

    @Operation(summary = "Create a city department")
    @PostMapping("/advanced/cityDept")
    public ResponseEntity<CityDept> createCityDept(
            @RequestBody CityDept cityDept
    ) {
        CityDept saved = cityDeptService.createCityDept(cityDept);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Update a city department")
    @PatchMapping("/advanced/cityDept/{cityDeptId}")
    public ResponseEntity<CityDept> updateCityDept(
            @PathVariable("cityDeptId") int cityDeptId,
            @RequestBody CityDept updatedCityDept
    ) throws NonExistingCityDeptException {
        return ResponseEntity.ok(cityDeptService.updateCityDept(cityDeptId, updatedCityDept));
    }

    @Operation(summary = "Delete a city department")
    @DeleteMapping("/advanced/cityDept/{cityDeptId}")
    public ResponseEntity<String> deleteCityDept(@PathVariable("cityDeptId") int cityDeptId)
            throws NonExistingUserException, NonExistingCityDeptException {
        return cityDeptService.deleteCityDept(cityDeptId);
    }
}
