package fer.proinz.prijave.controller;

import fer.proinz.prijave.model.Category;
import fer.proinz.prijave.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Category> getCategoryById(@PathVariable("categoryId") int categoryId) {
        Optional<Category> categoryOptional = categoryService.getCategoryById(categoryId);
        return categoryOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a category")
    @PostMapping("/advanced/category")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category saved = categoryService.createCategory(category);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Update a category")
    @PatchMapping("/advanced/category/{categoryId}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable("categoryId") int categoryId,
            @RequestBody Category category
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, category));
    }

    @Operation(summary = "Delete a category")
    @DeleteMapping("/advanced/category/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable("categoryId") int categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

}
