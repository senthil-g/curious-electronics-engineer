package org.sen.webapp;

public class SheetNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    SheetNotFoundException(String message){
        super(message);
    }
}