package org.sen.webapp;

class BounceTypeUnkownException extends Exception {
    private static final long serialVersionUID = 1L;
    BounceTypeUnkownException(String message){
        super(message);
    }
}