package com.egfavre;

import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        File f = new File("Grocery.csv");
        Scanner fileScanner = new Scanner(f);
        ArrayList<Item> items = new ArrayList<>();

        fileScanner.nextLine();
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            String[] columns = line.split(",");
            Item item = new Item();
            items.add(item);
        }

        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    HashMap m = new HashMap<>();
                    return new ModelAndView(m, "messages.html");
                },
            new MustacheTemplateEngine()
         );


    }
}
