package red.webservertools.Item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import red.webservertools.WebServerTools;

public class ModItems {
    public static final Item BAN_HAMMER = registerItem( "ban_hammer", new Item(new Item.Settings()));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(WebServerTools.MOD_ID, name), item);
    }


    public static void registerModItems() {
        WebServerTools.LOGGER.info("Registering Mod items for " + WebServerTools.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(BAN_HAMMER);
        });
    }
}
