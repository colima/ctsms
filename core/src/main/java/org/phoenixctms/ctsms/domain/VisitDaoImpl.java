// Generated by: hibernate/SpringHibernateDaoImpl.vsl in andromda-spring-cartridge.
// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package org.phoenixctms.ctsms.domain;

import java.text.MessageFormat;
import java.util.Collection;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.phoenixctms.ctsms.query.CriteriaUtil;
import org.phoenixctms.ctsms.query.SubCriteriaMap;
import org.phoenixctms.ctsms.vo.PSFVO;
import org.phoenixctms.ctsms.vo.TrialOutVO;
import org.phoenixctms.ctsms.vo.UserOutVO;
import org.phoenixctms.ctsms.vo.VisitInVO;
import org.phoenixctms.ctsms.vo.VisitOutVO;
import org.phoenixctms.ctsms.vo.VisitTypeVO;

/**
 * @see Visit
 */
public class VisitDaoImpl
extends VisitDaoBase
{

	private static final String UNIQUE_VISIT_NAME = "{0} - {1}";

	private static String getUniqueVisitName(VisitOutVO visitVO) {
		if (visitVO != null && visitVO.getTrial() != null) {
			return MessageFormat.format(UNIQUE_VISIT_NAME, visitVO.getTrial().getName(), visitVO.getTitle());
		}
		return null;
	}

	private org.hibernate.Criteria createVisitCriteria() {
		org.hibernate.Criteria visitCriteria = this.getSession().createCriteria(Visit.class);
		return visitCriteria;
	}

	@Override
	protected Collection<Visit> handleFindByTrial(Long trialId, PSFVO psf)
			throws Exception {
		org.hibernate.Criteria visitCriteria = createVisitCriteria();
		SubCriteriaMap criteriaMap = new SubCriteriaMap(Visit.class, visitCriteria);
		if (trialId != null) {
			visitCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		CriteriaUtil.applyPSFVO(criteriaMap, psf);
		return visitCriteria.list();
	}

	@Override
	protected Collection<Visit> handleFindByTrialTitleToken(Long trialId,
			String title, String token) throws Exception {
		org.hibernate.Criteria visitCriteria = createVisitCriteria();
		if (trialId != null) {
			visitCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		if (title != null) {
			visitCriteria.add(Restrictions.eq("title", title));
		}
		if (token != null) {
			visitCriteria.add(Restrictions.eq("token", token));
		}
		return visitCriteria.list();
	}

	@Override
	protected long handleGetCount(Long trialId)
			throws Exception {
		org.hibernate.Criteria visitCriteria = createVisitCriteria();
		if (trialId != null) {
			visitCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		return (Long) visitCriteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private Visit loadVisitFromVisitInVO(VisitInVO visitInVO)
	{
		// TODO implement loadVisitFromVisitInVO
		// throw new UnsupportedOperationException("org.phoenixctms.ctsms.domain.loadVisitFromVisitInVO(VisitInVO) not yet implemented.");
		Visit visit = null;
		Long id = visitInVO.getId();
		if (id != null) {
			visit = this.load(id);
		}
		if (visit == null)
		{
			visit = Visit.Factory.newInstance();
		}
		return visit;
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private Visit loadVisitFromVisitOutVO(VisitOutVO visitOutVO)
	{
		// TODO implement loadVisitFromVisitOutVO
		// throw new UnsupportedOperationException("org.phoenixctms.ctsms.domain.loadVisitFromVisitOutVO(VisitOutVO) not yet implemented.");
		Visit visit = this.load(visitOutVO.getId());
		if (visit == null)
		{
			visit = Visit.Factory.newInstance();
		}
		return visit;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public VisitInVO toVisitInVO(final Visit entity)
	{
		return super.toVisitInVO(entity);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void toVisitInVO(
			Visit source,
			VisitInVO target)
	{
		super.toVisitInVO(source, target);
		VisitType type = source.getType();
		Trial trial = source.getTrial();
		if (type != null) {
			target.setTypeId(type.getId());
		}
		if (trial != null) {
			target.setTrialId(trial.getId());
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public VisitOutVO toVisitOutVO(final Visit entity)
	{
		return super.toVisitOutVO(entity);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void toVisitOutVO(
			Visit source,
			VisitOutVO target)
	{
		super.toVisitOutVO(source, target);
		// WARNING! No conversion for target.trial (can't convert source.getTrial():org.phoenixctms.ctsms.domain.Trial to org.phoenixctms.ctsms.vo.TrialOutVO
		// WARNING! No conversion for target.modifiedUser (can't convert source.getModifiedUser():org.phoenixctms.ctsms.domain.User to org.phoenixctms.ctsms.vo.UserOutVO
		// WARNING! No conversion for target.type (can't convert source.getType():org.phoenixctms.ctsms.domain.VisitType to org.phoenixctms.ctsms.vo.VisitTypeVO
		VisitType type = source.getType();
		Trial trial = source.getTrial();
		User modifiedUser = source.getModifiedUser();
		if (type != null) {
			target.setType(this.getVisitTypeDao().toVisitTypeVO(type));
		}
		if (trial != null) {
			target.setTrial(this.getTrialDao().toTrialOutVO(trial));
		}
		if (modifiedUser != null) {
			target.setModifiedUser(this.getUserDao().toUserOutVO(modifiedUser));
		}
		target.setUniqueName(getUniqueVisitName(target));
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Visit visitInVOToEntity(VisitInVO visitInVO)
	{
		Visit entity = this.loadVisitFromVisitInVO(visitInVO);
		this.visitInVOToEntity(visitInVO, entity, true);
		return entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void visitInVOToEntity(
			VisitInVO source,
			Visit target,
			boolean copyIfNull)
	{
		super.visitInVOToEntity(source, target, copyIfNull);
		Long typeId = source.getTypeId();
		Long trialId = source.getTrialId();
		if (typeId != null) {
			target.setType(this.getVisitTypeDao().load(typeId));
		} else if (copyIfNull) {
			target.setType(null);
		}
		if (trialId != null) {
			Trial trial = this.getTrialDao().load(trialId);
			target.setTrial(trial);
			trial.addVisits(target);
		} else if (copyIfNull) {
			Trial trial = target.getTrial();
			target.setTrial(null);
			if (trial != null) {
				trial.removeVisits(target);
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Visit visitOutVOToEntity(VisitOutVO visitOutVO)
	{
		Visit entity = this.loadVisitFromVisitOutVO(visitOutVO);
		this.visitOutVOToEntity(visitOutVO, entity, true);
		return entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void visitOutVOToEntity(
			VisitOutVO source,
			Visit target,
			boolean copyIfNull)
	{
		super.visitOutVOToEntity(source, target, copyIfNull);
		VisitTypeVO typeVO = source.getType();
		TrialOutVO trialVO = source.getTrial();
		UserOutVO modifiedUserVO = source.getModifiedUser();
		if (typeVO != null) {
			target.setType(this.getVisitTypeDao().visitTypeVOToEntity(typeVO));
		} else if (copyIfNull) {
			target.setType(null);
		}
		if (trialVO != null) {
			Trial trial = this.getTrialDao().trialOutVOToEntity(trialVO);
			target.setTrial(trial);
			trial.addVisits(target);
		} else if (copyIfNull) {
			Trial trial = target.getTrial();
			target.setTrial(null);
			if (trial != null) {
				trial.removeVisits(target);
			}
		}
		if (modifiedUserVO != null) {
			target.setModifiedUser(this.getUserDao().userOutVOToEntity(modifiedUserVO));
		} else if (copyIfNull) {
			target.setModifiedUser(null);
		}
	}
}