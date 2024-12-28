package com.example.demo;

import java.lang.reflect.Field;

public class Utils {

    public static void injectObjects(Object target, String fieldName, Object toInject) {



        boolean wasPrivate = false;



        try {


            Field field = target.getClass().getDeclaredField(fieldName);



            if( field.isAccessible()== false) {

                field.setAccessible(true);


                wasPrivate = true;


            }

            if(field.canAccess(target) == false) {

                field.setAccessible(true);


                wasPrivate = true;


            }

            field.set(target, toInject);





            if(wasPrivate) field.setAccessible(false);




        }
        catch (NoSuchFieldException | IllegalAccessException e) {





            e.printStackTrace();




        }
    }

}


