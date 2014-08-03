/*
 * Copyright 2014 Harald Wellmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.cdi.undertow.weld.impl;

import java.util.Set;

import io.undertow.servlet.api.ClassIntrospecter;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.util.ConstructorInstanceFactory;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;


public class CdiInstanceFactoryBuilder implements ClassIntrospecter {
    
    
    private BeanManager beanManager;

    public CdiInstanceFactoryBuilder(BeanManager beanManager) {
        this.beanManager = beanManager;
    }
    
    @Override
    public <T> InstanceFactory<T> createInstanceFactory(Class<T> klass) throws NoSuchMethodException {
        Set<Bean<?>> beans = beanManager.getBeans(klass);
        if (beans.isEmpty()) {
            return new ConstructorInstanceFactory<T>(klass.getDeclaredConstructor());
        }
        else {
            return new CdiInstanceFactory<T>(beanManager, klass);
        }
    }
}
