package io.github.haykam821.bundleunduplicator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;

@Mixin(BundleItem.class)
public class BundleItemMixin {
	@Inject(method = {"onStackClicked", "onClicked"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BundleItem;addToBundle(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V"))
	private void preventMergingBundledBundles(ItemStack bundle, ItemStack stack, ClickType clickType, PlayerInventory inventory, CallbackInfoReturnable<Boolean> ci) {
		if (bundle.getCount() == 1) return;
		
		// Move the bundles that are not being acted on to another stack
		ItemStack separatedStack = bundle.copy();
		separatedStack.decrement(1);
		if (inventory.player instanceof ServerPlayerEntity || inventory.player.isCreative()) {
			inventory.offerOrDrop(separatedStack);
		}

		// Only act on a single bundle, rather than a separatable stack
		bundle.setCount(1);
	}
}