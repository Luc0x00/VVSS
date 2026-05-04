package drinkshop.service;

import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceStep1Test {
    /**
     * Maparea claselor pe arhitectura cerută în laborator:
     * S (Service)   -> ProductService
     * E (Entitate)  -> Product
     * V (Validator) -> ProductValidator
     * R (Repository)-> Repository<Integer, Product>
     *
     * Strategia aleasă: Integrare top-down, breadth first (S testează mock-uri pentru V și R).
     */

    @Mock
    private Repository<Integer, Product> productRepo;

    @Mock
    private ProductValidator validator;

    @InjectMocks
    private ProductService productService;

    @Test
    public void testAddProduct_ValidProduct_CallsSave() {
        Product validProduct = new Product(1, "Cappuccino", 15.5, null, null);

        doNothing().when(validator).validate(validProduct);
        when(productRepo.save(validProduct)).thenReturn(validProduct);

        productService.addProduct(validProduct);

        verify(validator, times(1)).validate(validProduct);
        verify(productRepo, times(1)).save(validProduct);
    }

    @Test
    public void testAddProduct_InvalidProduct_ThrowsValidationException() {
        Product invalidProduct = new Product(-1, "", -5.0, null, null);
        String expectedError = "ID invalid!\nNumele nu poate fi gol!\nPret invalid!\n";

        doThrow(new ValidationException(expectedError)).when(validator).validate(invalidProduct);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            productService.addProduct(invalidProduct);
        });

        assertEquals(expectedError, exception.getMessage());
        verify(validator, times(1)).validate(invalidProduct);
        verify(productRepo, never()).save(any(Product.class));
    }

    @Test
    public void testGetAllProducts_ReturnsListFromRepo() {
        Product p1 = new Product(1, "Espresso", 10.0, null, null);
        Product p2 = new Product(2, "Latte", 12.0, null, null);
        List<Product> expectedList = Arrays.asList(p1, p2);

        when(productRepo.findAll()).thenReturn(expectedList);

        List<Product> actualList = productService.getAllProducts();

        assertNotNull(actualList);
        assertEquals(2, actualList.size());
        assertEquals("Espresso", actualList.get(0).getNume());

        verify(productRepo, times(1)).findAll();
        verifyNoInteractions(validator);
    }
}