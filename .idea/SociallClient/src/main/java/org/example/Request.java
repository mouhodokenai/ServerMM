package org.example;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Request {
    private String Request;

    private ArrayList<String> ListAttributes;
    Request(String Request, ArrayList<String> ListAttributes){
        this.Request = Request;
        this.ListAttributes = ListAttributes;
    }

}