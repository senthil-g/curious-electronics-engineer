package org.sen.webapp;

class BlockTypeUnkownException extends Exception {
    private static final long serialVersionUID = 1L;
    BlockTypeUnkownException(String message){
        super(message);
    }
}