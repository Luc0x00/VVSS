package drinkshop.service;

import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceStep2Test {

    @Mock
    private Repository<Integer, Product> mockRepo;

    private ProductValidator realValidator;
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        realValidator = new ProductValidator();
        productService = new ProductService(mockRepo, realValidator);
    }

    @Test
    public void testAddProduct_IntegrationWithValidator_ValidProduct_SavesToRepo() {
        Product validProduct = new Product(1, "Ceai Verde", 12.0, null, null);
        when(mockRepo.save(validProduct)).thenReturn(validProduct);

        productService.addProduct(validProduct);

        verify(mockRepo, times(1)).save(validProduct);
    }

    @Test
    public void testAddProduct_IntegrationWithValidator_InvalidProduct_ThrowsException() {
        Product invalidProduct = new Product(-1, "", -5.0, null, null);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            productService.addProduct(invalidProduct);
        });

        assertTrue(exception.getMessage().contains("ID invalid!"));
        assertTrue(exception.getMessage().contains("Numele nu poate fi gol!"));

        verify(mockRepo, never()).save(any());
    }
}