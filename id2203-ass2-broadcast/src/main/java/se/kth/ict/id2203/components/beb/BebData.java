package se.kth.ict.id2203.components.beb;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class BebData extends Pp2pDeliver {
	private static final long serialVersionUID = 42348327L;
	private final BebDeliver data;
	
	protected BebData(Address source, BebDeliver data) {
		super(source);
		this.data = data;
	}
	
	public BebDeliver getData() {
		return data;
	}
}
