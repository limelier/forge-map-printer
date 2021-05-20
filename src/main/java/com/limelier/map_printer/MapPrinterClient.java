package com.limelier.map_printer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod("map_printer")
@Mod.EventBusSubscriber(Dist.CLIENT)
public class MapPrinterClient
{
    public static final String MOD_ID = "map_printer";
    public static final String MOD_NAME = "Map Printer";

    private static final KeyBinding PRINT_KEY_BINDING = new KeyBinding(
            "key."+MOD_ID+".print",
            GLFW.GLFW_KEY_M,
            "key.category."+MOD_ID
    );

    static {
        ClientRegistry.registerKeyBinding(PRINT_KEY_BINDING);
    }

    @SubscribeEvent
    public static void onClientTickEvent(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        // client tick end

        if (PRINT_KEY_BINDING.isDown()) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            assert player != null;
            ItemStack mainHandStack = player.getMainHandItem();
            MapSerializer.saveMap(mainHandStack);
        }
    }
}
