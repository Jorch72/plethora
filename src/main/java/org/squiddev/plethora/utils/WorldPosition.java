package org.squiddev.plethora.utils;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public final class WorldPosition {
	private final int dimension;
	private WeakReference<World> world;
	private final Vec3d pos;

	public WorldPosition(@Nonnull World world, @Nonnull Vec3d pos) {
		Preconditions.checkNotNull(world, "world cannot be null");
		Preconditions.checkNotNull(pos, "pos cannot be null");

		this.dimension = world.provider.getDimension();
		this.world = new WeakReference<World>(world);
		this.pos = pos;
	}

	private WorldPosition(int dimension, @Nonnull Vec3d pos) {
		this.dimension = dimension;
		this.world = new WeakReference<World>(null);
		this.pos = pos;
	}

	public WorldPosition(@Nonnull World world, @Nonnull BlockPos pos) {
		Preconditions.checkNotNull(world, "world cannot be null");
		Preconditions.checkNotNull(pos, "pos cannot be null");

		this.dimension = world.provider.getDimension();
		this.world = new WeakReference<World>(world);
		this.pos = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}

	public WorldPosition(@Nonnull World world, double x, double y, double z) {
		this(world, new Vec3d(x, y, z));
	}

	@Nullable
	public World getWorld() {
		return world.get();
	}

	@Nullable
	public World getWorld(MinecraftServer server) {
		World world = this.world.get();
		if (world == null) {
			world = server.worldServerForDimension(dimension);
			if (world != null) this.world = new WeakReference<World>(world);
		}

		return world;
	}

	public int getDimension() {
		return dimension;
	}

	@Nonnull
	public Vec3d getPos() {
		return pos;
	}

	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("dim", dimension);
		tag.setDouble("x", pos.xCoord);
		tag.setDouble("y", pos.yCoord);
		tag.setDouble("z", pos.zCoord);
		return tag;
	}

	public static WorldPosition deserializeNBT(NBTTagCompound nbt) {
		return new WorldPosition(nbt.getInteger("dim"), new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z")));
	}
}
