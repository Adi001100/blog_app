package blog.service;

import blog.domain.Category;
import blog.dto.CategoryDetails;
import blog.dto.CategoryFormData;
import blog.dto.CategoryListItem;
import blog.exception.CategoryAlreadyExistException;
import blog.exception.CategoryNotFoundByCategoryNameException;
import blog.exception.CategoryNotFoundByIdException;
import blog.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryDetails saveCategory(CategoryFormData categoryFormData) {
        if (Boolean.FALSE.equals(checkCategoryName(categoryFormData))) {
            Category categoryToCreate = new Category(categoryFormData);
            Category categoryCreated = categoryRepository.save(categoryToCreate);
            categoryCreated.setCreatedAt(LocalDateTime.now());
            return new CategoryDetails(categoryCreated);
        } else {
            throw new CategoryAlreadyExistException(categoryFormData.getCategoryName());
        }
    }

    public List<CategoryListItem> getCategoryListItems() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryListItem::new)
                .collect(Collectors.toList());
    }

    private Boolean checkCategoryName(CategoryFormData categoryFormData) {
        boolean result = false;
        List<CategoryListItem> categoryListItems = getCategoryListItems();
        for (CategoryListItem categoryListItem : categoryListItems) {
            if (categoryListItem.getCategoryName().equalsIgnoreCase(categoryFormData.getCategoryName())) {
                result = true;
            }
        }
        return result;
    }

    public CategoryDetails getCategoryDetailsById(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            return new CategoryDetails(categoryRepository.getById(id));
        } else {
            throw new CategoryNotFoundByIdException(id);
        }
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public CategoryDetails getCategoryDetailsByCategoryName(String categoryName) {
        CategoryDetails details = null;
        Category category = categoryRepository.findByCategoryName(categoryName);
        if (category != null) {
            details = new CategoryDetails(category);
        } else {
            throw new CategoryNotFoundByCategoryNameException(categoryName);
        }
        return details;
    }
}
