/**
 * Copyright (c) 2013, ControlsFX
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package dhcoder.tool.javafx.property;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Convenience utility class for creating {@link PropertySheet.Item} instances via reflection.
 */
public final class PropertyUtils {

    private PropertyUtils() { }

    /**
     * Use reflection to pull out a list of properties from a target class, converting them to a list of {@link Item}s.
     */
    public static ObservableList<Item> getProperties(final Object target) {
        ObservableList<Item> list = FXCollections.observableArrayList();

        Map<String, Method> getMethods = new HashMap<>();
        Map<String, Method> setMethods = new HashMap<>();
        Map<String, PropertyMeta> metaData = new HashMap<>();

        for (PropertyMeta propertyMeta : target.getClass().getAnnotationsByType(PropertyMeta.class)) {
            metaData.put(propertyMeta.name(), propertyMeta);
        }

        for (Method method : target.getClass().getDeclaredMethods()) {
            if (method.getName().startsWith("get")) {
                getMethods.put(method.getName().substring(3), method);
            }
            else if (method.getName().startsWith("set")) {
                setMethods.put(method.getName().substring(3), method);
            }
        }

        for (String methodName : getMethods.keySet()) {

            String displayName = null;

            if (metaData.containsKey(methodName)) {
                PropertyMeta propertyMeta = metaData.get(methodName);
                if (!propertyMeta.displayName().isEmpty()) {
                    displayName = propertyMeta.displayName();
                }
            }

            if (displayName == null) {
                // Convert method name to display name
                StringBuilder nameBuilder = new StringBuilder(methodName);
                for (int i = 1; i < nameBuilder.length(); i++) {
                    if (Character.isUpperCase(nameBuilder.charAt(i))) {
                        nameBuilder.insert(i, ' ');
                        i += 1;
                    }
                }
                displayName = nameBuilder.toString();
            }
            PropertyItem propertyItem = new PropertyItem(target, displayName, getMethods.get(methodName));
            if (setMethods.containsKey(methodName)) {
                propertyItem.setSetMethod(setMethods.get(methodName));
            }

            list.add(propertyItem);
        }

        return list;
    }
}
