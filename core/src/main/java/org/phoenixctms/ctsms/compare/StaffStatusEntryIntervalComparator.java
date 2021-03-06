package org.phoenixctms.ctsms.compare;

import java.util.Date;

import org.phoenixctms.ctsms.vo.StaffStatusEntryOutVO;

public class StaffStatusEntryIntervalComparator extends IntervalComparatorBase<StaffStatusEntryOutVO> {

	public StaffStatusEntryIntervalComparator(boolean intDesc) {
		super(intDesc);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Date getStart(StaffStatusEntryOutVO item) {
		return item.getStart();
	}

	@Override
	protected Date getStop(StaffStatusEntryOutVO item) {
		return item.getStop();
	}
}
