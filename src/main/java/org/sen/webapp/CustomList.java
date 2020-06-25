package org.sen.webapp;

import java.util.ArrayList;
import java.util.logging.Logger;

class CustomList<E> extends ArrayList<E> {
    private static final long serialVersionUID = 8683452581122892189L;
    private static Logger logger = Logger.getLogger(CustomList.class.getName());
    @Override
    public E set( int index , E element )
        {
            logger.info("Entity to be set : " + element.toString() + ", index : " + index);
            return super.set(index, element);
        }
}