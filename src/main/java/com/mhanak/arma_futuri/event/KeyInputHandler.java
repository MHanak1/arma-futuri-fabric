package com.mhanak.arma_futuri.event;

import com.mhanak.arma_futuri.item.RifleItem;
import com.mhanak.arma_futuri.util.ArmorData;
import com.mhanak.arma_futuri.util.IEntityAccess;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_CATEGORY_ArmaFuturi = "key.category.arma_futuri.arma_futuri";
    public static final String KEY_TOGGLE_FLASHLIGHT = "key.arma_futuri.toggle_flashlight";
    public static final String KEY_ENABLE_SHIELD = "key.arma_futuri.toggle_shield";

    public static KeyBinding flashlightKey;
    public static KeyBinding shieldKey;

    private static boolean attackPressed = false;


    public static void registerKeyInputs(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                if (flashlightKey.wasPressed()) {
                    //boolean lightEnabled = ArmorData.getFlashlightActive((IEntityDataSaver) client.player);
                    //ArmaFuturiMod.LOGGER.info("Key Input set the flashlight to {}", !lightEnabled);
                    //ArmorData.setFlashlightActive((IEntityDataSaver) client.player, lightEnabled);
                    ArmorData.toggleFlashlight((IEntityAccess) client.player);
                }
                if (shieldKey.wasPressed()) {
                    ArmorData.toggleShieldActive((IEntityAccess) client.player);
                }
                if (!client.options.jumpKey.isPressed() && ArmorData.getJetpackActive((IEntityAccess) client.player)) {
                    ArmorData.setJetpackActive((IEntityAccess) client.player, client.options.jumpKey.isPressed());
                }
                if (client.options.attackKey.isPressed() && !attackPressed){
                    Item item = client.player.getMainHandStack().getItem();
                    if (item instanceof RifleItem) {
                        if (((RifleItem) item).canShoot(client.player.getMainHandStack(), client.player)) {
                            ((RifleItem) item).shootAsShooter(client.world, client.player, client.player.getMainHandStack());
                        }
                        if (!((RifleItem)item).isAutomatic()) {
                            attackPressed = true;
                        }
                    }
                }if (!client.options.attackKey.isPressed()) {
                    attackPressed = false;
                }
            }
        });
    }
    public static void register(){

        flashlightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_TOGGLE_FLASHLIGHT,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KEY_CATEGORY_ArmaFuturi
        ));

        shieldKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ENABLE_SHIELD,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_T,
                KEY_CATEGORY_ArmaFuturi
        ));

        registerKeyInputs();
    }
}
