package org.squiddev.plethora.gameplay.modules.methods;

import com.google.common.collect.Maps;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.squiddev.plethora.api.IWorldLocation;
import org.squiddev.plethora.api.WorldLocation;
import org.squiddev.plethora.api.method.IContext;
import org.squiddev.plethora.api.method.IUnbakedContext;
import org.squiddev.plethora.api.method.MethodResult;
import org.squiddev.plethora.api.module.IModuleContainer;
import org.squiddev.plethora.api.module.SubtargetedModuleMethod;
import org.squiddev.plethora.api.module.SubtargetedModuleObjectMethod;
import org.squiddev.plethora.api.reference.BlockReference;
import org.squiddev.plethora.gameplay.modules.PlethoraModules;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static dan200.computercraft.core.apis.ArgumentHelper.getInt;
import static org.squiddev.plethora.api.method.ArgumentHelper.assertBetween;
import static org.squiddev.plethora.gameplay.ConfigGameplay.Scanner.radius;

public final class MethodsScanner {
	@SubtargetedModuleObjectMethod.Inject(
		module = PlethoraModules.SCANNER_S, target = IWorldLocation.class, worldThread = true,
		doc = "function():table -- Scan all blocks in the vicinity"
	)
	@Nullable
	public static Object[] scan(@Nonnull IWorldLocation location, @Nonnull IContext<IModuleContainer> context, @Nonnull Object[] args) throws LuaException {
		final World world = location.getWorld();
		final BlockPos pos = location.getPos();
		final int x = pos.getX(), y = pos.getY(), z = pos.getZ();

		int i = 0;
		HashMap<Integer, Object> map = Maps.newHashMap();
		for (int oX = x - radius; oX <= x + radius; oX++) {
			for (int oY = y - radius; oY <= y + radius; oY++) {
				for (int oZ = z - radius; oZ <= z + radius; oZ++) {
					BlockPos newPos = new BlockPos(oX, oY, oZ);
					IBlockState block = world.getBlockState(newPos);

					HashMap<String, Object> data = Maps.newHashMap();
					data.put("x", oX - x);
					data.put("y", oY - y);
					data.put("z", oZ - z);
					ResourceLocation name = block.getBlock().getRegistryName();
					data.put("name", name == null ? "unknown" : name.toString());

					map.put(++i, data);
				}
			}
		}

		return new Object[]{map};
	}

	@SubtargetedModuleMethod.Inject(
		module = PlethoraModules.SCANNER_S,
		target = IWorldLocation.class,
		doc = "function(x:integer, y:integer, z:integer):table -- Get metadata about a nearby block"
	)
	@Nonnull
	public static MethodResult getBlockMeta(@Nonnull final IUnbakedContext<IModuleContainer> context, @Nonnull Object[] args) throws LuaException {
		final int x = getInt(args, 0);
		final int y = getInt(args, 1);
		final int z = getInt(args, 2);

		assertBetween(x, -radius, radius, "X coordinate out of bounds (%s)");
		assertBetween(y, -radius, radius, "Y coordinate out of bounds (%s)");
		assertBetween(z, -radius, radius, "Z coordinate out of bounds (%s)");

		return MethodResult.nextTick(new Callable<MethodResult>() {
			@Override
			public MethodResult call() throws Exception {
				IContext<IModuleContainer> baked = context.bake();
				IWorldLocation location = baked.getContext(IWorldLocation.class);

				World world = location.getWorld();
				BlockPos pos = location.getPos().add(x, y, z);

				Map<Object, Object> meta = baked
					.makePartialChild(new BlockReference(new WorldLocation(world, pos)))
					.getMeta();

				return MethodResult.result(meta);
			}
		});
	}
}
