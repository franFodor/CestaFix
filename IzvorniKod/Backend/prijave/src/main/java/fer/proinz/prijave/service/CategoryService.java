package fer.proinz.prijave.service;

import fer.proinz.prijave.model.Category;
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
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            Category saved = categoryRepository.save(updatedCategory);
            return saved;
        } else {
            throw new NoSuchElementException("No category with this id");
        }
    }

    public ResponseEntity<String> deleteCategory(int categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        if (categoryOptional.isPresent()) {
            categoryRepository.deleteById(categoryId);
            return ResponseEntity.ok("Category with id " + categoryId + " is deleted.");
        } else {
            throw new RuntimeException("Category with id " + " does not exist!");
        }
    }
}