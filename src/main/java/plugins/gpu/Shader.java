/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.lwjgl.opengl.GL43C
 */
package net.runelite.client.plugins.gpu;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import net.runelite.client.plugins.gpu.ShaderException;
import net.runelite.client.plugins.gpu.template.Template;
import org.lwjgl.opengl.GL43C;

public class Shader {
    @VisibleForTesting
    final List<Unit> units = new ArrayList<Unit>();

    public Shader add(int type, String name) {
        this.units.add(new Unit(type, name));
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int compile(Template template) throws ShaderException {
        int program = GL43C.glCreateProgram();
        int[] shaders = new int[this.units.size()];
        int i = 0;
        boolean ok = false;
        try {
            String err;
            while (i < shaders.length) {
                Unit unit = this.units.get(i);
                int shader = GL43C.glCreateShader((int)unit.type);
                if (shader == 0) {
                    throw new ShaderException("Unable to create shader of type " + unit.type);
                }
                String source = template.load(unit.filename);
                GL43C.glShaderSource((int)shader, (CharSequence)source);
                GL43C.glCompileShader((int)shader);
                if (GL43C.glGetShaderi((int)shader, (int)35713) != 1) {
                    String err2 = GL43C.glGetShaderInfoLog((int)shader);
                    GL43C.glDeleteShader((int)shader);
                    throw new ShaderException(err2);
                }
                GL43C.glAttachShader((int)program, (int)shader);
                shaders[i++] = shader;
            }
            GL43C.glLinkProgram((int)program);
            if (GL43C.glGetProgrami((int)program, (int)35714) == 0) {
                err = GL43C.glGetProgramInfoLog((int)program);
                throw new ShaderException(err);
            }
            GL43C.glValidateProgram((int)program);
            if (GL43C.glGetProgrami((int)program, (int)35715) == 0) {
                err = GL43C.glGetProgramInfoLog((int)program);
                throw new ShaderException(err);
            }
            ok = true;
        }
        finally {
            while (i > 0) {
                int shader = shaders[--i];
                GL43C.glDetachShader((int)program, (int)shader);
                GL43C.glDeleteShader((int)shader);
            }
            if (!ok) {
                GL43C.glDeleteProgram((int)program);
            }
        }
        return program;
    }

    @VisibleForTesting
    static class Unit {
        private final int type;
        private final String filename;

        public Unit(int type, String filename) {
            this.type = type;
            this.filename = filename;
        }

        public int getType() {
            return this.type;
        }

        public String getFilename() {
            return this.filename;
        }
    }
}

