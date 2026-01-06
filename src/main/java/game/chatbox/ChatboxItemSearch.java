/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  com.google.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.ItemComposition
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.game.chatbox;

import com.google.common.primitives.Ints;
import com.google.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.game.chatbox.ChatboxTextInput;

@Singleton
public class ChatboxItemSearch
extends ChatboxTextInput {
    private static final int ICON_HEIGHT = 32;
    private static final int ICON_WIDTH = 36;
    private static final int PADDING = 6;
    private static final int MAX_RESULTS = 24;
    private static final int FONT_SIZE = 16;
    private static final int HOVERED_OPACITY = 128;
    private final ChatboxPanelManager chatboxPanelManager;
    private final ItemManager itemManager;
    private final Client client;
    private final Map<Integer, ItemComposition> results = new LinkedHashMap<Integer, ItemComposition>();
    private String tooltipText;
    private int index = -1;
    private Consumer<Integer> onItemSelected;

    @Inject
    private ChatboxItemSearch(ChatboxPanelManager chatboxPanelManager, ClientThread clientThread, ItemManager itemManager, Client client) {
        super(chatboxPanelManager, clientThread);
        this.chatboxPanelManager = chatboxPanelManager;
        this.itemManager = itemManager;
        this.client = client;
        this.lines(1);
        this.prompt("Item Search");
        this.onChanged(searchString -> clientThread.invokeLater(() -> {
            this.filterResults();
            this.update();
        }));
    }

    @Override
    protected void update() {
        Widget container = this.chatboxPanelManager.getContainerWidget();
        container.deleteAllChildren();
        Widget promptWidget = container.createChild(-1, 4);
        promptWidget.setText(this.getPrompt());
        promptWidget.setTextColor(0x800000);
        promptWidget.setFontId(this.getFontID());
        promptWidget.setOriginalX(0);
        promptWidget.setOriginalY(5);
        promptWidget.setXPositionMode(1);
        promptWidget.setYPositionMode(0);
        promptWidget.setOriginalHeight(16);
        promptWidget.setXTextAlignment(1);
        promptWidget.setYTextAlignment(1);
        promptWidget.setWidthMode(1);
        promptWidget.revalidate();
        this.buildEdit(0, 21, container.getWidth(), 16);
        Widget separator = container.createChild(-1, 9);
        separator.setOriginalX(0);
        separator.setOriginalY(40);
        separator.setXPositionMode(1);
        separator.setYPositionMode(0);
        separator.setOriginalHeight(0);
        separator.setOriginalWidth(16);
        separator.setWidthMode(1);
        separator.setTextColor(0x666666);
        separator.revalidate();
        int x = 6;
        int y = 18;
        int idx = 0;
        for (ItemComposition itemComposition : this.results.values()) {
            Widget item = container.createChild(-1, 5);
            item.setXPositionMode(0);
            item.setYPositionMode(0);
            item.setOriginalX(x);
            item.setOriginalY(y + 32);
            item.setOriginalHeight(32);
            item.setOriginalWidth(36);
            item.setName("<col=ff9040>" + itemComposition.getName());
            item.setItemId(itemComposition.getId());
            item.setItemQuantity(10000);
            item.setItemQuantityMode(0);
            item.setBorderType(1);
            item.setAction(0, this.tooltipText);
            item.setHasListener(true);
            if (this.index == idx) {
                item.setOpacity(128);
            } else {
                item.setOnMouseOverListener(new Object[]{ev -> item.setOpacity(128)});
                item.setOnMouseLeaveListener(new Object[]{ev -> item.setOpacity(0)});
            }
            item.setOnOpListener(new Object[]{ev -> {
                if (this.onItemSelected != null) {
                    this.onItemSelected.accept(itemComposition.getId());
                }
                this.chatboxPanelManager.close();
            }});
            if ((x += 42) + 36 >= container.getWidth()) {
                y += 38;
                x = 6;
            }
            item.revalidate();
            ++idx;
        }
    }

    @Override
    public void keyPressed(KeyEvent ev) {
        if (!this.chatboxPanelManager.shouldTakeInput()) {
            return;
        }
        switch (ev.getKeyCode()) {
            case 10: {
                ev.consume();
                if (this.index <= -1) break;
                if (this.onItemSelected != null) {
                    this.onItemSelected.accept(this.results.keySet().toArray(new Integer[this.results.size()])[this.index]);
                }
                this.chatboxPanelManager.close();
                break;
            }
            case 9: 
            case 39: {
                ev.consume();
                if (this.results.isEmpty()) break;
                ++this.index;
                if (this.index >= this.results.size()) {
                    this.index = 0;
                }
                this.clientThread.invokeLater(this::update);
                break;
            }
            case 37: {
                ev.consume();
                if (this.results.isEmpty()) break;
                --this.index;
                if (this.index < 0) {
                    this.index = this.results.size() - 1;
                }
                this.clientThread.invokeLater(this::update);
                break;
            }
            case 38: {
                ev.consume();
                if (this.results.size() < 12) break;
                this.index -= 12;
                if (this.index < 0) {
                    this.index = this.results.size() == 24 ? (this.index += this.results.size()) : (this.index += 24);
                    this.index = Ints.constrainToRange((int)this.index, (int)0, (int)(this.results.size() - 1));
                }
                this.clientThread.invokeLater(this::update);
                break;
            }
            case 40: {
                ev.consume();
                if (this.results.size() < 12) break;
                this.index += 12;
                if (this.index >= 24) {
                    this.index = this.results.size() == 24 ? (this.index -= this.results.size()) : (this.index -= 24);
                    this.index = Ints.constrainToRange((int)this.index, (int)0, (int)(this.results.size() - 1));
                }
                this.clientThread.invokeLater(this::update);
                break;
            }
            default: {
                super.keyPressed(ev);
            }
        }
    }

    @Override
    protected void close() {
        this.value("");
        this.results.clear();
        this.index = -1;
        super.close();
    }

    @Override
    @Deprecated
    public ChatboxTextInput onDone(Consumer<String> onDone) {
        throw new UnsupportedOperationException();
    }

    private void filterResults() {
        this.results.clear();
        this.index = -1;
        String search = this.getValue().toLowerCase();
        if (search.isEmpty()) {
            return;
        }
        HashSet<ItemIcon> itemIcons = new HashSet<ItemIcon>();
        for (int i = 0; i < this.client.getItemCount() && this.results.size() < 24; ++i) {
            ItemIcon itemIcon;
            ItemComposition itemComposition = this.itemManager.getItemComposition(this.itemManager.canonicalize(i));
            String name = itemComposition.getName().toLowerCase();
            if (name.equals("null") || !name.contains(search) || this.results.containsKey(itemComposition.getId()) || itemIcons.contains(itemIcon = new ItemIcon(itemComposition.getInventoryModel(), itemComposition.getAmbient(), itemComposition.getContrast(), itemComposition.getColorToReplaceWith(), itemComposition.getTextureToReplaceWith()))) continue;
            itemIcons.add(itemIcon);
            this.results.put(itemComposition.getId(), itemComposition);
        }
    }

    public ChatboxItemSearch onItemSelected(Consumer<Integer> onItemSelected) {
        this.onItemSelected = onItemSelected;
        return this;
    }

    public ChatboxItemSearch tooltipText(String text) {
        this.tooltipText = text;
        return this;
    }

    public Consumer<Integer> getOnItemSelected() {
        return this.onItemSelected;
    }

    private static final class ItemIcon {
        private final int modelId;
        private final int ambient;
        private final int contrast;
        private final short[] colorsToReplace;
        private final short[] texturesToReplace;

        public ItemIcon(int modelId, int ambient, int contrast, short[] colorsToReplace, short[] texturesToReplace) {
            this.modelId = modelId;
            this.ambient = ambient;
            this.contrast = contrast;
            this.colorsToReplace = colorsToReplace;
            this.texturesToReplace = texturesToReplace;
        }

        public int getModelId() {
            return this.modelId;
        }

        public int getAmbient() {
            return this.ambient;
        }

        public int getContrast() {
            return this.contrast;
        }

        public short[] getColorsToReplace() {
            return this.colorsToReplace;
        }

        public short[] getTexturesToReplace() {
            return this.texturesToReplace;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ItemIcon)) {
                return false;
            }
            ItemIcon other = (ItemIcon)o;
            if (this.getModelId() != other.getModelId()) {
                return false;
            }
            if (this.getAmbient() != other.getAmbient()) {
                return false;
            }
            if (this.getContrast() != other.getContrast()) {
                return false;
            }
            if (!Arrays.equals(this.getColorsToReplace(), other.getColorsToReplace())) {
                return false;
            }
            return Arrays.equals(this.getTexturesToReplace(), other.getTexturesToReplace());
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + this.getModelId();
            result = result * 59 + this.getAmbient();
            result = result * 59 + this.getContrast();
            result = result * 59 + Arrays.hashCode(this.getColorsToReplace());
            result = result * 59 + Arrays.hashCode(this.getTexturesToReplace());
            return result;
        }

        public String toString() {
            return "ChatboxItemSearch.ItemIcon(modelId=" + this.getModelId() + ", ambient=" + this.getAmbient() + ", contrast=" + this.getContrast() + ", colorsToReplace=" + Arrays.toString(this.getColorsToReplace()) + ", texturesToReplace=" + Arrays.toString(this.getTexturesToReplace()) + ")";
        }
    }
}

