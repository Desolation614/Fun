/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

import ch.qos.logback.core.util.OptionHelper;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JNDIUtil {
    static final String RESTRICTION_MSG = "JNDI name must start with java: but was ";

    public static Context getInitialContext() throws NamingException {
        return new InitialContext();
    }

    public static String lookup(Context ctx, String name) throws NamingException {
        if (ctx == null) {
            return null;
        }
        if (OptionHelper.isEmpty(name)) {
            return null;
        }
        if (!name.startsWith("java:")) {
            throw new NamingException(RESTRICTION_MSG + name);
        }
        Object lookup = ctx.lookup(name);
        return lookup == null ? null : lookup.toString();
    }
}

