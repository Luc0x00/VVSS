package drinkshop.service.validator;

import drinkshop.domain.Product;

public class ProductValidator implements Validator<Product> {

    @Override
    public void validate(Product product) {

        String errors = "";

        if (product.getId() <= 0)
            errors += "ID invalid!\n";

        if (product.getNume() == null || product.getNume().isBlank())
            errors += "Numele nu poate fi gol!\n";

        if (product.getPret() <= 0)
            errors += "Pret invalid!\n";

        if (product.getNume().length() > 255)
            errors += "Numele nu poate fi mai lunf de 255 de caractere!\n";

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
