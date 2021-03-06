package org.squiddev.plethora.core;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraftforge.fml.common.Optional;
import org.squiddev.cctweaks.CCTweaks;
import org.squiddev.cctweaks.api.lua.IMethodDescriptor;
import org.squiddev.plethora.api.PlethoraAPI;
import org.squiddev.plethora.api.method.*;
import org.squiddev.plethora.api.reference.IReference;
import org.squiddev.plethora.utils.DebugLogger;

import java.util.List;

import static org.squiddev.plethora.api.reference.Reference.id;

/**
 * Handles the base for various Lua objects
 *
 * @see dan200.computercraft.api.peripheral.IPeripheral
 * @see dan200.computercraft.api.lua.ILuaObject
 */
@Optional.Interface(
	iface = "org.squiddev.cctweaks.api.lua.IMethodDescriptor",
	modid = CCTweaks.ID
)
public class MethodWrapper implements IMethodDescriptor {
	private final List<IMethod<?>> methods;
	private final List<IUnbakedContext<?>> contexts;

	private final String[] names;

	public MethodWrapper(List<IMethod<?>> methods, List<IUnbakedContext<?>> contexts) {
		this.contexts = contexts;
		this.methods = methods;

		String[] names = this.names = new String[methods.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = methods.get(i).getName();
		}
	}

	public String[] getMethodNames() {
		return names;
	}

	public IMethod<?> getMethod(int i) {
		return methods.get(i);
	}

	public IUnbakedContext<?> getContext(int i) {
		return contexts.get(i);
	}

	protected IReference<?>[] getReferences(IComputerAccess access, ILuaContext context) {
		return new IReference[]{id(access), id(context)};
	}

	/**
	 * Check if the methods are the same on both.
	 *
	 * This isn't a perfect test for equality: it doesn't check contexts are the same
	 *
	 * @param other The item to match against
	 * @return Whether the methods are equal
	 */
	public boolean equalMethods(MethodWrapper other) {
		// Do the easy version: check they are the same items with same order.
		if (methods.equals(other.methods)) return true;

		if (methods.size() != other.methods.size()) return false;

		/*
			Doubt this is possible but better safe than sorry?
			We *could* make a hash set but this path is not going to be visited much, also not sure if there would be
			any efficiency gain: the method count is pretty small.
		  */
		for (IMethod method : methods) {
			if (!other.methods.contains(method)) return false;
		}
		return true;
	}

	private static final IMethodRegistry registry = PlethoraAPI.instance().methodRegistry();

	@SuppressWarnings("unchecked")
	protected static MethodResult doCallMethod(IMethod method, IUnbakedContext context, Object[] args) throws LuaException {
		try {
			CostHelpers.checkCost(context.getCostHandler(), registry.getBaseMethodCost(method));
			return method.apply(context, args);
		} catch (LuaException e) {
			throw e;
		} catch (Throwable e) {
			DebugLogger.error("Unexpected error calling " + method.getName(), e);
			throw new LuaException("Java Exception Thrown: " + e.toString());
		}
	}

	@Override
	@Optional.Method(modid = CCTweaks.ID)
	public boolean willYield(int method) {
		return getMethod(method).willYield();
	}
}
