package com.hy.boke.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取spring容器中的bean
 */
@Component
public class SpringTool implements ApplicationContextAware {
    public static ApplicationContext applicationContext = null;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringTool.applicationContext == null){
            SpringTool.applicationContext = applicationContext;
        }
    }

    /**
     * 根据class获取bean
     */
    public static <T> T getBean(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }
    /**
     * 根据name
     */
    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }
}
