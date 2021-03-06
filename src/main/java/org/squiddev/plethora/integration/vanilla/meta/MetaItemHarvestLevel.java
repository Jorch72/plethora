package org.squiddev.plethora.integration.vanilla.meta;

import com.google.common.collect.Maps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.squiddev.plethora.api.meta.BasicMetaProvider;
import org.squiddev.plethora.api.meta.IMetaProvider;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Displays classes for tools
 */
@IMetaProvider.Inject(value = ItemStack.class, namespace = "toolClass")
public class MetaItemHarvestLevel extends BasicMetaProvider<ItemStack> {
	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public Map<Object, Object> getMeta(@Nonnull ItemStack stack) {
		Item item = stack.getItem();
		if (!item.getToolClasses(stack).isEmpty()) {
			HashMap<Object, Object> types = Maps.newHashMap();

			for (String tool : item.getToolClasses(stack)) {
				types.put(tool, item.getHarvestLevel(stack, tool));
			}

			return types;
		} else {
			return Collections.emptyMap();
		}
	}
}
