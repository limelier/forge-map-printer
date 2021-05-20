package com.limelier.map_printer;

import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.storage.MapData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MapSerializer {
    private static final String imageFolder = "maps";

    public static void saveMap(ItemStack mapStack) {
        final Minecraft client = Minecraft.getInstance();

        if (mapStack.getItem() != Items.FILLED_MAP) return;

        // get the pixels from the map item
        assert client.level != null;
        MapData mapData = FilledMapItem.getSavedData(mapStack, client.level);
        assert mapData != null;
        byte[] colors = mapData.colors;

        // create a new image
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);

        // loop through all pixels
        for (int i = 0; i < colors.length; i++) {
            int x = i % 128;
            int y = i / 128;

            // get the right color
            int l = colors[i] & 255;
            int color = MaterialColor.MATERIAL_COLORS[l / 4].calculateRGBColor(l & 3);

            // convert from abgr to argb
            int a = color >> 24 & 255;
            int b = color >> 16 & 255;
            int g = color >> 8 & 255;
            int r = color >> 0 & 255;

            if (r == 0 && g == 0 && b == 0) a = 0;

            color = a << 24 | r << 16 | g << 8 | b << 0;

            // set the color in the image
            image.setRGB(x, y, color);
        }

        // create a file directory
        String imageName = mapData.getId() + ".png";
        String imageSubFolder = client.isLocalServer()
                ? Objects.requireNonNull(client.getSingleplayerServer()).getWorldData().getLevelName()
                : Objects.requireNonNull(client.getCurrentServer()).name;
        String imageDirectory = client.gameDirectory.getAbsolutePath() + "/" + imageFolder + "/" + imageSubFolder + "/" + imageName;
        File imageFile = new File(imageDirectory);

        // create the folders if they don't exist
        imageFile.getParentFile().mkdirs();

        // try writing to the png image
        try {
            ImageIO.write(image, "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        assert client.player != null;
        client.player.displayClientMessage(new StringTextComponent("Map saved to: " + imageDirectory), false);
    }
}
