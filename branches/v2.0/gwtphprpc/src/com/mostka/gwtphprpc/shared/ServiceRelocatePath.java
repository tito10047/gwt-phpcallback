package com.mostka.gwtphprpc.shared;

/**
 * Created by Jozef Môstka on 27.5.2014.
 * https://google.com/+JozefMôstka/about
 */

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceRelocatePath {
    String value();
}