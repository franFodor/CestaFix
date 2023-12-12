package fer.proinz.prijave.controller;

import fer.proinz.prijave.model.CityDepartment;
import fer.proinz.prijave.service.CityDepartmentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CityDepartmentController {

    private final CityDepartmentService cityDepartmentService;

    @GetMapping("/public/cityDept/getAll")
    public List<CityDepartment> getAllCityDepartments() {
        return cityDepartmentService.getAllCityDepartments();
    }

    @GetMapping("/public/cityDept/{cityDeptId}")
    public ResponseEntity<CityDepartment> getCityDepartmentById(
            @PathVariable("cityDeptId") int cityDeptId) {
        Optional<CityDepartment> cityDeptOpt = cityDepartmentService.getCityDepartmentById(cityDeptId);
        return cityDeptOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/advanced/cityDept")
    public ResponseEntity<CityDepartment> updateCityDepartment() {
        return null;
    }
}
