package org.phoenixctms.ctsms.util;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.hibernate.LockMode;
import org.phoenixctms.ctsms.adapt.ProbandListStatusEntryCollisionFinder;
import org.phoenixctms.ctsms.compare.AlphanumStringComparator;
import org.phoenixctms.ctsms.compare.BankAccountOutVOComparator;
import org.phoenixctms.ctsms.compare.CvPositionPDFVOComparator;
import org.phoenixctms.ctsms.compare.MoneyTransferOutVOComparator;
import org.phoenixctms.ctsms.compare.VisitScheduleItemOutVOComparator;
import org.phoenixctms.ctsms.domain.*;
import org.phoenixctms.ctsms.enumeration.ECRFFieldStatusQueue;
import org.phoenixctms.ctsms.enumeration.InputFieldType;
import org.phoenixctms.ctsms.enumeration.JournalModule;
import org.phoenixctms.ctsms.enumeration.PaymentMethod;
import org.phoenixctms.ctsms.enumeration.PermissionProfile;
import org.phoenixctms.ctsms.enumeration.PermissionProfileGroup;
import org.phoenixctms.ctsms.enumeration.VariablePeriod;
import org.phoenixctms.ctsms.excel.ExcelExporter;
import org.phoenixctms.ctsms.excel.ExcelUtil;
import org.phoenixctms.ctsms.excel.ReimbursementsExcelDefaultSettings;
import org.phoenixctms.ctsms.excel.ReimbursementsExcelSettingCodes;
import org.phoenixctms.ctsms.excel.ReimbursementsExcelWriter;
import org.phoenixctms.ctsms.excel.VisitScheduleExcelDefaultSettings;
import org.phoenixctms.ctsms.excel.VisitScheduleExcelSettingCodes;
import org.phoenixctms.ctsms.excel.VisitScheduleExcelWriter;
import org.phoenixctms.ctsms.exception.AuthorisationException;
import org.phoenixctms.ctsms.exception.ServiceException;
import org.phoenixctms.ctsms.pdf.CVPDFPainter;
import org.phoenixctms.ctsms.pdf.CourseCertificatePDFDefaultSettings;
import org.phoenixctms.ctsms.pdf.CourseCertificatePDFPainter;
import org.phoenixctms.ctsms.pdf.CourseCertificatePDFSettingCodes;
import org.phoenixctms.ctsms.pdf.CourseParticipantListPDFPainter;
import org.phoenixctms.ctsms.pdf.ProbandLetterPDFPainter;
import org.phoenixctms.ctsms.security.CryptoUtil;
import org.phoenixctms.ctsms.util.L10nUtil.Locales;
import org.phoenixctms.ctsms.util.Settings.Bundle;
import org.phoenixctms.ctsms.util.date.BookingDuration;
import org.phoenixctms.ctsms.util.date.DateCalc;
import org.phoenixctms.ctsms.util.date.DateInterval;
import org.phoenixctms.ctsms.util.date.ShiftDuration;
import org.phoenixctms.ctsms.vo.AddressTypeVO;
import org.phoenixctms.ctsms.vo.AspSubstanceVO;
import org.phoenixctms.ctsms.vo.BankAccountOutVO;
import org.phoenixctms.ctsms.vo.CourseOutVO;
import org.phoenixctms.ctsms.vo.CourseParticipationStatusEntryInVO;
import org.phoenixctms.ctsms.vo.CourseParticipationStatusEntryOutVO;
import org.phoenixctms.ctsms.vo.CriteriaInstantVO;
import org.phoenixctms.ctsms.vo.CriterionInVO;
import org.phoenixctms.ctsms.vo.CriterionInstantVO;
import org.phoenixctms.ctsms.vo.CriterionOutVO;
import org.phoenixctms.ctsms.vo.CriterionPropertyVO;
import org.phoenixctms.ctsms.vo.CriterionRestrictionVO;
import org.phoenixctms.ctsms.vo.CriterionTieVO;
import org.phoenixctms.ctsms.vo.CvPositionPDFVO;
import org.phoenixctms.ctsms.vo.CvSectionVO;
import org.phoenixctms.ctsms.vo.DutyRosterTurnOutVO;
import org.phoenixctms.ctsms.vo.ECRFFieldOutVO;
import org.phoenixctms.ctsms.vo.ECRFFieldStatusEntryOutVO;
import org.phoenixctms.ctsms.vo.ECRFFieldStatusQueueCountVO;
import org.phoenixctms.ctsms.vo.ECRFFieldValueInVO;
import org.phoenixctms.ctsms.vo.ECRFFieldValueJsonVO;
import org.phoenixctms.ctsms.vo.ECRFFieldValueOutVO;
import org.phoenixctms.ctsms.vo.ECRFOutVO;
import org.phoenixctms.ctsms.vo.ECRFProgressVO;
import org.phoenixctms.ctsms.vo.ECRFSectionProgressVO;
import org.phoenixctms.ctsms.vo.InputFieldOutVO;
import org.phoenixctms.ctsms.vo.InputFieldSelectionSetValueOutVO;
import org.phoenixctms.ctsms.vo.InputFieldTypeVO;
import org.phoenixctms.ctsms.vo.InquiryOutVO;
import org.phoenixctms.ctsms.vo.InquiryValueInVO;
import org.phoenixctms.ctsms.vo.InquiryValueJsonVO;
import org.phoenixctms.ctsms.vo.InquiryValueOutVO;
import org.phoenixctms.ctsms.vo.InventoryBookingDurationSummaryDetailVO;
import org.phoenixctms.ctsms.vo.InventoryBookingDurationSummaryVO;
import org.phoenixctms.ctsms.vo.InventoryBookingOutVO;
import org.phoenixctms.ctsms.vo.InventoryOutVO;
import org.phoenixctms.ctsms.vo.LecturerCompetenceVO;
import org.phoenixctms.ctsms.vo.LecturerOutVO;
import org.phoenixctms.ctsms.vo.MoneyTransferByBankAccountSummaryDetailVO;
import org.phoenixctms.ctsms.vo.MoneyTransferByCostTypeSummaryDetailVO;
import org.phoenixctms.ctsms.vo.MoneyTransferByPaymentMethodSummaryDetailVO;
import org.phoenixctms.ctsms.vo.MoneyTransferOutVO;
import org.phoenixctms.ctsms.vo.MoneyTransferSummaryVO;
import org.phoenixctms.ctsms.vo.PasswordInVO;
import org.phoenixctms.ctsms.vo.PasswordOutVO;
import org.phoenixctms.ctsms.vo.PermissionProfileVO;
import org.phoenixctms.ctsms.vo.ProbandAddressOutVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryOutVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryTagOutVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryTagValueInVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryTagValueJsonVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryTagValueOutVO;
import org.phoenixctms.ctsms.vo.ProbandListStatusEntryInVO;
import org.phoenixctms.ctsms.vo.ProbandListStatusEntryOutVO;
import org.phoenixctms.ctsms.vo.ProbandOutVO;
import org.phoenixctms.ctsms.vo.ReimbursementsExcelVO;
import org.phoenixctms.ctsms.vo.ShiftDurationSummaryDetailVO;
import org.phoenixctms.ctsms.vo.ShiftDurationSummaryVO;
import org.phoenixctms.ctsms.vo.StaffAddressOutVO;
import org.phoenixctms.ctsms.vo.StaffImageOutVO;
import org.phoenixctms.ctsms.vo.StaffOutVO;
import org.phoenixctms.ctsms.vo.TrialOutVO;
import org.phoenixctms.ctsms.vo.UserInVO;
import org.phoenixctms.ctsms.vo.UserOutVO;
import org.phoenixctms.ctsms.vo.VisitOutVO;
import org.phoenixctms.ctsms.vo.VisitScheduleExcelVO;
import org.phoenixctms.ctsms.vo.VisitScheduleItemOutVO;

public final class ServiceUtil {


	private final static String INPUT_FIELD_VALIDATION_ERROR_MESSAGE = "{0}: {1}";
	public final static Comparator<String> MONEY_TRANSFER_COST_TYPE_COMPARATOR = new AlphanumStringComparator(true);
	public final static String ECRF_FIELD_VALUE_DAO_ECRF_FIELD_ALIAS = "ecrfField0";
	public final static String ECRF_FIELD_VALUE_DAO_ECRF_FIELD_VALUE_ALIAS = "ecrfFieldValue0";
	public final static String INQUIRY_VALUE_DAO_INQUIRY_ALIAS = "inquiry0";
	public final static String INQUIRY_VALUE_DAO_INQUIRY_VALUE_ALIAS = "inquiryValue0";
	public final static String PROBAND_LIST_ENTRY_TAG_VALUE_DAO_PROBAND_LIST_ENTRY_TAG_ALIAS = "probandListEntryTag0";
	public final static String PROBAND_LIST_ENTRY_TAG_VALUE_DAO_PROBAND_LIST_ENTRY_TAG_VALUE_ALIAS = "tagValue0";


	public final static boolean LOG_ADD_UPDATE_ECRF_NO_DIFF = false;
	public final static boolean LOG_ADD_UPDATE_INPUT_FIELD_NO_DIFF = false;
	public final static boolean LOG_ECRF_FIELD_VALUE_PROBAND = false;


	public final static boolean LOG_ECRF_FIELD_VALUE_TRIAL = false;

	public final static boolean LOG_INQUIRY_VALUE_PROBAND = false;

	public final static boolean LOG_INQUIRY_VALUE_TRIAL = false;

	public final static boolean LOG_PROBAND_LIST_ENTRY_TAG_VALUE_PROBAND = false;

	public final static boolean LOG_PROBAND_LIST_ENTRY_TAG_VALUE_TRIAL = false;

	public final static boolean LOG_ECRF_FIELD_STATUS_ENTRY_TRIAL = false;
	public final static boolean LOG_ECRF_FIELD_STATUS_ENTRY_PROBAND = false;

	public static InputFieldSelectionSetValueOutVO addAutocompleteSelectionSetValue(InputField inputField, String textValue, Timestamp now, User user,
			InputFieldSelectionSetValueDao selectionSetValueDao, JournalEntryDao journalEntryDao) throws Exception {
		if (textValue != null && textValue.length() > 0 && inputField != null &&
				InputFieldType.AUTOCOMPLETE.equals(inputField.getFieldType()) && inputField.getLearn() &&
				selectionSetValueDao.getCount(inputField.getId(), textValue) == 0) {
			InputFieldSelectionSetValue selectionSetValue = InputFieldSelectionSetValue.Factory.newInstance();
			selectionSetValue.setField(inputField);
			inputField.addSelectionSetValues(selectionSetValue);
			selectionSetValue.setLocalized(false);
			selectionSetValue.setNameL10nKey(textValue);
			selectionSetValue.setPreset(false);
			selectionSetValue.setValue(textValue);
			CoreUtil.modifyVersion(selectionSetValue, now, user);
			selectionSetValue = selectionSetValueDao.create(selectionSetValue);
			InputFieldSelectionSetValueOutVO result = selectionSetValueDao.toInputFieldSelectionSetValueOutVO(selectionSetValue);
			logSystemMessage(selectionSetValue.getField(), result.getField(), now, user, SystemMessageCodes.SELECTION_SET_VALUE_CREATED, result, null, journalEntryDao);
			return result;
		}
		return null;
	}

	private static void addCostTypeDetailComment(MoneyTransfer mt,MoneyTransferByCostTypeSummaryDetailVO byCostTypeDetail) {

		if (mt.isShowComment()) {
			String comment;
			try {
				if (!CoreUtil.isPassDecryption()) {
					throw new Exception();
				}
				comment = (String) CryptoUtil.decryptValue(mt.getCommentIv(), mt.getEncryptedComment());
			} catch (Exception e) {
				comment = null;
				byCostTypeDetail.setDecrypted(false);
			}
			if (!CommonUtil.isEmptyString(comment)) {
				byCostTypeDetail.getComments().add(comment);
			}
		}
		//		if () {
		//			if (!CommonUtil.isEmptyString(mt.getReference())) {
		//				mt.getReferences().add(mt.getReference());
		//			}
		//		}
		//		if () {
		//			if (!CommonUtil.isEmptyString(mt.getVoucherCode())) {
		//				mt.getVoucherCodes().add(mt.getVoucherCode());
		//			}
		//		}

	}

	public static ArrayList<ECRFFieldStatusQueueCountVO> addEcrfFieldStatusEntryCounts(Collection<ECRFFieldStatusQueueCountVO> a, Collection<ECRFFieldStatusQueueCountVO> b) {

		ECRFFieldStatusQueue[] queues = ECRFFieldStatusQueue.values();
		HashMap<ECRFFieldStatusQueue,Long[]> aMap = new HashMap<ECRFFieldStatusQueue, Long[]>(queues.length);
		HashMap<ECRFFieldStatusQueue,Long[]> bMap = new HashMap<ECRFFieldStatusQueue, Long[]>(queues.length);
		ArrayList<ECRFFieldStatusQueueCountVO> result = new ArrayList<ECRFFieldStatusQueueCountVO>(queues.length);
		if (a != null) {
			Iterator<ECRFFieldStatusQueueCountVO> it = a.iterator();
			while (it.hasNext()) {
				ECRFFieldStatusQueueCountVO count = it.next();
				aMap.put(count.getQueue(), new Long[] { count.getTotal(), count.getInitial(), count.getUpdated(), count.getProposed(), count.getResolved(), count.getUnresolved() });
			}
		}
		if (b != null) {
			Iterator<ECRFFieldStatusQueueCountVO> it = b.iterator();
			while (it.hasNext()) {
				ECRFFieldStatusQueueCountVO count = it.next();
				bMap.put(count.getQueue(), new Long[] { count.getTotal(), count.getInitial(), count.getUpdated(), count.getProposed(), count.getResolved(), count.getUnresolved() });
			}
		}
		for (int i = 0; i < queues.length; i++) {
			if (!aMap.containsKey(queues[i])) {
				aMap.put(queues[i], new Long[] { 0l, 0l, 0l, 0l, 0l, 0l });
			}
			if (!bMap.containsKey(queues[i])) {
				bMap.put(queues[i], new Long[] { 0l, 0l, 0l, 0l, 0l, 0l });
			}
			ECRFFieldStatusQueueCountVO count = new ECRFFieldStatusQueueCountVO();
			count.setQueue(queues[i]);
			count.setTotal(aMap.get(queues[i])[0] + bMap.get(queues[i])[0]);
			count.setInitial(aMap.get(queues[i])[1] + bMap.get(queues[i])[1]);
			count.setUpdated(aMap.get(queues[i])[2] + bMap.get(queues[i])[2]);
			count.setProposed(aMap.get(queues[i])[3] + bMap.get(queues[i])[3]);
			count.setResolved(aMap.get(queues[i])[4] + bMap.get(queues[i])[4]);
			count.setUnresolved(aMap.get(queues[i])[5] + bMap.get(queues[i])[5]);
			result.add(count);
		}
		return result;

	}

	public static ProbandListStatusEntryOutVO addProbandListStatusEntry(ProbandListEntry listEntry, Boolean signup, String reasonL10nKey, Object[] args, Timestamp realTimestamp,
			Long probandListStatusTypeId, ECRF ecrf, ECRFStatusType newState,
			Timestamp now, User user,
			ProbandDao probandDao, ProbandListEntryDao probandListEntryDao, ProbandListStatusEntryDao probandListStatusEntryDao, ProbandListStatusTypeDao probandListStatusTypeDao,
			JournalEntryDao journalEntryDao) throws Exception {
		if (L10nUtil.containsProbandListStatusReason(Locales.PROBAND_LIST_STATUS_ENTRY_REASON, reasonL10nKey)) {
			ProbandListStatusType statusType = null;
			if (probandListStatusTypeId != null) {
				statusType = CheckIDUtil.checkProbandListStatusTypeId(probandListStatusTypeId, probandListStatusTypeDao);
			} else {
				if (ecrf != null && newState != null && newState.isApplyEcrfProbandListStatus() && ecrf.getProbandListStatus() != null) {
					statusType = ecrf.getProbandListStatus();
				} else if (listEntry.getLastStatus() != null) {
					statusType = listEntry.getLastStatus().getStatus();
				}
			}
			if (statusType != null) {
				String reason = L10nUtil.getProbandListStatusReason(Locales.PROBAND_LIST_STATUS_ENTRY_REASON,
						reasonL10nKey, DefaultProbandListStatusReasons.DEFAULT_REASON, args);
				if (!statusType.isReasonRequired() || !CommonUtil.isEmptyString(reason)) {
					ProbandListStatusEntryInVO newProbandListStatusEntry = new ProbandListStatusEntryInVO();
					newProbandListStatusEntry.setListEntryId(listEntry.getId());
					newProbandListStatusEntry.setRealTimestamp(realTimestamp);
					newProbandListStatusEntry.setReason(reason);
					newProbandListStatusEntry.setStatusId(statusType.getId());
					return addProbandListStatusEntry(newProbandListStatusEntry, signup, now, user, false, false, probandDao, probandListEntryDao, probandListStatusEntryDao,
							probandListStatusTypeDao,
							journalEntryDao);
				}
			} else {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.PROBAND_LIST_STATUS_TYPE_REQUIRED);
			}
		}
		return null;
	}

	public static ProbandListStatusEntryOutVO addProbandListStatusEntry(ProbandListEntry listEntry, Boolean signup, String reasonL10nKey, Object[] args, Timestamp realTimestamp,
			Long probandListStatusTypeId,
			Timestamp now, User user,
			ProbandDao probandDao, ProbandListEntryDao probandListEntryDao, ProbandListStatusEntryDao probandListStatusEntryDao, ProbandListStatusTypeDao probandListStatusTypeDao,
			JournalEntryDao journalEntryDao) throws Exception {
		return addProbandListStatusEntry(listEntry, signup, reasonL10nKey, args, now, probandListStatusTypeId, null, null, now, user, probandDao, probandListEntryDao,
				probandListStatusEntryDao, probandListStatusTypeDao, journalEntryDao);
	}





	public static ProbandListStatusEntryOutVO addProbandListStatusEntry(ProbandListStatusEntryInVO newProbandListStatusEntry, Boolean signup, Timestamp now, User user,
			boolean logTrial,
			boolean logProband,
			ProbandDao probandDao, ProbandListEntryDao probandListEntryDao, ProbandListStatusEntryDao probandListStatusEntryDao, ProbandListStatusTypeDao probandListStatusTypeDao,
			JournalEntryDao journalEntryDao) throws Exception {
		checkAddProbandListStatusEntryInput(newProbandListStatusEntry, signup, probandDao, probandListEntryDao, probandListStatusEntryDao, probandListStatusTypeDao);
		// ProbandListStatusEntryDao probandListStatusEntryDao = this.getProbandListStatusEntryDao();
		ProbandListStatusEntry probandListStatusEntry = probandListStatusEntryDao.probandListStatusEntryInVOToEntity(newProbandListStatusEntry);
		CoreUtil.modifyVersion(probandListStatusEntry, now, user);
		probandListStatusEntry = probandListStatusEntryDao.create(probandListStatusEntry);
		ProbandListEntry listEntry = probandListStatusEntry.getListEntry();
		listEntry.setLastStatus(probandListStatusEntry);
		probandListEntryDao.update(listEntry);
		// /JournalEntryDao journalEntryDao = this.getJournalEntryDao();
		ProbandListStatusEntryOutVO result = probandListStatusEntryDao.toProbandListStatusEntryOutVO(probandListStatusEntry);
		if (logProband) {
			logSystemMessage(probandListStatusEntry.getListEntry().getProband(), result.getListEntry().getTrial(), now, user, SystemMessageCodes.PROBAND_LIST_STATUS_ENTRY_CREATED,
					result, null, journalEntryDao);
		}
		if (logTrial) {
			// logSystemMessage(probandListStatusEntry.getListEntry().getTrial(), result.getListEntry().getTrial(), now, user,
			// SystemMessageCodes.PROBAND_LIST_STATUS_ENTRY_CREATED, result, null, journalEntryDao);
			logSystemMessage(probandListStatusEntry.getListEntry().getTrial(), result.getListEntry().getProband(), now, user,
					SystemMessageCodes.PROBAND_LIST_STATUS_ENTRY_CREATED, result, null, journalEntryDao);
		}
		return result;
	}

	private static void appendCvStaffPath(StringBuilder staffPath, StaffOutVO staff, boolean first) {
		if (staff != null) {
			if (first || !staff.isPerson()) {
				if (staffPath.length() > 0) {
					staffPath.append(", ");
				}
				staffPath.append(CommonUtil.getCvStaffName(staff));
			}
			appendCvStaffPath(staffPath, staff.getParent(), false);
		}
	}

	public static void appendDistinctProbandAddressColumnValues(Collection addresses,
			HashMap<String, Object> fieldRow,
			boolean aggregateAddresses,
			String streetsColumnName,
			String zipCodesColumnName,
			String cityNamesColumnName) {
		Iterator<ProbandAddressOutVO> addressesIt = addresses.iterator();
		String fieldKey;
		while (addressesIt.hasNext()) {
			ProbandAddressOutVO addressOutVO = addressesIt.next();
			StringBuilder fieldValue;
			if (aggregateAddresses) {
				fieldKey = streetsColumnName;
				if (fieldRow.containsKey(fieldKey)) {
					fieldValue = new StringBuilder((String) fieldRow.get(fieldKey));
				} else {
					fieldValue = new StringBuilder();
				}
				if (fieldValue.length() > 0) {
					fieldValue.append(ExcelUtil.EXCEL_LINE_BREAK);
				}
				fieldValue
				.append(CommonUtil.getStreetString(addressOutVO.getStreetName(), addressOutVO.getHouseNumber(), addressOutVO.getEntrance(), addressOutVO.getDoorNumber()));
				fieldRow.put(fieldKey, fieldValue.toString());
				fieldKey = zipCodesColumnName;
				if (fieldRow.containsKey(fieldKey)) {
					fieldValue = new StringBuilder((String) fieldRow.get(fieldKey));
				} else {
					fieldValue = new StringBuilder();
				}
				if (fieldValue.length() > 0) {
					fieldValue.append(ExcelUtil.EXCEL_LINE_BREAK);
				}
				fieldValue.append(addressOutVO.getZipCode());
				fieldRow.put(fieldKey, fieldValue.toString());
				fieldKey = cityNamesColumnName;
				if (fieldRow.containsKey(fieldKey)) {
					fieldValue = new StringBuilder((String) fieldRow.get(fieldKey));
				} else {
					fieldValue = new StringBuilder();
				}
				if (fieldValue.length() > 0) {
					fieldValue.append(ExcelUtil.EXCEL_LINE_BREAK);
				}
				fieldValue.append(addressOutVO.getCityName());
				fieldRow.put(fieldKey, fieldValue.toString());
			} else {
				fieldKey = addressOutVO.getType().getName(); // aggregateAddresses ? ProbandListExcelWriter.getAddressesColumnName() : addressOutVO.getType().getName();
				if (fieldRow.containsKey(fieldKey)) {
					fieldValue = new StringBuilder((String) fieldRow.get(fieldKey));
				} else {
					fieldValue = new StringBuilder();
				}
				if (fieldValue.length() > 0) {
					fieldValue.append(ExcelUtil.EXCEL_LINE_BREAK);
				}
				fieldValue.append(addressOutVO.getName());
				fieldRow.put(fieldKey, fieldValue.toString());
			}
		}
	}

	public static void applyLogonLimitations(PasswordInVO password) {
		password.setExpires(Settings.getBoolean(SettingCodes.LOGON_EXPIRES, Settings.Bundle.SETTINGS, false));
		password.setValidityPeriod(password.isExpires() ? Settings.getVariablePeriod(SettingCodes.LOGON_VALIDITY_PERIOD, Settings.Bundle.SETTINGS, VariablePeriod.EXPLICIT) : null);
		password.setValidityPeriodDays(VariablePeriod.EXPLICIT.equals(password.getValidityPeriod()) ? Settings.getLongNullable(SettingCodes.LOGON_VALIDITY_PERIOD_DAYS,
				Settings.Bundle.SETTINGS, null) : null);
		password.setLimitLogons(Settings.getBoolean(SettingCodes.LOGON_LIMIT_LOGONS, Settings.Bundle.SETTINGS, false));
		password.setMaxSuccessfulLogons(password.isLimitLogons() ? Settings.getLongNullable(SettingCodes.LOGON_MAX_SUCCESSFUL_LOGONS, Settings.Bundle.SETTINGS, null) : null);
		password.setLimitWrongPasswordAttempts(Settings.getBoolean(SettingCodes.LOGON_LIMIT_WRONG_PASSWORD_ATTEMPTS, Settings.Bundle.SETTINGS, false));
		password.setMaxWrongPasswordAttemptsSinceLastSuccessfulLogon(password.isLimitWrongPasswordAttempts() ? Settings.getLongNullable(
				SettingCodes.LOGON_MAX_WRONG_PASSWORD_ATTEMPTS_SINCE_LAST_SUCCESSFUL_LOGON, Settings.Bundle.SETTINGS, null) : null);
	}

	public static void applyOneTimeLogonLimitation(PasswordInVO password) {
		password.setLimitLogons(true);
		password.setMaxSuccessfulLogons(1L);
	}

	public static String aspSubstanceIDsToString(Collection<Long> aspSubstanceIds, AspSubstanceDao aspSubstanceDao) {
		Collection<AspSubstanceVO> result = new ArrayList<AspSubstanceVO>(aspSubstanceIds.size());
		Iterator<Long> it = aspSubstanceIds.iterator();
		while (it.hasNext()) {
			result.add(aspSubstanceDao.toAspSubstanceVO(aspSubstanceDao.load(it.next())));
		}
		return CommonUtil.aspSubstanceVOCollectionToString(result);
	}

	public static void cancelNotifications(Collection<Notification> notifications, NotificationDao notificationDao,
			org.phoenixctms.ctsms.enumeration.NotificationType notificationType)
					throws Exception {
		Iterator<Notification> notificationsIt = notifications.iterator();
		while (notificationsIt.hasNext()) {
			Notification notification = notificationsIt.next();
			if (!notification.isObsolete() && (notificationType == null || notificationType.equals(notification.getType().getType()))) {
				notification.setObsolete(true);
				notificationDao.update(notification);
			}
		}
	}

	public static void checkAddCourseParticipationStatusEntryInput(CourseParticipationStatusEntryInVO courseParticipationIn, boolean admin, Boolean selfRegistration,
			StaffDao staffDao, CourseDao courseDao, CvSectionDao cvSectionDao, CourseParticipationStatusTypeDao courseParticipationStatusTypeDao,
			CourseParticipationStatusEntryDao courseParticipationStatusEntryDao) throws ServiceException
			{
		// referential checks
		Staff staff = CheckIDUtil.checkStaffId(courseParticipationIn.getStaffId(), staffDao);
		Course course = CheckIDUtil.checkCourseId(courseParticipationIn.getCourseId(), courseDao, LockMode.PESSIMISTIC_WRITE);
		if (courseParticipationIn.getSectionId() != null) {
			CheckIDUtil.checkCvSectionId(courseParticipationIn.getSectionId(), cvSectionDao);
		}
		CourseParticipationStatusType state = CheckIDUtil.checkCourseParticipationStatusTypeId(courseParticipationIn.getStatusId(), courseParticipationStatusTypeDao);
		// other input checks
		if (!staff.isPerson()) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.COURSE_PARTICIPATION_STAFF_NOT_PERSON);
		}
		if (selfRegistration != null && selfRegistration.booleanValue() != course.isSelfRegistration()) {
			throw L10nUtil.initServiceException(selfRegistration ? ServiceExceptionCodes.COURSE_PARTICIPATION_COURSE_SELF_REGISTRATION
					: ServiceExceptionCodes.COURSE_PARTICIPATION_COURSE_ADMIN_REGISTRATION);
		}
		if (courseParticipationIn.getShowCv() && !course.isShowCvPreset()) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.COURSE_PARTICIPATION_SHOW_CV_PRESET_DISABLED);
		}
		if (courseParticipationIn.getShowCv() && courseParticipationIn.getSectionId() == null) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.COURSE_PARTICIPATION_CV_SECTION_REQUIRED);
		}
		if (!courseParticipationIn.getShowCv() && courseParticipationIn.getShowCommentCv()) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.COURSE_PARTICIPATION_SHOW_CV_DISABLED);
		}
		if (courseParticipationStatusEntryDao.getStaffCourseStatusCount(courseParticipationIn.getStaffId(), courseParticipationIn.getCourseId(), null) > 0) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.COURSE_PARTICIPATION_ALREADY_PARTICIPATING, CommonUtil.staffOutVOToString(staffDao.toStaffOutVO(staff)));
		}
		if (course.isSelfRegistration() && !admin) {
			if (course.getMaxNumberOfParticipants() != null
					&& courseParticipationStatusEntryDao.getStaffCourseStatusCount(null, courseParticipationIn.getCourseId(), null) >= course.getMaxNumberOfParticipants()) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.COURSE_PARTICIPATION_MAX_NUMBER_OF_PARTICIPANTS_EXCEEDED, course.getMaxNumberOfParticipants());
			}
			if (course.getParticipationDeadline() != null && course.getParticipationDeadline().compareTo(CommonUtil.dateToTimestamp(new Date())) < 0) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.COURSE_PARTICIPATION_DEADLINE_EXCEEDED); // ,(new
				// SimpleDateFormat()).format(course.getParticipationDeadline()));
			}
		}
		boolean validState = false;
		Iterator<CourseParticipationStatusType> statesIt = courseParticipationStatusTypeDao.findInitialStates(admin, course.isSelfRegistration()).iterator();
		while (statesIt.hasNext()) {
			if (state.equals(statesIt.next())) {
				validState = true;
				break;
			}
		}
		if (!validState) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.COURSE_PARTICIPATION_INVALID_INITIAL_PARTICIPATION_STATUS_TYPE,
					L10nUtil.getCourseParticipationStatusTypeName(Locales.USER, state.getNameL10nKey()));
		}
			}

	private static void checkAddProbandListStatusEntryInput(ProbandListStatusEntryInVO probandListStatusEntryIn, Boolean signup,
			ProbandDao probandDao, ProbandListEntryDao probandListEntryDao, ProbandListStatusEntryDao probandListStatusEntryDao, ProbandListStatusTypeDao probandListStatusTypeDao
			) throws ServiceException
			{
		// referential checks
		// ProbandListEntryDao probandListEntryDao = this.getProbandListEntryDao();
		ProbandListEntry probandListEntry = CheckIDUtil.checkProbandListEntryId(probandListStatusEntryIn.getListEntryId(), probandListEntryDao);
		ProbandListStatusEntry lastStatus = probandListEntry.getLastStatus();
		// ProbandListStatusTypeDao probandListStatusTypeDao = this.getProbandListStatusTypeDao();
		ProbandListStatusType state = CheckIDUtil.checkProbandListStatusTypeId(probandListStatusEntryIn.getStatusId(), probandListStatusTypeDao);
		checkTrialLocked(probandListEntry.getTrial());
		checkProbandLocked(probandListEntry.getProband());
		boolean validState = false;
		if (lastStatus == null) {
			Iterator<ProbandListStatusType> statesIt = probandListStatusTypeDao.findInitialStates(signup, probandListEntry.getProband().isPerson()).iterator();
			while (statesIt.hasNext()) {
				if (state.equals(statesIt.next())) {
					validState = true;
					break;
				}
			}
			if (!validState) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.INVALID_INITIAL_PROBAND_LIST_STATUS_TYPE,
						L10nUtil.getProbandListStatusTypeName(Locales.USER, state.getNameL10nKey()));
			}
		} else {
			ProbandListStatusType lastState = lastStatus.getStatus();
			Iterator<ProbandListStatusType> statesIt = probandListStatusTypeDao.findTransitions(lastState.getId()).iterator();
			while (statesIt.hasNext()) {
				if (state.equals(statesIt.next())) {
					validState = true;
					break;
				}
			}
			if (!validState) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.INVALID_NEW_PROBAND_LIST_STATUS_TYPE,
						L10nUtil.getProbandListStatusTypeName(Locales.USER, state.getNameL10nKey()));
			}
			if (probandListStatusEntryIn.getRealTimestamp().compareTo(lastStatus.getRealTimestamp()) < 0) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.PROBAND_LIST_STATUS_REAL_DATE_LESS_THAN_LAST_DATE);
			}
		}
		if (signup != null && state.isSignup() && !probandListEntry.getTrial().isSignupProbandList()) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.TRIAL_SIGNUP_DISABLED);
		}
		String reason = probandListStatusEntryIn.getReason();
		if (CommonUtil.isEmptyString(reason) && state.isReasonRequired()) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.PROBAND_LIST_STATUS_ENTRY_REASON_REQUIRED);
		}
		if ((new ProbandListStatusEntryCollisionFinder(probandDao, probandListEntryDao, probandListStatusEntryDao)).collides(probandListStatusEntryIn)) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.PROBAND_LIST_ENTRY_PROBAND_BLOCKED, probandListEntry.getProband().getId().toString());
			// xCommonUtil.probandOutVOToString(probandDao.toProbandOutVO(probandListEntry.getProband())));
		}
			}

	public static void checkInputFieldBooleanValue(InputField inputField, boolean optional, boolean booleanValue, InputFieldDao inputFieldDao) throws ServiceException {
		if (inputField != null) {
			InputFieldType fieldType = inputField.getFieldType();
			if (fieldType != null) {
				if (InputFieldType.CHECKBOX.equals(fieldType)) {
				} else if (booleanValue == true) {
					throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_BOOLEAN_VALUE_NOT_FALSE,
							CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
				}
			}
		}
	}

	public static void checkInputFieldDateValue(InputField inputField, boolean optional, Date dateValue, InputFieldDao inputFieldDao) throws ServiceException {
		if (inputField != null) {
			InputFieldType fieldType = inputField.getFieldType();
			if (fieldType != null) {
				if (InputFieldType.DATE.equals(fieldType)) {
					if (!optional && dateValue == null) {
						throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_VALUE_REQUIRED,
								CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
					}
					if (dateValue != null) {
						Date minDate = inputField.getMinDate();
						if (minDate != null && DateCalc.getStartOfDay(minDate).compareTo(DateCalc.getStartOfDay(dateValue)) > 0) {
							throw L10nUtil.initServiceException(getValidationErrorMsg(inputFieldDao.toInputFieldOutVO(inputField)));
						}
						Date maxDate = inputField.getMaxDate();
						if (maxDate != null && DateCalc.getStartOfDay(maxDate).compareTo(DateCalc.getStartOfDay(dateValue)) < 0) {
							throw L10nUtil.initServiceException(getValidationErrorMsg(inputFieldDao.toInputFieldOutVO(inputField)));
						}
					}
				} else if (dateValue != null) {
					throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_DATE_VALUE_NOT_NULL,
							CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
				}
			}
		}
	}

	public static void checkInputFieldFloatValue(InputField inputField, boolean optional, Float floatValue, InputFieldDao inputFieldDao) throws ServiceException {
		if (inputField != null) {
			InputFieldType fieldType = inputField.getFieldType();
			if (fieldType != null) {
				if (InputFieldType.FLOAT.equals(fieldType)) {
					if (!optional && floatValue == null) {
						throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_VALUE_REQUIRED,
								CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
					}
					if (floatValue != null) {
						Float lowerLimit = inputField.getFloatLowerLimit();
						if (lowerLimit != null && lowerLimit.compareTo(floatValue) > 0) {
							throw L10nUtil.initServiceException(getValidationErrorMsg(inputFieldDao.toInputFieldOutVO(inputField)));
						}
						Float upperLimit = inputField.getFloatUpperLimit();
						if (upperLimit != null && upperLimit.compareTo(floatValue) < 0) {
							throw L10nUtil.initServiceException(getValidationErrorMsg(inputFieldDao.toInputFieldOutVO(inputField)));
						}
					}
				} else if (floatValue != null) {
					throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_FLOAT_VALUE_NOT_NULL,
							CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
				}
			}
		}
	}

	public static void checkInputFieldInkValue(InputField inputField, boolean optional, byte[] inkValue, InputFieldDao inputFieldDao) throws ServiceException {
		if (inputField != null) {
			InputFieldType fieldType = inputField.getFieldType();
			if (fieldType != null) {
				if (InputFieldType.SKETCH.equals(fieldType)) {
					if (!optional && (inkValue == null || inkValue.length == 0)) {
						throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_VALUE_REQUIRED,
								CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
					}
				} else if (inkValue != null) {
					throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_INK_VALUE_NOT_NULL,
							CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
				}
			}
		}
	}

	public static void checkInputFieldLongValue(InputField inputField, boolean optional, Long longValue, InputFieldDao inputFieldDao) throws ServiceException {
		if (inputField != null) {
			InputFieldType fieldType = inputField.getFieldType();
			if (fieldType != null) {
				if (InputFieldType.INTEGER.equals(fieldType)) {
					if (!optional && longValue == null) {
						throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_VALUE_REQUIRED,
								CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
					}
					if (longValue != null) {
						Long lowerLimit = inputField.getLongLowerLimit();
						if (lowerLimit != null && lowerLimit.compareTo(longValue) > 0) {
							throw L10nUtil.initServiceException(getValidationErrorMsg(inputFieldDao.toInputFieldOutVO(inputField)));
						}
						Long upperLimit = inputField.getLongUpperLimit();
						if (upperLimit != null && upperLimit.compareTo(longValue) < 0) {
							throw L10nUtil.initServiceException(getValidationErrorMsg(inputFieldDao.toInputFieldOutVO(inputField)));
						}
					}
				} else if (longValue != null) {
					throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_LONG_VALUE_NOT_NULL,
							CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
				}
			}
		}
	}


	public static void checkInputFieldSelectionSetValues(InputField inputField, boolean optional, Collection<Long> selectionSetValueIds,
			InputFieldDao inputFieldDao, InputFieldSelectionSetValueDao selectionSetValueDao) throws ServiceException {
		if (inputField != null) {
			InputFieldType fieldType = inputField.getFieldType();
			if (fieldType != null) {
				if (isLoadSelectionSet(fieldType)) {
					// if (InputFieldType.SELECT_ONE.equals(fieldType) || InputFieldType.SELECT_MANY.equals(fieldType) || InputFieldType.SKETCH.equals(fieldType)) {
					if (!optional && (selectionSetValueIds == null || selectionSetValueIds.size() == 0)) {
						// if (!InputFieldType.SKETCH.equals(fieldType) || inputField.getSelectionSetValues().size() > 0) {
						if (!InputFieldType.SKETCH.equals(fieldType) || selectionSetValueDao.getCount(inputField.getId()) > 0) {
							throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_SELECTION_REQUIRED,
									CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
						}
					}
					if (selectionSetValueIds != null && selectionSetValueIds.size() > 0) {
						if (InputFieldType.SELECT_ONE_DROPDOWN.equals(fieldType) || InputFieldType.SELECT_ONE_RADIO_H.equals(fieldType)
								|| InputFieldType.SELECT_ONE_RADIO_V.equals(fieldType)) {
							if (selectionSetValueIds.size() != 1) {
								throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_SINGLE_SELECTION_REQUIRED,
										CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
							}
						} else {
							Integer minSelections = inputField.getMinSelections();
							if (minSelections != null && minSelections.compareTo(selectionSetValueIds.size()) > 0) {
								throw L10nUtil.initServiceException(getValidationErrorMsg(inputFieldDao.toInputFieldOutVO(inputField)));
							}
							Integer maxSelections = inputField.getMaxSelections();
							if (maxSelections != null && maxSelections.compareTo(selectionSetValueIds.size()) < 0) {
								throw L10nUtil.initServiceException(getValidationErrorMsg(inputFieldDao.toInputFieldOutVO(inputField)));
							}
						}
						Iterator<Long> it = selectionSetValueIds.iterator();
						HashSet<Long> dupeCheck = new HashSet<Long>(selectionSetValueIds.size());
						while (it.hasNext()) {
							Long id = it.next();
							if (id == null) {
								throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_SELECTION_SET_VALUE_ID_IS_NULL,
										CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
							}
							InputFieldSelectionSetValue selectionSetValue = CheckIDUtil.checkInputFieldSelectionSetValueId(id, selectionSetValueDao);
							if (!dupeCheck.add(selectionSetValue.getId())) {
								InputFieldSelectionSetValueOutVO selectionSetValueVO = selectionSetValueDao.toInputFieldSelectionSetValueOutVO(selectionSetValue);
								throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_DUPLICATE_SELECTION,
										CommonUtil.inputFieldOutVOToString(selectionSetValueVO.getField()), selectionSetValueVO.getName());
							}
						}
					}
				} else if (selectionSetValueIds != null && selectionSetValueIds.size() > 0) {
					throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_SELECTION_NOT_EMTPY,
							CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
				}
			}
		}
	}

	public static void checkInputFieldTextValue(InputField inputField, boolean optional, String textValue, InputFieldDao inputFieldDao,
			InputFieldSelectionSetValueDao selectionSetValueDao)
					throws ServiceException {
		if (inputField != null) {
			InputFieldType fieldType = inputField.getFieldType();
			if (fieldType != null) {
				if (InputFieldType.SINGLE_LINE_TEXT.equals(fieldType) || InputFieldType.MULTI_LINE_TEXT.equals(fieldType)) {
					if (!optional && CommonUtil.isEmptyString(textValue)) {
						throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_VALUE_REQUIRED,
								CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
					}
					if (textValue != null && textValue.length() > 0) {
						String regExp = inputField.getRegExp();
						if (regExp != null && regExp.length() > 0) {
							java.util.regex.Pattern valuePattern = null;
							try {
								valuePattern = Pattern.compile(regExp);
							} catch (PatternSyntaxException e) {
								throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_INVALID_REGEXP_PATTERN,
										CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)), e.getMessage());
							}
							if (valuePattern != null && !valuePattern.matcher(textValue).find()) {
								throw L10nUtil.initServiceException(getValidationErrorMsg(inputFieldDao.toInputFieldOutVO(inputField)));
							}
						}
					}
				} else if (InputFieldType.AUTOCOMPLETE.equals(fieldType)) {
					if (!optional && CommonUtil.isEmptyString(textValue)) {
						throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_VALUE_REQUIRED,
								CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
					}
					if (textValue != null && textValue.length() > 0) {
						if (inputField.getStrict() && !inputField.getLearn() && selectionSetValueDao.getCount(inputField.getId(), textValue) == 0) {
							throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_TEXT_VALUE_NOT_FOUND,
									CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
						}
					}
				} else if (textValue != null) {
					throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_TEXT_VALUE_NOT_NULL,
							CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
				}
			}
		}
	}

	public static void checkInputFieldTimestampValue(InputField inputField, boolean optional, Date timestampValue, InputFieldDao inputFieldDao) throws ServiceException {
		if (inputField != null) {
			InputFieldType fieldType = inputField.getFieldType();
			if (fieldType != null) {
				if (InputFieldType.TIMESTAMP.equals(fieldType)) {
					if (!optional && timestampValue == null) {
						throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_VALUE_REQUIRED,
								CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
					}
					if (timestampValue != null) {
						Date minTimestamp = inputField.getMinTimestamp();
						if (minTimestamp != null && minTimestamp.compareTo(timestampValue) > 0) {
							throw L10nUtil.initServiceException(getValidationErrorMsg(inputFieldDao.toInputFieldOutVO(inputField)));
						}
						Date maxTimestamp = inputField.getMaxTimestamp();
						if (maxTimestamp != null && maxTimestamp.compareTo(timestampValue) < 0) {
							throw L10nUtil.initServiceException(getValidationErrorMsg(inputFieldDao.toInputFieldOutVO(inputField)));
						}
					}
				} else if (timestampValue != null) {
					throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_TIMESTAMP_VALUE_NOT_NULL,
							CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
				}
			}
		}
	}

	public static void checkInputFieldTimeValue(InputField inputField, boolean optional, Date timeValue, InputFieldDao inputFieldDao) throws ServiceException {
		if (inputField != null) {
			InputFieldType fieldType = inputField.getFieldType();
			if (fieldType != null) {
				if (InputFieldType.TIME.equals(fieldType)) {
					if (!optional && timeValue == null) {
						throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_VALUE_REQUIRED,
								CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
					}
					if (timeValue != null) {
						Date minTime = inputField.getMinTime();
						if (minTime != null && minTime.compareTo(timeValue) > 0) {
							throw L10nUtil.initServiceException(getValidationErrorMsg(inputFieldDao.toInputFieldOutVO(inputField)));
						}
						Date maxTime = inputField.getMaxTime();
						if (maxTime != null && maxTime.compareTo(timeValue) < 0) {
							throw L10nUtil.initServiceException(getValidationErrorMsg(inputFieldDao.toInputFieldOutVO(inputField)));
						}
					}
				} else if (timeValue != null) {
					throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_TIME_VALUE_NOT_NULL,
							CommonUtil.inputFieldOutVOToString(inputFieldDao.toInputFieldOutVO(inputField)));
				}
			}
		}
	}

	public static void checkLocale(String locale) throws ServiceException {
		if (!CoreUtil.checkSupportedLocale(locale)) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.INVALID_LOCALE);
		}
	}

	public static void checkLockedEcrfs(ECRF ecrf, ECRFStatusEntryDao ecrfStatusEntryDao, ECRFDao ecrfDao) throws ServiceException {
		long valuesLockedEcrfCount = ecrfStatusEntryDao.getCount(null, ecrf.getId(), null, true, null, null, null, null); // row lock order
		if (valuesLockedEcrfCount > 0) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.LOCKED_ECRFS, ecrfDao.toECRFOutVO(ecrf).getUniqueName(), valuesLockedEcrfCount);
		}
	}

	public static void checkLogonLimitations(PasswordInVO password) throws ServiceException {
		if (password.isExpires()) {
			if (password.getValidityPeriod() == null) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.PASSWORD_VALIDITY_PERIOD_REQUIRED);
			} else if (VariablePeriod.EXPLICIT.equals(password.getValidityPeriod())) {
				if (password.getValidityPeriodDays() == null) {
					throw L10nUtil.initServiceException(ServiceExceptionCodes.PASSWORD_VALIDITY_PERIOD_EXPLICIT_DAYS_REQUIRED);
				} else if (password.getValidityPeriodDays() < 1) {
					throw L10nUtil.initServiceException(ServiceExceptionCodes.PASSWORD_VALIDITY_PERIOD_EXPLICIT_DAYS_LESS_THAN_ONE);
				}
			}
		}
		if (password.isLimitLogons()) {
			if (password.getMaxSuccessfulLogons() == null) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.PASSWORD_NUMBER_OF_MAX_SUCCESSFUL_LOGONS_REQUIRED);
			} else if (password.getMaxSuccessfulLogons() < 1) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.PASSWORD_NUMBER_OF_MAX_SUCCESSFUL_LOGONS_LESS_THAN_ONE);
			}
		}
		if (password.isLimitWrongPasswordAttempts()) {
			if (password.getMaxWrongPasswordAttemptsSinceLastSuccessfulLogon() == null) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.PASSWORD_NUMBER_OF_MAX_WRONG_PASSWORD_ATTEMPTS_REQUIRED);
			} else if (password.getMaxWrongPasswordAttemptsSinceLastSuccessfulLogon() < 1) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.PASSWORD_NUMBER_OF_MAX_WRONG_PASSWORD_ATTEMPTS_LESS_THAN_ONE);
			}
		}
	}

	public static void checkProbandLocked(Proband proband) throws ServiceException { // , ProbandDao probandDao
		if (proband != null && proband.getCategory().isLocked()) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.PROBAND_LOCKED, Long.toString(proband.getId()),
					L10nUtil.getProbandCategoryName(Locales.USER, proband.getCategory().getNameL10nKey()));
		}
	}

	public static void checkReminderPeriod(VariablePeriod reminderPeriod, Long reminderPeriodDays) throws ServiceException {
		if (VariablePeriod.EXPLICIT.equals(reminderPeriod)) {
			if (reminderPeriodDays == null) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.REMINDER_PERIOD_EXPLICIT_DAYS_REQUIRED);
			} else if (reminderPeriodDays < 1) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.REMINDER_PERIOD_EXPLICIT_DAYS_LESS_THAN_ONE);
			}
		}
	}

	public static void checkTimeZone(String timeZone) throws ServiceException {
		if (!CoreUtil.checkTimeZone(timeZone)) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.INVALID_TIME_ZONE);
		}
	}

	public static void checkTrialLocked(Trial trial) throws ServiceException { // , TrialDao trialDao)
		if (trial != null && trial.getStatus().isLockdown()) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.TRIAL_LOCKED, Long.toString(trial.getId()),
					L10nUtil.getTrialStatusTypeName(Locales.USER, trial.getStatus().getNameL10nKey()));
		}
	}

	public static void checkUpdateCourseParticipationStatusEntryInput(CourseParticipationStatusEntry originalCourseParticipation,
			CourseParticipationStatusEntryInVO courseParticipationIn, boolean admin,
			CvSectionDao cvSectionDao, CourseParticipationStatusTypeDao courseParticipationStatusTypeDao,
			CourseParticipationStatusEntryDao courseParticipationStatusEntryDao) throws ServiceException
			{
		// referential checks
		if (courseParticipationIn.getSectionId() != null) {
			CheckIDUtil.checkCvSectionId(courseParticipationIn.getSectionId(), cvSectionDao);
		}
		CourseParticipationStatusType state = CheckIDUtil.checkCourseParticipationStatusTypeId(courseParticipationIn.getStatusId(), courseParticipationStatusTypeDao);
		// other input checks
		Staff staff = originalCourseParticipation.getStaff();
		if (!staff.getId().equals(courseParticipationIn.getStaffId())) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.COURSE_PARTICIPATION_STAFF_CHANGED);
		}
		Course course = originalCourseParticipation.getCourse();
		if (!course.getId().equals(courseParticipationIn.getCourseId())) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.COURSE_PARTICIPATION_COURSE_CHANGED);
		}
		if (courseParticipationIn.getShowCv() && !course.isShowCvPreset()) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.COURSE_PARTICIPATION_SHOW_CV_PRESET_DISABLED);
		}
		if (courseParticipationIn.getShowCv() && courseParticipationIn.getSectionId() == null) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.COURSE_PARTICIPATION_CV_SECTION_REQUIRED);
		}
		if (!courseParticipationIn.getShowCv() && courseParticipationIn.getShowCommentCv()) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.COURSE_PARTICIPATION_SHOW_CV_DISABLED);
		}
		boolean validState = false;
		Iterator<CourseParticipationStatusType> statesIt = courseParticipationStatusTypeDao.findTransitions(originalCourseParticipation.getStatus().getId(), admin,
				course.isSelfRegistration()).iterator();
		while (statesIt.hasNext()) {
			if (state.equals(statesIt.next())) {
				validState = true;
				break;
			}
		}
		if (!validState) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.COURSE_PARTICIPATION_INVALID_NEW_PARTICIPATION_STATUS_TYPE,
					L10nUtil.getCourseParticipationStatusTypeName(Locales.USER, state.getNameL10nKey()));
		}
			}

	public static void checkUserInput(UserInVO userIn, String plainDepartmentPassword, DepartmentDao departmentDao, StaffDao staffDao) throws Exception {
		Department department = CheckIDUtil.checkDepartmentId(userIn.getDepartmentId(), departmentDao);
		if (!CryptoUtil.checkDepartmentPassword(department, plainDepartmentPassword)) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.DEPARTMENT_PASSWORT_WRONG);
		}
		if (userIn.getIdentityId() != null) {
			CheckIDUtil.checkStaffId(userIn.getIdentityId(), staffDao);
		}
		checkLocale(userIn.getLocale());
		checkTimeZone(userIn.getTimeZone());
	}

	public static void checkUsernameExists(String username, UserDao userDao) throws ServiceException {
		if (userDao.searchUniqueName(username) != null) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.USERNAME_ALREADY_EXISTS, username);
		}
	}

	public static CourseCertificatePDFPainter createCourseCertificatePDFPainter(Collection<CourseParticipationStatusEntryOutVO> participantVOs, StaffDao staffDao,
			StaffAddressDao staffAddressDao, LecturerDao lecturerDao, LecturerCompetenceDao competenceDao) throws Exception {
		CourseCertificatePDFPainter painter = new CourseCertificatePDFPainter();
		Collection allCompetences = competenceDao.loadAllSorted(0, 0);
		competenceDao.toLecturerCompetenceVOCollection(allCompetences);
		if (participantVOs != null) {
			HashMap<Long, StaffOutVO> institutionVOMap = new HashMap<Long, StaffOutVO>(participantVOs.size());
			HashMap<Long, StaffAddressOutVO> institutionAddressVOMap = new HashMap<Long, StaffAddressOutVO>(participantVOs.size());
			HashMap<Long, HashMap<Long, Collection<LecturerOutVO>>> lecturerVOMap = new HashMap<Long, HashMap<Long, Collection<LecturerOutVO>>>(participantVOs.size());
			Iterator<CourseParticipationStatusEntryOutVO> participantIt = participantVOs.iterator();
			while (participantIt.hasNext()) {
				CourseParticipationStatusEntryOutVO participationVO = participantIt.next();
				CourseOutVO courseVO = participationVO.getCourse();
				StaffOutVO institutionVO = courseVO.getInstitution();
				if (institutionVO != null && !institutionVOMap.containsKey(courseVO.getId())) {
					institutionVO = staffDao.toStaffOutVO(staffDao.load(institutionVO.getId()), Settings.getInt(CourseCertificatePDFSettingCodes.GRAPH_MAX_STAFF_INSTANCES,
							Bundle.COURSE_CERTIFICATE_PDF, CourseCertificatePDFDefaultSettings.GRAPH_MAX_STAFF_INSTANCES));
					institutionVOMap.put(courseVO.getId(), institutionVO);
					institutionAddressVOMap.put(courseVO.getId(), findOrganisationCvAddress(institutionVO, true, staffAddressDao));
				}
				if (!lecturerVOMap.containsKey(courseVO.getId())) {
					HashMap<Long, Collection<LecturerOutVO>> competenceLecturerVOMap = new HashMap<Long, Collection<LecturerOutVO>>(allCompetences.size());
					Iterator<LecturerCompetenceVO> competenceIt = allCompetences.iterator();
					while (competenceIt.hasNext()) {
						LecturerCompetenceVO competenceVO = competenceIt.next();
						Collection lecturers = lecturerDao.findByCourseStaffCompetence(courseVO.getId(), null, competenceVO.getId(), null);
						lecturerDao.toLecturerOutVOCollection(lecturers);
						competenceLecturerVOMap.put(competenceVO.getId(), lecturers);
					}
					lecturerVOMap.put(courseVO.getId(), competenceLecturerVOMap);
				}
			}
			painter.setInstitutionAddressVOMap(institutionAddressVOMap);
			painter.setInstitutionVOMap(institutionVOMap);
			painter.setLecturerVOMap(lecturerVOMap);
			painter.setParticipantVOs(participantVOs);
		}
		painter.setAllCompetenceVOs(allCompetences);
		return painter;
	}

	public static CourseParticipantListPDFPainter createCourseParticipantListPDFPainter(Collection<CourseOutVO> courseVOs, boolean blank, LecturerDao lecturerDao,
			LecturerCompetenceDao competenceDao, CourseParticipationStatusEntryDao courseParticipationDao, InventoryBookingDao bookingDao) throws Exception {
		CourseParticipantListPDFPainter painter = new CourseParticipantListPDFPainter();
		Collection allCompetences = competenceDao.loadAllSorted(0, 0);
		competenceDao.toLecturerCompetenceVOCollection(allCompetences);
		if (courseVOs != null) {
			HashMap<Long, Collection<CourseParticipationStatusEntryOutVO>> participationVOMap = new HashMap<Long, Collection<CourseParticipationStatusEntryOutVO>>(courseVOs.size());
			HashMap<Long, HashMap<Long, Collection<LecturerOutVO>>> lecturerVOMap = new HashMap<Long, HashMap<Long, Collection<LecturerOutVO>>>(courseVOs.size());
			HashMap<Long, Collection<InventoryBookingOutVO>> bookingVOMap = new HashMap<Long, Collection<InventoryBookingOutVO>>(allCompetences.size());
			Iterator<CourseOutVO> courseIt = courseVOs.iterator();
			while (courseIt.hasNext()) {
				CourseOutVO courseVO = courseIt.next();
				HashMap<Long, Collection<LecturerOutVO>> competenceLecturerVOMap = new HashMap<Long, Collection<LecturerOutVO>>(allCompetences.size());
				Iterator<LecturerCompetenceVO> competenceIt = allCompetences.iterator();
				while (competenceIt.hasNext()) {
					LecturerCompetenceVO competenceVO = competenceIt.next();
					Collection lecturers = lecturerDao.findByCourseStaffCompetence(courseVO.getId(), null, competenceVO.getId(), null);
					lecturerDao.toLecturerOutVOCollection(lecturers);
					competenceLecturerVOMap.put(competenceVO.getId(), lecturers);
				}
				lecturerVOMap.put(courseVO.getId(), competenceLecturerVOMap);
				Collection participations = blank ? new ArrayList<CourseParticipationStatusEntry>() : courseParticipationDao.findByCourseSorted(courseVO.getId());
				courseParticipationDao.toCourseParticipationStatusEntryOutVOCollection(participations);
				participationVOMap.put(courseVO.getId(), participations);
				Collection bookings = bookingDao.findByCourseSorted(courseVO.getId(), true, true);
				bookingDao.toInventoryBookingOutVOCollection(bookings);
				bookingVOMap.put(courseVO.getId(), bookings);
			}
			painter.setCourseVOs(courseVOs);
			painter.setLecturerVOMap(lecturerVOMap);
			painter.setParticipationVOMap(participationVOMap);
			painter.setBookingVOMap(bookingVOMap);
		}
		painter.setDrawPageNumbers(!blank);
		painter.setAllCompetenceVOs(allCompetences);
		return painter;
	}

	public static CVPDFPainter createCVPDFPainter(Collection<StaffOutVO> staffVOs, StaffDao staffDao, CvSectionDao cvSectionDao, CvPositionDao cvPositionDao,
			CourseParticipationStatusEntryDao courseParticipationDao, StaffAddressDao staffAddressDao) throws Exception {
		CVPDFPainter painter = new CVPDFPainter();
		Collection allCvSections = cvSectionDao.loadAllSorted(0, 0);
		cvSectionDao.toCvSectionVOCollection(allCvSections);
		if (staffVOs != null) {
			ArrayList<StaffOutVO> personVOs = new ArrayList<StaffOutVO>(staffVOs.size());
			HashMap<Long, StaffAddressOutVO> addressVOMap = new HashMap<Long, StaffAddressOutVO>(staffVOs.size());
			HashMap<Long, StaffImageOutVO> imageVOMap = new HashMap<Long, StaffImageOutVO>(staffVOs.size());
			HashMap<Long, HashMap<Long, Collection<CvPositionPDFVO>>> cvPositionVOMap = new HashMap<Long, HashMap<Long, Collection<CvPositionPDFVO>>>(staffVOs.size());
			Iterator<StaffOutVO> staffIt = staffVOs.iterator();
			while (staffIt.hasNext()) {
				StaffOutVO staffVO = staffIt.next();
				if (staffVO.isPerson()) {
					personVOs.add(staffVO);
					StaffAddressOutVO addressVO = findOrganisationCvAddress(staffVO, true, staffAddressDao);
					addressVOMap.put(staffVO.getId(), addressVO);
					imageVOMap.put(staffVO.getId(), staffDao.toStaffImageOutVO(staffDao.load(staffVO.getId())));
					HashMap<Long, Collection<CvPositionPDFVO>> staffPositionVOMap = new HashMap<Long, Collection<CvPositionPDFVO>>(allCvSections.size());
					Iterator<CvSectionVO> sectionIt = allCvSections.iterator();
					while (sectionIt.hasNext()) {
						CvSectionVO sectionVO = sectionIt.next();
						staffPositionVOMap.put(sectionVO.getId(), loadCvPositions(staffVO.getId(), sectionVO.getId(), cvPositionDao, courseParticipationDao));
					}
					cvPositionVOMap.put(staffVO.getId(), staffPositionVOMap);
				}
			}
			painter.setStaffVOs(personVOs);
			painter.setCvPositionVOMap(cvPositionVOMap);
			painter.setAddressVOMap(addressVOMap);
			painter.setImageVOMap(imageVOMap);
		}
		painter.setAllCvSectionVOs(allCvSections);
		return painter;
	}

	public static void createKeyPair(User user, String plainDepartmentPassword, KeyPairDao keyPairDao) throws Exception {
		KeyPair keyPair = KeyPair.Factory.newInstance();
		java.security.KeyPair keys = CryptoUtil.createKeyPair();
		keyPair.setPublicKey(keys.getPublic().getEncoded());
		CryptoUtil.encryptPrivateKey(keyPair, keys.getPrivate().getEncoded(), plainDepartmentPassword);
		user.setKeyPair(keyPair);
		keyPair.setUser(user);
		keyPairDao.create(keyPair);
	}

	public static Password createPassword(Password password, User user, Timestamp timestamp, Password lastPassword, String plainNewPassword, String plainDepartmentPassword,
			PasswordDao passwordDao) throws Exception {
		CryptoUtil.encryptPasswords(password, plainNewPassword, plainDepartmentPassword);
		password.setSuccessfulLogons(0L);
		password.setWrongPasswordAttemptsSinceLastSuccessfulLogon(0L);
		password.setLastLogonAttemptHost(null);
		password.setLastLogonAttemptTimestamp(null);
		password.setLastSuccessfulLogonHost(null);
		password.setLastSuccessfulLogonTimestamp(null);
		password.setTimestamp(timestamp);
		password.setPreviousPassword(lastPassword);
		password.setUser(user);
		user.getPasswords().add(password);
		return passwordDao.create(password);
	}

	public static PasswordOutVO createPassword(Password password, User user, Timestamp timestamp, Password lastPassword, String plainNewPassword, String plainDepartmentPassword,
			PasswordDao passwordDao, JournalEntryDao journalEntryDao) throws Exception {
		password = createPassword(password, user, timestamp, lastPassword, plainNewPassword, plainDepartmentPassword, passwordDao);
		PasswordOutVO result = passwordDao.toPasswordOutVO(password);
		logSystemMessage(user, result.getUser(), timestamp, CoreUtil.getUser(), SystemMessageCodes.PASSWORD_CREATED, result, null, journalEntryDao);
		return result;
	}

	public static ECRFFieldValueInVO createPresetEcrfFieldInValue(ECRFField ecrfField, long listEntryId, Long index,
			InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao) {
		ECRFFieldValueInVO ecrfFieldValueIn = new ECRFFieldValueInVO();
		InputField inputField = ecrfField.getField();
		if (ecrfField.isSeries()) {
			ecrfFieldValueIn.setIndex(index);
		} else {
			ecrfFieldValueIn.setIndex(null);
		}
		ecrfFieldValueIn.setEcrfFieldId(ecrfField.getId());
		ecrfFieldValueIn.setListEntryId(listEntryId);
		ecrfFieldValueIn.setReasonForChange(null);
		Boolean booleanPreset = inputField.getBooleanPreset();
		ecrfFieldValueIn.setBooleanValue(booleanPreset == null ? false : booleanPreset.booleanValue());
		ecrfFieldValueIn.setDateValue(CoreUtil.forceDate(inputField.getDatePreset()));
		ecrfFieldValueIn.setTimeValue(CoreUtil.forceDate(inputField.getTimePreset()));
		ecrfFieldValueIn.setFloatValue(inputField.getFloatPreset());
		ecrfFieldValueIn.setLongValue(inputField.getLongPreset());
		ecrfFieldValueIn.setTimestampValue(inputField.getTimestampPreset());
		ecrfFieldValueIn.setInkValues(null);
		if (InputFieldType.AUTOCOMPLETE.equals(inputField.getFieldType())) {
			ecrfFieldValueIn.setTextValue(getAutocompletePresetValue(inputField.getId(), inputFieldSelectionSetValueDao));
		} else {
			if (inputField.isLocalized()) {
				ecrfFieldValueIn.setTextValue(L10nUtil.getInputFieldTextPreset(Locales.USER, inputField.getTextPresetL10nKey()));
			} else {
				ecrfFieldValueIn.setTextValue(inputField.getTextPresetL10nKey());
			}
			if (InputFieldType.SELECT_ONE_DROPDOWN.equals(inputField.getFieldType())
					|| InputFieldType.SELECT_ONE_RADIO_H.equals(inputField.getFieldType())
					|| InputFieldType.SELECT_ONE_RADIO_V.equals(inputField.getFieldType())
					|| InputFieldType.SELECT_MANY_H.equals(inputField.getFieldType())
					|| InputFieldType.SELECT_MANY_V.equals(inputField.getFieldType())) {
				Iterator<InputFieldSelectionSetValue> it = inputFieldSelectionSetValueDao.findByFieldPreset(inputField.getId(), true, null).iterator();
				while (it.hasNext()) {
					ecrfFieldValueIn.getSelectionValueIds().add(it.next().getId());
				}
			}
		}
		return ecrfFieldValueIn;

	}

	public static ArrayList<ECRFFieldValueInVO> createPresetEcrfFieldInValues(long listEntryId, long ecrfId, String section, Long index, ECRFFieldDao ecrfFieldDao,
			ECRFFieldValueDao ecrfFieldValueDao, InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao) {
		ArrayList<ECRFFieldValueInVO> result = new ArrayList<ECRFFieldValueInVO>();
		Iterator<ECRFField> ecrfFieldIt = ecrfFieldDao.findByEcrfSectionPosition(ecrfId, section, null).iterator();
		while (ecrfFieldIt.hasNext()) {
			ECRFField ecrfField = ecrfFieldIt.next();
			if (ecrfFieldValueDao.getByListEntryEcrfFieldIndex(listEntryId, ecrfField.getId(), index) == null) {
				result.add(createPresetEcrfFieldInValue(ecrfField, listEntryId, index, inputFieldSelectionSetValueDao));
			}
		}
		return result;
	}

	public static ECRFFieldValueJsonVO createPresetEcrfFieldJsonValue(ECRFField ecrfField, Long index, InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao) {
		ECRFFieldValueJsonVO ecrfFieldValueVO = new ECRFFieldValueJsonVO();
		InputField inputField = ecrfField.getField();
		if (ecrfField.isSeries()) {
			ecrfFieldValueVO.setIndex(index);
			ecrfFieldValueVO.setSeries(true);
		} else {
			ecrfFieldValueVO.setIndex(null);
			ecrfFieldValueVO.setSeries(false);
		}
		ecrfFieldValueVO.setEcrfFieldId(ecrfField.getId());
		ecrfFieldValueVO.setPosition(ecrfField.getPosition());
		ecrfFieldValueVO.setJsVariableName(ecrfField.getJsVariableName());
		ecrfFieldValueVO.setJsValueExpression(ecrfField.getJsValueExpression());
		ecrfFieldValueVO.setJsOutputExpression(ecrfField.getJsOutputExpression());
		ecrfFieldValueVO.setDisabled(ecrfField.isDisabled());
		ecrfFieldValueVO.setSection(ecrfField.getSection());
		ECRF ecrf = ecrfField.getEcrf();
		if (ecrf != null) {
			ecrfFieldValueVO.setProbandGroupToken(ecrf.getGroup() != null ? ecrf.getGroup().getToken() : null);
			ecrfFieldValueVO.setVisitToken(ecrf.getVisit() != null ? ecrf.getVisit().getToken() : null);
		}
		ecrfFieldValueVO.setInputFieldId(inputField.getId());
		ecrfFieldValueVO.setInputFieldType(inputField.getFieldType());
		if (inputField.isLocalized()) {
			ecrfFieldValueVO.setInputFieldName(L10nUtil.getInputFieldName(Locales.USER, inputField.getNameL10nKey()));
		} else {
			ecrfFieldValueVO.setInputFieldName(inputField.getNameL10nKey());
		}
		Boolean booleanPreset = inputField.getBooleanPreset();
		ecrfFieldValueVO.setBooleanValue(booleanPreset == null ? false : booleanPreset.booleanValue());
		ecrfFieldValueVO.setDateValue(CoreUtil.forceDate(inputField.getDatePreset()));
		ecrfFieldValueVO.setTimeValue(CoreUtil.forceDate(inputField.getTimePreset()));
		ecrfFieldValueVO.setFloatValue(inputField.getFloatPreset());
		ecrfFieldValueVO.setLongValue(inputField.getLongPreset());
		ecrfFieldValueVO.setTimestampValue(inputField.getTimestampPreset());
		ecrfFieldValueVO.setInkValues(null);
		if (InputFieldType.AUTOCOMPLETE.equals(inputField.getFieldType())) {
			ecrfFieldValueVO.setTextValue(getAutocompletePresetValue(inputField.getId(), inputFieldSelectionSetValueDao));
		} else {
			if (inputField.isLocalized()) {
				ecrfFieldValueVO.setTextValue(L10nUtil.getInputFieldTextPreset(Locales.USER, inputField.getTextPresetL10nKey()));
			} else {
				ecrfFieldValueVO.setTextValue(inputField.getTextPresetL10nKey());
			}
			if (isLoadSelectionSet(inputField.getFieldType())) {
				Iterator<InputFieldSelectionSetValue> it = inputField.getSelectionSetValues().iterator();
				while (it.hasNext()) {
					InputFieldSelectionSetValue selectionValue = it.next();
					if (selectionValue.isPreset()) {
						ecrfFieldValueVO.getSelectionValueIds().add(selectionValue.getId());
					}
					ecrfFieldValueVO.getInputFieldSelectionSetValues().add(inputFieldSelectionSetValueDao.toInputFieldSelectionSetValueJsonVO(selectionValue));
				}
			}
		}
		return ecrfFieldValueVO;
	}

	public static ECRFFieldValueOutVO createPresetEcrfFieldOutValue(ECRFFieldOutVO ecrfFieldVO, ProbandListEntryOutVO listEntryVO, Long index, Locales locale,
			ECRFFieldStatusEntryDao ecrfFieldStatusEntryDao, ECRFFieldStatusTypeDao ecrfFieldStatusTypeDao
			) { //InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao) {
		ECRFFieldValueOutVO ecrfFieldValueVO = new ECRFFieldValueOutVO();
		if (ecrfFieldVO.getSeries()) {
			ecrfFieldValueVO.setIndex(index);
		} else {
			ecrfFieldValueVO.setIndex(null);
		}
		InputFieldOutVO inputField = ecrfFieldVO.getField();
		if (locale != null && inputField.getLocalized()) {
			InputFieldOutVO localizedInputField = new InputFieldOutVO();
			localizedInputField.copy(localizedInputField);
			if (!InputFieldType.AUTOCOMPLETE.equals(inputField.getFieldType().getType())) {
				localizedInputField.setTextPreset(L10nUtil.getInputFieldTextPreset(locale, localizedInputField.getTextPresetL10nKey()));
			}
			localizedInputField.setName(L10nUtil.getInputFieldName(locale, localizedInputField.getNameL10nKey()));
			localizedInputField.setTitle(L10nUtil.getInputFieldTitle(locale, localizedInputField.getTitleL10nKey()));
			localizedInputField.setComment(L10nUtil.getInputFieldComment(locale, localizedInputField.getCommentL10nKey()));
			localizedInputField.setValidationErrorMsg(L10nUtil.getInputFieldValidationErrorMsg(locale, localizedInputField.getValidationErrorMsgL10nKey()));
			inputField = localizedInputField;
		}
		ecrfFieldValueVO.setEcrfField(ecrfFieldVO);
		ecrfFieldValueVO.setListEntry(listEntryVO);
		ecrfFieldValueVO.setBooleanValue(inputField.getBooleanPreset());
		ecrfFieldValueVO.setDateValue(inputField.getDatePreset());
		ecrfFieldValueVO.setTimeValue(inputField.getTimePreset());
		ecrfFieldValueVO.setFloatValue(inputField.getFloatPreset());
		ecrfFieldValueVO.setLongValue(inputField.getLongPreset());
		ecrfFieldValueVO.setTimestampValue(inputField.getTimestampPreset());
		ecrfFieldValueVO.setInkValues(null);
		//		if (InputFieldType.AUTOCOMPLETE.equals(inputField.getFieldType().getType())) {
		//			ecrfFieldValueVO.setTextValue(getAutocompletePresetValue(inputField.getId(), inputFieldSelectionSetValueDao));
		//		} else {
		ecrfFieldValueVO.setTextValue(inputField.getTextPreset());
		Iterator<InputFieldSelectionSetValueOutVO> it = inputField.getSelectionSetValues().iterator();
		while (it.hasNext()) {
			InputFieldSelectionSetValueOutVO selectionValue = it.next();
			if (selectionValue.isPreset()) {
				if (locale != null && selectionValue.getLocalized()) {
					InputFieldSelectionSetValueOutVO localizedSelectionValue = new InputFieldSelectionSetValueOutVO();
					localizedSelectionValue.copy(selectionValue);
					localizedSelectionValue.setName(L10nUtil.getInputFieldSelectionSetValueName(locale, localizedSelectionValue.getNameL10nKey()));
					selectionValue = localizedSelectionValue;
				}
				ecrfFieldValueVO.getSelectionValues().add(selectionValue);
			}
		}
		if (listEntryVO != null && ecrfFieldVO != null) {

			ECRFFieldStatusQueue[] queues = ECRFFieldStatusQueue.values();
			for (int i = 0; i < queues.length; i++) {
				ECRFFieldStatusEntry lastStatus = ecrfFieldStatusEntryDao.findLastStatus(queues[i], listEntryVO.getId(), ecrfFieldVO.getId(), ecrfFieldValueVO.getIndex());
				if (lastStatus != null) {
					ecrfFieldValueVO.getLastFieldStatuses().add(ecrfFieldStatusTypeDao.toECRFFieldStatusTypeVO(lastStatus.getStatus()));
					if (!lastStatus.getStatus().isResolved()
							&& (ecrfFieldValueVO.getLastUnresolvedFieldStatusEntry() == null || ecrfFieldValueVO.getLastUnresolvedFieldStatusEntry().getId() < lastStatus.getId())) {
						ecrfFieldValueVO.setLastUnresolvedFieldStatusEntry(ecrfFieldStatusEntryDao.toECRFFieldStatusEntryOutVO(lastStatus));
					}
				}
			}
			// populateEcrfFieldStatusEntryCount(ecrfFieldValueVO.getEcrfFieldStatusQueueCounts(), listEntryVO.getId(), ecrfFieldVO.getId(), ecrfFieldValueVO.getIndex(),
			// ecrfFieldStatusEntryDao);

			//			// ECRFFieldStatusEntryDao ecrfFieldStatusEntryDao = this.getECRFFieldStatusEntryDao();
			//			ecrfFieldValueVO.setValidationUnresolved(ecrfFieldStatusEntryDao.getCount(ECRFFieldStatusQueue.VALIDATION, listEntryVO.getId(), ecrfFieldVO.getId(),
			//					ecrfFieldValueVO.getIndex(), true, false) > 0l);
			//			ecrfFieldValueVO.setQueryUnresolved(ecrfFieldStatusEntryDao.getCount(ECRFFieldStatusQueue.QUERY, listEntryVO.getId(), ecrfFieldVO.getId(), ecrfFieldValueVO.getIndex(),
			//					true, false) > 0l);
		}

		//		}
		return ecrfFieldValueVO;
	}

	public static InquiryValueInVO createPresetInquiryInValue(Inquiry inquiry, long probandId,
			InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao) {
		InquiryValueInVO inquiryValueIn = new InquiryValueInVO();
		InputField inputField = inquiry.getField();
		inquiryValueIn.setInquiryId(inquiry.getId());
		inquiryValueIn.setProbandId(probandId);
		Boolean booleanPreset = inputField.getBooleanPreset();
		inquiryValueIn.setBooleanValue(booleanPreset == null ? false : booleanPreset.booleanValue());
		inquiryValueIn.setDateValue(CoreUtil.forceDate(inputField.getDatePreset()));
		inquiryValueIn.setTimeValue(CoreUtil.forceDate(inputField.getTimePreset()));
		inquiryValueIn.setFloatValue(inputField.getFloatPreset());
		inquiryValueIn.setLongValue(inputField.getLongPreset());
		inquiryValueIn.setTimestampValue(inputField.getTimestampPreset());
		inquiryValueIn.setInkValues(null);
		if (InputFieldType.AUTOCOMPLETE.equals(inputField.getFieldType())) {
			inquiryValueIn.setTextValue(getAutocompletePresetValue(inputField.getId(),inputFieldSelectionSetValueDao));
		} else {
			if (inputField.isLocalized()) {
				inquiryValueIn.setTextValue(L10nUtil.getInputFieldTextPreset(Locales.USER, inputField.getTextPresetL10nKey()));
			} else {
				inquiryValueIn.setTextValue(inputField.getTextPresetL10nKey());
			}
			if (InputFieldType.SELECT_ONE_DROPDOWN.equals(inputField.getFieldType())
					|| InputFieldType.SELECT_ONE_RADIO_H.equals(inputField.getFieldType())
					|| InputFieldType.SELECT_ONE_RADIO_V.equals(inputField.getFieldType())
					|| InputFieldType.SELECT_MANY_H.equals(inputField.getFieldType())
					|| InputFieldType.SELECT_MANY_V.equals(inputField.getFieldType())) {
				Iterator<InputFieldSelectionSetValue> it = inputFieldSelectionSetValueDao.findByFieldPreset(inputField.getId(), true, null).iterator();
				while (it.hasNext()) {
					inquiryValueIn.getSelectionValueIds().add(it.next().getId());
				}
			}
		}
		return inquiryValueIn;

	}

	public static InquiryValueJsonVO createPresetInquiryJsonValue(Inquiry inquiry, InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao) {
		InquiryValueJsonVO inquiryValueVO = new InquiryValueJsonVO();
		InputField inputField = inquiry.getField();
		inquiryValueVO.setInquiryId(inquiry.getId());
		inquiryValueVO.setPosition(inquiry.getPosition());
		inquiryValueVO.setJsVariableName(inquiry.getJsVariableName());
		inquiryValueVO.setJsValueExpression(inquiry.getJsValueExpression());
		inquiryValueVO.setJsOutputExpression(inquiry.getJsOutputExpression());
		inquiryValueVO.setDisabled(inquiry.isDisabled());
		inquiryValueVO.setCategory(inquiry.getCategory());
		inquiryValueVO.setInputFieldId(inputField.getId());
		inquiryValueVO.setInputFieldType(inputField.getFieldType());
		if (inputField.isLocalized()) {
			inquiryValueVO.setInputFieldName(L10nUtil.getInputFieldName(Locales.USER, inputField.getNameL10nKey()));
		} else {
			inquiryValueVO.setInputFieldName(inputField.getNameL10nKey());
		}
		Boolean booleanPreset = inputField.getBooleanPreset();
		inquiryValueVO.setBooleanValue(booleanPreset == null ? false : booleanPreset.booleanValue());
		inquiryValueVO.setDateValue(CoreUtil.forceDate(inputField.getDatePreset()));
		inquiryValueVO.setTimeValue(CoreUtil.forceDate(inputField.getTimePreset()));
		inquiryValueVO.setFloatValue(inputField.getFloatPreset());
		inquiryValueVO.setLongValue(inputField.getLongPreset());
		inquiryValueVO.setTimestampValue(inputField.getTimestampPreset());
		inquiryValueVO.setInkValues(null);
		if (InputFieldType.AUTOCOMPLETE.equals(inputField.getFieldType())) {
			inquiryValueVO.setTextValue(getAutocompletePresetValue(inputField.getId(),inputFieldSelectionSetValueDao));
		} else {
			if (inputField.isLocalized()) {
				inquiryValueVO.setTextValue(L10nUtil.getInputFieldTextPreset(Locales.USER, inputField.getTextPresetL10nKey()));
			} else {
				inquiryValueVO.setTextValue(inputField.getTextPresetL10nKey());
			}
			if (isLoadSelectionSet(inputField.getFieldType())) {
				Iterator<InputFieldSelectionSetValue> it = inputField.getSelectionSetValues().iterator();
				while (it.hasNext()) {
					InputFieldSelectionSetValue selectionValue = it.next();
					if (selectionValue.isPreset()) {
						inquiryValueVO.getSelectionValueIds().add(selectionValue.getId());
					}
					inquiryValueVO.getInputFieldSelectionSetValues().add(inputFieldSelectionSetValueDao.toInputFieldSelectionSetValueJsonVO(selectionValue));
				}
			}
		}
		return inquiryValueVO;
	}

	public static InquiryValueOutVO createPresetInquiryOutValue(ProbandOutVO probandVO, InquiryOutVO inquiryVO, Locales locale
			) { //InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao) {
		InquiryValueOutVO inquiryValueVO = new InquiryValueOutVO();
		InputFieldOutVO inputField = inquiryVO.getField();
		if (locale != null && inputField.getLocalized()) {
			InputFieldOutVO localizedInputField = new InputFieldOutVO();
			localizedInputField.copy(localizedInputField);
			if (!InputFieldType.AUTOCOMPLETE.equals(inputField.getFieldType().getType())) {
				localizedInputField.setTextPreset(L10nUtil.getInputFieldTextPreset(locale, localizedInputField.getTextPresetL10nKey()));
			}
			localizedInputField.setName(L10nUtil.getInputFieldName(locale, localizedInputField.getNameL10nKey()));
			localizedInputField.setTitle(L10nUtil.getInputFieldTitle(locale, localizedInputField.getTitleL10nKey()));
			localizedInputField.setComment(L10nUtil.getInputFieldComment(locale, localizedInputField.getCommentL10nKey()));
			localizedInputField.setValidationErrorMsg(L10nUtil.getInputFieldValidationErrorMsg(locale, localizedInputField.getValidationErrorMsgL10nKey()));
			inputField = localizedInputField;
		}
		inquiryValueVO.setInquiry(inquiryVO);
		inquiryValueVO.setProband(probandVO);
		inquiryValueVO.setBooleanValue(inputField.getBooleanPreset());
		inquiryValueVO.setDateValue(inputField.getDatePreset());
		inquiryValueVO.setTimeValue(inputField.getTimePreset());
		inquiryValueVO.setFloatValue(inputField.getFloatPreset());
		inquiryValueVO.setLongValue(inputField.getLongPreset());
		inquiryValueVO.setTimestampValue(inputField.getTimestampPreset());
		inquiryValueVO.setInkValues(null);


		//		if (InputFieldType.AUTOCOMPLETE.equals(inputField.getFieldType().getType())) {
		//			inquiryValueVO.setTextValue(getAutocompletePresetValue(inputField.getId(),inputFieldSelectionSetValueDao));
		//		} else {
		inquiryValueVO.setTextValue(inputField.getTextPreset());

		Iterator<InputFieldSelectionSetValueOutVO> it = inputField.getSelectionSetValues().iterator();
		while (it.hasNext()) {
			InputFieldSelectionSetValueOutVO selectionValue = it.next();
			if (selectionValue.isPreset()) {
				if (locale != null && selectionValue.getLocalized()) {
					InputFieldSelectionSetValueOutVO localizedSelectionValue = new InputFieldSelectionSetValueOutVO();
					localizedSelectionValue.copy(selectionValue);
					localizedSelectionValue.setName(L10nUtil.getInputFieldSelectionSetValueName(locale, localizedSelectionValue.getNameL10nKey()));
					selectionValue = localizedSelectionValue;
				}
				inquiryValueVO.getSelectionValues().add(selectionValue);
			}
		}
		//		}
		return inquiryValueVO;


	}


	public static ProbandListEntryTagValueInVO createPresetProbandListEntryTagInValue(ProbandListEntryTag listEntryTag, long listEntryId,
			InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao) {
		ProbandListEntryTagValueInVO probandListEntryTagValueIn = new ProbandListEntryTagValueInVO();
		InputField inputField = listEntryTag.getField();
		probandListEntryTagValueIn.setTagId(listEntryTag.getId());
		probandListEntryTagValueIn.setListEntryId(listEntryId);
		Boolean booleanPreset = inputField.getBooleanPreset();
		probandListEntryTagValueIn.setBooleanValue(booleanPreset == null ? false : booleanPreset.booleanValue());
		probandListEntryTagValueIn.setDateValue(CoreUtil.forceDate(inputField.getDatePreset()));
		probandListEntryTagValueIn.setTimeValue(CoreUtil.forceDate(inputField.getTimePreset()));
		probandListEntryTagValueIn.setFloatValue(inputField.getFloatPreset());
		probandListEntryTagValueIn.setLongValue(inputField.getLongPreset());
		probandListEntryTagValueIn.setTimestampValue(inputField.getTimestampPreset());
		probandListEntryTagValueIn.setInkValues(null);
		if (InputFieldType.AUTOCOMPLETE.equals(inputField.getFieldType())) {
			probandListEntryTagValueIn.setTextValue(getAutocompletePresetValue(inputField.getId(),inputFieldSelectionSetValueDao));
		} else {
			if (inputField.isLocalized()) {
				probandListEntryTagValueIn.setTextValue(L10nUtil.getInputFieldTextPreset(Locales.USER, inputField.getTextPresetL10nKey()));
			} else {
				probandListEntryTagValueIn.setTextValue(inputField.getTextPresetL10nKey());
			}
			if (InputFieldType.SELECT_ONE_DROPDOWN.equals(inputField.getFieldType())
					|| InputFieldType.SELECT_ONE_RADIO_H.equals(inputField.getFieldType())
					|| InputFieldType.SELECT_ONE_RADIO_V.equals(inputField.getFieldType())
					|| InputFieldType.SELECT_MANY_H.equals(inputField.getFieldType())
					|| InputFieldType.SELECT_MANY_V.equals(inputField.getFieldType())) {
				Iterator<InputFieldSelectionSetValue> it = inputFieldSelectionSetValueDao.findByFieldPreset(inputField.getId(), true, null).iterator();
				while (it.hasNext()) {
					probandListEntryTagValueIn.getSelectionValueIds().add(it.next().getId());
				}
			}
		}
		return probandListEntryTagValueIn;

	}

	public static ProbandListEntryTagValueJsonVO createPresetProbandListEntryTagJsonValue(ProbandListEntryTag listEntryTag,
			InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao) {
		ProbandListEntryTagValueJsonVO listEntryTagValueVO = new ProbandListEntryTagValueJsonVO();
		InputField inputField = listEntryTag.getField();
		listEntryTagValueVO.setTagId(listEntryTag.getId());
		listEntryTagValueVO.setPosition(listEntryTag.getPosition());
		listEntryTagValueVO.setJsVariableName(listEntryTag.getJsVariableName());
		listEntryTagValueVO.setJsValueExpression(listEntryTag.getJsValueExpression());
		listEntryTagValueVO.setJsOutputExpression(listEntryTag.getJsOutputExpression());
		listEntryTagValueVO.setDisabled(listEntryTag.isDisabled());
		listEntryTagValueVO.setInputFieldId(inputField.getId());
		listEntryTagValueVO.setInputFieldType(inputField.getFieldType());
		if (inputField.isLocalized()) {
			listEntryTagValueVO.setInputFieldName(L10nUtil.getInputFieldName(Locales.USER, inputField.getNameL10nKey()));
		} else {
			listEntryTagValueVO.setInputFieldName(inputField.getNameL10nKey());
		}
		Boolean booleanPreset = inputField.getBooleanPreset();
		listEntryTagValueVO.setBooleanValue(booleanPreset == null ? false : booleanPreset.booleanValue());
		listEntryTagValueVO.setDateValue(CoreUtil.forceDate(inputField.getDatePreset()));
		listEntryTagValueVO.setTimeValue(CoreUtil.forceDate(inputField.getTimePreset()));
		listEntryTagValueVO.setFloatValue(inputField.getFloatPreset());
		listEntryTagValueVO.setLongValue(inputField.getLongPreset());
		listEntryTagValueVO.setTimestampValue(inputField.getTimestampPreset());
		listEntryTagValueVO.setInkValues(null);
		if (InputFieldType.AUTOCOMPLETE.equals(inputField.getFieldType())) {
			listEntryTagValueVO.setTextValue(getAutocompletePresetValue(inputField.getId(),inputFieldSelectionSetValueDao));
		} else {
			if (inputField.isLocalized()) {
				listEntryTagValueVO.setTextValue(L10nUtil.getInputFieldTextPreset(Locales.USER, inputField.getTextPresetL10nKey()));
			} else {
				listEntryTagValueVO.setTextValue(inputField.getTextPresetL10nKey());
			}
			if (isLoadSelectionSet(inputField.getFieldType())) {
				Iterator<InputFieldSelectionSetValue> it = inputField.getSelectionSetValues().iterator();
				while (it.hasNext()) {
					InputFieldSelectionSetValue selectionValue = it.next();
					if (selectionValue.isPreset()) {
						listEntryTagValueVO.getSelectionValueIds().add(selectionValue.getId());
					}
					listEntryTagValueVO.getInputFieldSelectionSetValues().add(inputFieldSelectionSetValueDao.toInputFieldSelectionSetValueJsonVO(selectionValue));
				}
			}
		}

		return listEntryTagValueVO;
	}

	public static ProbandListEntryTagValueOutVO createPresetProbandListEntryTagOutValue(ProbandListEntryOutVO probandListEntryVO, ProbandListEntryTagOutVO listEntryTagVO,
			Locales locale) { //, InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao) {
		ProbandListEntryTagValueOutVO listEntryTagValueVO = new ProbandListEntryTagValueOutVO();
		InputFieldOutVO inputField = listEntryTagVO.getField();
		if (locale != null && inputField.getLocalized()) {
			InputFieldOutVO localizedInputField = new InputFieldOutVO();
			localizedInputField.copy(localizedInputField);
			if (!InputFieldType.AUTOCOMPLETE.equals(inputField.getFieldType().getType())) {
				localizedInputField.setTextPreset(L10nUtil.getInputFieldTextPreset(locale, localizedInputField.getTextPresetL10nKey()));
			}
			localizedInputField.setName(L10nUtil.getInputFieldName(locale, localizedInputField.getNameL10nKey()));
			localizedInputField.setTitle(L10nUtil.getInputFieldTitle(locale, localizedInputField.getTitleL10nKey()));
			localizedInputField.setComment(L10nUtil.getInputFieldComment(locale, localizedInputField.getCommentL10nKey()));
			localizedInputField.setValidationErrorMsg(L10nUtil.getInputFieldValidationErrorMsg(locale, localizedInputField.getValidationErrorMsgL10nKey()));
			inputField = localizedInputField;
		}
		listEntryTagValueVO.setTag(listEntryTagVO);
		listEntryTagValueVO.setListEntry(probandListEntryVO);
		listEntryTagValueVO.setBooleanValue(inputField.getBooleanPreset());
		listEntryTagValueVO.setDateValue(inputField.getDatePreset());
		listEntryTagValueVO.setTimeValue(inputField.getTimePreset());
		listEntryTagValueVO.setFloatValue(inputField.getFloatPreset());
		listEntryTagValueVO.setLongValue(inputField.getLongPreset());
		listEntryTagValueVO.setTimestampValue(inputField.getTimestampPreset());
		listEntryTagValueVO.setInkValues(null);




		//		if (InputFieldType.AUTOCOMPLETE.equals(inputField.getFieldType().getType())) {
		//			listEntryTagValueVO.setTextValue(getAutocompletePresetValue(inputField.getId(),inputFieldSelectionSetValueDao));
		//		} else {
		listEntryTagValueVO.setTextValue(inputField.getTextPreset());

		Iterator<InputFieldSelectionSetValueOutVO> it = inputField.getSelectionSetValues().iterator();
		while (it.hasNext()) {
			InputFieldSelectionSetValueOutVO selectionValue = it.next();
			if (selectionValue.isPreset()) {
				if (locale != null && selectionValue.getLocalized()) {
					InputFieldSelectionSetValueOutVO localizedSelectionValue = new InputFieldSelectionSetValueOutVO();
					localizedSelectionValue.copy(selectionValue);
					localizedSelectionValue.setName(L10nUtil.getInputFieldSelectionSetValueName(locale, localizedSelectionValue.getNameL10nKey()));
					selectionValue = localizedSelectionValue;
				}
				listEntryTagValueVO.getSelectionValues().add(selectionValue);
			}
		}
		//		}
		return listEntryTagValueVO;


	}

	public static ProbandLetterPDFPainter createProbandLetterPDFPainter(Collection<ProbandOutVO> probandVOs, ProbandAddressDao probandAddressDao) throws Exception {
		ProbandLetterPDFPainter painter = new ProbandLetterPDFPainter();
		if (probandVOs != null) {
			ArrayList<ProbandOutVO> VOs = new ArrayList<ProbandOutVO>(probandVOs.size());
			HashMap<Long, Collection<ProbandAddressOutVO>> addressVOMap = new HashMap<Long, Collection<ProbandAddressOutVO>>(probandVOs.size());
			Iterator<ProbandOutVO> probandIt = probandVOs.iterator();
			while (probandIt.hasNext()) {
				ProbandOutVO probandVO = probandIt.next();
				if (probandVO.isPerson()) { // && !probandVO.isBlinded()
					Collection addresses = probandAddressDao.findByProband(probandVO.getId(), true, false, null, null);
					probandAddressDao.toProbandAddressOutVOCollection(addresses);
					addressVOMap.put(probandVO.getId(), addresses);
					VOs.add(probandVO);
				}
			}
			painter.setProbandVOs(VOs);
			painter.setAddressVOMap(addressVOMap);
		}
		return painter;
	}

	public static ProbandLetterPDFPainter createProbandLetterPDFPainter(ProbandAddressOutVO probandAddress) throws Exception {
		ProbandLetterPDFPainter painter = new ProbandLetterPDFPainter();
		if (probandAddress != null) {
			ProbandOutVO probandVO = probandAddress.getProband();
			HashMap<Long, Collection<ProbandAddressOutVO>> addressVOMap = new HashMap<Long, Collection<ProbandAddressOutVO>>(1);
			ArrayList<ProbandOutVO> probandVOs = new ArrayList<ProbandOutVO>(1);
			if (probandVO.isPerson()) { // && !probandVO.isBlinded()
				ArrayList<ProbandAddressOutVO> addresses = new ArrayList<ProbandAddressOutVO>(1);
				addresses.add(probandAddress);
				addressVOMap.put(probandVO.getId(), addresses);
				probandVOs.add(probandVO);
			}
			painter.setProbandVOs(probandVOs);
			painter.setAddressVOMap(addressVOMap);
		}
		return painter;
	}

	public static ReimbursementsExcelVO createReimbursementsExcel(Collection<MoneyTransfer> moneyTransfers, Collection<String> costTypes, TrialOutVO trialVO,
			ProbandOutVO probandVO, String costType, PaymentMethod method, Boolean paid,
			MoneyTransferDao moneyTransferDao,
			BankAccountDao bankAccountDao,
			ProbandAddressDao probandAddressDao,
			AddressTypeDao addressTypeDao,
			UserDao userDao) throws Exception {
		boolean passDecryption = CoreUtil.isPassDecryption();
		ReimbursementsExcelWriter writer = new ReimbursementsExcelWriter(!passDecryption, method);
		writer.setCostType(costType);
		writer.setPaid(paid);
		writer.setTrial(trialVO);
		writer.setProband(probandVO);
		boolean showAddresses;
		boolean aggregateAddresses;
		if (method != null) {
			switch (method) {
				case PETTY_CASH:
					showAddresses = Settings.getBoolean(ReimbursementsExcelSettingCodes.PETTY_CASH_SHOW_ADDRESSES, Bundle.REIMBURSEMENTS_EXCEL,
							ReimbursementsExcelDefaultSettings.PETTY_CASH_SHOW_ADDRESSES);
					aggregateAddresses = Settings.getBoolean(ReimbursementsExcelSettingCodes.PETTY_CASH_AGGREGATE_ADDRESSES, Bundle.REIMBURSEMENTS_EXCEL,
							ReimbursementsExcelDefaultSettings.PETTY_CASH_AGGREGATE_ADDRESSES);
					break;
				case VOUCHER:
					showAddresses = Settings.getBoolean(ReimbursementsExcelSettingCodes.VOUCHER_SHOW_ADDRESSES, Bundle.REIMBURSEMENTS_EXCEL,
							ReimbursementsExcelDefaultSettings.VOUCHER_SHOW_ADDRESSES);
					aggregateAddresses = Settings.getBoolean(ReimbursementsExcelSettingCodes.VOUCHER_AGGREGATE_ADDRESSES, Bundle.REIMBURSEMENTS_EXCEL,
							ReimbursementsExcelDefaultSettings.VOUCHER_AGGREGATE_ADDRESSES);
					break;
				case WIRE_TRANSFER:
					showAddresses = Settings.getBoolean(ReimbursementsExcelSettingCodes.WIRE_TRANSFER_SHOW_ADDRESSES, Bundle.REIMBURSEMENTS_EXCEL,
							ReimbursementsExcelDefaultSettings.WIRE_TRANSFER_SHOW_ADDRESSES);
					aggregateAddresses = Settings.getBoolean(ReimbursementsExcelSettingCodes.WIRE_TRANSFER_AGGREGATE_ADDRESSES, Bundle.REIMBURSEMENTS_EXCEL,
							ReimbursementsExcelDefaultSettings.WIRE_TRANSFER_AGGREGATE_ADDRESSES);
					break;
				default:
					showAddresses = false;
					aggregateAddresses = false;
					break;
			}
		} else {
			showAddresses = Settings.getBoolean(ReimbursementsExcelSettingCodes.SHOW_ADDRESSES, Bundle.REIMBURSEMENTS_EXCEL, ReimbursementsExcelDefaultSettings.SHOW_ADDRESSES);
			aggregateAddresses = Settings.getBoolean(ReimbursementsExcelSettingCodes.AGGREGATE_ADDRESSES, Bundle.REIMBURSEMENTS_EXCEL,
					ReimbursementsExcelDefaultSettings.AGGREGATE_ADDRESSES);
		}
		Collection addressTypes = showAddresses ? addressTypeDao.findByStaffProbandAnimalId(null, true, null, null) : new ArrayList();
		addressTypeDao.toAddressTypeVOCollection(addressTypes);
		ArrayList<String> distinctColumnNames;
		if (passDecryption) {
			distinctColumnNames = new ArrayList<String>((aggregateAddresses ? 3 : addressTypes.size()));
			if (aggregateAddresses) {
				distinctColumnNames.add(ReimbursementsExcelWriter.getStreetsColumnName());
				distinctColumnNames.add(ReimbursementsExcelWriter.getZipCodesColumnName());
				distinctColumnNames.add(ReimbursementsExcelWriter.getCityNamesColumnName());
			} else {
				Iterator<AddressTypeVO> addressTypesIt = addressTypes.iterator();
				while (addressTypesIt.hasNext()) {
					distinctColumnNames.add(addressTypesIt.next().getName());
				}
			}
		} else {
			distinctColumnNames = new ArrayList<String>();
		}
		Collection VOs = null;
		Iterator VOIt;
		boolean listMoneyTransfers = false;
		HashMap<Long, HashMap<String, Object>> distinctFieldRows = null;
		if (method != null) {
			MoneyTransferSummaryVO summary;
			switch (method) {
				case PETTY_CASH:
					listMoneyTransfers = true;
					break;
				case VOUCHER:
					listMoneyTransfers = true;
					break;
				case WIRE_TRANSFER:
					summary = new MoneyTransferSummaryVO();
					summary.setListEntry(null);
					summary.setTrial(trialVO);
					summary.setProband(probandVO);
					if (probandVO != null) {
						summary.setId(probandVO.getId());
					} else if (trialVO != null) {
						summary.setId(trialVO.getId());
					}
					populateMoneyTransferSummary(summary, costTypes, moneyTransfers, false, false, true, false, bankAccountDao);
					VOs = summary.getTotalsByBankAccounts();
					distinctFieldRows = new HashMap<Long, HashMap<String, Object>>(VOs.size());
					VOIt = VOs.iterator();
					while (VOIt.hasNext()) {
						MoneyTransferByBankAccountSummaryDetailVO vo = (MoneyTransferByBankAccountSummaryDetailVO) VOIt.next();
						HashMap<String, Object> fieldRow = new HashMap<String, Object>(distinctColumnNames.size());
						Collection addresses = showAddresses ? probandAddressDao.findByProband(vo.getBankAccount().getProband().getId(), null, null, true, null)
								: new ArrayList<ProbandAddress>();
						probandAddressDao.toProbandAddressOutVOCollection(addresses);
						appendDistinctProbandAddressColumnValues(addresses,
								fieldRow,
								aggregateAddresses,
								ReimbursementsExcelWriter.getStreetsColumnName(),
								ReimbursementsExcelWriter.getZipCodesColumnName(),
								ReimbursementsExcelWriter.getCityNamesColumnName());
						distinctFieldRows.put(vo.getId(), fieldRow);
					}
					break;
				default:
					listMoneyTransfers = true;
			}
		} else {
			listMoneyTransfers = true;
		}
		if (listMoneyTransfers) {
			VOs = moneyTransfers;
			moneyTransferDao.toMoneyTransferOutVOCollection(VOs);
			distinctFieldRows = new HashMap<Long, HashMap<String, Object>>(VOs.size());
			VOIt = VOs.iterator();
			while (VOIt.hasNext()) {
				MoneyTransferOutVO vo = (MoneyTransferOutVO) VOIt.next();
				HashMap<String, Object> fieldRow = new HashMap<String, Object>(distinctColumnNames.size());
				Collection addresses = showAddresses ? probandAddressDao.findByProband(vo.getProband().getId(), null, null, true, null) : new ArrayList<ProbandAddress>();
				probandAddressDao.toProbandAddressOutVOCollection(addresses);
				appendDistinctProbandAddressColumnValues(addresses,
						fieldRow,
						aggregateAddresses,
						ReimbursementsExcelWriter.getStreetsColumnName(),
						ReimbursementsExcelWriter.getZipCodesColumnName(),
						ReimbursementsExcelWriter.getCityNamesColumnName());
				distinctFieldRows.put(vo.getId(), fieldRow);
			}
			VOs = new ArrayList<MoneyTransferOutVO>(VOs);
			Collections.sort((List<MoneyTransferOutVO>) VOs, new MoneyTransferOutVOComparator());
		}
		writer.setVOs(VOs);
		writer.setDistinctColumnNames(distinctColumnNames);
		writer.setDistinctFieldRows(distinctFieldRows);
		User user = CoreUtil.getUser();
		writer.getExcelVO().setRequestingUser(userDao.toUserOutVO(user));
		(new ExcelExporter(writer, writer)).write();
		return writer.getExcelVO();
	}

	public static VisitScheduleExcelVO creatVisitScheduleExcel(Collection<VisitScheduleItem> visitScheduleItems, VisitScheduleExcelWriter.Styles style, ProbandOutVO probandVO,
			TrialOutVO trialVO,
			VisitScheduleItemDao visitScheduleItemDao,
			ProbandListStatusEntryDao probandListStatusEntryDao,
			ProbandAddressDao probandAddressDao,
			UserDao userDao) throws Exception {
		boolean passDecryption = CoreUtil.isPassDecryption();
		VisitScheduleExcelWriter writer = new VisitScheduleExcelWriter(!passDecryption, style);
		writer.setTrial(trialVO);
		writer.setProband(probandVO);
		writer.setAddress(probandVO == null ? null : probandAddressDao.toProbandAddressOutVO(probandAddressDao.findByProbandWireTransfer(probandVO.getId())));
		boolean showEnrollmentStatusReason = false;
		boolean showEnrollmentStatus = false;
		boolean showEnrollmentStatusTimestamp = false;
		boolean showEnrollmentStatusTypeIsCount = false;
		boolean showAliquotVisitReimbursement = false;
		boolean showFirstVisitReimbursement = false;
		switch (style) {
			case TRIAL_VISIT_SCHEDULE:
				showEnrollmentStatusReason = Settings.getBoolean(VisitScheduleExcelSettingCodes.TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_REASON, Bundle.VISIT_SCHEDULE_EXCEL,
						VisitScheduleExcelDefaultSettings.TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_REASON);
				showEnrollmentStatus = Settings.getBoolean(VisitScheduleExcelSettingCodes.TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS, Bundle.VISIT_SCHEDULE_EXCEL,
						VisitScheduleExcelDefaultSettings.TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS);
				showEnrollmentStatusTimestamp = Settings.getBoolean(VisitScheduleExcelSettingCodes.TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TIMESTAMP,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TIMESTAMP);
				showEnrollmentStatusTypeIsCount = Settings.getBoolean(VisitScheduleExcelSettingCodes.TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TYPE_IS_COUNT,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TYPE_IS_COUNT);
				showAliquotVisitReimbursement = Settings.getBoolean(VisitScheduleExcelSettingCodes.TRIAL_VISIT_SCHEDULE_SHOW_ALIQUOT_VISIT_REIMBURSEMENT,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.TRIAL_VISIT_SCHEDULE_SHOW_ALIQUOT_VISIT_REIMBURSEMENT);
				showFirstVisitReimbursement = Settings.getBoolean(VisitScheduleExcelSettingCodes.TRIAL_VISIT_SCHEDULE_SHOW_FIRST_VISIT_REIMBURSEMENT, Bundle.VISIT_SCHEDULE_EXCEL,
						VisitScheduleExcelDefaultSettings.TRIAL_VISIT_SCHEDULE_SHOW_FIRST_VISIT_REIMBURSEMENT);
				break;
			case PROBAND_VISIT_SCHEDULE:
				showEnrollmentStatusReason = Settings.getBoolean(VisitScheduleExcelSettingCodes.PROBAND_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_REASON, Bundle.VISIT_SCHEDULE_EXCEL,
						VisitScheduleExcelDefaultSettings.PROBAND_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_REASON);
				showEnrollmentStatus = Settings.getBoolean(VisitScheduleExcelSettingCodes.PROBAND_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS, Bundle.VISIT_SCHEDULE_EXCEL,
						VisitScheduleExcelDefaultSettings.PROBAND_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS);
				showEnrollmentStatusTimestamp = Settings.getBoolean(VisitScheduleExcelSettingCodes.PROBAND_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TIMESTAMP,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.PROBAND_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TIMESTAMP);
				showEnrollmentStatusTypeIsCount = Settings.getBoolean(VisitScheduleExcelSettingCodes.PROBAND_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TYPE_IS_COUNT,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.PROBAND_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TYPE_IS_COUNT);
				showAliquotVisitReimbursement = Settings.getBoolean(VisitScheduleExcelSettingCodes.PROBAND_VISIT_SCHEDULE_SHOW_ALIQUOT_VISIT_REIMBURSEMENT,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.PROBAND_VISIT_SCHEDULE_SHOW_ALIQUOT_VISIT_REIMBURSEMENT);
				showFirstVisitReimbursement = Settings.getBoolean(VisitScheduleExcelSettingCodes.PROBAND_VISIT_SCHEDULE_SHOW_FIRST_VISIT_REIMBURSEMENT,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.PROBAND_VISIT_SCHEDULE_SHOW_FIRST_VISIT_REIMBURSEMENT);
				break;
			case PROBAND_TRIAL_VISIT_SCHEDULE:
				showEnrollmentStatusReason = Settings.getBoolean(VisitScheduleExcelSettingCodes.PROBAND_TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_REASON,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.PROBAND_TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_REASON);
				showEnrollmentStatus = Settings.getBoolean(VisitScheduleExcelSettingCodes.PROBAND_TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS, Bundle.VISIT_SCHEDULE_EXCEL,
						VisitScheduleExcelDefaultSettings.PROBAND_TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS);
				showEnrollmentStatusTimestamp = Settings.getBoolean(VisitScheduleExcelSettingCodes.PROBAND_TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TIMESTAMP,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.PROBAND_TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TIMESTAMP);
				showEnrollmentStatusTypeIsCount = Settings.getBoolean(VisitScheduleExcelSettingCodes.PROBAND_TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TYPE_IS_COUNT,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.PROBAND_TRIAL_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TYPE_IS_COUNT);
				showAliquotVisitReimbursement = Settings.getBoolean(VisitScheduleExcelSettingCodes.PROBAND_TRIAL_VISIT_SCHEDULE_SHOW_ALIQUOT_VISIT_REIMBURSEMENT,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.PROBAND_TRIAL_VISIT_SCHEDULE_SHOW_ALIQUOT_VISIT_REIMBURSEMENT);
				showFirstVisitReimbursement = Settings.getBoolean(VisitScheduleExcelSettingCodes.PROBAND_TRIAL_VISIT_SCHEDULE_SHOW_FIRST_VISIT_REIMBURSEMENT,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.PROBAND_TRIAL_VISIT_SCHEDULE_SHOW_FIRST_VISIT_REIMBURSEMENT);
				break;
			case TRAVEL_EXPENSES_VISIT_SCHEDULE:
				showEnrollmentStatusReason = Settings.getBoolean(VisitScheduleExcelSettingCodes.TRAVEL_EXPENSES_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_REASON,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.TRAVEL_EXPENSES_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_REASON);
				showEnrollmentStatus = Settings.getBoolean(VisitScheduleExcelSettingCodes.TRAVEL_EXPENSES_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS, Bundle.VISIT_SCHEDULE_EXCEL,
						VisitScheduleExcelDefaultSettings.TRAVEL_EXPENSES_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS);
				showEnrollmentStatusTimestamp = Settings.getBoolean(VisitScheduleExcelSettingCodes.TRAVEL_EXPENSES_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TIMESTAMP,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.TRAVEL_EXPENSES_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TIMESTAMP);
				showEnrollmentStatusTypeIsCount = Settings.getBoolean(VisitScheduleExcelSettingCodes.TRAVEL_EXPENSES_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TYPE_IS_COUNT,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.TRAVEL_EXPENSES_VISIT_SCHEDULE_SHOW_ENROLLMENT_STATUS_TYPE_IS_COUNT);
				showAliquotVisitReimbursement = Settings.getBoolean(VisitScheduleExcelSettingCodes.TRAVEL_EXPENSES_VISIT_SCHEDULE_SHOW_ALIQUOT_VISIT_REIMBURSEMENT,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.TRAVEL_EXPENSES_VISIT_SCHEDULE_SHOW_ALIQUOT_VISIT_REIMBURSEMENT);
				showFirstVisitReimbursement = Settings.getBoolean(VisitScheduleExcelSettingCodes.TRAVEL_EXPENSES_VISIT_SCHEDULE_SHOW_FIRST_VISIT_REIMBURSEMENT,
						Bundle.VISIT_SCHEDULE_EXCEL, VisitScheduleExcelDefaultSettings.TRAVEL_EXPENSES_VISIT_SCHEDULE_SHOW_FIRST_VISIT_REIMBURSEMENT);
				break;
			default:
		}
		ArrayList<String> distinctColumnNames;
		if (passDecryption) {
			distinctColumnNames = new ArrayList<String>(
					(showEnrollmentStatusReason ? 1 : 0) +
					(showEnrollmentStatus ? 1 : 0) +
					(showEnrollmentStatusTimestamp ? 1 : 0) +
					(showEnrollmentStatusTypeIsCount ? 1 : 0) +
					(showAliquotVisitReimbursement ? 1 : 0) +
					(showFirstVisitReimbursement ? 1 : 0));
			if (showEnrollmentStatusReason) {
				distinctColumnNames.add(VisitScheduleExcelWriter.getEnrollmentStatusReasonColumnName());
			}
		} else {
			distinctColumnNames = new ArrayList<String>(
					(showEnrollmentStatus ? 1 : 0) +
					(showEnrollmentStatusTimestamp ? 1 : 0) +
					(showEnrollmentStatusTypeIsCount ? 1 : 0) +
					(showAliquotVisitReimbursement ? 1 : 0) +
					(showFirstVisitReimbursement ? 1 : 0));
		}
		if (showEnrollmentStatus) {
			distinctColumnNames.add(VisitScheduleExcelWriter.getEnrollmentStatusColumnName());
		}
		if (showEnrollmentStatusTimestamp) {
			distinctColumnNames.add(VisitScheduleExcelWriter.getEnrollmentStatusTimestampColumnName());
		}
		if (showEnrollmentStatusTypeIsCount) {
			distinctColumnNames.add(VisitScheduleExcelWriter.getEnrollmentStatusTypeIsCountColumnName());
		}
		if (showAliquotVisitReimbursement) {
			distinctColumnNames.add(VisitScheduleExcelWriter.getAliquotVisitReimbursementColumnName());
		}
		if (showFirstVisitReimbursement) {
			distinctColumnNames.add(VisitScheduleExcelWriter.getFirstVisitReimbursementColumnName());
		}
		Collection VOs = new ArrayList<VisitScheduleItemOutVO>(visitScheduleItems.size());
		String fieldKey;
		Object fieldValue;
		HashMap<Long, HashMap<String, Object>> distinctFieldRows = new HashMap<Long, HashMap<String, Object>>(VOs.size());
		Iterator<VisitScheduleItem> visitScheduleItemsIt = visitScheduleItems.iterator();
		HashMap<Long, HashMap<Long, Integer>> itemsPerGroupVisitCountMap = new HashMap<Long, HashMap<Long, Integer>>();
		while (visitScheduleItemsIt.hasNext()) {
			VisitScheduleItem visitScheduleItem = visitScheduleItemsIt.next();
			VisitScheduleItemOutVO vo = visitScheduleItemDao.toVisitScheduleItemOutVO(visitScheduleItem);
			HashMap<String, Object> fieldRow = new HashMap<String, Object>(distinctColumnNames.size());
			if (probandVO != null) {
				ProbandListStatusEntry probandListStatusEntry = probandListStatusEntryDao.findRecentStatus(vo.getTrial().getId(), probandVO.getId(), visitScheduleItem.getStop());
				ProbandListStatusEntryOutVO probandListStatusEntryVO = null;
				if (probandListStatusEntry != null) {
					probandListStatusEntryVO = probandListStatusEntryDao.toProbandListStatusEntryOutVO(probandListStatusEntry);
				}
				fieldKey = VisitScheduleExcelWriter.RECENT_PROBAND_LIST_STATUS_ENTRY;
				fieldValue = probandListStatusEntryVO;
				fieldRow.put(fieldKey, fieldValue); // for rowcolor
				if (showEnrollmentStatus) {
					fieldKey = VisitScheduleExcelWriter.getEnrollmentStatusColumnName();
					fieldValue = probandListStatusEntryVO == null ? "" : probandListStatusEntryVO.getStatus().getName();
					fieldRow.put(fieldKey, fieldValue);
				}
				if (passDecryption && showEnrollmentStatusReason) {
					fieldKey = VisitScheduleExcelWriter.getEnrollmentStatusReasonColumnName();
					fieldValue = probandListStatusEntryVO == null ? "" : probandListStatusEntryVO.getReason();
					fieldRow.put(fieldKey, fieldValue);
				}
				if (showEnrollmentStatusTimestamp) {
					fieldKey = VisitScheduleExcelWriter.getEnrollmentStatusTimestampColumnName();
					fieldValue = probandListStatusEntryVO == null ? null : probandListStatusEntryVO.getRealTimestamp();
					fieldRow.put(fieldKey, fieldValue);
				}
				if (showEnrollmentStatusTypeIsCount) {
					fieldKey = VisitScheduleExcelWriter.getEnrollmentStatusTypeIsCountColumnName();
					fieldValue = probandListStatusEntryVO == null ? null : probandListStatusEntryVO.getStatus().isCount();
					fieldRow.put(fieldKey, fieldValue);
				}
			}
			Long groupId = vo.getGroup() == null ? null : vo.getGroup().getId();
			Long visitId = vo.getVisit() == null ? null : vo.getVisit().getId();
			HashMap<Long, Integer> itemsPerVisitCountMap;
			int count = 0;
			if (itemsPerGroupVisitCountMap.containsKey(groupId)) {
				itemsPerVisitCountMap = itemsPerGroupVisitCountMap.get(groupId);
				if (itemsPerVisitCountMap.containsKey(visitId)) {
					count = itemsPerVisitCountMap.get(visitId);
				}
			} else {
				itemsPerVisitCountMap = new HashMap<Long, Integer>();
				itemsPerGroupVisitCountMap.put(groupId, itemsPerVisitCountMap);
			}
			itemsPerVisitCountMap.put(visitId, count + 1);
			distinctFieldRows.put(vo.getId(), fieldRow);
			VOs.add(vo);
		}
		switch (style) {
			case TRIAL_VISIT_SCHEDULE:
				Collections.sort((List<VisitScheduleItemOutVO>) VOs, new VisitScheduleItemOutVOComparator(false));
				break;
			case PROBAND_VISIT_SCHEDULE:
				Collections.sort((List<VisitScheduleItemOutVO>) VOs, new VisitScheduleItemOutVOComparator(true));
				break;
			case PROBAND_TRIAL_VISIT_SCHEDULE:
				Collections.sort((List<VisitScheduleItemOutVO>) VOs, new VisitScheduleItemOutVOComparator(true));
				break;
			case TRAVEL_EXPENSES_VISIT_SCHEDULE:
				Collections.sort((List<VisitScheduleItemOutVO>) VOs, new VisitScheduleItemOutVOComparator(true));
				break;
			default:
		}
		HashMap<Long, HashSet<Long>> firstCheckMap = new HashMap<Long, HashSet<Long>>(itemsPerGroupVisitCountMap.size());
		Iterator<VisitScheduleItemOutVO> vosIt = VOs.iterator();
		while (vosIt.hasNext()) { // final VOs order is relevant here
			VisitScheduleItemOutVO vo = vosIt.next();
			HashMap<String, Object> fieldRow = distinctFieldRows.get(vo.getId());
			VisitOutVO visit = vo.getVisit();
			Long groupId = vo.getGroup() == null ? null : vo.getGroup().getId();
			Long visitId = visit == null ? null : visit.getId();
			HashSet<Long> firstCheck;
			if (firstCheckMap.containsKey(groupId)) {
				firstCheck = firstCheckMap.get(groupId);
			} else {
				firstCheck = new HashSet<Long>();
				firstCheckMap.put(groupId, firstCheck);
			}
			boolean isFirstItemOfGroupVisit = firstCheck.add(visitId);
			if (showAliquotVisitReimbursement) {
				fieldKey = VisitScheduleExcelWriter.getAliquotVisitReimbursementColumnName();
				if (visit != null) {
					fieldValue = (Float) (visit.getReimbursement() / ((float) itemsPerGroupVisitCountMap.get(groupId).get(visitId)));
				} else {
					fieldValue = null;
				}
				fieldRow.put(fieldKey, fieldValue);
			}
			if (showFirstVisitReimbursement) {
				fieldKey = VisitScheduleExcelWriter.getFirstVisitReimbursementColumnName();
				if (visit != null && isFirstItemOfGroupVisit) {
					fieldValue = visit.getReimbursement();
				} else {
					fieldValue = null;
				}
				fieldRow.put(fieldKey, fieldValue);
			}
		}
		writer.setVOs(VOs);
		writer.setDistinctColumnNames(distinctColumnNames);
		writer.setDistinctFieldRows(distinctFieldRows);
		User user = CoreUtil.getUser();
		writer.getExcelVO().setRequestingUser(userDao.toUserOutVO(user));
		(new ExcelExporter(writer, writer)).write();
		return writer.getExcelVO();
	}

	private static StaffAddressOutVO findOrganisationCvAddress(StaffOutVO staffVO, boolean first, StaffAddressDao staffAddressDao) {
		StaffAddressOutVO addressVO;
		if (staffVO != null) {
			try {
				if (first || !staffVO.isPerson()) {
					addressVO = staffAddressDao.toStaffAddressOutVO(staffAddressDao.findByStaff(staffVO.getId(), true, null, null, null).iterator().next());
				} else {
					throw new NoSuchElementException();
				}
			} catch (NoSuchElementException e) {
				addressVO = findOrganisationCvAddress(staffVO.getParent(), false, staffAddressDao);
			}
		} else {
			addressVO = null;
		}
		return addressVO;
	}

	public static String getAutocompletePresetValue(Long inputFieldId, InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao) {
		InputFieldSelectionSetValue preset = inputFieldSelectionSetValueDao.getFieldPreset(inputFieldId);
		if (preset != null) {
			return preset.getValue();
		}
		return null;

	}

	public static String getCvAddressBlock(StaffOutVO staff, String lineBreak, StaffAddressDao staffAddressDao) {
		return CommonUtil.getCvAddressBlock(staff, findOrganisationCvAddress(staff, true, staffAddressDao), lineBreak);
	}

	public static String getCvStaffPathString(StaffOutVO staff) {
		StringBuilder result = new StringBuilder();
		appendCvStaffPath(result, staff, true);
		return result.toString();
	}

	public static Collection<ECRFFieldValueJsonVO> getEcrfFieldJsonValues(Collection<Map> ecrfFieldValues, HashMap<String, Long> maxSeriesIndexMap,
			HashMap<String, Long> fieldMaxPositionMap, HashMap<String, Long> fieldMinPositionMap,
			HashMap<String, Set<ECRFField>> seriesEcrfFieldMap, boolean filterJsValues, ECRFFieldValueDao ecrfFieldValueDao,
			InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao) throws Exception {
		// int seriesComplete = 0;
		// boolean seriesAdded = false;
		ArrayList<ECRFFieldValueJsonVO> result = new ArrayList<ECRFFieldValueJsonVO>(ecrfFieldValues.size());
		Iterator<Map> ecrfFieldValuesIt = ecrfFieldValues.iterator(); // must be sorted by section and position!
		while (ecrfFieldValuesIt.hasNext()) {
			Map<String, Object> entities = ecrfFieldValuesIt.next();
			ECRFFieldValue ecrfFieldValue = (ECRFFieldValue) entities.get(ECRF_FIELD_VALUE_DAO_ECRF_FIELD_VALUE_ALIAS);
			ECRFField ecrfField;
			Set<ECRFField> seriesEcrfFields;
			if (ecrfFieldValue != null) {
				ecrfField = ecrfFieldValue.getEcrfField();
				if (!filterJsValues || !CommonUtil.isEmptyString(ecrfField.getJsVariableName())) {
					result.add(ecrfFieldValueDao.toECRFFieldValueJsonVO(ecrfFieldValue));
					Long maxSeriesIndex;
					Long fieldMaxPosition;
					if (ecrfField.isSeries()
							&& maxSeriesIndexMap != null && seriesEcrfFieldMap != null && fieldMaxPositionMap != null
							&& (maxSeriesIndex = maxSeriesIndexMap.get(ecrfField.getSection())) != null
							&& (fieldMaxPosition = fieldMaxPositionMap.get(ecrfField.getSection())) != null
							&& (seriesEcrfFields = seriesEcrfFieldMap.get(ecrfField.getSection())) != null
							&& maxSeriesIndex.equals(ecrfFieldValue.getIndex())
							&& seriesEcrfFields.contains(ecrfField)
							&& ecrfField.getPosition() == fieldMaxPosition.longValue()) {
						// seriesComplete++;
						// if (seriesComplete == seriesEcrfFields.size()
						// || ecrfFieldValueVO.getPosition() == fieldMaxPosition) {
						// seriesComplete = 0;
						// if (ecrfFieldValue.getPosition() == fieldMaxPosition) {
						Iterator<ECRFField> seriesEcrfFieldsIt = seriesEcrfFields.iterator();
						while (seriesEcrfFieldsIt.hasNext()) {
							ECRFField seriesEcrfField = seriesEcrfFieldsIt.next();
							result.add(createPresetEcrfFieldJsonValue(seriesEcrfField, maxSeriesIndex + 1l, inputFieldSelectionSetValueDao));
						}
						// }
					}
				}
			} else {
				ecrfField = (ECRFField) entities.get(ECRF_FIELD_VALUE_DAO_ECRF_FIELD_ALIAS);
				if (!filterJsValues || !CommonUtil.isEmptyString(ecrfField.getJsVariableName())) {
					Long fieldMinPosition;
					if (ecrfField.isSeries()) {
						if (seriesEcrfFieldMap != null && fieldMinPositionMap != null
								&& (fieldMinPosition = fieldMinPositionMap.get(ecrfField.getSection())) != null
								&& (seriesEcrfFields = seriesEcrfFieldMap.get(ecrfField.getSection())) != null
								&& seriesEcrfFields.contains(ecrfField)
								&& ecrfField.getPosition() == fieldMinPosition.longValue()) {
							Iterator<ECRFField> seriesEcrfFieldsIt = seriesEcrfFields.iterator();
							while (seriesEcrfFieldsIt.hasNext()) {
								ECRFField seriesEcrfField = seriesEcrfFieldsIt.next();
								result.add(createPresetEcrfFieldJsonValue(seriesEcrfField, 0l, inputFieldSelectionSetValueDao));
							}
						}
					} else {
						result.add(createPresetEcrfFieldJsonValue(ecrfField, null, inputFieldSelectionSetValueDao));
					}
				}
			}
		}
		return result;
	}

	public static Collection<ECRFFieldValueOutVO> getEcrfFieldValues(ProbandListEntryOutVO listEntryVO, Collection<Map> ecrfFieldValues,
			HashMap<String, Long> maxSeriesIndexMap,
			HashMap<String, Long> fieldMaxPositionMap, HashMap<String, Long> fieldMinPositionMap,
			HashMap<String, Set<ECRFField>> seriesEcrfFieldMap, Locales locale, ECRFFieldDao ecrfFieldDao, ECRFFieldValueDao ecrfFieldValueDao,
			ECRFFieldStatusEntryDao ecrfFieldStatusEntryDao, ECRFFieldStatusTypeDao ecrfFieldStatusTypeDao
			// InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao
			) throws Exception {
		// int seriesComplete = 0;
		// boolean seriesAdded = false;
		// Integer pageSize = (psf == null ? null : psf.getPageSize());
		// boolean updateRowCount = (psf != null && psf.getRowCount() != null ? psf.getUpdateRowCount() : false);
		ArrayList<ECRFFieldValueOutVO> result = new ArrayList<ECRFFieldValueOutVO>(ecrfFieldValues.size());
		Iterator<Map> ecrfFieldValuesIt = ecrfFieldValues.iterator(); // must be sorted by section and position!
		while (ecrfFieldValuesIt.hasNext()) {
			// if (pageSize != null && result.size() >= pageSize) {
			// break;
			// } else {
			Map<String, Object> entities = ecrfFieldValuesIt.next();
			ECRFFieldValue ecrfFieldValue = (ECRFFieldValue) entities.get(ECRF_FIELD_VALUE_DAO_ECRF_FIELD_VALUE_ALIAS);
			ECRFField ecrfField;
			Set<ECRFField> seriesEcrfFields;
			if (ecrfFieldValue != null) {
				ecrfField = ecrfFieldValue.getEcrfField();
				result.add(ecrfFieldValueDao.toECRFFieldValueOutVO(ecrfFieldValue));
				Long maxSeriesIndex;
				Long fieldMaxPosition;
				if (ecrfField.isSeries()
						&& maxSeriesIndexMap != null && seriesEcrfFieldMap != null && fieldMaxPositionMap != null
						&& (maxSeriesIndex = maxSeriesIndexMap.get(ecrfField.getSection())) != null
						&& (fieldMaxPosition = fieldMaxPositionMap.get(ecrfField.getSection())) != null
						&& (seriesEcrfFields = seriesEcrfFieldMap.get(ecrfField.getSection())) != null
						&& maxSeriesIndex.equals(ecrfFieldValue.getIndex())
						&& seriesEcrfFields.contains(ecrfField)
						&& ecrfField.getPosition() == fieldMaxPosition.longValue()) {
					// seriesComplete++;
					// if (seriesComplete == seriesEcrfFields.size()
					// || ecrfFieldValueVO.getEcrfField().getPosition() == fieldMaxPosition) {
					// seriesComplete = 0;
					// if (ecrfFieldValueVO.getEcrfField().getPosition() == fieldMaxPosition) {
					Iterator<ECRFField> seriesEcrfFieldsIt = seriesEcrfFields.iterator();
					while (seriesEcrfFieldsIt.hasNext()) {
						ECRFField seriesEcrfField = seriesEcrfFieldsIt.next();
						// if (pageSize != null && result.size() >= pageSize) {
						// break;// new section will be incomplete, so dont use unless fields are optional..
						// } else {
						result.add(createPresetEcrfFieldOutValue(ecrfFieldDao.toECRFFieldOutVO(seriesEcrfField), listEntryVO, maxSeriesIndex + 1l, locale, ecrfFieldStatusEntryDao,
								ecrfFieldStatusTypeDao));
						// if (updateRowCount) {
						// psf.setRowCount(psf.getRowCount() + 1l);
						// }
						// }
					}
					// }
				}
			} else {
				ecrfField = (ECRFField) entities.get(ECRF_FIELD_VALUE_DAO_ECRF_FIELD_ALIAS);
				Long fieldMinPosition;
				if (ecrfField.isSeries()) {
					if (seriesEcrfFieldMap != null && fieldMinPositionMap != null
							&& (fieldMinPosition = fieldMinPositionMap.get(ecrfField.getSection())) != null
							&& (seriesEcrfFields = seriesEcrfFieldMap.get(ecrfField.getSection())) != null
							&& seriesEcrfFields.contains(ecrfField)
							&& ecrfField.getPosition() == fieldMinPosition.longValue()) {
						Iterator<ECRFField> seriesEcrfFieldsIt = seriesEcrfFields.iterator();
						while (seriesEcrfFieldsIt.hasNext()) {
							ECRFField seriesEcrfField = seriesEcrfFieldsIt.next();
							result.add(createPresetEcrfFieldOutValue(ecrfFieldDao.toECRFFieldOutVO(seriesEcrfField), listEntryVO, 0l, locale, ecrfFieldStatusEntryDao,
									ecrfFieldStatusTypeDao));
						}
					}
				} else {
					result.add(createPresetEcrfFieldOutValue(ecrfFieldDao.toECRFFieldOutVO(ecrfField), listEntryVO, null, locale, ecrfFieldStatusEntryDao, ecrfFieldStatusTypeDao));
				}
			}
			// }
		}
		return result;
	}

	public static Collection<InquiryValueJsonVO> getInquiryJsonValues(Collection<Map> inquiryValues,
			boolean filterJsValues, InquiryValueDao inquiryValueDao,
			InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao) throws Exception {
		// Collection<Map> inquiryValues = inquiryValueDao.f
		ArrayList<InquiryValueJsonVO> result = new ArrayList<InquiryValueJsonVO>(inquiryValues.size());
		Iterator<Map> inquiryValuesIt = inquiryValues.iterator();
		while (inquiryValuesIt.hasNext()) {
			Map<String, Object> entities = inquiryValuesIt.next();
			InquiryValue inquiryValue = (InquiryValue) entities.get(INQUIRY_VALUE_DAO_INQUIRY_VALUE_ALIAS);
			if (inquiryValue != null) {
				if (!filterJsValues || !CommonUtil.isEmptyString(inquiryValue.getInquiry().getJsVariableName())) {
					result.add(inquiryValueDao.toInquiryValueJsonVO(inquiryValue));
				}
			} else {
				Inquiry inquiry = (Inquiry) entities.get(INQUIRY_VALUE_DAO_INQUIRY_ALIAS);
				if (!filterJsValues || !CommonUtil.isEmptyString(inquiry.getJsVariableName())) {
					result.add(createPresetInquiryJsonValue(inquiry, inputFieldSelectionSetValueDao));
				}
			}
		}
		return result;
	}

	public static Collection<InquiryValueOutVO> getInquiryValues(ProbandOutVO probandVO, Collection<Map> inquiryValues, Locales locale,
			InquiryDao inquiryDao, InquiryValueDao inquiryValueDao) // , InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao)
					throws Exception {
		ArrayList<InquiryValueOutVO> result = new ArrayList<InquiryValueOutVO>(inquiryValues.size());
		Iterator<Map> inquiryValuesIt = inquiryValues.iterator();
		while (inquiryValuesIt.hasNext()) {
			Map<String, Object> entities = inquiryValuesIt.next();
			InquiryValueOutVO inquiryValueVO;
			InquiryValue inquiryValue = (InquiryValue) entities.get(INQUIRY_VALUE_DAO_INQUIRY_VALUE_ALIAS);
			if (inquiryValue != null) {
				inquiryValueVO = inquiryValueDao.toInquiryValueOutVO(inquiryValue);
			} else {
				InquiryOutVO inquiryVO = inquiryDao.toInquiryOutVO((Inquiry) entities.get(INQUIRY_VALUE_DAO_INQUIRY_ALIAS));
				inquiryValueVO = createPresetInquiryOutValue(probandVO, inquiryVO, locale); // , inputFieldSelectionSetValueDao);
			}
			result.add(inquiryValueVO);
		}
		return result;
	}

	public static Date getLogonExpirationDate(Password password) {
		return DateCalc.addInterval(getPasswordDate(password), password.getValidityPeriod(), password.getValidityPeriodDays());
	}



	public static Date getPasswordDate(Password password) {
		return DateCalc.getStartOfDay(password.getTimestamp());
	}

	public static Collection<PermissionProfileVO> getPermissionProfiles(PermissionProfileGroup profileGroup, Locales locale) {
		Collection<PermissionProfileVO> result; // = new ArrayList<VariablePeriodVO>();
		PermissionProfile[] permissionProfiles = PermissionProfile.values();
		if (permissionProfiles != null) {
			result = new ArrayList<PermissionProfileVO>(permissionProfiles.length);
			for (int i = 0; i < permissionProfiles.length; i++) {
				PermissionProfileGroup group = PermissionProfileGrouping.getGroupFromPermissionProfile(permissionProfiles[i]);
				if (profileGroup == null || profileGroup.equals(group)) {
					result.add(L10nUtil.createPermissionProfileVO(locale, permissionProfiles[i]));
				}
			}
		} else {
			result = new ArrayList<PermissionProfileVO>();
		}
		return result;
	}

	public static Collection<ProbandListEntryTagValueJsonVO> getProbandListEntryTagJsonValues(Collection<Map> probandListEntryTagValues, boolean filterJsValues,
			ProbandListEntryTagValueDao probandListEntryTagValueDao, InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao) throws Exception {
		ArrayList<ProbandListEntryTagValueJsonVO> result = new ArrayList<ProbandListEntryTagValueJsonVO>(probandListEntryTagValues.size());
		Iterator<Map> probandListEntryTagValuesIt = probandListEntryTagValues.iterator();
		while (probandListEntryTagValuesIt.hasNext()) {
			Map<String, Object> entities = probandListEntryTagValuesIt.next();
			ProbandListEntryTagValue listEntryTagValue = (ProbandListEntryTagValue) entities.get(PROBAND_LIST_ENTRY_TAG_VALUE_DAO_PROBAND_LIST_ENTRY_TAG_VALUE_ALIAS);
			if (listEntryTagValue != null) {
				if (!filterJsValues || !CommonUtil.isEmptyString(listEntryTagValue.getTag().getJsVariableName())) {
					result.add(probandListEntryTagValueDao.toProbandListEntryTagValueJsonVO(listEntryTagValue));
				}
			} else {
				ProbandListEntryTag listEntryTag = (ProbandListEntryTag) entities.get(PROBAND_LIST_ENTRY_TAG_VALUE_DAO_PROBAND_LIST_ENTRY_TAG_ALIAS);
				if (!filterJsValues || !CommonUtil.isEmptyString(listEntryTag.getJsVariableName())) {
					result.add(createPresetProbandListEntryTagJsonValue(listEntryTag, inputFieldSelectionSetValueDao));
				}
			}
		}
		return result;
	}

	public static Collection<ProbandListEntryTagValueOutVO> getProbandListEntryTagValues(ProbandListEntryOutVO probandListEntryVO, Collection<Map> probandListEntryTagValues,
			Locales locale, ProbandListEntryTagDao probandListEntryTagDao, ProbandListEntryTagValueDao probandListEntryTagValueDao
			// InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao
			) throws Exception {
		ArrayList<ProbandListEntryTagValueOutVO> result = new ArrayList<ProbandListEntryTagValueOutVO>(probandListEntryTagValues.size());
		Iterator<Map> probandListEntryTagValuesIt = probandListEntryTagValues.iterator();
		while (probandListEntryTagValuesIt.hasNext()) {
			Map<String, Object> entities = probandListEntryTagValuesIt.next();
			ProbandListEntryTagValue listEntryTagValue = (ProbandListEntryTagValue) entities.get(PROBAND_LIST_ENTRY_TAG_VALUE_DAO_PROBAND_LIST_ENTRY_TAG_VALUE_ALIAS);
			ProbandListEntryTagValueOutVO listEntryTagValueVO;
			if (listEntryTagValue != null) {
				listEntryTagValueVO = probandListEntryTagValueDao.toProbandListEntryTagValueOutVO(listEntryTagValue);
			} else {
				ProbandListEntryTagOutVO listEntryTagVO = probandListEntryTagDao.toProbandListEntryTagOutVO((ProbandListEntryTag) entities
						.get(PROBAND_LIST_ENTRY_TAG_VALUE_DAO_PROBAND_LIST_ENTRY_TAG_ALIAS));
				listEntryTagValueVO = createPresetProbandListEntryTagOutValue(probandListEntryVO, listEntryTagVO, locale); // , inputFieldSelectionSetValueDao);
			}
			result.add(listEntryTagValueVO);
		}
		return result;
	}

	private static String getValidationErrorMsg(InputFieldOutVO inputField) {
		return MessageFormat.format(INPUT_FIELD_VALIDATION_ERROR_MESSAGE, CommonUtil.inputFieldOutVOToString(inputField), inputField.getValidationErrorMsg());
	}

	public static AuthorisationException initAuthorisationExceptionWithPosition(String errorCode, boolean logError, CriterionInstantVO criterion, Object... args)
			throws AuthorisationException {
		AuthorisationException exception = L10nUtil.initAuthorisationException(errorCode, args);
		if (criterion != null) {
			exception.setData(criterion.getPosition());
		}
		exception.setLogError(logError);
		return exception;
	}

	private static TreeMap<BankAccountOutVO, MoneyTransferByBankAccountSummaryDetailVO> initBankAccountMap() {
		return new TreeMap<BankAccountOutVO, MoneyTransferByBankAccountSummaryDetailVO>(new BankAccountOutVOComparator());
	}

	private static TreeMap<String, MoneyTransferByCostTypeSummaryDetailVO> initCostTypeMap(Collection<String> costTypes, boolean comments) {
		TreeMap<String, MoneyTransferByCostTypeSummaryDetailVO> map = new TreeMap<String, MoneyTransferByCostTypeSummaryDetailVO>(MONEY_TRANSFER_COST_TYPE_COMPARATOR);
		MoneyTransferByCostTypeSummaryDetailVO byCostTypeDetail;
		if (costTypes != null && costTypes.size() > 0) {
			Iterator<String> it = costTypes.iterator();
			while (it.hasNext()) {
				String costType = it.next();
				if (costType == null) {
					costType = "";
				}
				byCostTypeDetail = new MoneyTransferByCostTypeSummaryDetailVO();
				byCostTypeDetail.setTotal(0.0f);
				byCostTypeDetail.setCount(0l);
				byCostTypeDetail.setCostType(costType);
				byCostTypeDetail.setDecrypted(comments);
				map.put(costType, byCostTypeDetail);
			}
		} else {
			String costType = "";
			byCostTypeDetail = new MoneyTransferByCostTypeSummaryDetailVO();
			byCostTypeDetail.setTotal(0.0f);
			byCostTypeDetail.setCount(0l);
			byCostTypeDetail.setCostType(costType);
			byCostTypeDetail.setDecrypted(comments);
			map.put(costType, byCostTypeDetail);
		}
		return map;
	}

	private static LinkedHashMap<PaymentMethod, MoneyTransferByPaymentMethodSummaryDetailVO> initPaymentMethodMap() {
		PaymentMethod[] paymentMethods = PaymentMethod.values(); // same element order as SelectionSetService.handleGetPaymentMethods
		LinkedHashMap<PaymentMethod, MoneyTransferByPaymentMethodSummaryDetailVO> map = new LinkedHashMap<PaymentMethod, MoneyTransferByPaymentMethodSummaryDetailVO>(
				paymentMethods.length);
		MoneyTransferByPaymentMethodSummaryDetailVO byPaymentMethodDetail;
		for (int i = 0; i < paymentMethods.length; i++) {
			byPaymentMethodDetail = new MoneyTransferByPaymentMethodSummaryDetailVO();
			byPaymentMethodDetail.setTotal(0.0f);
			byPaymentMethodDetail.setCount(0l);
			byPaymentMethodDetail.setMethod(L10nUtil.createPaymentMethodVO(Locales.USER, paymentMethods[i]));
			map.put(paymentMethods[i], byPaymentMethodDetail);
		}
		return map;
	}

	public static void initSeriesEcrfFieldMaps(Collection<ECRFField> seriesEcrfFields, long probandListEntryId, long ecrfId, HashMap<String, Long> maxSeriesIndexMap,
			HashMap<String, Long> fieldMaxPositionMap, HashMap<String, Long> fieldMinPositionMap, HashMap<String, Set<ECRFField>> seriesEcrfFieldMap,
			ECRFFieldValueDao ecrfFieldValueDao) {
		// HashMap<String, Set<ECRFField>> seriesEcrfFieldJsMap,
		Iterator<ECRFField> it = seriesEcrfFields.iterator();
		while (it.hasNext()) {
			ECRFField seriesEcrfField = it.next();
			String section = seriesEcrfField.getSection();
			if (!maxSeriesIndexMap.containsKey(section)) {
				maxSeriesIndexMap.put(section, ecrfFieldValueDao.getMaxIndex(probandListEntryId, ecrfId, section));
			}
			if (!fieldMaxPositionMap.containsKey(section)) {
				fieldMaxPositionMap.put(section, seriesEcrfField.getPosition());
			} else {
				if (seriesEcrfField.getPosition() > fieldMaxPositionMap.get(section)) {
					fieldMaxPositionMap.put(section, seriesEcrfField.getPosition());
				}
			}
			if (!fieldMinPositionMap.containsKey(section)) {
				fieldMinPositionMap.put(section, seriesEcrfField.getPosition());
			} else {
				if (seriesEcrfField.getPosition() < fieldMinPositionMap.get(section)) {
					fieldMinPositionMap.put(section, seriesEcrfField.getPosition());
				}
			}
			if (!seriesEcrfFieldMap.containsKey(section)) {
				seriesEcrfFieldMap.put(section, new LinkedHashSet<ECRFField>());
			}
			seriesEcrfFieldMap.get(section).add(seriesEcrfField);
			// if (!CommonUtil.isEmptyString(seriesEcrfField.getJsVariableName())) {
			// if (!seriesEcrfFieldJsMap.containsKey(section)) {
			// seriesEcrfFieldJsMap.put(section, new LinkedHashSet<ECRFField>());
			// }
			// seriesEcrfFieldJsMap.get(section).add(seriesEcrfField);
			// }
		}
	}

	public static boolean isInputFieldType(ECRFFieldOutVO ecrfField, InputFieldType type) {
		if (ecrfField != null) {
			return isInputFieldType(ecrfField.getField(), type);
		}
		return false;
	}

	public static boolean isInputFieldType(InputField field, InputFieldType type) {
		if (field != null) {
			return field.getFieldType().equals(type);
		}
		return false;
	}

	public static boolean isInputFieldType(InputFieldOutVO field, InputFieldType type) {
		InputFieldTypeVO typeVO;
		if (field != null && (typeVO = field.getFieldType()) != null) {
			return typeVO.getType().equals(type);
		}
		return false;
	}

	public static boolean isInputFieldType(Inquiry inquiry, InputFieldType type) {
		if (inquiry != null) {
			return isInputFieldType(inquiry.getField(), type);
		}
		return false;
	}

	public static boolean isInputFieldType(InquiryOutVO inquiry, InputFieldType type) {
		if (inquiry != null) {
			return isInputFieldType(inquiry.getField(), type);
		}
		return false;
	}

	public static boolean isInputFieldType(ProbandListEntryTag tag, InputFieldType type) {
		if (tag != null) {
			return isInputFieldType(tag.getField(), type);
		}
		return false;
	}

	public static boolean isInputFieldType(ProbandListEntryTagOutVO tag, InputFieldType type) {
		if (tag != null) {
			return isInputFieldType(tag.getField(), type);
		}
		return false;
	}

	public static boolean isLoadSelectionSet(InputFieldType fieldType) {
		return InputFieldType.SELECT_ONE_DROPDOWN.equals(fieldType)
				|| InputFieldType.SELECT_ONE_RADIO_H.equals(fieldType)
				|| InputFieldType.SELECT_ONE_RADIO_V.equals(fieldType)
				|| InputFieldType.SELECT_MANY_H.equals(fieldType)
				|| InputFieldType.SELECT_MANY_V.equals(fieldType)
				|| InputFieldType.SKETCH.equals(fieldType);
	}

	public static Collection<CvPositionPDFVO> loadCvPositions(Long staffId, Long sectionId, CvPositionDao cvPositionDao, CourseParticipationStatusEntryDao courseParticipationDao)
			throws Exception {
		Collection cvPositions = cvPositionDao.findByStaffSection(staffId, sectionId, true, null);
		Collection courseParticipations = courseParticipationDao.findByStaffSection(staffId, sectionId, true, true, true, null);
		cvPositionDao.toCvPositionPDFVOCollection(cvPositions);
		courseParticipationDao.toCvPositionPDFVOCollection(courseParticipations);
		ArrayList<CvPositionPDFVO> result = new ArrayList<CvPositionPDFVO>(cvPositions.size() + courseParticipations.size());
		result.addAll(cvPositions);
		result.addAll(courseParticipations);
		Collections.sort(result, new CvPositionPDFVOComparator());
		return result;
	}

	public static JournalEntry logSystemMessage(InputField inputField, InputFieldOutVO inputFieldVO, Timestamp now, User modified, String systemMessageCode, Object result,
			Object original, JournalEntryDao journalEntryDao) throws Exception {
		return journalEntryDao.addSystemMessage(inputField, now, modified, systemMessageCode, new Object[] { CommonUtil.inputFieldOutVOToString(inputFieldVO) },
				new Object[] { CoreUtil.getSystemMessageCommentContent(result, original, !CommonUtil.getUseJournalEncryption(JournalModule.INPUT_FIELD_JOURNAL, null)) });
	}

	private static JournalEntry logSystemMessage(Inventory inventory, ProbandOutVO probandVO, Timestamp now, User modified, String systemMessageCode, Object result,
			Object original, JournalEntryDao journalEntryDao) throws Exception {
		// we don't print proband name etc...
		boolean journalEncrypted = CommonUtil.getUseJournalEncryption(JournalModule.INVENTORY_JOURNAL, null);
		return journalEntryDao.addSystemMessage(inventory, now, modified, systemMessageCode, journalEncrypted ? new Object[] { CommonUtil.probandOutVOToString(probandVO) }
		: new Object[] { Long.toString(probandVO.getId()) },
		new Object[] { CoreUtil.getSystemMessageCommentContent(result, original, !journalEncrypted) });
	}

	public static JournalEntry logSystemMessage(Proband proband, ProbandOutVO probandVO, Timestamp now, User modified, String systemMessageCode, Object result, Object original,
			JournalEntryDao journalEntryDao) throws Exception {
		boolean journalEncrypted = CommonUtil.getUseJournalEncryption(JournalModule.PROBAND_JOURNAL, null);
		return journalEntryDao.addSystemMessage(proband, now, modified, systemMessageCode, journalEncrypted ? new Object[] { CommonUtil.probandOutVOToString(probandVO) }
		: new Object[] { Long.toString(probandVO.getId()) },
		new Object[] { CoreUtil.getSystemMessageCommentContent(result, original, !journalEncrypted) });
	}

	public static JournalEntry logSystemMessage(Proband proband, TrialOutVO trialVO, Timestamp now, User modified, String systemMessageCode, Object result, Object original,
			JournalEntryDao journalEntryDao) throws Exception {
		return journalEntryDao.addSystemMessage(proband, now, modified, systemMessageCode, new Object[] { CommonUtil.trialOutVOToString(trialVO) },
				new Object[] { CoreUtil.getSystemMessageCommentContent(result, original, !CommonUtil.getUseJournalEncryption(JournalModule.PROBAND_JOURNAL, null)) });
	}

	public static JournalEntry logSystemMessage(Staff staff, ProbandOutVO probandVO, Timestamp now, User modified, String systemMessageCode, Object result, Object original,
			JournalEntryDao journalEntryDao) throws Exception {
		boolean journalEncrypted = CommonUtil.getUseJournalEncryption(JournalModule.STAFF_JOURNAL, null);
		return journalEntryDao.addSystemMessage(staff, now, modified, systemMessageCode, journalEncrypted ? new Object[] { CommonUtil.probandOutVOToString(probandVO) }
		: new Object[] { Long.toString(probandVO.getId()) },
		new Object[] { CoreUtil.getSystemMessageCommentContent(result, original, !journalEncrypted) });
	}

	public static JournalEntry logSystemMessage(Staff staff, TrialOutVO trialVO, Timestamp now, User modified, String systemMessageCode, Object result, Object original,
			JournalEntryDao journalEntryDao) throws Exception {
		return journalEntryDao.addSystemMessage(staff, now, modified, systemMessageCode, new Object[] { CommonUtil.trialOutVOToString(trialVO) },
				new Object[] { CoreUtil.getSystemMessageCommentContent(result, original, !CommonUtil.getUseJournalEncryption(JournalModule.STAFF_JOURNAL, null)) });
	}

	public static JournalEntry logSystemMessage(Trial trial, ProbandOutVO probandVO, Timestamp now, User modified, String systemMessageCode, Object result, Object original,
			JournalEntryDao journalEntryDao) throws Exception {
		// we don't print proband name etc...
		boolean journalEncrypted = CommonUtil.getUseJournalEncryption(JournalModule.TRIAL_JOURNAL, null);
		return journalEntryDao.addSystemMessage(trial, now, modified, systemMessageCode, journalEncrypted ? new Object[] { CommonUtil.probandOutVOToString(probandVO) }
		: new Object[] { Long.toString(probandVO.getId()) },
		new Object[] { CoreUtil.getSystemMessageCommentContent(result, original, !journalEncrypted) });
	}

	//	public static JournalEntry logSystemMessage(Trial trial, StaffOutVO staffVO, Timestamp now, User modified, String systemMessageCode, Object result, Object original,
	//			JournalEntryDao journalEntryDao) throws Exception {
	//		return journalEntryDao.addSystemMessage(trial, now, modified, systemMessageCode, new Object[] { CommonUtil.staffOutVOToString(staffVO) }, systemMessageCode,
	//				new Object[] { CoreUtil.getSystemMessageCommentContent(result, original, !CommonUtil.getUseJournalEncryption(JournalModule.TRIAL_JOURNAL, null)) });
	//	}
	public static JournalEntry logSystemMessage(Trial trial, StaffOutVO staffVO, Timestamp now, User modified, String systemMessageCode, Object result, Object original,
			JournalEntryDao journalEntryDao) throws Exception {
		return journalEntryDao.addSystemMessage(trial, now, modified, systemMessageCode, new Object[] { CommonUtil.staffOutVOToString(staffVO) },
				new Object[] { CoreUtil.getSystemMessageCommentContent(result, original, !CommonUtil.getUseJournalEncryption(JournalModule.TRIAL_JOURNAL, null)) });
	}

	public static JournalEntry logSystemMessage(Trial trial, TrialOutVO trialVO, Timestamp now, User modified, String systemMessageCode, Object result, Object original,
			JournalEntryDao journalEntryDao) throws Exception {
		return journalEntryDao.addSystemMessage(trial, now, modified, systemMessageCode, new Object[] { CommonUtil.trialOutVOToString(trialVO) },
				new Object[] { CoreUtil.getSystemMessageCommentContent(result, original, !CommonUtil.getUseJournalEncryption(JournalModule.TRIAL_JOURNAL, null)) });
	}

	private static JournalEntry logSystemMessage(User user, ProbandOutVO probandVO, Timestamp now, User modified, String systemMessageCode, ProbandOutVO result, Object original,
			JournalEntryDao journalEntryDao) throws Exception {
		if (user == null) {
			return null;
		}
		// we don't print proband name etc...
		boolean journalEncrypted = CommonUtil.getUseJournalEncryption(JournalModule.USER_JOURNAL, null);
		return journalEntryDao.addSystemMessage(user, now, modified, systemMessageCode, journalEncrypted ? new Object[] { CommonUtil.probandOutVOToString(probandVO) }
		: new Object[] { Long.toString(probandVO.getId()) },
		new Object[] { CoreUtil.getSystemMessageCommentContent(result, original, !journalEncrypted) });
	}

	public static JournalEntry logSystemMessage(User user, UserOutVO userVO, Timestamp now, User modified, String systemMessageCode, Object result, Object original,
			JournalEntryDao journalEntryDao) throws Exception {
		if (user == null) {
			return null;
		}
		return journalEntryDao.addSystemMessage(user, now, modified, systemMessageCode, new Object[] { CommonUtil.userOutVOToString(userVO) },
				new Object[] { CoreUtil.getSystemMessageCommentContent(result, original, !CommonUtil.getUseJournalEncryption(JournalModule.USER_JOURNAL, null)) });
	}







	public static void notifyParticipationStatusUpdated(CourseParticipationStatusType oldStatus, CourseParticipationStatusEntry courseParticipation, boolean toLecturers, Date now,
			NotificationDao notificationDao) throws Exception {
		CourseParticipationStatusType newState = courseParticipation.getStatus();
		if (oldStatus == null || !oldStatus.equals(newState)) {
			if (newState.isNotify()) {
				notificationDao.addNotification(courseParticipation, false, toLecturers, now, null);
			}
		}
	}

	public static void populateBookingDurationSummary(boolean inventoryBreakDown, InventoryBookingDurationSummaryVO summary, InventoryBookingDao inventoryBookingDao)
			throws Exception {
		ArrayList<InventoryBookingDurationSummaryDetailVO> assigned = new ArrayList<InventoryBookingDurationSummaryDetailVO>();
		InventoryBookingDurationSummaryDetailVO notAssigned = new InventoryBookingDurationSummaryDetailVO();
		HashMap<Long, InventoryBookingDurationSummaryDetailVO> durationSummaryDetailsMap = new HashMap<Long, InventoryBookingDurationSummaryDetailVO>();
		HashMap<Long, InventoryBookingDurationSummaryDetailVO> courseDurationSummaryDetailsMap = new HashMap<Long, InventoryBookingDurationSummaryDetailVO>();
		HashMap<Long, InventoryBookingDurationSummaryDetailVO> trialDurationSummaryDetailsMap = new HashMap<Long, InventoryBookingDurationSummaryDetailVO>();
		HashMap<Long, InventoryBookingDurationSummaryDetailVO> probandDurationSummaryDetailsMap = new HashMap<Long, InventoryBookingDurationSummaryDetailVO>();
		summary.setIntervalDuration((new DateInterval(summary.getStart(), summary.getStop())).getDuration());
		InventoryOutVO inventory;
		TrialOutVO trial;
		CourseOutVO course;
		ProbandOutVO proband;
		Iterator<InventoryBooking> it = null;
		boolean showDurationAndLoad = false;
		if (inventoryBreakDown) {
			if ((course = summary.getCourse()) != null) {
				it = inventoryBookingDao.findByCourseCalendarInterval(course.getId(), summary.getCalendar(), CommonUtil.dateToTimestamp(summary.getStart()),
						CommonUtil.dateToTimestamp(summary.getStop()), null).iterator();
			}
			if ((proband = summary.getProband()) != null) {
				it = inventoryBookingDao.findByProbandCalendarInterval(proband.getId(), summary.getCalendar(), CommonUtil.dateToTimestamp(summary.getStart()),
						CommonUtil.dateToTimestamp(summary.getStop()), null).iterator();
			} else if ((trial = summary.getTrial()) != null) {
				it = inventoryBookingDao.findByTrialCalendarInterval(trial.getId(), summary.getCalendar(), CommonUtil.dateToTimestamp(summary.getStart()),
						CommonUtil.dateToTimestamp(summary.getStop()), null).iterator();
			} else {
				it = inventoryBookingDao.findByInventoryCalendarInterval(null, summary.getCalendar(), CommonUtil.dateToTimestamp(summary.getStart()),
						CommonUtil.dateToTimestamp(summary.getStop())).iterator();
				showDurationAndLoad = CommonUtil.isEmptyString(summary.getCalendar()); // true;
			}
			summary.setInventory(null);
		} else {
			inventory = summary.getInventory();
			it = inventoryBookingDao.findByInventoryCalendarInterval(inventory == null ? null : inventory.getId(), summary.getCalendar(),
					CommonUtil.dateToTimestamp(summary.getStart()), CommonUtil.dateToTimestamp(summary.getStop())).iterator();
			summary.setCourse(null);
			summary.setTrial(null);
			summary.setProband(null);
			showDurationAndLoad = CommonUtil.isEmptyString(summary.getCalendar()); // true;
		}
		boolean fullBookings = Settings.getBoolean(SettingCodes.BOOKING_SUMMARY_FULL_BOOKINGS, Bundle.SETTINGS, DefaultSettings.BOOKING_SUMMARY_FULL_BOOKINGS);
		boolean mergeOverlapping = Settings.getBoolean(SettingCodes.BOOKING_SUMMARY_MERGE_OVERLAPPING, Bundle.SETTINGS, DefaultSettings.BOOKING_SUMMARY_MERGE_OVERLAPPING);
		summary.setBookingCount(0);
		if (!mergeOverlapping) {
			summary.setTotalDuration(0l);
		}
		// Long key;
		InventoryBookingOutVO booking;
		HashMap<Long, ArrayList<DateInterval>> inventoryIntervalsMap = new HashMap<Long, ArrayList<DateInterval>>();
		while (it.hasNext()) {
			booking = inventoryBookingDao.toInventoryBookingOutVO(it.next());
			// InventoryBookingDurationSummaryDetailVO durationSummaryDetail;
			if (inventoryBreakDown) {
				inventory = booking.getInventory();
				updateBookingSummaryDetail(inventory.getId(), durationSummaryDetailsMap, assigned, inventory, null, null, null, booking, summary, fullBookings, mergeOverlapping);
			} else {
				course = booking.getCourse();
				boolean isAssigned = updateBookingSummaryDetail(course == null ? null : course.getId(), courseDurationSummaryDetailsMap, assigned, null, course, null, null,
						booking, summary, fullBookings, mergeOverlapping);
				proband = booking.getProband();
				isAssigned = isAssigned
						| updateBookingSummaryDetail(proband == null ? null : proband.getId(), probandDurationSummaryDetailsMap, assigned, null, null, proband, null, booking,
								summary, fullBookings, mergeOverlapping);
				trial = booking.getTrial();
				isAssigned = isAssigned
						| updateBookingSummaryDetail(trial == null ? null : trial.getId(), trialDurationSummaryDetailsMap, assigned, null, null, null, trial, booking, summary,
								fullBookings, mergeOverlapping);
				if (!isAssigned) {
					notAssigned.setBookingCount(notAssigned.getBookingCount() + 1);
					if (!mergeOverlapping) {
						durationSummaryDetailsMap.put(booking.getInventory().getId(), null);
						notAssigned.setTotalDuration(notAssigned.getTotalDuration() + booking.getTotalDuration());
						if (summary.getIntervalDuration() > 0.0f) {
							int inventoryCount = durationSummaryDetailsMap.keySet().size();
							if (inventoryCount > 0) {
								notAssigned.setLoad(((float) notAssigned.getTotalDuration()) / ((float) (summary.getIntervalDuration() * inventoryCount)));
							}
						}
					}
				}
			}
			summary.setBookingCount(summary.getBookingCount() + 1);
			if (mergeOverlapping) {
				if (showDurationAndLoad) {
					ArrayList<DateInterval> intervals;
					if (inventoryIntervalsMap.containsKey(booking.getInventory().getId())) {
						intervals = inventoryIntervalsMap.get(booking.getInventory().getId());
					} else {
						intervals = new ArrayList<DateInterval>();
						inventoryIntervalsMap.put(booking.getInventory().getId(), intervals);
					}
					if (!fullBookings) {
						Date start = booking.getStart();
						Date stop = booking.getStop();
						if (summary.getStart() != null && booking.getStart().before(summary.getStart())) {
							start = summary.getStart();
						}
						if (summary.getStop() != null && booking.getStop().after(summary.getStop())) {
							stop = summary.getStop();
						}
						intervals.add(new DateInterval(start, stop));
					} else {
						intervals.add(new DateInterval(booking.getStart(), booking.getStop()));
					}
				}
			} else {
				summary.setTotalDuration(summary.getTotalDuration() + booking.getTotalDuration());
			}
		}
		if (mergeOverlapping) {
			if (showDurationAndLoad) {
				long totalDuration = 0l;
				int inventoryCount = 0;
				Iterator<Entry<Long, ArrayList<DateInterval>>> inventoryIntervalsIt = inventoryIntervalsMap.entrySet().iterator();
				while (inventoryIntervalsIt.hasNext()) {
					Entry<Long, ArrayList<DateInterval>> inventoryIntervals = inventoryIntervalsIt.next();
					Long inventoryId = inventoryIntervals.getKey();
					long duration = 0l;
					Iterator<DateInterval> intervalsIt = DateInterval.mergeIntervals(inventoryIntervals.getValue()).iterator();
					while (intervalsIt.hasNext()) {
						duration += intervalsIt.next().getDuration();
					}
					InventoryBookingDurationSummaryDetailVO durationSummaryDetail = durationSummaryDetailsMap.get(inventoryId);
					if (durationSummaryDetail != null) {
						durationSummaryDetail.setTotalDuration(duration);
						if (summary.getIntervalDuration() > 0.0f) { // summary.getInventory() != null &&
							durationSummaryDetail.setLoad(((float) durationSummaryDetail.getTotalDuration()) / ((float) summary.getIntervalDuration()));
						}
					}
					totalDuration += duration;
					inventoryCount++;
				}
				summary.setTotalDuration(totalDuration);
				if (summary.getIntervalDuration() > 0.0f && inventoryCount > 0) { // summary.getInventory() != null &&
					summary.setLoad(((float) summary.getTotalDuration()) / ((float) (summary.getIntervalDuration() * inventoryCount)));
				}
			}
		} else {
			if (summary.getIntervalDuration() > 0.0f) { // summary.getInventory() != null &&
				int inventoryCount = inventoryBreakDown ? durationSummaryDetailsMap.keySet().size() : 1;
				if (inventoryCount > 0) {
					summary.setLoad(((float) summary.getTotalDuration()) / ((float) (summary.getIntervalDuration() * inventoryCount)));
				}
			}
		}
		summary.setAssigneds(assigned);
		summary.setNotAssigned(notAssigned);
	}

	public static void populateEcrfFieldStatusEntryCount(Collection<ECRFFieldStatusQueueCountVO> counts, Long listEntryId,
			ECRFFieldStatusEntryDao ecrfFieldStatusEntryDao) {
		if (counts != null) {
			counts.clear();
			if (listEntryId != null) {
				ECRFFieldStatusQueue[] queues = ECRFFieldStatusQueue.values();
				for (int i = 0; i < queues.length; i++) {
					ECRFFieldStatusQueueCountVO count = new ECRFFieldStatusQueueCountVO();
					count.setQueue(queues[i]);
					count.setTotal(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, false, null, null, null, null));
					count.setInitial(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, true, true, null, null, false));
					count.setUpdated(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, true, null, true, null, null));
					count.setProposed(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, true, null, null, true, null));
					// count.setResolved(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, true, false, null, null, true));
					count.setResolved(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, true, null, null, null, true));
					count.setUnresolved(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, true, null, null, null, false));
					counts.add(count);
				}
			}
		}
	}

	private static void populateEcrfFieldStatusEntryCount(Collection<ECRFFieldStatusQueueCountVO> counts, Long listEntryId, Long ecrfId,
			ECRFFieldStatusEntryDao ecrfFieldStatusEntryDao) {
		if (counts != null) {
			counts.clear();
			if (listEntryId != null && ecrfId != null) {
				ECRFFieldStatusQueue[] queues = ECRFFieldStatusQueue.values();
				for (int i = 0; i < queues.length; i++) {
					ECRFFieldStatusQueueCountVO count = new ECRFFieldStatusQueueCountVO();
					count.setQueue(queues[i]);
					count.setTotal(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfId, false, null, null, null, null));
					count.setInitial(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfId, true, true, null, null, false));
					count.setUpdated(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfId, true, null, true, null, null));
					count.setProposed(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfId, true, null, null, true, null));
					// count.setResolved(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfId, true, false, null, null, true));
					count.setResolved(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfId, true, null, null, null, true));
					count.setUnresolved(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfId, true, null, null, null, false));
					counts.add(count);
				}
			}
		}
	}

	public static void populateEcrfFieldStatusEntryCount(Collection<ECRFFieldStatusQueueCountVO> counts, Long listEntryId, Long ecrfFieldId, Long index,
			ECRFFieldStatusEntryDao ecrfFieldStatusEntryDao) {
		if (counts != null) {
			counts.clear();
			if (listEntryId != null && ecrfFieldId != null) {
				ECRFFieldStatusQueue[] queues = ECRFFieldStatusQueue.values();
				for (int i = 0; i < queues.length; i++) {
					ECRFFieldStatusQueueCountVO count = new ECRFFieldStatusQueueCountVO();
					count.setQueue(queues[i]);
					count.setTotal(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfFieldId, index, false, null, null, null, null));
					count.setInitial(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfFieldId, index, true, true, null, null, false));
					count.setUpdated(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfFieldId, index, true, null, true, null, null));
					count.setProposed(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfFieldId, index, true, null, null, true, null));
					// count.setResolved(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfFieldId, index, true, false, null, null, true));
					count.setResolved(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfFieldId, index, true, null, null, null, true));
					count.setUnresolved(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfFieldId, index, true, null, null, null, false));
					counts.add(count);
				}
			}
		}
	}

	private static void populateEcrfFieldStatusEntryCount(Collection<ECRFFieldStatusQueueCountVO> counts, Long listEntryId, Long ecrfId, String section,
			ECRFFieldStatusEntryDao ecrfFieldStatusEntryDao) {
		if (counts != null) {
			counts.clear();
			if (listEntryId != null && ecrfId != null) {
				ECRFFieldStatusQueue[] queues = ECRFFieldStatusQueue.values();
				for (int i = 0; i < queues.length; i++) {
					ECRFFieldStatusQueueCountVO count = new ECRFFieldStatusQueueCountVO();
					count.setQueue(queues[i]);
					count.setTotal(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfId, section, false, null, null, null, null));
					count.setInitial(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfId, section, true, true, null, null, false));
					count.setUpdated(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfId, section, true, null, true, null, null));
					count.setProposed(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfId, section, true, null, null, true, null));
					// count.setResolved(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfId, section, true, false, null, null, true));
					count.setResolved(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfId, section, true, null, null, null, true));
					count.setUnresolved(ecrfFieldStatusEntryDao.getCount(queues[i], listEntryId, ecrfId, section, true, null, null, null, false));
					counts.add(count);
				}
			}
		}
	}

	public static ECRFProgressVO populateEcrfProgress(ECRFOutVO ecrfVO, ProbandListEntryOutVO listEntryVO, boolean sectionDetail,
			ECRFStatusEntryDao ecrfStatusEntryDao, ECRFStatusTypeDao ecrfStatusTypeDao,
			ECRFFieldDao ecrfFieldDao, ECRFFieldValueDao ecrfFieldValueDao, ECRFFieldStatusEntryDao ecrfFieldStatusEntryDao) throws Exception {
		ECRFProgressVO result = new ECRFProgressVO();
		result.setEcrf(ecrfVO);
		result.setListEntry(listEntryVO);
		ECRFStatusEntry statusEntry = ecrfStatusEntryDao.findByEcrfListEntry(ecrfVO.getId(), listEntryVO.getId());
		if (statusEntry != null) {
			result.setStatus(ecrfStatusTypeDao.toECRFStatusTypeVO(statusEntry.getStatus()));
		} else {
			result.setStatus(null);
		}
		// ECRFFieldDao ecrfFieldDao = this.getECRFFieldDao();
		// ECRFFieldValueDao ecrfFieldValueDao = this.getECRFFieldValueDao();
		if (sectionDetail) {
			populateEcrfSectionProgress(ecrfFieldDao.findByTrialEcrfSeriesJs(null, ecrfVO.getId(), true, null, null, null), ecrfVO, listEntryVO, result, true, ecrfFieldValueDao,
					ecrfFieldStatusEntryDao);
		} else {
			populateEcrfProgress(ecrfVO, listEntryVO, result, ecrfFieldDao, ecrfFieldValueDao, ecrfFieldStatusEntryDao);
		}
		// result.setUnresolvedValidationLastFieldStatusCount(result.getUnresolvedValidationLastFieldStatusCount()
		// + ecrfFieldStatusEntryDao.getCount(ECRFFieldStatusQueue.VALIDATION, listEntryVO.getId(), ecrfVO.getId(), true, false));
		// result.setUnresolvedQueryLastFieldStatusCount(result.getUnresolvedQueryLastFieldStatusCount()
		// + ecrfFieldStatusEntryDao.getCount(ECRFFieldStatusQueue.QUERY, listEntryVO.getId(), ecrfVO.getId(), true, false));
		return result;
	}

	private static void populateEcrfProgress(ECRFOutVO ecrfVO, ProbandListEntryOutVO listEntryVO, ECRFProgressVO result,
			ECRFFieldDao ecrfFieldDao, ECRFFieldValueDao ecrfFieldValueDao, ECRFFieldStatusEntryDao ecrfFieldStatusEntryDao) throws Exception {
		result.setFieldCount(ecrfFieldDao.getCount(null, ecrfVO.getId(), false, null));
		result.setSavedValueCount(ecrfFieldValueDao.getCount(listEntryVO.getId(), ecrfVO.getId(), true, false, null));
		result.setMandatoryFieldCount(ecrfFieldDao.getCount(null, ecrfVO.getId(), false, false));
		result.setMandatorySavedValueCount(ecrfFieldValueDao.getCount(listEntryVO.getId(), ecrfVO.getId(), true, false, false));
		populateEcrfFieldStatusEntryCount(result.getEcrfFieldStatusQueueCounts(), listEntryVO.getId(), ecrfVO.getId(), ecrfFieldStatusEntryDao);
	}

	public static ECRFProgressVO populateEcrfProgress(ECRFOutVO ecrfVO, ProbandListEntryOutVO listEntryVO, String section,
			ECRFStatusEntryDao ecrfStatusEntryDao, ECRFStatusTypeDao ecrfStatusTypeDao,
			ECRFFieldDao ecrfFieldDao, ECRFFieldValueDao ecrfFieldValueDao, ECRFFieldStatusEntryDao ecrfFieldStatusEntryDao) throws Exception {
		ECRFProgressVO result = new ECRFProgressVO();
		result.setEcrf(ecrfVO);
		result.setListEntry(listEntryVO);
		ECRFStatusEntry statusEntry = ecrfStatusEntryDao.findByEcrfListEntry(ecrfVO.getId(), listEntryVO.getId());
		if (statusEntry != null) {
			result.setStatus(ecrfStatusTypeDao.toECRFStatusTypeVO(statusEntry.getStatus()));
		} else {
			result.setStatus(null);
		}

		populateEcrfSectionProgress(ecrfFieldDao.findByTrialEcrfSectionSeriesJs(null, ecrfVO.getId(),section, true, null, null, null),ecrfVO,listEntryVO,result,false,ecrfFieldValueDao,ecrfFieldStatusEntryDao);

		populateEcrfProgress(ecrfVO,listEntryVO,result,ecrfFieldDao,ecrfFieldValueDao,ecrfFieldStatusEntryDao);

		return result;
	}

	private static void populateEcrfSectionProgress(Collection<ECRFField> ecrfFields, ECRFOutVO ecrfVO, ProbandListEntryOutVO listEntryVO, ECRFProgressVO result,
			boolean increment,
			ECRFFieldValueDao ecrfFieldValueDao, ECRFFieldStatusEntryDao ecrfFieldStatusEntryDao) throws Exception {
		if (increment) {
			result.setMandatoryFieldCount(0l);
			result.setMandatorySavedValueCount(0l);
			result.setFieldCount(0l);
			result.setSavedValueCount(0l);
		}
		// result.setUnresolvedValidationLastFieldStatusCount(0l);
		// result.setUnresolvedQueryLastFieldStatusCount(0l);
		Iterator<ECRFField> it = ecrfFields.iterator();
		HashMap<String, ECRFSectionProgressVO> sectionProgressMap = new HashMap<String, ECRFSectionProgressVO>();
		while (it.hasNext()) {
			ECRFField ecrfField = it.next();
			String section = ecrfField.getSection();
			ECRFSectionProgressVO sectionProgress;
			if (sectionProgressMap.containsKey(section)) {
				sectionProgress = sectionProgressMap.get(section);
				if (!ecrfField.isSeries()) {
					sectionProgress.setFieldCount(sectionProgress.getFieldCount() + 1l);
					sectionProgress.setMandatoryFieldCount(sectionProgress.getMandatoryFieldCount() + (ecrfField.isOptional() ? 0l : 1l));
				}
			} else {
				sectionProgress = new ECRFSectionProgressVO();
				sectionProgress.setSection(section);
				populateEcrfFieldStatusEntryCount(sectionProgress.getEcrfFieldStatusQueueCounts(), listEntryVO.getId(), ecrfVO.getId(), section, ecrfFieldStatusEntryDao);
				result.setEcrfFieldStatusQueueCounts(addEcrfFieldStatusEntryCounts(result.getEcrfFieldStatusQueueCounts(),
						sectionProgress.getEcrfFieldStatusQueueCounts()));
				if (!ecrfField.isSeries()) {
					sectionProgress.setFieldCount(1l);
					sectionProgress.setMandatoryFieldCount(ecrfField.isOptional() ? 0l : 1l);
					sectionProgress.setSavedValueCount(ecrfFieldValueDao.getCount(listEntryVO.getId(), ecrfVO.getId(), section, true, null));
					sectionProgress.setMandatorySavedValueCount(ecrfFieldValueDao.getCount(listEntryVO.getId(), ecrfVO.getId(), section, true, false));
					if (increment) {
						result.setSavedValueCount(result.getSavedValueCount() + sectionProgress.getSavedValueCount());
						result.setMandatorySavedValueCount(result.getMandatorySavedValueCount() + sectionProgress.getMandatorySavedValueCount());
					}
					sectionProgress.setIndex(null);
					sectionProgress.setSeries(false);
				} else {
					sectionProgress.setFieldCount(0l);
					sectionProgress.setMandatoryFieldCount(0l);
					sectionProgress.setSavedValueCount(0l);
					sectionProgress.setMandatorySavedValueCount(0l);
					sectionProgress.setIndex(ecrfFieldValueDao.getMaxIndex(listEntryVO.getId(), ecrfVO.getId(), section));
					sectionProgress.setSeries(true);
				}
				sectionProgressMap.put(section, sectionProgress);
				result.getSections().add(sectionProgress);
			}
			if (increment && !ecrfField.isSeries()) {
				result.setFieldCount(result.getFieldCount() + 1l);
				result.setMandatoryFieldCount(result.getMandatoryFieldCount() + (ecrfField.isOptional() ? 0l : 1l));
			}
			// if (sectionProgressMap.containsKey(section)) {
			// sectionProgress = sectionProgressMap.get(section);
			// sectionProgress.setFieldCount(sectionProgress.getFieldCount() + 1l);
			// result.setFieldCount(result.getFieldCount() + 1l);
			// if (!ecrfField.isOptional()) {
			// sectionProgress.setMandatoryFieldCount(sectionProgress.getMandatoryFieldCount() + 1l);
			// if (!ecrfField.isSeries()) {
			// result.setMandatoryFieldCount(result.getMandatoryFieldCount() + 1l);
			// }
			// }
			// } else {
			// sectionProgress = new ECRFSectionProgressVO();
			// sectionProgress.setFieldCount(1l);
			// //result.setFieldCount(result.getFieldCount() + 1l);
			// sectionProgress.setMandatoryFieldCount(ecrfField.isOptional() ? 0l : 1l);
			// sectionProgress.setSection(section);
			// sectionProgress.setSavedValueCount(ecrfFieldValueDao.getCount(listEntryVO.getId(), ecrfVO.getId(), section, true, null));
			// //result.setSavedValueCount(result.getSavedValueCount() + sectionProgress.getSavedValueCount());
			// sectionProgress.setMandatorySavedValueCount(ecrfFieldValueDao.getCount(listEntryVO.getId(), ecrfVO.getId(), section, true, false));
			// if (ecrfField.isSeries()) {
			// sectionProgress.setIndex(ecrfFieldValueDao.getMaxIndex(listEntryVO.getId(), ecrfVO.getId(), section));
			// sectionProgress.setSeries(true);
			// } else {
			//
			// sectionProgress.setSavedValueCount(ecrfFieldValueDao.getCount(listEntryVO.getId(), ecrfVO.getId(), section, true, null));
			// sectionProgress.setMandatorySavedValueCount(ecrfFieldValueDao.getCount(listEntryVO.getId(), ecrfVO.getId(), section, true, false));
			//
			// result.setMandatoryFieldCount(result.getMandatoryFieldCount() + sectionProgress.getMandatoryFieldCount());
			// result.setMandatorySavedValueCount(result.getMandatorySavedValueCount() + sectionProgress.getMandatorySavedValueCount());
			// result.setFieldCount(result.getFieldCount() + sectionProgress.getFieldCount());
			// result.setSavedValueCount(result.getSavedValueCount() + sectionProgress.getSavedValueCount());
			// sectionProgress.setIndex(null);
			// sectionProgress.setSeries(false);
			// }
			// sectionProgressMap.put(section, sectionProgress);
			// result.getSections().add(sectionProgress);
			// }
		}
	}

	public static void populateMoneyTransferSummary(MoneyTransferSummaryVO summary, Collection<String> costTypes, Collection<MoneyTransfer> moneyTransfers, boolean byCostType,
			boolean byPaymentMethod, boolean byBankAccount, boolean comments, BankAccountDao bankAccountDao) {
		TreeMap<String, MoneyTransferByCostTypeSummaryDetailVO> totalsByCostTypeMap = initCostTypeMap(costTypes, comments);
		HashMap<String, LinkedHashMap<PaymentMethod, MoneyTransferByPaymentMethodSummaryDetailVO>> byCostTypeByPaymentMethodMap = new HashMap<String, LinkedHashMap<PaymentMethod, MoneyTransferByPaymentMethodSummaryDetailVO>>(
				totalsByCostTypeMap.size());
		HashMap<String, TreeMap<BankAccountOutVO, MoneyTransferByBankAccountSummaryDetailVO>> byCostTypeByBankAccountMap = new HashMap<String, TreeMap<BankAccountOutVO, MoneyTransferByBankAccountSummaryDetailVO>>(
				totalsByCostTypeMap.size());
		LinkedHashMap<PaymentMethod, MoneyTransferByPaymentMethodSummaryDetailVO> totalsByPaymentMethodMap = initPaymentMethodMap();
		HashMap<PaymentMethod, TreeMap<String, MoneyTransferByCostTypeSummaryDetailVO>> byPaymentMethodByCostTypeMap = new HashMap<PaymentMethod, TreeMap<String, MoneyTransferByCostTypeSummaryDetailVO>>(
				totalsByPaymentMethodMap.size());
		HashMap<PaymentMethod, TreeMap<BankAccountOutVO, MoneyTransferByBankAccountSummaryDetailVO>> byPaymentMethodByBankAccountMap = new HashMap<PaymentMethod, TreeMap<BankAccountOutVO, MoneyTransferByBankAccountSummaryDetailVO>>(
				totalsByPaymentMethodMap.size());
		TreeMap<BankAccountOutVO, MoneyTransferByBankAccountSummaryDetailVO> totalsByBankAccountMap = initBankAccountMap();
		HashMap<Long, LinkedHashMap<PaymentMethod, MoneyTransferByPaymentMethodSummaryDetailVO>> byBankAccountByPaymentMethodMap = new HashMap<Long, LinkedHashMap<PaymentMethod, MoneyTransferByPaymentMethodSummaryDetailVO>>();
		HashMap<Long, TreeMap<String, MoneyTransferByCostTypeSummaryDetailVO>> byBankAccountByCostTypeMap = new HashMap<Long, TreeMap<String, MoneyTransferByCostTypeSummaryDetailVO>>();
		MoneyTransferByCostTypeSummaryDetailVO byCostTypeDetail;
		MoneyTransferByPaymentMethodSummaryDetailVO byPaymentMethodDetail;
		MoneyTransferByBankAccountSummaryDetailVO byBankAccountDetail;
		String costType;
		BankAccountOutVO bankAccount;
		Long bankAccountId;
		LinkedHashMap<PaymentMethod, MoneyTransferByPaymentMethodSummaryDetailVO> byPaymentMethodMap;
		TreeMap<String, MoneyTransferByCostTypeSummaryDetailVO> byCostTypeMap;
		TreeMap<BankAccountOutVO, MoneyTransferByBankAccountSummaryDetailVO> byBankAccountMap;
		summary.setTotal(0.0f);
		summary.setCount(0l);
		summary.setPaidCount(0l);
		Iterator<MoneyTransfer> it = moneyTransfers.iterator();
		while (it.hasNext()) {
			MoneyTransfer mt = it.next();
			if (byCostType) {
				costType = mt.getCostType() == null ? "" : mt.getCostType();
			} else {
				costType = ""; // initialize
			}
			if (byBankAccount) {
				bankAccount = bankAccountDao.toBankAccountOutVO(mt.getBankAccount());
				bankAccountId = bankAccount == null ? null : bankAccount.getId();
			} else {
				bankAccount = null; // initialize
				bankAccountId = null;
			}
			if (byCostType) {
				// update totals by cost type:
				if (totalsByCostTypeMap.containsKey(costType)) {
					byCostTypeDetail = totalsByCostTypeMap.get(costType);
				} else {
					byCostTypeDetail = new MoneyTransferByCostTypeSummaryDetailVO();
					byCostTypeDetail.setTotal(0.0f);
					byCostTypeDetail.setCount(0l);
					byCostTypeDetail.setCostType(costType);
					byCostTypeDetail.setDecrypted(comments);
					totalsByCostTypeMap.put(costType, byCostTypeDetail);
				}
				byCostTypeDetail.setTotal(byCostTypeDetail.getTotal() + mt.getAmount());
				byCostTypeDetail.setCount(byCostTypeDetail.getCount() + 1l);
				if (comments) {
					addCostTypeDetailComment(mt,byCostTypeDetail);
				}
				if (byPaymentMethod) {
					// update payment methods per cost type:
					if (byCostTypeByPaymentMethodMap.containsKey(costType)) {
						byPaymentMethodMap = byCostTypeByPaymentMethodMap.get(costType);
					} else {
						byPaymentMethodMap = initPaymentMethodMap();
						byCostTypeByPaymentMethodMap.put(costType, byPaymentMethodMap);
					}
					byPaymentMethodDetail = byPaymentMethodMap.get(mt.getMethod());
					byPaymentMethodDetail.setTotal(byPaymentMethodDetail.getTotal() + mt.getAmount());
					byPaymentMethodDetail.setCount(byPaymentMethodDetail.getCount() + 1l);
				}
				if (byBankAccount && bankAccountId != null) {
					// update bank accounts per cost type:
					if (byCostTypeByBankAccountMap.containsKey(costType)) {
						byBankAccountMap = byCostTypeByBankAccountMap.get(costType);
					} else {
						byBankAccountMap = initBankAccountMap();
						byCostTypeByBankAccountMap.put(costType, byBankAccountMap);
					}
					if (byBankAccountMap.containsKey(bankAccount)) {
						byBankAccountDetail = byBankAccountMap.get(bankAccount);
					} else {
						byBankAccountDetail = new MoneyTransferByBankAccountSummaryDetailVO();
						byBankAccountDetail.setTotal(0.0f);
						byBankAccountDetail.setCount(0l);
						byBankAccountDetail.setBankAccount(bankAccount);
						byBankAccountDetail.setId(bankAccountId);
						byBankAccountMap.put(bankAccount, byBankAccountDetail);
					}
					byBankAccountDetail.setTotal(byBankAccountDetail.getTotal() + mt.getAmount());
					byBankAccountDetail.setCount(byBankAccountDetail.getCount() + 1l);
				}
			}
			if (byPaymentMethod) {
				// update totals by payment method:
				byPaymentMethodDetail = totalsByPaymentMethodMap.get(mt.getMethod());
				byPaymentMethodDetail.setTotal(byPaymentMethodDetail.getTotal() + mt.getAmount());
				byPaymentMethodDetail.setCount(byPaymentMethodDetail.getCount() + 1l);
				if (byCostType) {
					// update cost types per payment method:
					if (byPaymentMethodByCostTypeMap.containsKey(mt.getMethod())) {
						byCostTypeMap = byPaymentMethodByCostTypeMap.get(mt.getMethod());
					} else {
						byCostTypeMap = initCostTypeMap(costTypes, comments);
						byPaymentMethodByCostTypeMap.put(mt.getMethod(), byCostTypeMap);
					}
					if (byCostTypeMap.containsKey(costType)) {
						byCostTypeDetail = byCostTypeMap.get(costType);
					} else {
						byCostTypeDetail = new MoneyTransferByCostTypeSummaryDetailVO();
						byCostTypeDetail.setTotal(0.0f);
						byCostTypeDetail.setCount(0l);
						byCostTypeDetail.setCostType(costType);
						byCostTypeDetail.setDecrypted(comments);
						byCostTypeMap.put(costType, byCostTypeDetail);
					}
					byCostTypeDetail.setTotal(byCostTypeDetail.getTotal() + mt.getAmount());
					byCostTypeDetail.setCount(byCostTypeDetail.getCount() + 1l);
					if (comments) {
						addCostTypeDetailComment(mt,byCostTypeDetail);
					}
				}
				if (byBankAccount && bankAccountId != null) {
					// update bank accounts per payment method:
					if (byPaymentMethodByBankAccountMap.containsKey(mt.getMethod())) {
						byBankAccountMap = byPaymentMethodByBankAccountMap.get(mt.getMethod());
					} else {
						byBankAccountMap = initBankAccountMap();
						byPaymentMethodByBankAccountMap.put(mt.getMethod(), byBankAccountMap);
					}
					if (byBankAccountMap.containsKey(bankAccount)) {
						byBankAccountDetail = byBankAccountMap.get(bankAccount);
					} else {
						byBankAccountDetail = new MoneyTransferByBankAccountSummaryDetailVO();
						byBankAccountDetail.setTotal(0.0f);
						byBankAccountDetail.setCount(0l);
						byBankAccountDetail.setBankAccount(bankAccount);
						byBankAccountDetail.setId(bankAccountId);
						byBankAccountMap.put(bankAccount, byBankAccountDetail);
					}
					byBankAccountDetail.setTotal(byBankAccountDetail.getTotal() + mt.getAmount());
					byBankAccountDetail.setCount(byBankAccountDetail.getCount() + 1l);
				}
			}
			if (byBankAccount && bankAccountId != null) {
				// update totals by bank account:
				if (totalsByBankAccountMap.containsKey(bankAccount)) {
					byBankAccountDetail = totalsByBankAccountMap.get(bankAccount);
				} else {
					byBankAccountDetail = new MoneyTransferByBankAccountSummaryDetailVO();
					byBankAccountDetail.setTotal(0.0f);
					byBankAccountDetail.setCount(0l);
					byBankAccountDetail.setBankAccount(bankAccount);
					byBankAccountDetail.setId(bankAccountId);
					totalsByBankAccountMap.put(bankAccount, byBankAccountDetail);
				}
				byBankAccountDetail.setTotal(byBankAccountDetail.getTotal() + mt.getAmount());
				byBankAccountDetail.setCount(byBankAccountDetail.getCount() + 1l);
				if (byPaymentMethod) {
					// update payment methods per bank account:
					if (byBankAccountByPaymentMethodMap.containsKey(bankAccountId)) {
						byPaymentMethodMap = byBankAccountByPaymentMethodMap.get(bankAccountId);
					} else {
						byPaymentMethodMap = initPaymentMethodMap();
						byBankAccountByPaymentMethodMap.put(bankAccountId, byPaymentMethodMap);
					}
					byPaymentMethodDetail = byPaymentMethodMap.get(mt.getMethod());
					byPaymentMethodDetail.setTotal(byPaymentMethodDetail.getTotal() + mt.getAmount());
					byPaymentMethodDetail.setCount(byPaymentMethodDetail.getCount() + 1l);
				}
				if (byCostType) {
					// update cost types per bank account:
					if (byBankAccountByCostTypeMap.containsKey(bankAccountId)) {
						byCostTypeMap = byBankAccountByCostTypeMap.get(bankAccountId);
					} else {
						byCostTypeMap = initCostTypeMap(costTypes, comments);
						byBankAccountByCostTypeMap.put(bankAccountId, byCostTypeMap);
					}
					if (byCostTypeMap.containsKey(costType)) {
						byCostTypeDetail = byCostTypeMap.get(costType);
					} else {
						byCostTypeDetail = new MoneyTransferByCostTypeSummaryDetailVO();
						byCostTypeDetail.setTotal(0.0f);
						byCostTypeDetail.setCount(0l);
						byCostTypeDetail.setCostType(costType);
						byCostTypeDetail.setDecrypted(comments);
						byCostTypeMap.put(costType, byCostTypeDetail);
					}
					byCostTypeDetail.setTotal(byCostTypeDetail.getTotal() + mt.getAmount());
					byCostTypeDetail.setCount(byCostTypeDetail.getCount() + 1l);
					if (comments) {
						addCostTypeDetailComment(mt,byCostTypeDetail);
					}
				}
			}
			// update totals:
			summary.setTotal(summary.getTotal() + mt.getAmount());
			summary.setCount(summary.getCount() + 1l);
			if (mt.isPaid()) {
				summary.setPaidCount(summary.getPaidCount() + 1l);
			}
		}
		Iterator<Entry<PaymentMethod, MoneyTransferByPaymentMethodSummaryDetailVO>> paymentMethodsIt;
		Iterator<Entry<String, MoneyTransferByCostTypeSummaryDetailVO>> costTypesIt;
		Iterator<Entry<BankAccountOutVO, MoneyTransferByBankAccountSummaryDetailVO>> bankAccountsIt;
		summary.getTotalsByCostTypes().clear();
		costTypesIt = totalsByCostTypeMap.entrySet().iterator();
		while (costTypesIt.hasNext()) {
			Entry<String, MoneyTransferByCostTypeSummaryDetailVO> totalsMap = costTypesIt.next();
			if (byCostTypeByPaymentMethodMap.containsKey(totalsMap.getKey())) {
				paymentMethodsIt = byCostTypeByPaymentMethodMap.get(totalsMap.getKey()).entrySet().iterator();
				while (paymentMethodsIt.hasNext()) {
					totalsMap.getValue().getByPaymentMethods().add(paymentMethodsIt.next().getValue());
				}
			}
			if (byCostTypeByBankAccountMap.containsKey(totalsMap.getKey())) {
				bankAccountsIt = byCostTypeByBankAccountMap.get(totalsMap.getKey()).entrySet().iterator();
				while (bankAccountsIt.hasNext()) {
					totalsMap.getValue().getByBankAccounts().add(bankAccountsIt.next().getValue());
				}
			}
			summary.getTotalsByCostTypes().add(totalsMap.getValue());
		}
		summary.getTotalsByPaymentMethods().clear();
		paymentMethodsIt = totalsByPaymentMethodMap.entrySet().iterator();
		while (paymentMethodsIt.hasNext()) {
			Entry<PaymentMethod, MoneyTransferByPaymentMethodSummaryDetailVO> totalsMap = paymentMethodsIt.next();
			if (byPaymentMethodByCostTypeMap.containsKey(totalsMap.getKey())) {
				costTypesIt = byPaymentMethodByCostTypeMap.get(totalsMap.getKey()).entrySet().iterator();
				while (costTypesIt.hasNext()) {
					totalsMap.getValue().getByCostTypes().add(costTypesIt.next().getValue());
				}
			}
			if (byPaymentMethodByBankAccountMap.containsKey(totalsMap.getKey())) {
				bankAccountsIt = byPaymentMethodByBankAccountMap.get(totalsMap.getKey()).entrySet().iterator();
				while (bankAccountsIt.hasNext()) {
					totalsMap.getValue().getByBankAccounts().add(bankAccountsIt.next().getValue());
				}
			}
			summary.getTotalsByPaymentMethods().add(totalsMap.getValue());
		}
		summary.getTotalsByBankAccounts().clear();
		bankAccountsIt = totalsByBankAccountMap.entrySet().iterator();
		while (bankAccountsIt.hasNext()) {
			Entry<BankAccountOutVO, MoneyTransferByBankAccountSummaryDetailVO> totalsMap = bankAccountsIt.next();
			if (byBankAccountByCostTypeMap.containsKey(totalsMap.getKey().getId())) {
				costTypesIt = byBankAccountByCostTypeMap.get(totalsMap.getKey().getId()).entrySet().iterator();
				while (costTypesIt.hasNext()) {
					totalsMap.getValue().getByCostTypes().add(costTypesIt.next().getValue());
				}
			}
			if (byBankAccountByPaymentMethodMap.containsKey(totalsMap.getKey().getId())) {
				paymentMethodsIt = byBankAccountByPaymentMethodMap.get(totalsMap.getKey().getId()).entrySet().iterator();
				while (paymentMethodsIt.hasNext()) {
					totalsMap.getValue().getByPaymentMethods().add(paymentMethodsIt.next().getValue());
				}
			}
			summary.getTotalsByBankAccounts().add(totalsMap.getValue());
		}
	}

	public static void populateShiftDurationSummary(boolean trialBreakDown, ShiftDurationSummaryVO summary, DutyRosterTurnDao dutyRosterTurnDao, HolidayDao holidayDao) {
		ArrayList<ShiftDurationSummaryDetailVO> assigned = new ArrayList<ShiftDurationSummaryDetailVO>();
		ShiftDurationSummaryDetailVO notAssigned = new ShiftDurationSummaryDetailVO();
		HashMap<Long, ShiftDurationSummaryDetailVO> durationSummaryDetailsMap = new HashMap<Long, ShiftDurationSummaryDetailVO>();
		summary.setIntervalDuration((new DateInterval(summary.getStart(), summary.getStop())).getDuration());
		TrialOutVO trial;
		StaffOutVO staff;
		Iterator<DutyRosterTurn> it;
		if (trialBreakDown) {
			staff = summary.getStaff();
			it = dutyRosterTurnDao.findByStaffTrialCalendarInterval(staff == null ? null : staff.getId(), null, summary.getCalendar(),
					CommonUtil.dateToTimestamp(summary.getStart()), CommonUtil.dateToTimestamp(summary.getStop())).iterator();
			summary.setTrial(null);
		} else {
			trial = summary.getTrial();
			it = dutyRosterTurnDao.findByStaffTrialCalendarInterval(null, trial == null ? null : trial.getId(), summary.getCalendar(),
					CommonUtil.dateToTimestamp(summary.getStart()), CommonUtil.dateToTimestamp(summary.getStop())).iterator();
			summary.setStaff(null);
		}
		summary.setExtraShiftCount(0);
		summary.setNightShiftCount(0);
		summary.setShiftCount(0);
		summary.setTotalHolidayDuration(0);
		summary.setTotalNightDuration(0);
		summary.setTotalDuration(0);
		summary.setTotalWeightedDuration(0);
		int extraShiftIncrement;
		int nightShiftIncrement;
		Long key;
		DutyRosterTurnOutVO dutyRosterTurn;
		boolean fullShifts = Settings.getBoolean(SettingCodes.SHIFT_SUMMARY_FULL_SHIFTS, Bundle.SETTINGS, DefaultSettings.SHIFT_SUMMARY_FULL_SHIFTS);
		while (it.hasNext()) {
			dutyRosterTurn = dutyRosterTurnDao.toDutyRosterTurnOutVO(it.next());
			if (trialBreakDown) {
				trial = dutyRosterTurn.getTrial();
				key = trial == null ? null : trial.getId();
				staff = null;
			} else {
				staff = dutyRosterTurn.getStaff();
				key = staff == null ? null : staff.getId();
				trial = null;
			}
			ShiftDurationSummaryDetailVO durationSummaryDetail;
			if (key != null) {
				if (durationSummaryDetailsMap.containsKey(key)) {
					durationSummaryDetail = durationSummaryDetailsMap.get(key);
				} else {
					durationSummaryDetail = new ShiftDurationSummaryDetailVO();
					durationSummaryDetailsMap.put(key, durationSummaryDetail);
					assigned.add(durationSummaryDetail);
					if (trialBreakDown) {
						durationSummaryDetail.setStaff(null);
						durationSummaryDetail.setTrial(trial);
					} else {
						durationSummaryDetail.setStaff(staff);
						durationSummaryDetail.setTrial(null);
					}
					durationSummaryDetail.setExtraShiftCount(0);
					durationSummaryDetail.setNightShiftCount(0);
					durationSummaryDetail.setShiftCount(0);
					durationSummaryDetail.setTotalHolidayDuration(0);
					durationSummaryDetail.setTotalNightDuration(0);
					durationSummaryDetail.setTotalDuration(0);
					durationSummaryDetail.setTotalWeightedDuration(0);
				}
			} else {
				durationSummaryDetail = notAssigned;
			}
			if (!fullShifts) {
				Date start = dutyRosterTurn.getStart();
				Date stop = dutyRosterTurn.getStop();
				boolean exceeds = false;
				if (summary.getStart() != null && dutyRosterTurn.getStart().before(summary.getStart())) {
					start = summary.getStart();
					exceeds = true;
				}
				if (summary.getStop() != null && dutyRosterTurn.getStop().after(summary.getStop())) {
					stop = summary.getStop();
					exceeds = true;
				}
				if (exceeds) {
					ShiftDuration shiftDuration = new ShiftDuration();
					try {
						shiftDuration.add(start, stop, holidayDao);
						shiftDuration.updateDutyRosterTurn(dutyRosterTurn);
					} catch (Exception e) {
					}
				}
			}
			extraShiftIncrement = (dutyRosterTurn.isExtraShift() ? 1 : 0);
			nightShiftIncrement = (dutyRosterTurn.isNightShift() ? 1 : 0);
			durationSummaryDetail.setExtraShiftCount(durationSummaryDetail.getExtraShiftCount() + extraShiftIncrement);
			durationSummaryDetail.setNightShiftCount(durationSummaryDetail.getNightShiftCount() + nightShiftIncrement);
			durationSummaryDetail.setShiftCount(durationSummaryDetail.getShiftCount() + 1);
			durationSummaryDetail.setTotalHolidayDuration(durationSummaryDetail.getTotalHolidayDuration() + dutyRosterTurn.getHolidayDuration());
			durationSummaryDetail.setTotalNightDuration(durationSummaryDetail.getTotalNightDuration() + dutyRosterTurn.getNightDuration());
			durationSummaryDetail.setTotalDuration(durationSummaryDetail.getTotalDuration() + dutyRosterTurn.getTotalDuration());
			durationSummaryDetail.setTotalWeightedDuration(durationSummaryDetail.getTotalWeightedDuration() + dutyRosterTurn.getWeightedDuration());
			summary.setExtraShiftCount(summary.getExtraShiftCount() + extraShiftIncrement);
			summary.setNightShiftCount(summary.getNightShiftCount() + nightShiftIncrement);
			summary.setShiftCount(summary.getShiftCount() + 1);
			summary.setTotalHolidayDuration(summary.getTotalHolidayDuration() + dutyRosterTurn.getHolidayDuration());
			summary.setTotalNightDuration(summary.getTotalNightDuration() + dutyRosterTurn.getNightDuration());
			summary.setTotalDuration(summary.getTotalDuration() + dutyRosterTurn.getTotalDuration());
			summary.setTotalWeightedDuration(summary.getTotalWeightedDuration() + dutyRosterTurn.getWeightedDuration());
		}
		summary.setAssigneds(assigned);
		summary.setNotAssigned(notAssigned);
	}

	public static void removeEcrfField(ECRFField ecrfField, boolean deleteCascade, boolean checkProbandLocked, Timestamp now, User user,
			boolean logTrial,
			boolean logProband,
			// ProbandDao probandDao,
			ECRFFieldValueDao ecrfFieldValueDao,
			ECRFFieldStatusEntryDao ecrfFieldStatusEntryDao,
			InputFieldValueDao inputFieldValueDao,
			JournalEntryDao journalEntryDao,
			ECRFFieldDao ecrfFieldDao,
			NotificationDao notificationDao, NotificationRecipientDao notificationRecipientDao) throws Exception {
		if (deleteCascade) {
			Iterator<ECRFFieldValue> fieldValuesIt = ecrfField.getFieldValues().iterator();
			while (fieldValuesIt.hasNext()) {
				ECRFFieldValue fieldValue = fieldValuesIt.next();
				ProbandListEntry listEntry = fieldValue.getListEntry();
				if (checkProbandLocked) {
					checkProbandLocked(listEntry.getProband());
				}
				listEntry.removeEcrfValues(fieldValue);
				removeEcrfFieldValue(fieldValue, now, user, LOG_ECRF_FIELD_VALUE_TRIAL && logTrial, LOG_ECRF_FIELD_VALUE_PROBAND && logProband, inputFieldValueDao,
						ecrfFieldValueDao, journalEntryDao); // logTrial,
				// logProband
			}
			ecrfField.getFieldValues().clear();
			Iterator<ECRFFieldStatusEntry> fieldStatusEntriesIt = ecrfField.getEcrfFieldStatusEntries().iterator();
			while (fieldStatusEntriesIt.hasNext()) {
				ECRFFieldStatusEntry fieldStatus = fieldStatusEntriesIt.next();
				ProbandListEntry listEntry = fieldStatus.getListEntry();
				if (checkProbandLocked) {
					checkProbandLocked(listEntry.getProband());
				}
				listEntry.removeEcrfFieldStatusEntries(fieldStatus);
				removeEcrfFieldStatusEntry(fieldStatus, now, user, LOG_ECRF_FIELD_STATUS_ENTRY_TRIAL && logTrial, LOG_ECRF_FIELD_STATUS_ENTRY_PROBAND && logProband,
						ecrfFieldStatusEntryDao, journalEntryDao, notificationDao, notificationRecipientDao);
			}
			ecrfField.getEcrfFieldStatusEntries().clear();
		}
		ecrfField.setTrial(null);
		ecrfField.setField(null);
		ecrfField.setEcrf(null);
		ecrfFieldDao.remove(ecrfField);
	}

	public static ECRFFieldStatusEntryOutVO removeEcrfFieldStatusEntry(ECRFFieldStatusEntry fieldStatus, Timestamp now, User modifiedUser,
			boolean logTrial,
			boolean logProband,
			ECRFFieldStatusEntryDao ecrfFieldStatusEntryDao,
			JournalEntryDao journalEntryDao,
			NotificationDao notificationDao,
			NotificationRecipientDao notificationRecipientDao
			) throws Exception {
		ProbandListEntry listEntry = fieldStatus.getListEntry();
		ECRFFieldStatusEntryOutVO fieldStatusVO = null;
		if (logProband || logTrial) {
			fieldStatusVO = ecrfFieldStatusEntryDao.toECRFFieldStatusEntryOutVO(fieldStatus);
		}
		removeNotifications(fieldStatus.getNotifications(), notificationDao, notificationRecipientDao);
		fieldStatus.setListEntry(null);
		fieldStatus.setEcrfField(null);
		ecrfFieldStatusEntryDao.remove(fieldStatus);
		if (logProband) {
			// logSystemMessage(listEntry.getProband(), fieldStatusVO.getListEntry().getProband(), now, modifiedUser, SystemMessageCodes.ECRF_FIELD_STATUS_ENTRY_DELETED,
			// fieldStatusVO,
			// null, journalEntryDao);
			logSystemMessage(listEntry.getProband(), fieldStatusVO.getListEntry().getTrial(), now, modifiedUser, SystemMessageCodes.ECRF_FIELD_STATUS_ENTRY_DELETED, fieldStatusVO,
					null, journalEntryDao);
		}
		if (logTrial) {
			// logSystemMessage(listEntry.getTrial(), fieldStatusVO.getListEntry().getTrial(), now, modifiedUser, SystemMessageCodes.ECRF_FIELD_STATUS_ENTRY_DELETED, fieldStatusVO,
			// null,
			// journalEntryDao);
			logSystemMessage(listEntry.getTrial(), fieldStatusVO.getListEntry().getProband(), now, modifiedUser, SystemMessageCodes.ECRF_FIELD_STATUS_ENTRY_DELETED, fieldStatusVO,
					null,
					journalEntryDao);
		}
		return fieldStatusVO;
	}

	public static ECRFFieldValueOutVO removeEcrfFieldValue(ECRFFieldValue fieldValue, Timestamp now, User modifiedUser,
			boolean logTrial,
			boolean logProband,
			InputFieldValueDao inputFieldValueDao,
			ECRFFieldValueDao ecrfFieldValueDao,
			JournalEntryDao journalEntryDao) throws Exception {
		InputFieldValue value = fieldValue.getValue();
		ProbandListEntry listEntry = fieldValue.getListEntry();
		ECRFFieldValueOutVO fieldValueVO = null;
		if (logProband || logTrial) {
			fieldValueVO = ecrfFieldValueDao.toECRFFieldValueOutVO(fieldValue);
		}
		fieldValue.setListEntry(null);
		fieldValue.setEcrfField(null);
		ecrfFieldValueDao.remove(fieldValue);
		fieldValue.setValue(null);
		value.getSelectionValues().clear();
		inputFieldValueDao.remove(value);
		if (logProband) {
			// logSystemMessage(listEntry.getProband(), fieldValueVO.getListEntry().getProband(), now, modifiedUser, SystemMessageCodes.ECRF_FIELD_VALUE_DELETED, fieldValueVO,
			// null, journalEntryDao);
			logSystemMessage(listEntry.getProband(), fieldValueVO.getListEntry().getTrial(), now, modifiedUser, SystemMessageCodes.ECRF_FIELD_VALUE_DELETED, fieldValueVO,
					null, journalEntryDao);
		}
		if (logTrial) {
			// logSystemMessage(listEntry.getTrial(), fieldValueVO.getListEntry().getTrial(), now, modifiedUser, SystemMessageCodes.ECRF_FIELD_VALUE_DELETED, fieldValueVO, null,
			// journalEntryDao);
			logSystemMessage(listEntry.getTrial(), fieldValueVO.getListEntry().getProband(), now, modifiedUser, SystemMessageCodes.ECRF_FIELD_VALUE_DELETED, fieldValueVO, null,
					journalEntryDao);
		}
		return fieldValueVO;
	}

	public static void removeEcrfStatusEntry(ECRFStatusEntry ecrfStatusEntry, boolean deleteCascade,
			SignatureDao signatureDao,
			ECRFStatusEntryDao ecrfStatusEntryDao,
			NotificationDao notificationDao,
			NotificationRecipientDao notificationRecipientDao) throws Exception {
		if (deleteCascade) {
			Iterator<Signature> signaturesIt = ecrfStatusEntry.getSignatures().iterator();
			while (signaturesIt.hasNext()) {
				Signature signature = signaturesIt.next();
				signature.setEcrfStatusEntry(null);
				signatureDao.remove(signature);
			}
			ecrfStatusEntry.getSignatures().clear();
			removeNotifications(ecrfStatusEntry.getNotifications(), notificationDao, notificationRecipientDao);
		}
		ecrfStatusEntry.setEcrf(null);
		ecrfStatusEntry.setListEntry(null);
		ecrfStatusEntryDao.remove(ecrfStatusEntry);
	}

	public static void removeInquiry(Inquiry inquiry, boolean deleteCascade, boolean checkProbandLocked, Timestamp now, User user,
			boolean logTrial,
			boolean logProband,
			// ProbandDao probandDao,
			InquiryValueDao inquiryValueDao,
			InputFieldValueDao inputFieldValueDao,
			JournalEntryDao journalEntryDao,
			InquiryDao inquiryDao) throws Exception {
		if (deleteCascade) {
			Iterator<InquiryValue> inquiryValuesIt = inquiry.getInquiryValues().iterator();
			while (inquiryValuesIt.hasNext()) {
				InquiryValue inquiryValue = inquiryValuesIt.next();
				Proband proband = inquiryValue.getProband();
				if (checkProbandLocked) {
					checkProbandLocked(proband);
				}
				proband.removeInquiryValues(inquiryValue);
				removeInquiryValue(inquiryValue, now, user, LOG_INQUIRY_VALUE_TRIAL && logTrial, LOG_INQUIRY_VALUE_PROBAND && logProband, inputFieldValueDao, inquiryValueDao,
						journalEntryDao);
			}
			inquiry.getInquiryValues().clear();
		}
		inquiry.setTrial(null);
		inquiry.setField(null);
		inquiryDao.remove(inquiry);
	}

	public static void removeInquiryValue(InquiryValue inquiryValue, Timestamp now, User modifiedUser,
			boolean logTrial,
			boolean logProband,
			InputFieldValueDao inputFieldValueDao,
			InquiryValueDao inquiryValueDao,
			JournalEntryDao journalEntryDao) throws Exception {
		InputFieldValue value = inquiryValue.getValue();
		Proband proband = inquiryValue.getProband();
		Inquiry inquiry = inquiryValue.getInquiry();
		InquiryValueOutVO inquiryValueVO = null;
		if (logProband || logTrial) {
			inquiryValueVO = inquiryValueDao.toInquiryValueOutVO(inquiryValue);
		}
		inquiryValue.setProband(null);
		inquiryValue.setInquiry(null);
		inquiryValueDao.remove(inquiryValue);
		inquiryValue.setValue(null);
		value.getSelectionValues().clear();
		inputFieldValueDao.remove(value);
		if (logProband) {
			// logSystemMessage(proband, inquiryValueVO.getProband(), now, modifiedUser, SystemMessageCodes.INQUIRY_VALUE_DELETED, inquiryValueVO, null, journalEntryDao);
			logSystemMessage(proband, inquiryValueVO.getInquiry().getTrial(), now, modifiedUser, SystemMessageCodes.INQUIRY_VALUE_DELETED, inquiryValueVO, null, journalEntryDao);
		}
		if (logTrial) {
			// logSystemMessage(inquiry.getTrial(), inquiryValueVO.getInquiry().getTrial(), now, modifiedUser, SystemMessageCodes.INQUIRY_VALUE_DELETED, inquiryValueVO, null,
			// journalEntryDao);
			logSystemMessage(inquiry.getTrial(), inquiryValueVO.getProband(), now, modifiedUser, SystemMessageCodes.INQUIRY_VALUE_DELETED, inquiryValueVO, null,
					journalEntryDao);
		}
	}

	public static void removeNotifications(Collection<Notification> notificationsToRemove, NotificationDao notificationDao, NotificationRecipientDao notificationRecipientDao)
			throws Exception {
		Iterator<Notification> notificationsToRemoveIt = notificationsToRemove.iterator();
		while (notificationsToRemoveIt.hasNext()) {
			Notification notification = notificationsToRemoveIt.next();
			MaintenanceScheduleItem maintenanceScheduleItem = notification.getMaintenanceScheduleItem();
			InventoryStatusEntry inventoryStatusEntry = notification.getInventoryStatusEntry();
			InventoryBooking inventoryBooking = notification.getInventoryBooking();
			StaffStatusEntry staffStatusEntry = notification.getStaffStatusEntry();
			DutyRosterTurn dutyRosterTurn = notification.getDutyRosterTurn();
			ProbandStatusEntry probandStatusEntry = notification.getProbandStatusEntry();
			VisitScheduleItem visitScheduleItem = notification.getVisitScheduleItem();
			Course course = notification.getCourse();
			CourseParticipationStatusEntry courseParticipationStatusEntry = notification.getCourseParticipationStatusEntry();
			TimelineEvent timelineEvent = notification.getTimelineEvent();
			Proband proband = notification.getProband();
			Password password = notification.getPassword();
			Trial trial = notification.getTrial();
			ECRFStatusEntry ecrfStatusEntry = notification.getEcrfStatusEntry();
			ECRFFieldStatusEntry ecrfFieldStatusEntry = notification.getEcrfFieldStatusEntry();
			Staff staff = notification.getStaff();
			User user = notification.getUser();
			if (maintenanceScheduleItem != null) {
				Collection<Notification> notifications = maintenanceScheduleItem.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setMaintenanceScheduleItem(null);
			}
			if (inventoryStatusEntry != null) {
				Collection<Notification> notifications = inventoryStatusEntry.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setInventoryStatusEntry(null);
			}
			if (inventoryBooking != null) {
				Collection<Notification> notifications = inventoryBooking.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setInventoryBooking(null);
			}
			if (staffStatusEntry != null) {
				Collection<Notification> notifications = staffStatusEntry.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setStaffStatusEntry(null);
			}
			if (dutyRosterTurn != null) {
				Collection<Notification> notifications = dutyRosterTurn.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setDutyRosterTurn(null);
			}
			if (probandStatusEntry != null) {
				Collection<Notification> notifications = probandStatusEntry.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setProbandStatusEntry(null);
			}
			if (visitScheduleItem != null) {
				Collection<Notification> notifications = visitScheduleItem.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setVisitScheduleItem(null);
			}
			if (course != null) {
				Collection<Notification> notifications = course.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setCourse(null);
			}
			if (courseParticipationStatusEntry != null) {
				Collection<Notification> notifications = courseParticipationStatusEntry.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setCourseParticipationStatusEntry(null);
			}
			if (timelineEvent != null) {
				Collection<Notification> notifications = timelineEvent.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setTimelineEvent(null);
			}
			if (proband != null) {
				Collection<Notification> notifications = proband.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setProband(null);
			}
			if (password != null) {
				Collection<Notification> notifications = password.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setPassword(null);
			}
			if (trial != null) {
				Collection<Notification> notifications = trial.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setTrial(null);
			}
			if (ecrfStatusEntry != null) {
				Collection<Notification> notifications = ecrfStatusEntry.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setEcrfStatusEntry(null);
			}
			if (ecrfFieldStatusEntry != null) {
				Collection<Notification> notifications = ecrfFieldStatusEntry.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setEcrfFieldStatusEntry(null);
			}
			if (staff != null) {
				Collection<Notification> notifications = staff.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setStaff(null);
			}
			if (user != null) {
				Collection<Notification> notifications = user.getNotifications();
				if (!notificationsToRemove.equals(notifications)) {
					notifications.remove(notification);
				}
				notification.setUser(null);
			}
			Iterator<NotificationRecipient> recipientsIt = notification.getRecipients().iterator();
			while (recipientsIt.hasNext()) {
				NotificationRecipient recipient = recipientsIt.next();
				// recipient.getStaff().removeNotificationReceipts(recipient);
				recipient.setStaff(null);
				recipient.setNotification(null);
				notificationRecipientDao.remove(recipient);
			}
			notification.getRecipients().clear();
			notificationDao.remove(notification);
		}
		notificationsToRemove.clear();
	}

	public static void removeProband(Proband proband, ProbandOutVO result, boolean deleteCascade,
			User user,
			Timestamp now,
			ProbandDao probandDao,
			// TrialDao trialDao,
			ProbandContactParticularsDao probandContactParticularsDao,
			AnimalContactParticularsDao animalContactParticularsDao,
			JournalEntryDao journalEntryDao,
			NotificationDao notificationDao,
			NotificationRecipientDao notificationRecipientDao,
			ProbandTagValueDao probandTagValueDao,
			ProbandContactDetailValueDao probandContactDetailValueDao,
			ProbandAddressDao probandAddressDao,
			ProbandStatusEntryDao probandStatusEntryDao,
			DiagnosisDao diagnosisDao,
			ProcedureDao procedureDao,
			MedicationDao medicationDao,
			InventoryBookingDao inventoryBookingDao,
			MoneyTransferDao moneyTransferDao,
			BankAccountDao bankAccountDao,
			ProbandListStatusEntryDao probandListStatusEntryDao,
			ProbandListEntryDao probandListEntryDao,
			ProbandListEntryTagValueDao probandListEntryTagValueDao,
			InputFieldValueDao inputFieldValueDao,
			InquiryValueDao inquiryValueDao,
			ECRFFieldValueDao ecrfFieldValueDao,
			ECRFFieldStatusEntryDao ecrfFieldStatusEntryDao,
			SignatureDao signatureDao,
			ECRFStatusEntryDao ecrfStatusEntryDao,
			FileDao fileDao) throws Exception {
		if (deleteCascade) {
			boolean checkTrialLocked = Settings.getBoolean(SettingCodes.REMOVE_PROBAND_CHECK_TRIAL_LOCKED, Bundle.SETTINGS,
					DefaultSettings.REMOVE_PROBAND_CHECK_TRIAL_LOCKED);
			Iterator<ProbandTagValue> tagValuesIt = proband.getTagValues().iterator();
			while (tagValuesIt.hasNext()) {
				ProbandTagValue tagValue = tagValuesIt.next();
				tagValue.setProband(null);
				probandTagValueDao.remove(tagValue);
			}
			proband.getTagValues().clear();
			Iterator<ProbandContactDetailValue> contactDetailValuesIt = proband.getContactDetailValues().iterator();
			while (contactDetailValuesIt.hasNext()) {
				ProbandContactDetailValue contactDetailValue = contactDetailValuesIt.next();
				contactDetailValue.setProband(null);
				probandContactDetailValueDao.remove(contactDetailValue);
			}
			proband.getContactDetailValues().clear();
			Iterator<ProbandAddress> addressesIt = proband.getAddresses().iterator();
			while (addressesIt.hasNext()) {
				ProbandAddress address = addressesIt.next();
				address.setProband(null);
				probandAddressDao.remove(address);
			}
			proband.getAddresses().clear();
			Iterator<ProbandStatusEntry> statusEntriesIt = proband.getStatusEntries().iterator();
			while (statusEntriesIt.hasNext()) {
				ProbandStatusEntry statusEntry = statusEntriesIt.next();
				statusEntry.setProband(null);
				removeNotifications(statusEntry.getNotifications(), notificationDao, notificationRecipientDao);
				probandStatusEntryDao.remove(statusEntry);
			}
			proband.getStatusEntries().clear();
			Iterator<Medication> medicationsIt = proband.getMedications().iterator();
			while (medicationsIt.hasNext()) {
				Medication medication = medicationsIt.next();
				medication.setProband(null);
				Diagnosis diagnosis = medication.getDiagnosis();
				if (diagnosis != null) {
					diagnosis.removeMedications(medication);
					medication.setDiagnosis(null);
				}
				Procedure procedure = medication.getProcedure();
				if (procedure != null) {
					procedure.removeMedications(medication);
					medication.setProcedure(null);
				}
				medicationDao.remove(medication);
			}
			proband.getMedications().clear();
			Iterator<Diagnosis> diagnosesIt = proband.getDiagnoses().iterator();
			while (diagnosesIt.hasNext()) {
				Diagnosis diagnosis = diagnosesIt.next();
				AlphaId alphaId = diagnosis.getCode();
				alphaId.removeDiagnoses(diagnosis);
				diagnosis.setCode(null);
				diagnosis.setProband(null);
				diagnosisDao.remove(diagnosis);
			}
			proband.getDiagnoses().clear();
			Iterator<Procedure> proceduresIt = proband.getProcedures().iterator();
			while (proceduresIt.hasNext()) {
				Procedure procedure = proceduresIt.next();
				OpsCode opsCode = procedure.getCode();
				opsCode.removeProcedures(procedure);
				procedure.setCode(null);
				procedure.setProband(null);
				procedureDao.remove(procedure);
			}
			proband.getProcedures().clear();
			Iterator<InventoryBooking> bookingsIt = proband.getInventoryBookings().iterator();
			while (bookingsIt.hasNext()) {
				InventoryBooking booking = bookingsIt.next();
				InventoryBookingOutVO original = inventoryBookingDao.toInventoryBookingOutVO(booking);
				booking.setProband(null);
				CoreUtil.modifyVersion(booking, booking.getVersion(), now, user == null ? proband.getModifiedUser() : user); // if deleted by job...
				inventoryBookingDao.update(booking);
				InventoryBookingOutVO bookingVO = inventoryBookingDao.toInventoryBookingOutVO(booking);
				logSystemMessage(booking.getInventory(), result, now, user, SystemMessageCodes.PROBAND_DELETED_BOOKING_UPDATED, bookingVO, original, journalEntryDao);
			}
			proband.getInventoryBookings().clear();
			Iterator<MoneyTransfer> moneyTransfersIt = proband.getMoneyTransfers().iterator();
			while (moneyTransfersIt.hasNext()) {
				MoneyTransfer moneyTransfer = moneyTransfersIt.next();
				Trial trial = moneyTransfer.getTrial();
				if (trial != null) {
					if (checkTrialLocked) {
						checkTrialLocked(trial);
					}
					MoneyTransferOutVO moneyTransferVO = moneyTransferDao.toMoneyTransferOutVO(moneyTransfer);
					logSystemMessage(trial, result, now, user, SystemMessageCodes.PROBAND_DELETED_MONEY_TRANSFER_DELETED, moneyTransferVO, null, journalEntryDao);
					trial.removePayoffs(moneyTransfer);
					moneyTransfer.setTrial(null);
				}
				moneyTransfer.setProband(null);
				BankAccount bankAccount = moneyTransfer.getBankAccount();
				if (bankAccount != null) {
					bankAccount.removeMoneyTransfers(moneyTransfer);
					moneyTransfer.setBankAccount(null);
				}
				moneyTransferDao.remove(moneyTransfer);
			}
			proband.getMoneyTransfers().clear();
			Iterator<BankAccount> bankAccountIt = proband.getBankAccounts().iterator();
			while (bankAccountIt.hasNext()) {
				BankAccount bankAccount = bankAccountIt.next();
				bankAccount.setProband(null);
				bankAccountDao.remove(bankAccount);
			}
			proband.getBankAccounts().clear();
			Iterator<ProbandListEntry> trialParticipationsIt = proband.getTrialParticipations().iterator();
			while (trialParticipationsIt.hasNext()) {
				ProbandListEntry probandListEntry = trialParticipationsIt.next();
				Trial trial = probandListEntry.getTrial();
				if (checkTrialLocked) {
					checkTrialLocked(trial);
				}
				trial.removeProbandListEntries(probandListEntry);
				ProbandGroup group = probandListEntry.getGroup();
				if (group != null) {
					group.removeProbandListEntries(probandListEntry);
				}
				removeProbandListEntry(probandListEntry, true, now, user, true, false, probandListStatusEntryDao, probandListEntryTagValueDao, ecrfFieldValueDao,
						ecrfFieldStatusEntryDao, signatureDao,
						ecrfStatusEntryDao, inputFieldValueDao, journalEntryDao,
						probandListEntryDao, notificationDao, notificationRecipientDao);
			}
			proband.getTrialParticipations().clear();
			Iterator<InquiryValue> inquiryValuesIt = proband.getInquiryValues().iterator();
			while (inquiryValuesIt.hasNext()) {
				InquiryValue inquiryValue = inquiryValuesIt.next();
				Inquiry inquiry = inquiryValue.getInquiry();
				if (checkTrialLocked) {
					checkTrialLocked(inquiry.getTrial());
				}
				inquiry.removeInquiryValues(inquiryValue);
				removeInquiryValue(inquiryValue, now, user, LOG_INQUIRY_VALUE_TRIAL, false, inputFieldValueDao, inquiryValueDao, journalEntryDao);
			}
			proband.getInquiryValues().clear();
			Iterator<JournalEntry> journalEntriesIt = proband.getJournalEntries().iterator();
			while (journalEntriesIt.hasNext()) {
				JournalEntry journalEntry = journalEntriesIt.next();
				journalEntry.setProband(null);
				journalEntryDao.remove(journalEntry);
			}
			proband.getJournalEntries().clear();
			Iterator<File> filesIt = proband.getFiles().iterator();
			while (filesIt.hasNext()) {
				File file = filesIt.next();
				file.setProband(null);
				fileDao.remove(file);
			}
			proband.getFiles().clear();
			removeNotifications(proband.getNotifications(), notificationDao, notificationRecipientDao);
		}
		Iterator<Proband> childrenIt = proband.getChildren().iterator();
		while (childrenIt.hasNext()) {
			Proband child = childrenIt.next();
			child.removeParents(proband); // .setRenewal(null);
			CoreUtil.modifyVersion(child, child.getVersion(), now, user);
			probandDao.update(child);
			// CourseOutVO precedingCourseVO = courseDao.toCourseOutVO(precedingCourse);
			logSystemMessage(child, result, now, user, SystemMessageCodes.PROBAND_DELETED_PARENT_REMOVED, result, null, journalEntryDao);
		}
		proband.getChildren().clear();
		Iterator<Proband> parentsIt = proband.getParents().iterator();
		while (parentsIt.hasNext()) {
			Proband parent = parentsIt.next();
			parent.removeChildren(proband); // .setRenewal(null);
			probandDao.update(parent);
		}
		proband.getParents().clear();
		Staff physician = proband.getPhysician();
		if (physician != null) {
			logSystemMessage(physician, result, now, user, SystemMessageCodes.PROBAND_DELETED_PATIENT_REMOVED, result, null, journalEntryDao);
			physician.removePatients(proband);
			proband.setPhysician(null);
		}
		ProbandContactParticulars personParticulars = proband.getPersonParticulars();
		AnimalContactParticulars animalParticulars = proband.getAnimalParticulars();
		probandDao.remove(proband);
		if (personParticulars != null) {
			probandContactParticularsDao.remove(personParticulars);
		}
		if (animalParticulars != null) {
			animalContactParticularsDao.remove(animalParticulars);
		}

		logSystemMessage(user, result, now, user, SystemMessageCodes.PROBAND_DELETED, result, null, journalEntryDao);
	}

	public static void removeProbandListEntry(ProbandListEntry probandListEntry, boolean deleteCascade, Timestamp now, User user,
			boolean logTrial,
			boolean logProband,
			ProbandListStatusEntryDao probandListStatusEntryDao,
			ProbandListEntryTagValueDao probandListEntryTagValueDao,
			ECRFFieldValueDao ecrfFieldValueDao,
			ECRFFieldStatusEntryDao ecrfFieldStatusEntryDao,
			SignatureDao signatureDao,
			ECRFStatusEntryDao ecrfStatusEntryDao,
			InputFieldValueDao inputFieldValueDao,
			JournalEntryDao journalEntryDao,
			ProbandListEntryDao probandListEntryDao,
			NotificationDao notificationDao,
			NotificationRecipientDao notificationRecipientDao) throws Exception {
		if (deleteCascade) {
			Proband proband = probandListEntry.getProband();
			Trial trial = probandListEntry.getTrial();
			probandListEntry.setLastStatus(null);
			probandListEntryDao.update(probandListEntry);
			Iterator<ProbandListStatusEntry> statusEntriesIt = probandListEntry.getStatusEntries().iterator();
			while (statusEntriesIt.hasNext()) {
				ProbandListStatusEntry statusEntry = statusEntriesIt.next();
				ProbandListStatusEntryOutVO probandListStatusEntryVO = probandListStatusEntryDao.toProbandListStatusEntryOutVO(statusEntry);
				// logSystemMessage(proband, probandListStatusEntryVO.getListEntry().getProband(), now, user, SystemMessageCodes.PROBAND_LIST_STATUS_ENTRY_DELETED,
				// probandListStatusEntryVO, null, journalEntryDao);
				// logSystemMessage(trial, probandListStatusEntryVO.getListEntry().getTrial(), now, user, SystemMessageCodes.PROBAND_LIST_STATUS_ENTRY_DELETED,
				// probandListStatusEntryVO, null, journalEntryDao);
				logSystemMessage(proband, probandListStatusEntryVO.getListEntry().getTrial(), now, user, SystemMessageCodes.PROBAND_LIST_STATUS_ENTRY_DELETED,
						probandListStatusEntryVO, null, journalEntryDao);
				logSystemMessage(trial, probandListStatusEntryVO.getListEntry().getProband(), now, user, SystemMessageCodes.PROBAND_LIST_STATUS_ENTRY_DELETED,
						probandListStatusEntryVO, null, journalEntryDao);
				statusEntry.setListEntry(null);
				probandListStatusEntryDao.remove(statusEntry);
			}
			probandListEntry.getStatusEntries().clear();
			Iterator<ECRFStatusEntry> ecrfStatusEntriesIt = probandListEntry.getEcrfStatusEntries().iterator();
			while (ecrfStatusEntriesIt.hasNext()) {
				ECRFStatusEntry statusEntry = ecrfStatusEntriesIt.next();
				statusEntry.getEcrf().removeEcrfStatusEntries(statusEntry);
				removeEcrfStatusEntry(statusEntry, true, signatureDao, ecrfStatusEntryDao, notificationDao, notificationRecipientDao);
			}
			probandListEntry.getEcrfStatusEntries().clear();
			Iterator<ProbandListEntryTagValue> tagValuesIt = probandListEntry.getTagValues().iterator();
			while (tagValuesIt.hasNext()) {
				ProbandListEntryTagValue tagValue = tagValuesIt.next();
				tagValue.getTag().removeTagValues(tagValue);
				removeProbandListEntryTagValue(tagValue, now, user, LOG_PROBAND_LIST_ENTRY_TAG_VALUE_TRIAL && logTrial, LOG_PROBAND_LIST_ENTRY_TAG_VALUE_PROBAND && logProband,
						inputFieldValueDao,
						probandListEntryTagValueDao, journalEntryDao);
			}
			probandListEntry.getTagValues().clear();
			Iterator<ECRFFieldValue> ecrfValuesIt = probandListEntry.getEcrfValues().iterator();
			while (ecrfValuesIt.hasNext()) {
				ECRFFieldValue fieldValue = ecrfValuesIt.next();
				fieldValue.getEcrfField().removeFieldValues(fieldValue);
				removeEcrfFieldValue(fieldValue, now, user, LOG_ECRF_FIELD_VALUE_TRIAL && logTrial, LOG_ECRF_FIELD_VALUE_PROBAND && logProband, inputFieldValueDao,
						ecrfFieldValueDao, journalEntryDao); // logTrial,
				// logProband
			}
			probandListEntry.getEcrfValues().clear();
			Iterator<ECRFFieldStatusEntry> ecrfFieldStatusEntriesIt = probandListEntry.getEcrfFieldStatusEntries().iterator();
			while (ecrfFieldStatusEntriesIt.hasNext()) {
				ECRFFieldStatusEntry fieldStatus = ecrfFieldStatusEntriesIt.next();
				fieldStatus.getEcrfField().removeEcrfFieldStatusEntries(fieldStatus);
				removeEcrfFieldStatusEntry(fieldStatus, now, user, LOG_ECRF_FIELD_STATUS_ENTRY_TRIAL && logTrial, LOG_ECRF_FIELD_STATUS_ENTRY_PROBAND && logProband,
						ecrfFieldStatusEntryDao, journalEntryDao, notificationDao, notificationRecipientDao);
			}
			probandListEntry.getEcrfFieldStatusEntries().clear();
		}
		probandListEntry.setProband(null);
		probandListEntry.setTrial(null);
		probandListEntry.setGroup(null);
		probandListEntryDao.remove(probandListEntry);
	}

	public static void removeProbandListEntryTag(ProbandListEntryTag probandListEntryTag, boolean deleteCascade, boolean checkProbandLocked, Timestamp now, User user,
			boolean logTrial,
			boolean logProband,
			// ProbandDao probandDao,
			ProbandListEntryTagValueDao probandListEntryTagValueDao,
			InputFieldValueDao inputFieldValueDao,
			JournalEntryDao journalEntryDao,
			ProbandListEntryTagDao probandListEntryTagDao) throws Exception {
		if (deleteCascade) {
			Iterator<ProbandListEntryTagValue> tagValuesIt = probandListEntryTag.getTagValues().iterator();
			while (tagValuesIt.hasNext()) {
				ProbandListEntryTagValue tagValue = tagValuesIt.next();
				ProbandListEntry listEntry = tagValue.getListEntry();
				if (checkProbandLocked) {
					checkProbandLocked(listEntry.getProband());
				}
				listEntry.removeTagValues(tagValue);
				removeProbandListEntryTagValue(tagValue, now, user, LOG_PROBAND_LIST_ENTRY_TAG_VALUE_TRIAL && logTrial, LOG_PROBAND_LIST_ENTRY_TAG_VALUE_PROBAND && logProband,
						inputFieldValueDao,
						probandListEntryTagValueDao, journalEntryDao);
			}
			probandListEntryTag.getTagValues().clear();
		}
		probandListEntryTag.setTrial(null);
		probandListEntryTag.setField(null);
		probandListEntryTagDao.remove(probandListEntryTag);
	}

	public static void removeProbandListEntryTagValue(ProbandListEntryTagValue tagValue, Timestamp now, User modifiedUser,
			boolean logTrial,
			boolean logProband,
			InputFieldValueDao inputFieldValueDao,
			ProbandListEntryTagValueDao probandListEntryTagValueDao,
			JournalEntryDao journalEntryDao) throws Exception {
		InputFieldValue value = tagValue.getValue();
		ProbandListEntry listEntry = tagValue.getListEntry();
		ProbandListEntryTagValueOutVO tagValueVO = null;
		if (logProband || logTrial) {
			tagValueVO = probandListEntryTagValueDao.toProbandListEntryTagValueOutVO(tagValue);
		}
		tagValue.setListEntry(null);
		tagValue.setTag(null);
		probandListEntryTagValueDao.remove(tagValue);
		tagValue.setValue(null);
		value.getSelectionValues().clear();
		inputFieldValueDao.remove(value);
		if (logProband) {
			// logSystemMessage(listEntry.getProband(), tagValueVO.getListEntry().getProband(), now, modifiedUser, SystemMessageCodes.PROBAND_LIST_ENTRY_TAG_VALUE_DELETED,
			// tagValueVO,
			// null, journalEntryDao);
			logSystemMessage(listEntry.getProband(), tagValueVO.getListEntry().getTrial(), now, modifiedUser, SystemMessageCodes.PROBAND_LIST_ENTRY_TAG_VALUE_DELETED,
					tagValueVO,
					null, journalEntryDao);
		}
		if (logTrial) {
			// logSystemMessage(listEntry.getTrial(), tagValueVO.getListEntry().getTrial(), now, modifiedUser, SystemMessageCodes.PROBAND_LIST_ENTRY_TAG_VALUE_DELETED, tagValueVO,
			// null,
			// journalEntryDao);
			logSystemMessage(listEntry.getTrial(), tagValueVO.getListEntry().getProband(), now, modifiedUser, SystemMessageCodes.PROBAND_LIST_ENTRY_TAG_VALUE_DELETED, tagValueVO,
					null,
					journalEntryDao);
		}
	}

	public static String selectionSetValuesToString(Collection<InputFieldSelectionSetValueOutVO> selectionSetValues) {
		StringBuilder sb = new StringBuilder();
		if (selectionSetValues != null && selectionSetValues.size() > 0) {
			Iterator<InputFieldSelectionSetValueOutVO> it = selectionSetValues.iterator();
			while (it.hasNext()) {
				InputFieldSelectionSetValueOutVO selectionSetValue = it.next();
				if (sb.length() > 0) {
					sb.append(ExcelUtil.EXCEL_LINE_BREAK);
				}
				if (selectionSetValue != null) {
					String value = selectionSetValue.getValue();
					if (value != null && value.length() > 0) {
						sb.append(value);
					}
				}
			}
		}
		return sb.toString();
	}

	public static boolean testNotificationExists(Collection<Notification> notifications, org.phoenixctms.ctsms.enumeration.NotificationType notificationType, Boolean obsolete)
			throws Exception {
		Iterator<Notification> notificationsIt = notifications.iterator();
		while (notificationsIt.hasNext()) {
			Notification notification = notificationsIt.next();
			if ((obsolete == null || notification.isObsolete() == obsolete) && notification.getType().getType().equals(notificationType)) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<Long> toInputFieldSelectionSetValueIdCollection(Collection<InputFieldSelectionSetValue> selectionValues) { // lazyload persistentset prevention
		// ArrayList<Long> result;
		// if (selectionValues != null && selectionValues.size() > 0) {
		ArrayList<Long> result = new ArrayList<Long>(selectionValues.size());
		Iterator<InputFieldSelectionSetValue> it = selectionValues.iterator();
		while (it.hasNext()) {
			result.add(it.next().getId());
		}
		// } else {
		// result = new ArrayList<Long>();
		// }
		Collections.sort(result); // InVO ID sorting
		return result;
	}

	public static CriteriaInstantVO toInstant(Collection criterions, CriterionDao criterionDao) throws Exception {
		CriteriaInstantVO criteriaInstantVO = new CriteriaInstantVO();
		if (criterions != null) {
			Iterator it = criterions.iterator();
			while (it.hasNext()) {
				Object criterion = it.next();
				if (criterion != null) {
					if (criterion instanceof CriterionInVO) {
						criteriaInstantVO.getCriterions().add(toInstant((CriterionInVO) criterion));
					} else if (criterion instanceof CriterionOutVO) {
						criteriaInstantVO.getCriterions().add(toInstant((CriterionOutVO) criterion));
					} else if (criterion instanceof CriterionInstantVO) {
						criteriaInstantVO.getCriterions().add(new CriterionInstantVO((CriterionInstantVO) criterion));
					} else if (criterion instanceof Criterion) {
						criteriaInstantVO.getCriterions().add(criterionDao.toCriterionInstantVO((Criterion) criterion));
					} else {
						throw new IllegalArgumentException(L10nUtil.getMessage(MessageCodes.CRITERION_CLASS_NOT_SUPPORTED, DefaultMessages.CRITERION_CLASS_NOT_SUPPORTED, criterion
								.getClass().toString()));
					}
				} else {
					criteriaInstantVO.getCriterions().add(null);
				}
			}
		}
		return criteriaInstantVO;
	}

	private static CriterionInstantVO toInstant(CriterionInVO criterion) {
		CriterionInstantVO criterionInstantVO = new CriterionInstantVO();
		criterionInstantVO.setBooleanValue(criterion.getBooleanValue());
		criterionInstantVO.setDateValue(criterion.getDateValue());
		criterionInstantVO.setTimeValue(criterion.getTimeValue());
		criterionInstantVO.setFloatValue(criterion.getFloatValue());
		criterionInstantVO.setLongValue(criterion.getLongValue());
		criterionInstantVO.setPosition(criterion.getPosition());
		criterionInstantVO.setPropertyId(criterion.getPropertyId());
		criterionInstantVO.setRestrictionId(criterion.getRestrictionId());
		criterionInstantVO.setStringValue(criterion.getStringValue());
		criterionInstantVO.setTieId(criterion.getTieId());
		criterionInstantVO.setTimestampValue(criterion.getTimestampValue());
		return criterionInstantVO;
	}

	private static CriterionInstantVO toInstant(CriterionOutVO criterion) {
		CriterionPropertyVO criterionPropertyVO = criterion.getProperty();
		CriterionRestrictionVO criterionRestrictionVO = criterion.getRestriction();
		CriterionTieVO criterionTieVO = criterion.getTie();
		// CriteriaOutVO criteriaVO = criterion.getCriteria();
		CriterionInstantVO criterionInstantVO = new CriterionInstantVO();
		criterionInstantVO.setBooleanValue(criterion.getBooleanValue());
		criterionInstantVO.setDateValue(criterion.getDateValue());
		criterionInstantVO.setTimeValue(criterion.getTimeValue());
		criterionInstantVO.setFloatValue(criterion.getFloatValue());
		criterionInstantVO.setLongValue(criterion.getLongValue());
		criterionInstantVO.setPosition(criterion.getPosition());
		criterionInstantVO.setPropertyId(criterionPropertyVO == null ? null : criterionPropertyVO.getId());
		criterionInstantVO.setRestrictionId(criterionRestrictionVO == null ? null : criterionRestrictionVO.getId());
		criterionInstantVO.setStringValue(criterion.getStringValue());
		criterionInstantVO.setTieId(criterionTieVO == null ? null : criterionTieVO.getId());
		criterionInstantVO.setTimestampValue(criterion.getTimestampValue());
		return criterionInstantVO;
	}

	private static boolean updateBookingSummaryDetail(Long key,
			HashMap<Long, InventoryBookingDurationSummaryDetailVO> durationSummaryDetailsMap,
			ArrayList<InventoryBookingDurationSummaryDetailVO> assigned,
			InventoryOutVO inventory,
			CourseOutVO course,
			ProbandOutVO proband,
			TrialOutVO trial,
			InventoryBookingOutVO booking,
			InventoryBookingDurationSummaryVO summary,
			boolean fullBookings,
			boolean mergeOverlapping) {
		if (key != null) {
			InventoryBookingDurationSummaryDetailVO durationSummaryDetail = null;
			if (durationSummaryDetailsMap.containsKey(key)) {
				durationSummaryDetail = durationSummaryDetailsMap.get(key);
			} else {
				durationSummaryDetail = new InventoryBookingDurationSummaryDetailVO();
				durationSummaryDetailsMap.put(key, durationSummaryDetail);
				assigned.add(durationSummaryDetail);
				durationSummaryDetail.setInventory(inventory);
				durationSummaryDetail.setProband(proband);
				durationSummaryDetail.setCourse(course);
				durationSummaryDetail.setTrial(trial);
				durationSummaryDetail.setBookingCount(0);
				if (!mergeOverlapping) {
					durationSummaryDetail.setTotalDuration(0l);
				}
			}
			if (!fullBookings) {
				Date start = booking.getStart();
				Date stop = booking.getStop();
				boolean exceeds = false;
				if (summary.getStart() != null && booking.getStart().before(summary.getStart())) {
					start = summary.getStart();
					exceeds = true;
				}
				if (summary.getStop() != null && booking.getStop().after(summary.getStop())) {
					stop = summary.getStop();
					exceeds = true;
				}
				if (exceeds) {
					BookingDuration bookingDuration = new BookingDuration();
					try {
						bookingDuration.add(start, stop, null);
						bookingDuration.updateInventoryBooking(booking);
					} catch (Exception e) {
					}
				}
			}
			durationSummaryDetail.setBookingCount(durationSummaryDetail.getBookingCount() + 1);
			if (!mergeOverlapping) {
				durationSummaryDetail.setTotalDuration(durationSummaryDetail.getTotalDuration() + booking.getTotalDuration());
				if (summary.getIntervalDuration() > 0.0f) { // summary.getInventory() != null &&
					durationSummaryDetail.setLoad(((float) durationSummaryDetail.getTotalDuration()) / ((float) summary.getIntervalDuration()));
				}
			}
			return true;
		}
		return false;
	}

	private ServiceUtil() {
	}
}
