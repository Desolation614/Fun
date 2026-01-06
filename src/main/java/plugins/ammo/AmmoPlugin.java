/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.EquipmentInventorySlot
 *  net.runelite.api.Item
 *  net.runelite.api.ItemComposition
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.events.ItemContainerChanged
 *  net.runelite.api.events.VarbitChanged
 */
package net.runelite.client.plugins.ammo;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.ammo.AmmoCounter;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.AsyncBufferedImage;

@PluginDescriptor(name="Ammo", description="Shows the current ammo the player has equipped", tags={"bolts", "darts", "chinchompa", "equipment"}, enabledByDefault=false)
public class AmmoPlugin
extends Plugin {
    private static final Set<Integer> DIZANAS_QUIVER_IDS = ImmutableSet.builder().addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(28951))).addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(28955))).addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(28902))).build();
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private ItemManager itemManager;
    private AmmoCounter counterBox;
    private AmmoCounter quiverBox;
    private boolean isWearingQuiver;

    @Override
    protected void startUp() throws Exception {
        this.clientThread.invokeLater(() -> {
            ItemContainer container = this.client.getItemContainer(94);
            if (container != null) {
                this.checkInventory(container);
            }
        });
    }

    @Override
    protected void shutDown() throws Exception {
        this.infoBoxManager.removeInfoBox(this.counterBox);
        this.counterBox = null;
        this.removeQuiverInfobox();
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getItemContainer() != this.client.getItemContainer(94)) {
            return;
        }
        this.checkInventory(event.getItemContainer());
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarpId() == 4141 || event.getVarpId() == 4142) {
            this.checkQuiver();
        }
    }

    private void checkInventory(ItemContainer equipment) {
        ItemComposition weaponComp;
        Item cape = equipment.getItem(EquipmentInventorySlot.CAPE.getSlotIdx());
        this.isWearingQuiver = cape != null && DIZANAS_QUIVER_IDS.contains(cape.getId());
        this.checkQuiver();
        Item weapon = equipment.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
        if (weapon != null && (weaponComp = this.itemManager.getItemComposition(weapon.getId())).isStackable()) {
            this.updateInfobox(weapon, weaponComp);
            return;
        }
        Item ammo = equipment.getItem(EquipmentInventorySlot.AMMO.getSlotIdx());
        if (ammo == null) {
            this.removeInfobox();
            return;
        }
        ItemComposition comp = this.itemManager.getItemComposition(ammo.getId());
        if (!comp.isStackable()) {
            this.removeInfobox();
            return;
        }
        this.updateInfobox(ammo, comp);
    }

    private void updateInfobox(Item item, ItemComposition comp) {
        if (this.counterBox != null && this.counterBox.getItemID() == item.getId()) {
            this.counterBox.setCount(item.getQuantity());
            return;
        }
        this.removeInfobox();
        AsyncBufferedImage image = this.itemManager.getImage(item.getId(), 5, false);
        this.counterBox = new AmmoCounter(this, item.getId(), item.getQuantity(), comp.getName(), image);
        this.infoBoxManager.addInfoBox(this.counterBox);
    }

    private void removeInfobox() {
        this.infoBoxManager.removeInfoBox(this.counterBox);
        this.counterBox = null;
    }

    private void checkQuiver() {
        if (!this.isWearingQuiver) {
            this.removeQuiverInfobox();
            return;
        }
        int quiverAmmoId = this.client.getVarpValue(4142);
        int quiverAmmoCount = this.client.getVarpValue(4141);
        if (quiverAmmoId == -1 || quiverAmmoCount == 0) {
            this.removeQuiverInfobox();
            return;
        }
        this.updateQuiverInfobox(quiverAmmoId, quiverAmmoCount);
    }

    private void updateQuiverInfobox(int itemId, int count) {
        if (this.quiverBox != null && this.quiverBox.getItemID() == itemId) {
            this.quiverBox.setCount(count);
            return;
        }
        ItemComposition comp = this.itemManager.getItemComposition(itemId);
        this.removeQuiverInfobox();
        AsyncBufferedImage image = this.itemManager.getImage(itemId, 5, false);
        this.quiverBox = new AmmoCounter(this, itemId, count, comp.getName(), image);
        this.infoBoxManager.addInfoBox(this.quiverBox);
    }

    private void removeQuiverInfobox() {
        this.infoBoxManager.removeInfoBox(this.quiverBox);
        this.quiverBox = null;
    }
}

