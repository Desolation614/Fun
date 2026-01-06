/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui;

import java.text.ParseException;
import javax.swing.JFormattedTextField;

final class UnitFormatter
extends JFormattedTextField.AbstractFormatter {
    private final JFormattedTextField.AbstractFormatter delegate;
    private final String units;

    @Override
    public Object stringToValue(String text) throws ParseException {
        String trimmedText = text.endsWith(this.units) ? text.substring(0, text.length() - this.units.length()) : text;
        return this.delegate.stringToValue(trimmedText);
    }

    @Override
    public String valueToString(Object value) {
        return String.valueOf(value) + this.units;
    }

    public UnitFormatter(JFormattedTextField.AbstractFormatter delegate, String units) {
        this.delegate = delegate;
        this.units = units;
    }
}

