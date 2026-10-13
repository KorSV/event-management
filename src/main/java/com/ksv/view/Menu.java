package com.ksv.view;

import java.util.ArrayList;
import java.util.List;

public class Menu {

    private List<ItemMenu> items;


    public Menu() {
        items = new ArrayList<>();
    }

    public void addItem(int id, String title){
        this.items.add(new ItemMenu(id, title));
    }


    public void show(){
        if (items.isEmpty()) {
            System.out.println("В меню нет ни одного пункта. Добавьте пункты в меню.");
            return;
        }
        System.out.println("===Меню===");
        for(ItemMenu item:items){
            System.out.println(item);
        }
    }
}
