package com.mhanak.arma_futuri.item.armor;

import com.mhanak.arma_futuri.entity.client.armor.LightCombatArmorRenderer;
import com.mhanak.arma_futuri.item.ArmorItemWithExpansions;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class LightCombatArmorItem extends ArmorItemWithExpansions implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public LightCombatArmorItem(ArmorMaterial armorMaterial, ArmorItem.Type type, Settings properties) {
        super(armorMaterial, type, properties);
    }

    public int getExpansionSlots(ArmorItem.Type type) {
        if (Objects.requireNonNull(type) == Type.CHESTPLATE) {
            return 1;
        }
        return 0;
    }

    public float getDefaultArmorWeight(ArmorItem.Type type){
        return 1.0f;
    }

    public float getDefaultSpeedModifier(ArmorItem.Type type) {
        switch (type){
            case HELMET -> {
                return 0.95f;
            }
            case CHESTPLATE -> {
                return 0.8f;
            }
            case LEGGINGS -> {
                return 1.5f;
            }
            case BOOTS -> {
                return 1.2f;
            }
        }
        return 0;
    }

    public float getDefaultProtection(ArmorItem.Type type) {
        switch (type){
            case HELMET -> {
                return 0.3f;
            }
            case CHESTPLATE -> {
                return 0.8f;
            }
            case LEGGINGS -> {
                return 0.7f;
            }
            case BOOTS -> {
                return 0.4f;
            }
        }
        return 0;
    }

    @Override
    public float oxygenUsageMultiplier(Type type) {return 1;}

    @Override
    public float fallDamageMultiplier(Type type) {
        switch (type){
            case LEGGINGS -> {
                return 0.7f;
            }
            case BOOTS -> {
                return 0.6f;
            }
        }
        return 1;
    }

    @Override
    public boolean isSealed() {
        return false;
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
                if (this.renderer == null)
                    this.renderer = new LightCombatArmorRenderer();

                // This prepares our GeoArmorRenderer for the current render frame.
                // These parameters may be null however, so we don't do anything further with them
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    // Let's add our animation controller
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}