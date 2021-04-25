package se.kth.ict.id2203.components.epfd;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class HeartbeatReply extends Pp2pDeliver {
	
	private static final long serialVersionUID = -34645352345432L;
	private final Integer sequenceNumber;
	
	protected HeartbeatReply(Address source, Integer sequenceNumber) {
		super(source);
		this.sequenceNumber = sequenceNumber;
	}
	
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}
}
