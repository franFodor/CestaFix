package fer.proinz.prijave.service;

import fer.proinz.prijave.exception.NonExistingCategoryException;
import fer.proinz.prijave.exception.NonExistingCityDeptException;
import fer.proinz.prijave.exception.NonExistingUserException;
import fer.proinz.prijave.model.Category;
import fer.proinz.prijave.model.CityDept;
import fer.proinz.prijave.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final CityDeptService cityDeptService;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId);
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(int categoryId, Category updatedCategory)
            throws NonExistingCategoryException {
        return categoryRepository.findById(categoryId)
                .map(category -> {
                    if (updatedCategory.getCategoryName() != null) {
                        category.setCategoryName(updatedCategory.getCategoryName());
                    }
                    return categoryRepository.save(category);
                })
                .orElseThrow(NonExistingCategoryException::new);
    }

    public ResponseEntity<String> deleteCategory(int categoryId)
            throws NonExistingUserException, NonExistingCityDeptException, NonExistingCategoryException {

        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(NonExistingCategoryException::new);

        for (CityDept cityDept : cityDeptService.getAllCityDepts()) {
            if (cityDept.getCategory() == category) {
                // First delete all cityDepts that have the category you want to delete
                cityDeptService.deleteCityDept(cityDept.getCityDeptId());
            }
        }

        categoryRepository.deleteById(categoryId);
        return ResponseEntity.ok("Category with id " + categoryId + " is deleted.");
    }
}
