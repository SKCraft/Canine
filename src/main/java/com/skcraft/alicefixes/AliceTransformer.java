package com.skcraft.alicefixes;

import com.skcraft.alicefixes.transformers.TransformBreaker;
import com.skcraft.alicefixes.transformers.TransformTools;
import com.skcraft.alicefixes.transformers.TransformExcavationWand;
import com.skcraft.alicefixes.transformers.TransformTradeWand;
import com.skcraft.alicefixes.util.ASMHelper;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.launchwrapper.IClassTransformer;
import java.util.logging.Level;

public class AliceTransformer implements IClassTransformer {

    private final IClassTransformer[] transformers = {
        new TransformExcavationWand(),
        new TransformTradeWand(),
        new TransformTools("thaumcraft.common.items.equipment.ItemElementalAxe", "func_77648_a", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)Z"),
        new TransformTools("thaumcraft.common.items.equipment.ItemElementalShovel", "func_77660_a", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;IIIILnet/minecraft/entity/EntityLivingBase;)Z"),
        new TransformTools("gravisuite.ItemVajra", "a", "(L" + ASMHelper.getObf("ItemStack") + ";L" + ASMHelper.getObf("EntityPlayer") + ";L" + ASMHelper.getObf("World") + ";IIIIFFF)Z"),
        new TransformTools("thermalexpansion.item.tool.ItemWrench", "onItemUseFirst", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)Z"),
        new TransformTools("thermalexpansion.item.tool.ItemWrenchBattle", "onItemUseFirst", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)Z"),
        new TransformTools("gregtechmod.api.items.GT_Wrench_Item", "onItemUseFirst", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)Z"),
        new TransformBreaker()
    };

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) {
            return bytes;
        }

        for(IClassTransformer transformer : transformers) {
            bytes = transformer.transform(name, transformedName, bytes);
            if(bytes == null)
                FMLLog.log(Level.SEVERE, "Transformer " + transformer.getClass().getCanonicalName() + " has corrupted class " + name);
        }

        return bytes;
    }
}
