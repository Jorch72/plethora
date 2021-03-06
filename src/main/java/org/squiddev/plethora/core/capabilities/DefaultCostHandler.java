package org.squiddev.plethora.core.capabilities;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.squiddev.plethora.api.method.ICostHandler;

import java.util.Map;

import static org.squiddev.plethora.core.ConfigCore.CostSystem;

/**
 * A basic {@link ICostHandler} implementation. Every object registered with it is updated every tick.
 *
 * @see org.squiddev.plethora.core.PlethoraCore#onServerTick(TickEvent.ServerTickEvent)
 */
public final class DefaultCostHandler implements ICostHandler {
	/**
	 * Used to store all handlers.
	 * This uses the identity function
	 */
	private static final Map<Object, DefaultCostHandler> handlers = new MapMaker().weakKeys().makeMap();

	private double value;
	private final double regenRate;
	private final double limit;
	private final boolean allowNegative;

	public DefaultCostHandler(double initial, double regenRate, double limit, boolean allowNegative) {
		Preconditions.checkArgument(initial >= 0, "initial must be >= 0");
		Preconditions.checkArgument(regenRate > 0, "regenRate must be > 0");

		Preconditions.checkArgument(limit > 0, "limit must be > 0");
		Preconditions.checkArgument(limit > regenRate, "limit must be > regenRate");

		this.regenRate = regenRate;
		this.limit = limit;
		this.allowNegative = allowNegative;
	}

	public DefaultCostHandler() {
		this(CostSystem.initial, CostSystem.regen, CostSystem.limit, CostSystem.allowNegative);
	}

	@Override
	public synchronized double get() {
		return value;
	}

	@Override
	public synchronized boolean consume(double amount) {
		Preconditions.checkArgument(amount >= 0, "amount must be >= 0");

		if (allowNegative) {
			if (value <= 0) return false;
		} else {
			if (amount > value) return false;
		}

		value -= amount;
		return true;
	}

	private synchronized void regen() {
		if (value < limit) {
			value = Math.min(limit, value + regenRate);
		}
	}

	public static ICostHandler get(Object owner) {
		synchronized (handlers) {
			DefaultCostHandler handler = handlers.get(owner);
			if (handler == null) {
				handler = new DefaultCostHandler();
				handlers.put(owner, handler);
			}

			return handler;
		}
	}

	public static void update() {
		synchronized (handlers) {
			for (DefaultCostHandler handler : handlers.values()) {
				handler.regen();
			}
		}
	}

	public static void reset() {
		synchronized (handlers) {
			handlers.clear();
		}
	}
}
