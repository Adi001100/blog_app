package blog.service;

import blog.domain.Category;
import blog.domain.Post;
import blog.dto.CategoryDetails;
import blog.dto.CategoryFormData;
import blog.dto.CategoryListItem;
import blog.exception.CategoryAlreadyExistException;
import blog.exception.CategoryNotFoundByCategoryNameException;
import blog.repository.CategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryFormData categoryFormData;
    @InjectMocks
    private CategoryService categoryService;

    private Category book = new Category();
    private Category travel = new Category();
    private Category cook = new Category();


    @Test
    void test_createCategorySuccessful() {
        cook.setCategoryName("cook");
        when(categoryFormData.getCategoryName()).thenReturn("cook");
        when(categoryFormData.getDescription()).thenReturn("cook blog");
        when(categoryRepository.save(any())).thenReturn(cook);

        CategoryDetails categorySaved = categoryService.saveCategory(categoryFormData);
        assertEquals(cook.getCategoryName(), categorySaved.getCategoryName());
    }

    @Test
    void test_createCategoryFailed() {
        when(categoryFormData.getCategoryName()).thenReturn("book");
        when(categoryRepository.findAll()).thenReturn(createCategoryList());

        assertThrows(CategoryAlreadyExistException.class, () -> categoryService.saveCategory(categoryFormData));
    }


    @Test
    void test_ListAsStartEmptyList() {
        when(categoryRepository.findAll()).thenReturn(List.of());
        Assertions.assertThat(categoryService.getCategoryListItems()).isEmpty();
    }


    @Test
    void test_getCategoryListItems() {
        createCategoryList();
        when(categoryRepository.findAll()).thenReturn(List.of(book, travel));

        List<CategoryListItem> categoryListItems = categoryService.getCategoryListItems();
        assertEquals(travel.getCategoryName(), categoryListItems.get(1).getCategoryName());

        Assertions.assertThat(categoryService.getCategoryListItems())
                .hasSize(2);
    }

    @Test
    void test_categoryFindById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(book));
        org.junit.jupiter.api.Assertions.assertEquals(book.getCategoryName(), categoryService.getCategoryById(1L).getCategoryName());

    }

    @Test
    void test_getCategoryDetailsByIdSuccess() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(travel));
        when(categoryRepository.getOne(1L)).thenReturn(travel);
        travel.setCreatedAt(LocalDateTime.now());
        org.junit.jupiter.api.Assertions.assertEquals(travel.getCategoryName(), categoryService.getCategoryDetailsById(1L).getCategoryName());

    }


    @Test
    void test_getCategoryDetailsByCategoryNameSuccess() {
        when(categoryRepository.findByCategoryName("book")).thenReturn(book);
        book.setCreatedAt(LocalDateTime.now());
        org.junit.jupiter.api.Assertions.assertEquals(book.getCategoryName(), categoryService.getCategoryDetailsByCategoryName("book").getCategoryName());

    }

    @Test
    void test_getCategoryDetailsByCategoryNameFailed() {
        when(categoryRepository.findByCategoryName("book")).thenReturn(null);
        assertThrows(CategoryNotFoundByCategoryNameException.class, () -> categoryService.getCategoryDetailsByCategoryName("book"));

    }


    List<Category> createCategoryList() {
        travel.setCategoryName("travel");
        travel.setDescription("travel blog");
        travel.setCreatedAt(LocalDateTime.now());
        Post travelPost = new Post();
        travelPost.setCategory(travel);
        travel.setPostList(List.of(travelPost));
        book.setCategoryName("book");
        book.setDescription("book blog");
        book.setCreatedAt(LocalDateTime.now());
        Post bookPost = new Post();
        bookPost.setCategory(book);
        book.setPostList(List.of(bookPost));

        return List.of(book, travel);

    }

}