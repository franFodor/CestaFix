package fer.proinz.prijave.controller;

import fer.proinz.prijave.model.CityDepartment;
import fer.proinz.prijave.service.CityDepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CityDepartmentController {

    private final CityDepartmentService cityDepartmentService;

    @Operation(summary = "Get all city departments")
    @GetMapping("/public/cityDept/getAll")
    public List<CityDepartment> getAllCityDepartments() {
        return cityDepartmentService.getAllCityDepartments();
    }

    @Operation(summary = "Get a city department by its id")
    @GetMapping("/public/cityDept/{cityDeptId}")
    public ResponseEntity<CityDepartment> getCityDepartmentById(
            @PathVariable("cityDeptId") int cityDeptId
    ) {
        Optional<CityDepartment> cityDeptOpt = cityDepartmentService.getCityDepartmentById(cityDeptId);
        return cityDeptOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a city department")
    @PostMapping("/advanced/cityDept")
    public ResponseEntity<CityDepartment> createCityDepartment(
            @RequestBody CityDepartment cityDepartment
    ) {
        CityDepartment saved = cityDepartmentService.createCityDepartment(cityDepartment);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Update a city department")
    @PatchMapping("/advanced/cityDept/{citydeptId}")
    public ResponseEntity<CityDepartment> updateCityDepartment(
            @PathVariable("citydeptId") int citydeptId,
            @RequestBody CityDepartment cityDepartment
    ) {
        return ResponseEntity.ok(cityDepartmentService.updateCityDepartment(citydeptId, cityDepartment));
    }

    @Operation(summary = "Delete a city department")
    @DeleteMapping("/advanced/cityDept/{citydeptId}")
    public ResponseEntity<String> deleteCityDepartment(@PathVariable("citydeptId") int citydeptId) {
        return cityDepartmentService.deleteCityDepartment(citydeptId);
    }
}
