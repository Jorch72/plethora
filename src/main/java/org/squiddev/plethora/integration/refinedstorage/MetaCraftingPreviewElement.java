package org.squiddev.plethora.integration.refinedstorage;

import com.google.common.collect.Maps;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import org.squiddev.plethora.api.meta.BaseMetaProvider;
import org.squiddev.plethora.api.meta.IMetaProvider;
import org.squiddev.plethora.api.method.IPartialContext;

import javax.annotation.Nonnull;
import java.util.Map;

@IMetaProvider.Inject(value = ICraftingPreviewElement.class, modId = RS.ID)
public class MetaCraftingPreviewElement extends BaseMetaProvider<ICraftingPreviewElement> {
	@Nonnull
	@Override
	public Map<Object, Object> getMeta(@Nonnull IPartialContext<ICraftingPreviewElement> context) {
		ICraftingPreviewElement preview = context.getTarget();

		Map<Object, Object> out = Maps.newHashMap();
		out.put("id", preview.getId());
		out.put("available", preview.getAvailable());
		out.put("toCraft", preview.getToCraft());
		out.put("component", context.makePartialChild(preview.getElement()).getMeta());

		return out;
	}
}
