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
        File f = new File("groceryFinal.csv");
        Scanner fileScanner = new Scanner(f);
        ArrayList<Item> items = new ArrayList<>();

        fileScanner.nextLine();
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            String[] columns = line.split(",");
            Item item = new Item(columns[0], columns[1], columns[2], columns[3], columns[4]);
            items.add(item);
        }

        ArrayList<Item> deliList = new ArrayList<>();
        ArrayList<Item> dairyList = new ArrayList<>();
        ArrayList<Item> produceList = new ArrayList<>();
        ArrayList<Item> bakeryList = new ArrayList<>();
        ArrayList<Item> frozenList = new ArrayList<>();

        for (Item item:items) {

            if (item.department.equals("Deli")) {
                deliList.add(item);
            }
            if (item.department.equals("Dairy")) {
                dairyList.add(item);
            }
            if (item.department.equals("Produce")) {
                produceList.add(item);
            }
            if (item.department.equals("Bakery")) {
                bakeryList.add(item);
            }
            if (item.department.equals("Frozen")) {
                frozenList.add(item);
            }
        }

//display welcome page
        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    HashMap a = new HashMap<>();
                    return new ModelAndView(a, "welcome.html");
                },
            new MustacheTemplateEngine()
         );

//display viewItems page
        Spark.get(
                "/viewItems",
                (request, response) -> {
                    HashMap b = new HashMap();
                    b.put("items", items);
                    b.put("deliList", deliList);
                    b.put("dairyList", dairyList);
                    b.put("produceList", produceList);
                    b.put("bakeryList", bakeryList);
                    b.put("frozenList", frozenList);
                    return new ModelAndView(b, "viewItems.html");
                },
                new MustacheTemplateEngine()
        );
    }
}
