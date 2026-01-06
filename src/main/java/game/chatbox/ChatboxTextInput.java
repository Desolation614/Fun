/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.primitives.Ints
 *  com.google.inject.Inject
 *  net.runelite.api.FontTypeFace
 *  net.runelite.api.Point
 *  net.runelite.api.widgets.Widget
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.game.chatbox;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import com.google.inject.Inject;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import net.runelite.api.FontTypeFace;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.chatbox.ChatboxInput;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.MouseListener;
import net.runelite.client.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatboxTextInput
extends ChatboxInput
implements KeyListener,
MouseListener {
    private static final Logger log = LoggerFactory.getLogger(ChatboxTextInput.class);
    private static final int CURSOR_FLASH_RATE_MILLIS = 1000;
    private static final Pattern BREAK_MATCHER = Pattern.compile("[^a-zA-Z0-9']");
    private final ChatboxPanelManager chatboxPanelManager;
    protected final ClientThread clientThread;
    private String prompt;
    private int lines;
    private StringBuffer value = new StringBuffer();
    private int cursorStart = 0;
    private int cursorEnd = 0;
    private int selectionStart = -1;
    private int selectionEnd = -1;
    private IntPredicate charValidator = ChatboxTextInput.getDefaultCharValidator();
    private Runnable onClose = null;
    private Predicate<String> onDone = null;
    private Consumer<String> onChanged = null;
    private int fontID = 497;
    private boolean built = false;
    private Predicate<MouseEvent> isInBounds = null;
    private ToIntFunction<Integer> getLineOffset = null;
    private ToIntFunction<java.awt.Point> getPointCharOffset = null;

    private static IntPredicate getDefaultCharValidator() {
        return i -> i >= 32 && i < 127;
    }

    @Inject
    protected ChatboxTextInput(ChatboxPanelManager chatboxPanelManager, ClientThread clientThread) {
        this.chatboxPanelManager = chatboxPanelManager;
        this.clientThread = clientThread;
    }

    public ChatboxTextInput addCharValidator(IntPredicate validator) {
        this.charValidator = this.charValidator.and(validator);
        return this;
    }

    public ChatboxTextInput lines(int lines) {
        this.lines = lines;
        if (this.built) {
            this.clientThread.invoke(this::update);
        }
        return this;
    }

    public ChatboxTextInput prompt(String prompt) {
        this.prompt = prompt;
        if (this.built) {
            this.clientThread.invoke(this::update);
        }
        return this;
    }

    public ChatboxTextInput value(String value) {
        StringBuffer sb = new StringBuffer();
        for (char c : value.toCharArray()) {
            if (!this.charValidator.test(c)) continue;
            sb.append(c);
        }
        this.value = sb;
        this.cursorAt(this.value.length());
        return this;
    }

    public ChatboxTextInput cursorAt(int index) {
        return this.cursorAt(index, index);
    }

    public ChatboxTextInput cursorAt(int indexA, int indexB) {
        int end;
        int start;
        if (indexA < 0) {
            indexA = 0;
        }
        if (indexB < 0) {
            indexB = 0;
        }
        if (indexA > this.value.length()) {
            indexA = this.value.length();
        }
        if (indexB > this.value.length()) {
            indexB = this.value.length();
        }
        if ((start = indexA) > (end = indexB)) {
            int v = start;
            start = end;
            end = v;
        }
        this.cursorStart = start;
        this.cursorEnd = end;
        if (this.built) {
            this.clientThread.invoke(this::update);
        }
        return this;
    }

    public String getValue() {
        return this.value.toString();
    }

    public ChatboxTextInput charValidator(IntPredicate val) {
        if (val == null) {
            val = ChatboxTextInput.getDefaultCharValidator();
        }
        this.charValidator = val;
        return this;
    }

    public ChatboxTextInput onClose(Runnable onClose) {
        this.onClose = onClose;
        return this;
    }

    public ChatboxTextInput onDone(Consumer<String> onDone) {
        this.onDone = s -> {
            onDone.accept((String)s);
            return true;
        };
        return this;
    }

    public ChatboxTextInput onDone(Predicate<String> onDone) {
        this.onDone = onDone;
        return this;
    }

    public ChatboxTextInput onChanged(Consumer<String> onChanged) {
        this.onChanged = onChanged;
        return this;
    }

    public ChatboxTextInput fontID(int fontID) {
        this.fontID = fontID;
        return this;
    }

    protected void update() {
        Widget container = this.chatboxPanelManager.getContainerWidget();
        container.deleteAllChildren();
        Widget promptWidget = container.createChild(-1, 4);
        promptWidget.setText(this.prompt);
        promptWidget.setTextColor(0x800000);
        promptWidget.setFontId(this.fontID);
        promptWidget.setXPositionMode(1);
        promptWidget.setOriginalX(0);
        promptWidget.setYPositionMode(0);
        promptWidget.setOriginalY(8);
        promptWidget.setOriginalHeight(24);
        promptWidget.setXTextAlignment(1);
        promptWidget.setYTextAlignment(1);
        promptWidget.setWidthMode(1);
        promptWidget.revalidate();
        this.buildEdit(0, 50, container.getWidth(), 0);
    }

    protected void buildEdit(int x, int y, int w, int h) {
        ArrayList<Line> editLines = new ArrayList<Line>();
        Widget container = this.chatboxPanelManager.getContainerWidget();
        Widget cursor = container.createChild(-1, 3);
        long start = System.currentTimeMillis();
        cursor.setOnTimerListener(new Object[]{ev -> {
            boolean on = (System.currentTimeMillis() - start) % 1000L > 500L;
            cursor.setOpacity(on ? 255 : 0);
        }});
        cursor.setTextColor(0xFFFFFF);
        cursor.setHasListener(true);
        cursor.setFilled(true);
        cursor.setFontId(this.fontID);
        FontTypeFace font = cursor.getFont();
        if (h <= 0) {
            h = font.getBaseline();
        }
        int oy = y;
        int ox = x;
        int oh = h;
        int breakIndex = -1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.value.length(); ++i) {
            Line line;
            int count = i - sb.length();
            String c = "" + this.value.charAt(i);
            sb.append(c);
            if (BREAK_MATCHER.matcher(c).matches()) {
                breakIndex = sb.length();
            }
            if (i == this.value.length() - 1) {
                line = new Line(count, count + sb.length() - 1, sb.toString());
                editLines.add(line);
                break;
            }
            if (font.getTextWidth(sb.toString() + this.value.charAt(i + 1)) < w || editLines.size() >= this.lines - 1 && this.lines != 0) continue;
            if (breakIndex > 1) {
                String str = sb.substring(0, breakIndex);
                Line line2 = new Line(count, count + str.length() - 1, str);
                editLines.add(line2);
                sb.replace(0, breakIndex, "");
                breakIndex = -1;
                continue;
            }
            line = new Line(count, count + sb.length() - 1, sb.toString());
            editLines.add(line);
            sb.replace(0, sb.length(), "");
        }
        Rectangle bounds = new Rectangle(container.getCanvasLocation().getX() + container.getWidth(), y, 0, editLines.size() * oh);
        for (int i = 0; i < editLines.size() || i == 0; ++i) {
            Line line = editLines.size() > 0 ? (Line)editLines.get(i) : new Line(0, 0, "");
            String text = line.text;
            int len = text.length();
            String lt = Text.escapeJagex(text);
            String mt = "";
            String rt = "";
            boolean isStartLine = this.cursorOnLine(this.cursorStart, line.start, line.end) || this.cursorOnLine(this.cursorStart, line.start, line.end + 1) && i == editLines.size() - 1;
            boolean isEndLine = this.cursorOnLine(this.cursorEnd, line.start, line.end);
            if (isStartLine || isEndLine || this.cursorEnd > line.end && this.cursorStart < line.start) {
                int cIdx = Ints.constrainToRange((int)(this.cursorStart - line.start), (int)0, (int)len);
                int ceIdx = Ints.constrainToRange((int)(this.cursorEnd - line.start), (int)0, (int)len);
                lt = Text.escapeJagex(text.substring(0, cIdx));
                mt = Text.escapeJagex(text.substring(cIdx, ceIdx));
                rt = Text.escapeJagex(text.substring(ceIdx));
            }
            int ltw = font.getTextWidth(lt);
            int mtw = font.getTextWidth(mt);
            int rtw = font.getTextWidth(rt);
            int fullWidth = ltw + mtw + rtw;
            int ltx = ox;
            if (w > 0) {
                ltx += (w - fullWidth) / 2;
            }
            int mtx = ltx + ltw;
            int rtx = mtx + mtw;
            if (ltx < bounds.x) {
                bounds.setLocation(ltx, bounds.y);
            }
            if (fullWidth > bounds.width) {
                bounds.setSize(fullWidth, bounds.height);
            }
            if (editLines.size() == 0 || isStartLine) {
                cursor.setOriginalX(mtx - 1);
                cursor.setOriginalY(y);
                cursor.setOriginalWidth(2);
                cursor.setOriginalHeight(h);
                cursor.revalidate();
            }
            if (!Strings.isNullOrEmpty((String)lt)) {
                Widget leftText = container.createChild(-1, 4);
                leftText.setFontId(this.fontID);
                leftText.setText(lt);
                leftText.setOriginalX(ltx);
                leftText.setOriginalY(y);
                leftText.setOriginalWidth(ltw);
                leftText.setOriginalHeight(h);
                leftText.revalidate();
            }
            if (!Strings.isNullOrEmpty((String)mt)) {
                Widget background = container.createChild(-1, 3);
                background.setTextColor(0x113399);
                background.setFilled(true);
                background.setOriginalX(mtx - 1);
                background.setOriginalY(y);
                background.setOriginalWidth(2 + mtw);
                background.setOriginalHeight(h);
                background.revalidate();
                Widget middleText = container.createChild(-1, 4);
                middleText.setText(mt);
                middleText.setFontId(this.fontID);
                middleText.setOriginalX(mtx);
                middleText.setOriginalY(y);
                middleText.setOriginalWidth(mtw);
                middleText.setOriginalHeight(h);
                middleText.setTextColor(0xFFFFFF);
                middleText.revalidate();
            }
            if (!Strings.isNullOrEmpty((String)rt)) {
                Widget rightText = container.createChild(-1, 4);
                rightText.setText(rt);
                rightText.setFontId(this.fontID);
                rightText.setOriginalX(rtx);
                rightText.setOriginalY(y);
                rightText.setOriginalWidth(rtw);
                rightText.setOriginalHeight(h);
                rightText.revalidate();
            }
            y += h;
        }
        Point ccl = container.getCanvasLocation();
        this.isInBounds = ev -> bounds.contains(new java.awt.Point(ev.getX() - ccl.getX(), ev.getY() - ccl.getY()));
        this.getPointCharOffset = p -> {
            if (bounds.width <= 0) {
                return 0;
            }
            int cx = p.x - ccl.getX() - ox;
            int cy = p.y - ccl.getY() - oy;
            int currentLine = Ints.constrainToRange((int)(cy / oh), (int)0, (int)(editLines.size() - 1));
            Line line = (Line)editLines.get(currentLine);
            String tsValue = line.text;
            int charIndex = tsValue.length();
            int fullWidth = font.getTextWidth(tsValue);
            int tx = ox;
            if (w > 0) {
                tx += (w - fullWidth) / 2;
            }
            cx -= tx;
            for (int i = tsValue.length(); i >= 0 && charIndex >= 0 && charIndex <= tsValue.length(); --i) {
                int lcx = charIndex > 0 ? font.getTextWidth(Text.escapeJagex(tsValue.substring(0, charIndex - 1))) : 0;
                int mcx = font.getTextWidth(Text.escapeJagex(tsValue.substring(0, charIndex)));
                int rcx = charIndex + 1 <= tsValue.length() ? font.getTextWidth(Text.escapeJagex(tsValue.substring(0, charIndex + 1))) : mcx;
                int leftBound = (lcx + mcx) / 2;
                int rightBound = (mcx + rcx) / 2;
                if (cx < leftBound) {
                    --charIndex;
                    continue;
                }
                if (cx <= rightBound) break;
                ++charIndex;
            }
            charIndex = Ints.constrainToRange((int)charIndex, (int)0, (int)tsValue.length());
            return line.start + charIndex;
        };
        this.getLineOffset = code -> {
            if (editLines.size() < 2) {
                return this.cursorStart;
            }
            int currentLine = -1;
            for (int i = 0; i < editLines.size(); ++i) {
                Line l = (Line)editLines.get(i);
                if (!this.cursorOnLine(this.cursorStart, l.start, l.end) && (!this.cursorOnLine(this.cursorStart, l.start, l.end + 1) || i != editLines.size() - 1)) continue;
                currentLine = i;
                break;
            }
            if (currentLine == -1 || code == 38 && currentLine == 0 || code == 40 && currentLine == editLines.size() - 1) {
                return this.cursorStart;
            }
            Line line = (Line)editLines.get(currentLine);
            int direction = code == 38 ? -1 : 1;
            java.awt.Point dest = new java.awt.Point(cursor.getCanvasLocation().getX(), cursor.getCanvasLocation().getY() + direction * oh);
            int charOffset = this.getPointCharOffset.applyAsInt(dest);
            Line nextLine = (Line)editLines.get(currentLine + direction);
            if (direction == -1 && charOffset >= line.start || direction == 1 && charOffset > nextLine.end && currentLine + direction != editLines.size() - 1) {
                return nextLine.end;
            }
            return charOffset;
        };
    }

    private boolean cursorOnLine(int cursor, int start, int end) {
        return cursor >= start && cursor <= end;
    }

    private int getCharOffset(MouseEvent ev) {
        if (this.getPointCharOffset == null) {
            return 0;
        }
        return this.getPointCharOffset.applyAsInt(ev.getPoint());
    }

    @Override
    protected void open() {
        this.built = true;
        this.update();
    }

    @Override
    protected void close() {
        if (this.onClose != null) {
            this.onClose.run();
        }
    }

    public ChatboxTextInput build() {
        if (this.prompt == null) {
            throw new IllegalStateException("prompt must be non-null");
        }
        this.chatboxPanelManager.openInput(this);
        return this;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (!this.chatboxPanelManager.shouldTakeInput()) {
            return;
        }
        char c = e.getKeyChar();
        if (this.charValidator.test(c)) {
            if (this.cursorStart != this.cursorEnd) {
                this.value.delete(this.cursorStart, this.cursorEnd);
            }
            this.value.insert(this.cursorStart, c);
            this.cursorAt(this.cursorStart + 1);
            if (this.onChanged != null) {
                this.onChanged.accept(this.getValue());
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent ev) {
        if (!this.chatboxPanelManager.shouldTakeInput()) {
            return;
        }
        int code = ev.getKeyCode();
        if (ev.isControlDown()) {
            switch (code) {
                case 67: 
                case 88: {
                    if (this.cursorStart != this.cursorEnd) {
                        String s = this.value.substring(this.cursorStart, this.cursorEnd);
                        if (code == 88) {
                            this.value.delete(this.cursorStart, this.cursorEnd);
                            this.cursorAt(this.cursorStart);
                        }
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
                    }
                    return;
                }
                case 86: {
                    try {
                        String s = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
                        if (this.cursorStart != this.cursorEnd) {
                            this.value.delete(this.cursorStart, this.cursorEnd);
                        }
                        for (int i = 0; i < s.length(); ++i) {
                            char ch = s.charAt(i);
                            if (!this.charValidator.test(ch)) continue;
                            this.value.insert(this.cursorStart, ch);
                            ++this.cursorStart;
                        }
                        this.cursorAt(this.cursorStart);
                        if (this.onChanged != null) {
                            this.onChanged.accept(this.getValue());
                        }
                    }
                    catch (UnsupportedFlavorException | IOException ex) {
                        log.warn("Unable to get clipboard", (Throwable)ex);
                    }
                    return;
                }
                case 65: {
                    this.selectionStart = 0;
                    this.selectionEnd = this.value.length();
                    this.cursorAt(0, this.selectionEnd);
                    return;
                }
            }
            return;
        }
        int newPos = this.cursorStart;
        if (ev.isShiftDown()) {
            if (this.selectionEnd == -1 || this.selectionStart == -1) {
                this.selectionStart = this.cursorStart;
                this.selectionEnd = this.cursorStart;
            }
            newPos = this.selectionEnd;
        } else {
            this.selectionStart = -1;
            this.selectionEnd = -1;
        }
        switch (code) {
            case 127: {
                if (this.cursorStart != this.cursorEnd) {
                    this.value.delete(this.cursorStart, this.cursorEnd);
                    this.cursorAt(this.cursorStart);
                    if (this.onChanged != null) {
                        this.onChanged.accept(this.getValue());
                    }
                    return;
                }
                if (this.cursorStart < this.value.length()) {
                    this.value.deleteCharAt(this.cursorStart);
                    this.cursorAt(this.cursorStart);
                    if (this.onChanged != null) {
                        this.onChanged.accept(this.getValue());
                    }
                }
                return;
            }
            case 8: {
                if (this.cursorStart != this.cursorEnd) {
                    this.value.delete(this.cursorStart, this.cursorEnd);
                    this.cursorAt(this.cursorStart);
                    if (this.onChanged != null) {
                        this.onChanged.accept(this.getValue());
                    }
                    return;
                }
                if (this.cursorStart > 0) {
                    this.value.deleteCharAt(this.cursorStart - 1);
                    this.cursorAt(this.cursorStart - 1);
                    if (this.onChanged != null) {
                        this.onChanged.accept(this.getValue());
                    }
                }
                return;
            }
            case 37: {
                ev.consume();
                if (this.cursorStart != this.cursorEnd) {
                    newPos = this.cursorStart;
                    break;
                }
                --newPos;
                break;
            }
            case 39: {
                ev.consume();
                if (this.cursorStart != this.cursorEnd) {
                    newPos = this.cursorEnd;
                    break;
                }
                ++newPos;
                break;
            }
            case 38: {
                ev.consume();
                newPos = this.getLineOffset.applyAsInt(code);
                break;
            }
            case 40: {
                ev.consume();
                newPos = this.getLineOffset.applyAsInt(code);
                break;
            }
            case 36: {
                ev.consume();
                newPos = 0;
                break;
            }
            case 35: {
                ev.consume();
                newPos = this.value.length();
                break;
            }
            case 10: {
                ev.consume();
                if (this.onDone != null && !this.onDone.test(this.getValue())) {
                    return;
                }
                this.chatboxPanelManager.close();
                return;
            }
            case 27: {
                ev.consume();
                if (this.cursorStart != this.cursorEnd) {
                    this.cursorAt(this.cursorStart);
                    return;
                }
                this.chatboxPanelManager.close();
                return;
            }
            default: {
                return;
            }
        }
        if (newPos > this.value.length()) {
            newPos = this.value.length();
        }
        if (newPos < 0) {
            newPos = 0;
        }
        if (ev.isShiftDown()) {
            this.selectionEnd = newPos;
            this.cursorAt(this.selectionStart, newPos);
        } else {
            this.cursorAt(newPos);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public MouseEvent mouseClicked(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    @Override
    public MouseEvent mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != 1) {
            return mouseEvent;
        }
        if (this.isInBounds == null || !this.isInBounds.test(mouseEvent)) {
            if (this.cursorStart != this.cursorEnd) {
                this.selectionStart = -1;
                this.selectionEnd = -1;
                this.cursorAt(this.getCharOffset(mouseEvent));
            }
            return mouseEvent;
        }
        int nco = this.getCharOffset(mouseEvent);
        if (mouseEvent.isShiftDown() && this.selectionEnd != -1) {
            this.selectionEnd = nco;
            this.cursorAt(this.selectionStart, this.selectionEnd);
        } else {
            this.selectionStart = nco;
            this.cursorAt(nco);
        }
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseReleased(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseEntered(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseExited(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseDragged(MouseEvent mouseEvent) {
        if (!SwingUtilities.isLeftMouseButton(mouseEvent)) {
            return mouseEvent;
        }
        int nco = this.getCharOffset(mouseEvent);
        if (this.selectionStart != -1) {
            this.selectionEnd = nco;
            this.cursorAt(this.selectionStart, this.selectionEnd);
        }
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseMoved(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public int getLines() {
        return this.lines;
    }

    public int getCursorStart() {
        return this.cursorStart;
    }

    public int getCursorEnd() {
        return this.cursorEnd;
    }

    public IntPredicate getCharValidator() {
        return this.charValidator;
    }

    public Runnable getOnClose() {
        return this.onClose;
    }

    public Predicate<String> getOnDone() {
        return this.onDone;
    }

    public Consumer<String> getOnChanged() {
        return this.onChanged;
    }

    public int getFontID() {
        return this.fontID;
    }

    public boolean isBuilt() {
        return this.built;
    }

    private static class Line {
        private final int start;
        private final int end;
        private final String text;

        public Line(int start, int end, String text) {
            this.start = start;
            this.end = end;
            this.text = text;
        }
    }
}

