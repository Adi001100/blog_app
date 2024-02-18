package blog.controller;

import blog.dto.CategoryDetails;
import blog.dto.CategoryFormData;
import blog.dto.CategoryListItem;
import blog.exception.CategoryAlreadyExistException;
import blog.exception.CategoryNotFoundByIdException;
import blog.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

@Tag(
        name = "CRUD REST APIs for Category in BLOGMASTER",
        description = "to CREATE and FETCH category(s)"
)
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @PostMapping
    @RolesAllowed("ROLE_ADMIN")
    @Operation(summary = "Create category REST API",
            description = "REST API to create new category inside BLOGMASTER")
    @ApiResponse(
            responseCode = "201",
            description = "Category has been saved."
    )
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "Category already exist",
            content = @Content(
                    schema = @Schema(implementation = CategoryAlreadyExistException.class)
            )
    )
    public ResponseEntity<CategoryDetails> createCategory(@Valid @RequestBody CategoryFormData categoryFormData) {
        logger.info("Http request GET api/categories, body: {}", categoryFormData);
        categoryService.saveCategory(categoryFormData);
        logger.info("Category has been saved: {}", categoryFormData.getCategoryName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            summary = "List all categories REST API",
            description = "REST API to list all categories inside BLOGMASTER")
    @ApiResponse(
            responseCode = "200",
            description = "Categories has been listed."
    )
    public ResponseEntity<List<CategoryListItem>> getCategoryList() {
        logger.info("Http request GET /api/categories");
        logger.info("Categories has been listed.");
        return new ResponseEntity<>(categoryService.getCategoryListItems(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Find category with id REST API",
            description = "REST API to find category with id inside BLOGMASTER")
    @ApiResponse(
            responseCode = "200",
            description = "Category has been found.")
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n" +
                    "CategoryNotFoundById",
            content = @Content(
                    schema = @Schema(implementation = CategoryNotFoundByIdException.class)
            )
    )
    public ResponseEntity<CategoryDetails> getCategory(@PathVariable("id") Long id) {
        logger.info("Http request GET /api/categories/{} path variable: {}", id, id);
        CategoryDetails categoryDetails = categoryService.getCategoryDetailsById(id);
        logger.info("Category has been found by ID: {}", id);
        return new ResponseEntity<>(categoryDetails, HttpStatus.OK);
    }
}
