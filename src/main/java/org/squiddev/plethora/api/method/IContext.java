package org.squiddev.plethora.api.method;

import dan200.computercraft.api.lua.ILuaObject;
import org.squiddev.plethora.api.reference.IReference;

import javax.annotation.Nonnull;

/**
 * This holds the context for a method.
 *
 * This tracks the current object and all parent/associated objects
 */
public interface IContext<T> extends IPartialContext<T> {
	/**
	 * Make a child context
	 *
	 * @param target  The child's target
	 * @param context Additional context items
	 * @return The child context
	 */
	@Nonnull
	<U> IUnbakedContext<U> makeChild(@Nonnull IReference<U> target, @Nonnull IReference<?>... context);

	/**
	 * Get the unbaked context for this context.
	 *
	 * @return The unbaked context.
	 */
	@Nonnull
	IUnbakedContext<T> unbake();

	/**
	 * Get a lua object from this context
	 *
	 * @return The built Lua object
	 */
	@Nonnull
	ILuaObject getObject();
}
