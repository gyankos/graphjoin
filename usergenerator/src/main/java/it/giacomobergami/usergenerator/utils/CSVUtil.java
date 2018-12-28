package it.giacomobergami.usergenerator.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

public class CSVUtil {
    private static final Logger LOGGER = Logger.getLogger(CSVUtil.class .getName());
    private final static char DEFAULT_SEPARATOR = ' ';

    public static String toCSV(List<?> objectList, char separator, boolean displayHeader) {

        StringBuilder result =new StringBuilder();  
        if (objectList.size() == 0) {
            return result.toString();
        }   

        if(displayHeader){
            result.append(getHeaders(objectList.get(0),separator)); 
            result.append("\n");
        }

        for (Object obj : objectList) {
            result.append(addObjectRow(obj, separator)).append("\n");
        }

        return result.toString();
    }

    public static String getHeaders(Object obj,char separator) {
        StringBuilder resultHeader = new StringBuilder(); 
        boolean firstField = true;
        Field fields[] = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String value;
            try {
                value = field.getName();

                if(firstField){
                     resultHeader.append(value);
                     firstField = false;
                 }
                 else{
                    resultHeader.append(separator).append(value);
                 }
                field.setAccessible(false);
            } catch (IllegalArgumentException  e) {
                LOGGER.severe(e.toString());
            }
        }           
      return resultHeader.toString();

    }

    public static String addObjectRow(Object obj, char separator) {
        StringBuilder csvRow =new StringBuilder();  
        Field fields[] = obj.getClass().getDeclaredFields();
        boolean firstField = true;
        for (Field field : fields) {
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(obj);
                if(value == null)
                    value = "";
                if(firstField){
                    csvRow.append(value);
                    firstField = false;
                }
                else{
                    csvRow.append(separator).append(value);
                }
                field.setAccessible(false);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                LOGGER.severe(e.toString());
            }
        }
        return csvRow.toString();
    }
}