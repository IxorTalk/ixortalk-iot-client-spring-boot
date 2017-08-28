/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-present IxorTalk CVBA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.ixortalk.iot.client.core.config;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;

import static java.lang.String.valueOf;
import static java.util.Collections.newSetFromMap;
import static org.springframework.core.MethodIntrospector.selectMethods;

public class IotListenerAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered, BeanFactoryAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(IotListenerAnnotationBeanPostProcessor.class);

    private final Set<Class<?>> nonAnnotatedClasses = newSetFromMap(new ConcurrentHashMap<Class<?>, Boolean>(64));

    private BeanFactory beanFactory;

    private BeanExpressionResolver resolver = new StandardBeanExpressionResolver();

    private BeanExpressionContext expressionContext;

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            this.resolver = ((ConfigurableListableBeanFactory) beanFactory).getBeanExpressionResolver();
            this.expressionContext = new BeanExpressionContext((ConfigurableListableBeanFactory) beanFactory, null);
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!this.nonAnnotatedClasses.contains(bean.getClass())) {
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            Map<Method, IotListener> annotatedMethods = selectMethods(targetClass,
                    (MethodIntrospector.MetadataLookup<IotListener>) method -> findListenerAnnotations(method));
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(bean.getClass());
                this.LOGGER.debug("No @" + IotListener.class.getSimpleName() + " annotations found on bean type: " + bean.getClass());
            } else {
                annotatedMethods.entrySet().forEach(
                        methodIotListenerEntry -> processIotListener(methodIotListenerEntry.getValue(), methodIotListenerEntry.getKey(), bean, beanName));
                this.LOGGER.debug(annotatedMethods.size() + " @" + IotListener.class.getSimpleName() + " methods processed on bean '" + beanName + "': " + annotatedMethods);
            }
        }
        return bean;
    }

    private IotListener findListenerAnnotations(Method method) {
        return AnnotationUtils.findAnnotation(method, IotListener.class);
    }

    private void processIotListener(IotListener iotListener, Method method, Object bean, String beanName) {
        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException("Method " + method + " annotated with @" + IotListener.class.getSimpleName() + " should only take one parameter (being the payload)");
        }

        IotListenerEndpoint endpoint = new IotListenerEndpoint();
        endpoint.setMethod(method);
        endpoint.setBean(bean);
        endpoint.setId(getEndpointId(beanName, method.getName()));
        endpoint.setTopic(resolveTopic(iotListener));
        getIotListenerFactory().createIotListener(endpoint, getObjectMapper());

    }

    private IotListenerFactory getIotListenerFactory() {
        try {
            return this.beanFactory.getBean(IotListenerFactory.class);
        } catch (NoSuchBeanDefinitionException ex) {
            throw new BeanInitializationException("Could not get " + IotListenerFactory.class, ex);
        }
    }

    private ObjectMapper getObjectMapper() {
        try {
            return this.beanFactory.getBean(ObjectMapper.class);
        } catch (NoSuchBeanDefinitionException ex) {
            LOGGER.info("No " + ObjectMapper.class + " exists in the current context, creating default one.");
            return new ObjectMapper();
        }
    }

    private String resolveTopic(IotListener iotListener) {
        String topic = iotListener.topic();
        return valueOf(resolveExpression(topic));
    }

    private Object resolveExpression(String value) {
        String resolvedValue = resolve(value);

        if (!(resolvedValue.startsWith("#{") && value.endsWith("}"))) {
            return resolvedValue;
        }

        return this.resolver.evaluate(resolvedValue, this.expressionContext);
    }

    private String resolve(String value) {
        if (this.beanFactory != null && this.beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) this.beanFactory).resolveEmbeddedValue(value);
        }
        return value;
    }

    private String getEndpointId(String beanName, String methodName) {
        return IotListenerEndpoint.class.getSimpleName() + "#" + beanName + "#" + methodName;
    }
}
