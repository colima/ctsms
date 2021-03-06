package org.phoenixctms.ctsms.compare;

import java.util.Comparator;
import java.util.Date;

public abstract class IntervalComparatorBase<T> implements Comparator<T> {

	private int intDesc;

	private IntervalComparatorBase() {
	}

	protected IntervalComparatorBase(boolean intDesc) {
		this.intDesc = (intDesc ? -1 : 1);
	}

	@Override
	public int compare(T a, T b) {
		if (a != null && b != null) {
			Date intervalAStart = getStart(a);
			Date intervalAStop = getStop(a);
			Date intervalBStart = getStart(b);
			Date intervalBStop = getStop(b);
			if (intervalAStart != null && intervalAStop != null) { // closed interval
				if (intervalBStart != null && intervalBStop != null) { // closed duration
					return intDesc * intervalAStart.compareTo(intervalBStart);
				} else if (intervalBStart == null && intervalBStop != null) {
					return intDesc * -1;
				} else if (intervalBStart != null && intervalBStop == null) {
					return intDesc * intervalAStart.compareTo(intervalBStart);
				} else {
					return intDesc * -1;
				}
			} else if (intervalAStart == null && intervalAStop != null) {
				if (intervalBStart != null && intervalBStop != null) {
					return intDesc * 1;
				} else if (intervalBStart == null && intervalBStop != null) {
					return 0;
				} else if (intervalBStart != null && intervalBStop == null) {
					return intDesc * 1;
				} else {
					return 0;
				}
			} else if (intervalAStart != null && intervalAStop == null) {
				if (intervalBStart != null && intervalBStop != null) {
					return intDesc * intervalAStart.compareTo(intervalBStart);
				} else if (intervalBStart == null && intervalBStop != null) {
					return intDesc * -1;
				} else if (intervalBStart != null && intervalBStop == null) {
					return intDesc * intervalAStart.compareTo(intervalBStart);
				} else {
					return intDesc * -1;
				}
			} else {
				if (intervalBStart != null && intervalBStop != null) {
					return intDesc * 1;
				} else if (intervalBStart == null && intervalBStop != null) {
					return 0;
				} else if (intervalBStart != null && intervalBStop == null) {
					return intDesc * 1;
				} else {
					return 0;
				}
			}
		} else if (a == null && b != null) {
			return intDesc * -1;
		} else if (a != null && b == null) {
			return intDesc * 1;
		} else {
			return 0;
		}
	}

	protected abstract Date getStart(T item);

	protected abstract Date getStop(T item);
}
