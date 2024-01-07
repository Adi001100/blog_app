package blog.controller;

import blog.dto.CategoryDetails;
import blog.dto.CategoryFormData;
import blog.dto.CategoryListItem;
import blog.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @PostMapping
    @RolesAllowed("ROLE_ADMIN")
    @Operation(summary = "Save a category.")
    @ApiResponse(responseCode = "201", description = "Category has been saved.")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryFormData categoryFormData) {
        logger.info("Http request GET api/categories, body: " + categoryFormData.toString());
        categoryService.saveCategory(categoryFormData);
        logger.info("Category has been saved: " + categoryFormData.getCategoryName());
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all categories.")
    @ApiResponse(responseCode = "200", description = "Categories has been listed.")
    public ResponseEntity<List<CategoryListItem>> getCategoryList() {
        logger.info("Http request GET /api/categories");
        logger.info("Categories has been listed.");
        return new ResponseEntity<>(categoryService.getCategoryListItems(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find category with id.")
    @ApiResponse(responseCode = "200", description = "Category has been found.")
    public ResponseEntity<CategoryDetails> getCategory(@PathVariable("id") Long id) {
        logger.info("Http request GET /api/categories/{id} path variable: " + id, Level.INFO);
        CategoryDetails categoryDetails = categoryService.getCategoryDetailsById(id);
        logger.info("Category has been found by ID: " + id);
        return new ResponseEntity<>(categoryDetails, HttpStatus.OK);
    }
}
