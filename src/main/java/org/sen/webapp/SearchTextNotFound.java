package org.sen.webapp;

class SearchTextNotFound extends Exception {
    private static final long serialVersionUID = 1L;
    SearchTextNotFound(String message){
        super(message);
    }
}