package com.ecocycle.model;

import java.math.BigDecimal;

/** One row of the cart, as shown on screen. */
public class CartLine {

    private final Product product;
    private final int quantity;

    public CartLine(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }

    public BigDecimal getLineTotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    /** True when the customer wants more than is in stock (or the product is sold out). */
    public boolean isExceedsStock() {
        return quantity > product.getStock();
    }
}