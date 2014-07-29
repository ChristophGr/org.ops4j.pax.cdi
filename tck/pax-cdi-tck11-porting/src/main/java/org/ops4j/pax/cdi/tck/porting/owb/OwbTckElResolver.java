/*
 * Copyright 2012 Harald Wellmann.
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
package org.ops4j.pax.cdi.tck.porting.owb;

import javax.el.ELContext;

import org.apache.webbeans.el.ELContextStore;
import org.apache.webbeans.el22.WebBeansELResolver;

/**
 * Exist for TCK standalone EL resolver.
 * <p>
 * In standalone case, there is no JSF related artifacts, therefore {@link WebBeansELResolver} does
 * not work.
 * </p>
 */
public class OwbTckElResolver extends WebBeansELResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(ELContext context, Object obj, Object property) {
        // Bean instance
        Object contextualInstance = null;
        ELContextStore elContextStore = null;
        if (obj == null) {
            // Name of the bean
            String name = (String) property;
            // Local store, create if not exist
            elContextStore = ELContextStore.getInstance(true);

            contextualInstance = elContextStore.findBeanByName(name);

            if (contextualInstance != null) {
                context.setPropertyResolved(true);

                return contextualInstance;
            }

        }
        return contextualInstance;
    }

}
