// Generated by: hibernate/SpringHibernateDaoImpl.vsl in andromda-spring-cartridge.
// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package org.phoenixctms.ctsms.domain;

import java.util.Collection;
import java.util.HashMap;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.phoenixctms.ctsms.enumeration.DBModule;
import org.phoenixctms.ctsms.query.CriteriaUtil;
import org.phoenixctms.ctsms.query.QueryUtil;
import org.phoenixctms.ctsms.query.SubCriteriaMap;
import org.phoenixctms.ctsms.vo.CriteriaInstantVO;
import org.phoenixctms.ctsms.vo.DepartmentVO;
import org.phoenixctms.ctsms.vo.InventoryCategoryVO;
import org.phoenixctms.ctsms.vo.InventoryInVO;
import org.phoenixctms.ctsms.vo.InventoryOutVO;
import org.phoenixctms.ctsms.vo.PSFVO;
import org.phoenixctms.ctsms.vo.StaffOutVO;
import org.phoenixctms.ctsms.vo.UserOutVO;
import org.phoenixctms.ctsms.vocycle.InventoryReflexionGraph;

/**
 * @see ENTITY
 */
public class InventoryDaoImpl
extends InventoryDaoBase
{

	private org.hibernate.Criteria createInventoryCriteria() {
		org.hibernate.Criteria inventoryCriteria = this.getSession().createCriteria(Inventory.class);
		return inventoryCriteria;
	}
	@Override
	protected Collection<Inventory> handleFindByCriteria(
			CriteriaInstantVO criteria, PSFVO psf) throws Exception {
		Query query = QueryUtil.createSearchQuery(
				criteria,
				DBModule.INVENTORY_DB,
				psf,
				this.getSessionFactory(),
				this.getCriterionTieDao(),
				this.getCriterionPropertyDao(),
				this.getCriterionRestrictionDao());
		return query.list();
	}

	@Override
	protected Collection<Inventory> handleFindByIdDepartment(Long inventoryId,
			Long departmentId, PSFVO psf) throws Exception {
		org.hibernate.Criteria inventoryCriteria = createInventoryCriteria();
		SubCriteriaMap criteriaMap = new SubCriteriaMap(Inventory.class, inventoryCriteria);
		CriteriaUtil.applyIdDepartmentCriterion(inventoryCriteria, inventoryId, departmentId);
		CriteriaUtil.applyPSFVO(criteriaMap, psf);
		return inventoryCriteria.list();
	}

	@Override
	protected Collection<Inventory> handleFindByOwner(Long ownerId, PSFVO psf)
			throws Exception {
		Criteria inventoryCriteria = createInventoryCriteria();
		SubCriteriaMap criteriaMap = new SubCriteriaMap(Inventory.class, inventoryCriteria);
		if (ownerId != null) {
			inventoryCriteria.add(Restrictions.eq("owner.id", ownerId.longValue()));
		}
		CriteriaUtil.applyPSFVO(criteriaMap, psf);
		return inventoryCriteria.list();
	}

	@Override
	protected long handleGetChildrenCount(Long inventoryId) throws Exception {
		org.hibernate.Criteria inventoryCriteria = createInventoryCriteria();
		inventoryCriteria.add(Restrictions.eq("parent.id", inventoryId.longValue()));
		return (Long) inventoryCriteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	@Override
	protected long handleGetCountByCriteria(CriteriaInstantVO criteria, PSFVO psf) throws Exception {
		return QueryUtil.getSearchQueryResultCount(
				criteria,
				DBModule.INVENTORY_DB,
				psf,
				this.getSessionFactory(),
				this.getCriterionTieDao(),
				this.getCriterionPropertyDao(),
				this.getCriterionRestrictionDao());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Inventory inventoryInVOToEntity(InventoryInVO inventoryInVO)
	{
		Inventory entity = this.loadInventoryFromInventoryInVO(inventoryInVO);
		this.inventoryInVOToEntity(inventoryInVO, entity, true);
		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inventoryInVOToEntity(
			InventoryInVO source,
			Inventory target,
			boolean copyIfNull)
	{
		super.inventoryInVOToEntity(source, target, copyIfNull);
		Long departmentId = source.getDepartmentId();
		Long categoryId = source.getCategoryId();
		Long parentId = source.getParentId();
		Long ownerId = source.getOwnerId();
		if (departmentId != null) {
			target.setDepartment(this.getDepartmentDao().load(departmentId));
		} else if (copyIfNull) {
			target.setDepartment(null);
		}
		if (categoryId != null) {
			target.setCategory(this.getInventoryCategoryDao().load(categoryId));
		} else if (copyIfNull) {
			target.setCategory(null);
		}
		if (parentId != null) {
			Inventory parent = this.load(parentId);
			target.setParent(parent);
			parent.addChildren(target);
		} else if (copyIfNull) {
			Inventory parent = target.getParent();
			target.setParent(null);
			if (parent != null) {
				parent.removeChildren(target);
			}
		}
		if (ownerId != null) {
			Staff owner = this.getStaffDao().load(ownerId);
			target.setOwner(owner);
			owner.addInventories(target);
		} else if (copyIfNull) {
			Staff owner = target.getOwner();
			target.setOwner(null);
			if (owner != null) {
				owner.removeInventories(target);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Inventory inventoryOutVOToEntity(InventoryOutVO inventoryOutVO)
	{
		Inventory entity = this.loadInventoryFromInventoryOutVO(inventoryOutVO);
		this.inventoryOutVOToEntity(inventoryOutVO, entity, true);
		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inventoryOutVOToEntity(
			InventoryOutVO source,
			Inventory target,
			boolean copyIfNull)
	{
		super.inventoryOutVOToEntity(source, target, copyIfNull);
		InventoryCategoryVO categoryVO = source.getCategory();
		DepartmentVO departmentVO = source.getDepartment();
		InventoryOutVO parentVO = source.getParent();
		UserOutVO modifiedUserVO = source.getModifiedUser();
		StaffOutVO ownerVO = source.getOwner();
		if (categoryVO != null) {
			target.setCategory(this.getInventoryCategoryDao().inventoryCategoryVOToEntity(categoryVO));
		} else if (copyIfNull) {
			target.setCategory(null);
		}
		if (departmentVO != null) {
			target.setDepartment(this.getDepartmentDao().departmentVOToEntity(departmentVO));
		} else if (copyIfNull) {
			target.setDepartment(null);
		}
		if (parentVO != null) {
			Inventory parent = this.inventoryOutVOToEntity(parentVO);
			target.setParent(parent);
			parent.addChildren(target);
		} else if (copyIfNull) {
			Inventory parent = target.getParent();
			target.setParent(null);
			if (parent != null) {
				parent.removeChildren(target);
			}
		}
		if (modifiedUserVO != null) {
			target.setModifiedUser(this.getUserDao().userOutVOToEntity(modifiedUserVO));
		} else if (copyIfNull) {
			target.setModifiedUser(null);
		}
		if (ownerVO != null) {
			Staff owner = this.getStaffDao().staffOutVOToEntity(ownerVO);
			target.setOwner(owner);
			owner.addInventories(target);
		} else if (copyIfNull) {
			Staff owner = target.getOwner();
			target.setOwner(null);
			if (owner != null) {
				owner.removeInventories(target);
			}
		}
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private Inventory loadInventoryFromInventoryInVO(InventoryInVO inventoryInVO)
	{
		Inventory inventory = null;
		Long id = inventoryInVO.getId();
		if (id != null) {
			inventory = this.load(id);
		}
		if (inventory == null)
		{
			inventory = Inventory.Factory.newInstance();
		}
		return inventory;
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private Inventory loadInventoryFromInventoryOutVO(InventoryOutVO inventoryOutVO)
	{
		throw new UnsupportedOperationException("out value object to recursive entity not supported");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InventoryInVO toInventoryInVO(final Inventory entity)
	{
		return super.toInventoryInVO(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void toInventoryInVO(
			Inventory source,
			InventoryInVO target)
	{
		super.toInventoryInVO(source, target);
		Department department = source.getDepartment();
		InventoryCategory category = source.getCategory();
		Inventory parent = source.getParent();
		Staff owner = source.getOwner();
		if (department != null) {
			target.setDepartmentId(department.getId());
		}
		if (category != null) {
			target.setCategoryId(category.getId());
		}
		if (parent != null) {
			target.setParentId(parent.getId());
		}
		if (owner != null) {
			target.setOwnerId(owner.getId());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InventoryOutVO toInventoryOutVO(final Inventory entity)
	{
		return super.toInventoryOutVO(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void toInventoryOutVO(
			Inventory source,
			InventoryOutVO target)
	{
		(new InventoryReflexionGraph(this, this.getInventoryCategoryDao(), this.getDepartmentDao(), this.getStaffDao(), this.getUserDao())).toVOHelper(source, target,
				new HashMap<Class, HashMap<Long, Object>>());
	}

	@Override
	public void toInventoryOutVO(
			Inventory source,
			InventoryOutVO target, Integer... maxInstances)
	{
		(new InventoryReflexionGraph(this, this.getInventoryCategoryDao(), this.getDepartmentDao(), this.getStaffDao(), this.getUserDao(), maxInstances)).toVOHelper(source,
				target, new HashMap<Class, HashMap<Long, Object>>());
	}
}