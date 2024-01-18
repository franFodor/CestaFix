package fer.proinz.prijave.controller;

import fer.proinz.prijave.exception.NonExistingCategoryException;
import fer.proinz.prijave.exception.NonExistingCityDeptException;
import fer.proinz.prijave.exception.NonExistingUserException;
import fer.proinz.prijave.model.Category;
import fer.proinz.prijave.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories")
    @GetMapping("/public/category/getAll")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @Operation(summary = "Get a category by its id")
    @GetMapping("/public/category/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("categoryId") int categoryId)
            throws NonExistingCategoryException {
        return ResponseEntity.ok(categoryService
                .getCategoryById(categoryId)
                .orElseThrow(NonExistingCategoryException::new)
        );
    }

    @Operation(summary = "Create a category")
    @PostMapping("/advanced/category")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @Operation(summary = "Update a category")
    @PatchMapping("/advanced/category/{categoryId}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable("categoryId") int categoryId,
            @RequestBody Category updatedCategory
    ) throws NonExistingCategoryException {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, updatedCategory));
    }

    @Operation(summary = "Delete a category")
    @DeleteMapping("/advanced/category/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable("categoryId") int categoryId)
            throws NonExistingCityDeptException, NonExistingUserException, NonExistingCategoryException {
        return categoryService.deleteCategory(categoryId);
    }

}
