/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JFormattedTextField;
import net.runelite.client.ui.UnitFormatter;

public final class UnitFormatterFactory
extends JFormattedTextField.AbstractFormatterFactory {
    private final JFormattedTextField.AbstractFormatterFactory delegateFactory;
    private final String units;
    private final Map<JFormattedTextField, JFormattedTextField.AbstractFormatter> formatters = new HashMap<JFormattedTextField, JFormattedTextField.AbstractFormatter>(1);

    @Override
    public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
        return this.formatters.computeIfAbsent(tf, key -> new UnitFormatter(this.delegateFactory.getFormatter((JFormattedTextField)key), this.units));
    }

    public UnitFormatterFactory(JFormattedTextField.AbstractFormatterFactory delegateFactory, String units) {
        this.delegateFactory = delegateFactory;
        this.units = units;
    }
}

