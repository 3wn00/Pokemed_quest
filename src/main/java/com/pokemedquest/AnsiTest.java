package com.pokemedquest;

public class AnsiTest {
    public static void main(String[] args) {
        System.out.println("\u001B[31mThis text is red.\u001B[0m");
        System.out.println("\u001B[32mThis text is green.\u001B[0m");
        System.out.println("\u001B[34mThis text is blue.\u001B[0m");
        System.out.println("\u001B[33mThis text is yellow.\u001B[0m");
        System.out.println("\u001B[0mThis text is reset to default.");
    }
}
