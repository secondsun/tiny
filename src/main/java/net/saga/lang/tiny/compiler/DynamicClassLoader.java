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