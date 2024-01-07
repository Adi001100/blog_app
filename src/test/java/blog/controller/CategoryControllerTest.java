package blog.controller;

import blog.domain.Category;
import blog.dto.CategoryDetails;
import blog.dto.CategoryFormData;
import blog.dto.CategoryListItem;
import blog.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private final Category category = new Category();

    @BeforeEach
    void setUp() {
        category.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void test_createCategory() {
        CategoryFormData categoryFormData = new CategoryFormData("book", "desc");
        categoryFormData.setCategoryName("Test Category");
        when(categoryService.saveCategory(categoryFormData)).thenReturn(new CategoryDetails(category));
        ResponseEntity<?> response = categoryController.createCategory(categoryFormData);
        verify(categoryService, times(1)).saveCategory(categoryFormData);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void test_getCategoryList() {
        List<CategoryListItem> categoryList = new ArrayList<>();
        when(categoryService.getCategoryListItems()).thenReturn(categoryList);
        ResponseEntity<List<CategoryListItem>> response = categoryController.getCategoryList();
        verify(categoryService, times(1)).getCategoryListItems();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_getCategory() {
        CategoryDetails categoryDetails = new CategoryDetails(category);
        when(categoryService.getCategoryDetailsById(1L)).thenReturn(categoryDetails);
        ResponseEntity<CategoryDetails> response = categoryController.getCategory(1L);
        verify(categoryService, times(1)).getCategoryDetailsById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
