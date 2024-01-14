package fer.proinz.prijave.service;

import fer.proinz.prijave.model.CityDept;
import fer.proinz.prijave.repository.CityDeptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CityDeptService {

    private final CityDeptRepository cityDeptRepository;

    public List<CityDept> getAllCityDepts() {
        return cityDeptRepository.findAll();
    }

    public Optional<CityDept> getCityDeptById(int cityDeptId) {
        return cityDeptRepository.findById(cityDeptId);
    }

    public CityDept createCityDept(CityDept cityDept) {
        return cityDeptRepository.save(cityDept);
    }

    public CityDept updateCityDept(
            int cityDeptId,
            CityDept updatedCityDept
    ) {
        /*Optional<CityDepartment> cityDepartment = cityDepartmentRepository.findById(cityDepartmentId);
        if (cityDepartment.isPresent()) {
            return cityDepartmentRepository.save(updatedCityDepartment);
        } else {
            throw new NoSuchElementException("No city department with this id");
        }*/
        return cityDeptRepository.findById(cityDeptId)
                .map(cityDept -> {
                    if (updatedCityDept.getCityDeptName() != null) {
                        cityDept.setCityDeptName(updatedCityDept.getCityDeptName());
                    }
                    return cityDeptRepository.save(cityDept);
                })
                .orElseThrow(RuntimeException::new);
    }

    public ResponseEntity<String> deleteCityDept(int cityDeptId) {
        Optional<CityDept> cityDepartment = cityDeptRepository.findById(cityDeptId);
        if (cityDepartment.isPresent()) {
            cityDeptRepository.deleteById(cityDeptId);
            return ResponseEntity.ok("City department with id " + cityDeptId + "is deleted.");
        } else {
            throw new RuntimeException("City department with id " + cityDeptId + " does not exist!");
        }
    }
}
