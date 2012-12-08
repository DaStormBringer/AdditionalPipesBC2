package buildcraft.additionalpipes.pipes;

import java.util.Random;

import buildcraft.additionalpipes.pipes.logic.PipeLogicTeleport;
import buildcraft.api.core.Position;
import buildcraft.transport.PipeTransport;

public abstract class PipeTeleport extends APPipe {
	protected static final Random rand = new Random();

	public final PipeLogicTeleport logic;

	private boolean[] phasedBroadcastSignal = {false, false, false, false};

	public PipeTeleport(PipeTransport transport, PipeLogicTeleport logic, int itemID) {
		super(transport, logic, itemID);
		this.logic = logic;
	}

	@Override
	public void updateEntity() {
		for(int i = 0; i < broadcastSignal.length; i++) {
			if(phasedBroadcastSignal[i] != broadcastSignal[i]) {
				TeleportManager.instance.phasedSignals.get(logic.getFrequency())[i] += (broadcastSignal[i] ? 1 : -1);
				phasedBroadcastSignal[i] = broadcastSignal[i];
			}
		}
		super.updateEntity();
	}

	@Override
	public void initialize() {
		super.initialize();
		TeleportManager.instance.add(this);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		TeleportManager.instance.remove(this);
		logic.removePhasedSignals();
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		TeleportManager.instance.remove(this);
		logic.removePhasedSignals();
	}

	public Position getPosition() {
		return new Position(xCoord, yCoord, zCoord);
	}

}
