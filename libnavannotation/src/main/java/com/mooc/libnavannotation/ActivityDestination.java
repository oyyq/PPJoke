package com.mooc.libnavannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


/**
 * 被自定义FragmentDestination / ActivityDestination标记的类对象
 * 是NavGraph对象的mNodes中的节点
 */
@Target(ElementType.TYPE)
public @interface ActivityDestination {
    //deeplink
    String pageUrl();

    boolean needLogin() default false;

    boolean asStarter() default false;
}
