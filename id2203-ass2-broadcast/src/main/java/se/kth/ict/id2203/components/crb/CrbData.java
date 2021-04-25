package se.kth.ict.id2203.components.crb;

import se.kth.ict.id2203.ports.crb.CrbDeliver;
import se.kth.ict.id2203.ports.rb.RbDeliver;
import se.sics.kompics.address.Address;

public class CrbData extends RbDeliver {

	private static final long serialVersionUID = -2490125738135088267L;

	private final int[] vector;
	private final CrbDeliver data;

	public CrbData(Address source, CrbDeliver data, int[] vector) {
		super(source);
		this.vector = vector;
		this.data = data;
	}

	public int[] getVector() {
		return vector;
	}

	public CrbDeliver getData() {
		return data;
	}
}
