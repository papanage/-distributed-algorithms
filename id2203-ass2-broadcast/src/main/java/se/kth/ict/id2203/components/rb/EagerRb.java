package se.kth.ict.id2203.components.rb;

import java.util.HashSet;
import java.util.Set;


import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Negative;
import se.sics.kompics.address.Address;
import se.kth.ict.id2203.ports.rb.RbBroadcast;
import se.kth.ict.id2203.ports.rb.ReliableBroadcast;

public class EagerRb extends ComponentDefinition {

	private final Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
	private final Negative<ReliableBroadcast> rb = provides(ReliableBroadcast.class);

	private final Address self;
	private Integer sequenceNumber;
	private final Set<RbData> delivered;

	public EagerRb(EagerRbInit init) {

		//ставим значения из задания при инициализации
		this.sequenceNumber = 0;
		this.delivered = new HashSet<>();

		this.self = init.getSelfAddress();
		new HashSet<>(init.getAllAddresses());


		//хендлер на бродкаст
		subscribe(broadcastHandler, rb);
		subscribe(deliveryHandler, beb);
	}

	private Handler<RbBroadcast> broadcastHandler = new Handler<RbBroadcast>() {

		@Override
		public void handle(RbBroadcast event) {
			sequenceNumber++;
			RbData msg = new RbData(self,
					event.getDeliverEvent(), sequenceNumber);
			trigger(new BebBroadcast(msg), beb);
		}

	};
	
	private Handler<RbData> deliveryHandler = new Handler<RbData>() {

		@Override
		public void handle(RbData event) {
			
			if (!delivered.contains(event)) {
				delivered.add(event);
				trigger(event.getData(), rb);
				trigger(new BebBroadcast(event), beb);
			}
		}
	};
}
