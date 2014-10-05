/*
 * Copyright 2013 Harald Wellmann
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

package org.ops4j.pax.cdi.openwebbeans.impl.context;

import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.spi.Contextual;

import org.apache.webbeans.context.AbstractContext;
import org.apache.webbeans.context.creational.BeanInstanceBag;
import org.ops4j.pax.cdi.api.SingletonScoped;

/**
 * @author Harald Wellmann
 * 
 */
public class ServiceContext extends AbstractContext {

    private static final long serialVersionUID = 1L;

    public ServiceContext() {
        super(SingletonScoped.class);
    }

    @Override
    public void setComponentInstanceMap() {
        componentInstanceMap = new ConcurrentHashMap<Contextual<?>, BeanInstanceBag<?>>();
    }
}
