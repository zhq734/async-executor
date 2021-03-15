package com.zhq.executor.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExecutorSpringUtil implements ApplicationContextAware, DisposableBean {
  
  private static ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    if (ExecutorSpringUtil.applicationContext == null) {
      ExecutorSpringUtil.applicationContext = applicationContext;
    }
  }

  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  public static Object getBean(String name) {
  
    
    return getApplicationContext().getBean(name);
  }

  public static <T> T getBean(Class<T> clazz) {
    return getApplicationContext().getBean(clazz);
  }

  public static <T> Map<String, T> getBeanOfType(Class<T> clazz) {
    return getApplicationContext().getBeansOfType(clazz);
  }

  public static <T> T getBean(String name, Class<T> clazz) {
    return getApplicationContext().getBean(name, clazz);
  }
  
  
  @Override
  public void destroy() throws Exception {
      applicationContext = null;
  }
}