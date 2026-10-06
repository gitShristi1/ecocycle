package com.ecocycle.dao;

/** A problem with the order that the customer should be told about (the message is safe to show). */
public class OrderException extends Exception {

    public OrderException(String message) {
        super(message);
    }
}