package com.shop.cart.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_item_detail")
public class CartItemDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private int quantity;

    public CartItemDetail() {}  // Standardkonstruktor hinzugefügt

    public CartItemDetail(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }
    public int getQuantity() {
        return quantity;
    }    

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }
}
