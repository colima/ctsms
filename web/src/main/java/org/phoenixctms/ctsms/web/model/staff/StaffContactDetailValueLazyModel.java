package org.phoenixctms.ctsms.web.model.staff;

import java.util.ArrayList;
import java.util.Collection;

import org.phoenixctms.ctsms.exception.AuthenticationException;
import org.phoenixctms.ctsms.exception.AuthorisationException;
import org.phoenixctms.ctsms.exception.ServiceException;
import org.phoenixctms.ctsms.vo.PSFVO;
import org.phoenixctms.ctsms.vo.StaffContactDetailValueOutVO;
import org.phoenixctms.ctsms.web.model.LazyDataModelBase;
import org.phoenixctms.ctsms.web.util.WebUtil;

public class StaffContactDetailValueLazyModel extends LazyDataModelBase {

	private Long staffId;

	@Override
	protected Collection<StaffContactDetailValueOutVO> getLazyResult(PSFVO psf) {
		if (staffId != null) {
			try {
				return WebUtil.getServiceLocator().getStaffService().getStaffContactDetailValueList(WebUtil.getAuthentication(), staffId, null, psf);
			} catch (ServiceException e) {
			} catch (AuthenticationException e) {
				WebUtil.publishException(e);
			} catch (AuthorisationException e) {
			} catch (IllegalArgumentException e) {
			}
		}
		return new ArrayList<StaffContactDetailValueOutVO>();
	}

	@Override
	protected StaffContactDetailValueOutVO getRowElement(Long id) {
		try {
			return WebUtil.getServiceLocator().getStaffService().getStaffContactDetailValue(WebUtil.getAuthentication(), id);
		} catch (ServiceException e) {
		} catch (AuthenticationException e) {
			WebUtil.publishException(e);
		} catch (AuthorisationException e) {
		} catch (IllegalArgumentException e) {
		}
		return null;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}
}
