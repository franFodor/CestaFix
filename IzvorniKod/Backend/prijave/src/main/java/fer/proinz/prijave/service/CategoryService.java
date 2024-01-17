package fer.proinz.prijave.service;

import fer.proinz.prijave.model.Category;
import fer.proinz.prijave.model.CityDept;
import fer.proinz.prijave.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
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

    public Category updateCategory(int categoryId, Category updatedCategory) {
        /*Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            Category saved = categoryRepository.save(updatedCategory);
            return saved;
        } else {
            throw new NoSuchElementException("No category with this id");
        }*/
        return categoryRepository.findById(categoryId)
                .map(category -> {
                    if (updatedCategory.getCategoryName() != null) {
                        category.setCategoryName(updatedCategory.getCategoryName());
                    }
                    return categoryRepository.save(category);
                })
                .orElseThrow(RuntimeException::new);
    }

    public ResponseEntity<String> deleteCategory(int categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            for (CityDept cityDept : cityDeptService.getAllCityDepts()) {
                if (cityDept.getCategory() == category) {
                    cityDeptService.deleteCityDept(cityDept.getCityDeptId());
                }
            }
            categoryRepository.deleteById(categoryId);
            return ResponseEntity.ok("Category with id " + categoryId + " is deleted.");
        } else {
            throw new RuntimeException("Category with id " + " does not exist!");
        }
    }
}
