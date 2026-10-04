package com.ecocycle.util;

import java.util.Scanner;

/** Run this file once to turn a password into a BCrypt hash. Nothing is stored. */
public class GenerateHash {

    public static void main(String[] args) {
        System.out.print("Password to hash: ");
        try (Scanner in = new Scanner(System.in)) {
            System.out.println(PasswordUtil.hash(in.nextLine()));
        }
    }
}