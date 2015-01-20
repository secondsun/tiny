/**
 * Copyright (C) 2015 Summers Pittman (secondsun@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.saga.lang.tiny.compiler;

import java.io.FileOutputStream;
import me.qmx.jitescript.JiteClass;
import static me.qmx.jitescript.util.CodegenUtils.c;

public class DynamicClassLoader extends ClassLoader {

        public Class<?> define(JiteClass jiteClass) {
            byte[] classBytes = jiteClass.toBytes();

            try (FileOutputStream fos = new FileOutputStream(String.format("/tmp/%s.class", jiteClass.getClassName()))) {
                fos.write(classBytes);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (jiteClass.getChildClasses().size() > 0) {
                jiteClass.getChildClasses().stream().forEach((child) -> {
                    define(child);
                });
            }

            return super.defineClass(c(jiteClass.getClassName()), classBytes, 0, classBytes.length);
        }
    }