package net.blay09.mods.cookingforblockheads.compat;

import java.util.HashSet;

import net.blay09.mods.cookingforblockheads.CookingForBlockheads;
import net.blay09.mods.cookingforblockheads.api.capability.CapabilityKitchenConnector;
import net.blay09.mods.cookingforblockheads.api.capability.CapabilityKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.api.capability.KitchenItemProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class CompatCapabilityLoader {
    private final static HashSet<Class<? extends TileEntity>> itemProviderClasses = new HashSet<Class<? extends TileEntity>>();
    private final static HashSet<Class<? extends TileEntity>> connectorClasses = new HashSet<Class<? extends TileEntity>>();
    private final static CompatCapabilityLoader instance = new CompatCapabilityLoader();
    private static boolean registered = false;

    public static void register() {
        if (! registered) {
            MinecraftForge.EVENT_BUS.register(instance);
            registered = true;
        }
    }

    @SuppressWarnings("unchecked")
    private static void addTileEntityClass(final String className, final HashSet<Class<? extends TileEntity>> list) {
        try {
            Class<?> c = Class.forName(className);
            if (TileEntity.class.isAssignableFrom(c)) {
                list.add((Class<? extends TileEntity>) c);
                register();
            } else {
                CookingForBlockheads.logger.warn("Incompatible TileEntity class: {}", className);
            }
        } catch (ClassNotFoundException e) {
            CookingForBlockheads.logger.warn("TileEntity class not found: {}", className);
        }
    }

    public static void addKitchenItemProviderClass(String className) {
        addTileEntityClass(className, itemProviderClasses);
    }

    public static void addKitchenConnectorClass(String className) {
        addTileEntityClass(className, connectorClasses);
    }

    private final static ResourceLocation itemProviderResourceKey = new ResourceLocation(CookingForBlockheads.MOD_ID,
            CapabilityKitchenItemProvider.CAPABILITY.getName());
    private final static ResourceLocation connectorResourceKey = new ResourceLocation(CookingForBlockheads.MOD_ID,
            CapabilityKitchenConnector.CAPABILITY.getName());
    private final static KitchenConnectorCapabilityProvider connectorCapabilityProvider = new KitchenConnectorCapabilityProvider();

    @SubscribeEvent
    public void tileEntity(AttachCapabilitiesEvent<TileEntity> event) {
        final TileEntity entity = event.getObject();
        final Class<? extends TileEntity> entityClass = entity.getClass();

        if (itemProviderClasses.contains(entityClass)) {
            event.addCapability(itemProviderResourceKey, new KitchenItemCapabilityProvider(entity));
            return;
        }
        if (connectorClasses.contains(entityClass)) {
            event.addCapability(connectorResourceKey, connectorCapabilityProvider);
            return;
        }
        CookingForBlockheads.logger.warn("Entity class not handled: " + entityClass);
    }

    private static final class KitchenConnectorCapabilityProvider implements ICapabilityProvider {
        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return CapabilityKitchenConnector.CAPABILITY == capability;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            return null;
        }
    }

    private final static ItemStackHandler emptyItemHandler = new ItemStackHandler(0);

    private final static class KitchenItemCapabilityProvider implements ICapabilityProvider {
        private KitchenItemProvider kitchenProvider = null;
        private final TileEntity entity;

        public KitchenItemCapabilityProvider(final TileEntity entity) {
            this.entity = entity;
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return CapabilityKitchenItemProvider.CAPABILITY == capability;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (CapabilityKitchenItemProvider.CAPABILITY != capability) {
                return null;
            }

            if (kitchenProvider != null) {
                return (T) kitchenProvider;
            }

            IItemHandler handler = null;
            if (entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                handler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            }
            kitchenProvider = new KitchenItemProvider(handler != null ? handler : emptyItemHandler);
            return (T) kitchenProvider;
        }
    }

}
