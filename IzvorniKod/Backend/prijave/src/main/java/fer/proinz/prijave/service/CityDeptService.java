package fer.proinz.prijave.service;

import fer.proinz.prijave.exception.NonExistingCityDeptException;
import fer.proinz.prijave.exception.NonExistingUserException;
import fer.proinz.prijave.model.CityDept;
import fer.proinz.prijave.model.User;
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

    private final UserService userService;

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
    ) throws NonExistingCityDeptException {
        return cityDeptRepository.findById(cityDeptId)
                .map(cityDept -> {
                    if (updatedCityDept.getCityDeptName() != null) {
                        cityDept.setCityDeptName(updatedCityDept.getCityDeptName());
                    }
                    return cityDeptRepository.save(cityDept);
                })
                .orElseThrow(NonExistingCityDeptException::new);
    }

    public ResponseEntity<String> deleteCityDept(int cityDeptId)
            throws NonExistingUserException, NonExistingCityDeptException {

        CityDept cityDept = cityDeptRepository
                .findById(cityDeptId)
                .orElseThrow(NonExistingCityDeptException::new);

        for (User user : userService.getAllUsers()) {
            if (user.getCityDept() == cityDept) {
                // First delete all staff users that belong to cityDept you want to delete
                userService.deleteUser(user.getUserId());
            }
        }

        cityDeptRepository.deleteById(cityDeptId);
        return ResponseEntity.ok("City department with id " + cityDeptId + "is deleted.");
    }
}
