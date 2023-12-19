package fer.proinz.prijave.service;

import fer.proinz.prijave.model.CityDepartment;
import fer.proinz.prijave.repository.CityDepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CityDepartmentService {

    private final CityDepartmentRepository cityDepartmentRepository;

    public List<CityDepartment> getAllCityDepartments() {
        return cityDepartmentRepository.findAll();
    }

    public Optional<CityDepartment> getCityDepartmentById(int cityDepartmentId) {
        return cityDepartmentRepository.findById(cityDepartmentId);
    }

    public CityDepartment createCityDepartment(CityDepartment cityDepartment) {
        return cityDepartmentRepository.save(cityDepartment);
    }

    public CityDepartment updateCityDepartment(
            int cityDepartmentId,
            CityDepartment updatedCityDepartment
    ) {
        /*Optional<CityDepartment> cityDepartment = cityDepartmentRepository.findById(cityDepartmentId);
        if (cityDepartment.isPresent()) {
            return cityDepartmentRepository.save(updatedCityDepartment);
        } else {
            throw new NoSuchElementException("No city department with this id");
        }*/
        return cityDepartmentRepository.findById(cityDepartmentId)
                .map(cityDept -> {
                    if (updatedCityDepartment.getCitydeptName() != null) {
                        cityDept.setCitydeptName(updatedCityDepartment.getCitydeptName());
                    }
                    return cityDepartmentRepository.save(cityDept);
                })
                .orElseThrow(RuntimeException::new);
    }

    public ResponseEntity<String> deleteCityDepartment(int cityDepartmentId) {
        Optional<CityDepartment> cityDepartment = cityDepartmentRepository.findById(cityDepartmentId);
        if (cityDepartment.isPresent()) {
            cityDepartmentRepository.deleteById(cityDepartmentId);
            return ResponseEntity.ok("City department with id " + cityDepartmentId + "is deleted.");
        } else {
            throw new RuntimeException("City department with id " + cityDepartmentId + " does not exist!");
        }
    }
}
