package se.kth.ict.id2203.components.epfd;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class HeartbeatRequest extends Pp2pDeliver{
	
	private static final long serialVersionUID = -234324324321L;
	private final Integer sequenceNumber;
	
	public HeartbeatRequest(Address source, Integer sequenceNumber) {
		super(source);
		this.sequenceNumber = sequenceNumber;
	}
	
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}
}
