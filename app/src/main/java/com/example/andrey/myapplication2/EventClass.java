package com.example.andrey.myapplication2;

/**
 * Класс, описывающий происшествие
 */

public class EventClass {
    String category;
    String date;
    String address;
    String description;
    String author;

    EventClass(String _category, String _date, String _address, String _description, String _author) {
        category = _category;
        date = _date;
        address = _address;
        description = _description;
        author = _author;
    }
}
