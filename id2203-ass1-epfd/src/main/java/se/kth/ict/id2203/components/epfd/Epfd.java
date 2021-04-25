package se.kth.ict.id2203.components.epfd;

import se.kth.ict.id2203.ports.epfd.EventuallyPerfectFailureDetector;
import se.kth.ict.id2203.ports.epfd.Restore;
import se.kth.ict.id2203.ports.epfd.Suspect;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

import java.util.HashSet;
import java.util.Set;

public class Epfd extends ComponentDefinition {

	private long timeDelay;
	private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	private Positive<Timer> timer = requires(Timer.class);
	private Negative<EventuallyPerfectFailureDetector> epfd = provides(EventuallyPerfectFailureDetector.class);

	private final long delta;
	private Integer sequenceNumber;
	private Set<Address> alive;
	private Set<Address> suspected;
	private final Address self;
	private ScheduleTimeout timeout;
	private final Set<Address> nodes;

	public Epfd(EpfdInit init) {
		timeDelay = init.getInitialDelay();
		delta = init.getDeltaDelay();
		sequenceNumber = 0;
		alive = new HashSet<>(init.getAllAddresses());
		suspected = new HashSet<>();
		self = init.getSelfAddress();
		nodes = init.getAllAddresses();

		subscribe(handleStart, control);
		subscribe(handleCheckTimeout, timer);
		subscribe(handleHbRequests, pp2p);
		subscribe(handleHbReplies, pp2p);
	}

	private void setTimer(long timeDelay) {
		timeout = new ScheduleTimeout(timeDelay);
		timeout.setTimeoutEvent(new PublicTimeout(timeout));
		trigger(timeout, timer);
	}

	private Handler<Start> handleStart = new Handler<Start>() {

		@Override
		public void handle(Start event) {
			setTimer(timeDelay);
		}
	};

	private Handler<PublicTimeout> handleCheckTimeout = new Handler<PublicTimeout>() {

		@Override
		public void handle(PublicTimeout event) {

			for (Address node : alive) {
				if (suspected.contains(node)) {
					timeDelay += delta;
					break;
				}
			}
			sequenceNumber++;

			for (Address node : nodes) {
				if (!alive.contains(node)
						&& !suspected.contains(node)) {
					suspected.add(node);
					Suspect suspectEvent = new Suspect(node);
					trigger(suspectEvent, epfd);
				} else if (alive.contains(node)
						&& suspected.contains(node)) {
					suspected.remove(node);
					Restore restoreEvent = new Restore(node);
					trigger(restoreEvent, epfd);
				}

				HeartbeatRequest hbRequestMsg = new HeartbeatRequest(
						self, sequenceNumber);
				trigger(new Pp2pSend(node, hbRequestMsg), pp2p);
			}

			alive.clear();
			setTimer(timeDelay);
		}
	};

	private Handler<HeartbeatRequest> handleHbRequests = new Handler<HeartbeatRequest>() {

		@Override
		public void handle(HeartbeatRequest event) {
			HeartbeatReply hbReplyMsg = new HeartbeatReply(
					self, event.getSequenceNumber());
			trigger(new Pp2pSend(event.getSource(), hbReplyMsg), pp2p);
		}
	};

	private Handler<HeartbeatReply> handleHbReplies = new Handler<HeartbeatReply>() {

		@Override
		public void handle(HeartbeatReply event) {
			if (event.getSequenceNumber().equals(sequenceNumber)
					|| suspected.contains(event.getSource())) {
				alive.add(event.getSource());
			}
		}
	};
}
