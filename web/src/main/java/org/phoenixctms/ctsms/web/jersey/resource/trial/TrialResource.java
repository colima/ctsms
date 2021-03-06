package org.phoenixctms.ctsms.web.jersey.resource.trial;

import io.swagger.annotations.Api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.phoenixctms.ctsms.enumeration.FileModule;
import org.phoenixctms.ctsms.enumeration.HyperlinkModule;
import org.phoenixctms.ctsms.enumeration.JournalModule;
import org.phoenixctms.ctsms.exception.AuthenticationException;
import org.phoenixctms.ctsms.exception.AuthorisationException;
import org.phoenixctms.ctsms.exception.ServiceException;
import org.phoenixctms.ctsms.service.trial.TrialService;
import org.phoenixctms.ctsms.util.MethodTransfilter;
import org.phoenixctms.ctsms.vo.AuthenticationVO;
import org.phoenixctms.ctsms.vo.FileOutVO;
import org.phoenixctms.ctsms.vo.FilePDFVO;
import org.phoenixctms.ctsms.vo.HyperlinkOutVO;
import org.phoenixctms.ctsms.vo.JournalEntryOutVO;
import org.phoenixctms.ctsms.vo.TrialInVO;
import org.phoenixctms.ctsms.vo.TrialOutVO;
import org.phoenixctms.ctsms.web.jersey.index.TrialListIndex;
import org.phoenixctms.ctsms.web.jersey.resource.PSFUriPart;
import org.phoenixctms.ctsms.web.jersey.resource.Page;
import org.phoenixctms.ctsms.web.jersey.resource.ResourceUtils;
import org.phoenixctms.ctsms.web.jersey.resource.ServiceResourceBase;
import org.phoenixctms.ctsms.web.util.DefaultSettings;
import org.phoenixctms.ctsms.web.util.SettingCodes;
import org.phoenixctms.ctsms.web.util.Settings;
import org.phoenixctms.ctsms.web.util.Settings.Bundle;
import org.phoenixctms.ctsms.web.util.WebUtil;

@Api
@Path("/trial")
public class TrialResource extends ServiceResourceBase {

	@Context
	AuthenticationVO auth;
	// private final static DBModule dbModule = DBModule.TRIAL_DB;
	private final static FileModule fileModule = FileModule.TRIAL_DOCUMENT;
	private final static JournalModule journalModule = JournalModule.TRIAL_JOURNAL;
	private final static HyperlinkModule hyperlinkModule = HyperlinkModule.TRIAL_HYPERLINK;
	private final static Class SERVICE_INTERFACE = TrialService.class;
	private final static String ROOT_ENTITY_ID_METHOD_PARAM_NAME = "trialId";
	private static final MethodTransfilter GET_LIST_METHOD_NAME_TRANSFORMER = getGetListMethodNameTransformer(ROOT_ENTITY_ID_METHOD_PARAM_NAME, TrialOutVO.class);
	public final static TrialListIndex LIST_INDEX = new TrialListIndex(getListIndexNode(ResourceUtils.getMethodPath(TrialResource.class, "list")
			.replaceFirst("/\\{resource\\}", ""), // "listIndex"),
			SERVICE_INTERFACE, GET_LIST_METHOD_NAME_TRANSFORMER,
			getArgsUriPart(SERVICE_INTERFACE, "", new AuthenticationVO(), ROOT_ENTITY_ID_METHOD_PARAM_NAME, GET_LIST_METHOD_NAME_TRANSFORMER, 0l, new PSFUriPart())));
	// @GET
	// @Produces({MediaType.APPLICATION_JSON})
	// @Path("{psf:.*}")
	// public Page<TrialOutVO> getTrialList(@PathParam("psf") PSFUriPart psf,@Context UriInfo uriInfo)
	// throws AuthenticationException, AuthorisationException, ServiceException {
	// return new Page<TrialOutVO>(WebUtil.getServiceLocator().getTrialService().getTrialList(auth, null, null, PSFUriPart.updatePSF(psf,uriInfo)),psf);
	// }
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public TrialOutVO addTrial(TrialInVO in) throws AuthenticationException, AuthorisationException, ServiceException {
		return WebUtil.getServiceLocator().getTrialService().addTrial(auth, in);
	}

	@GET
	@Path("{id}/files/pdf")
	public Response aggregatePDFFiles(@PathParam("id") Long id, @Context UriInfo uriInfo) throws AuthenticationException, AuthorisationException, ServiceException {
		FilePDFVO vo = WebUtil.getServiceLocator().getFileService().aggregatePDFFiles(auth, fileModule, id, null, null, new PSFUriPart(uriInfo));
		// http://stackoverflow.com/questions/9204287/how-to-return-a-png-image-from-jersey-rest-service-method-to-the-browser
		// non-streamed
		ResponseBuilder response = Response.ok(vo.getDocumentDatas(), vo.getContentType().getMimeType());
		response.header(HttpHeaders.CONTENT_LENGTH, vo.getSize());
		return response.build();
	}

	// @HEAD
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}/files/pdf/head")
	public FilePDFVO aggregatePDFFilesHead(@PathParam("id") Long id, @Context UriInfo uriInfo)
			throws AuthenticationException, AuthorisationException, ServiceException {
		FilePDFVO result = WebUtil.getServiceLocator().getFileService().aggregatePDFFiles(auth, fileModule, id, null, null, new PSFUriPart(uriInfo));
		result.setDocumentDatas(null);
		return result;
	}

	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}")
	public TrialOutVO deleteTrial(@PathParam("id") Long id) throws AuthenticationException, AuthorisationException, ServiceException {
		return WebUtil.getServiceLocator().getTrialService()
				.deleteTrial(auth, id, Settings.getBoolean(SettingCodes.TRIAL_DEFERRED_DELETE, Bundle.SETTINGS, DefaultSettings.TRIAL_DEFERRED_DELETE), false);
	}

	@Override
	protected AuthenticationVO getAuth() {
		return auth;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}/ecrffieldmaxselectionsetvaluecount")
	public long getEcrfFieldMaxSelectionSetValueCount(@PathParam("id") Long id)
			throws AuthenticationException, AuthorisationException, ServiceException {
		return WebUtil.getServiceLocator().getInputFieldService().getEcrfFieldMaxSelectionSetValueCount(auth, id);
	}

	// @GET
	// @Produces({ MediaType.APPLICATION_JSON })
	// @Path("search")
	// public Page<CriteriaOutVO> getCriteriaList(@Context UriInfo uriInfo)
	// throws AuthenticationException, AuthorisationException, ServiceException {
	// PSFUriPart psf;
	// return new Page<CriteriaOutVO>(WebUtil.getServiceLocator().getSearchService().getCriteriaList(auth, dbModule, psf = new PSFUriPart(uriInfo)), psf);
	// }
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}/files/folders")
	public Page<String> getFileFolders(@PathParam("id") Long id, @Context UriInfo uriInfo)
			throws AuthenticationException, AuthorisationException, ServiceException {
		PSFUriPart psf;
		return new Page<String>(WebUtil.getServiceLocator().getFileService().getFileFolders(auth, fileModule, id, null, false, null, psf = new PSFUriPart(uriInfo)), psf);
	}

	@Override
	protected FileModule getFileModule() {
		return fileModule;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}/files")
	public Page<FileOutVO> getFiles(@PathParam("id") Long id, @Context UriInfo uriInfo)
			throws AuthenticationException, AuthorisationException, ServiceException {
		PSFUriPart psf;
		return new Page<FileOutVO>(WebUtil.getServiceLocator().getFileService().getFiles(auth, fileModule, id, null, null, psf = new PSFUriPart(uriInfo)), psf);
	}

	@Override
	protected MethodTransfilter getGetListMethodNameTransformer() {
		return GET_LIST_METHOD_NAME_TRANSFORMER;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}/links")
	public Page<HyperlinkOutVO> getHyperlinks(@PathParam("id") Long id, @Context UriInfo uriInfo)
			throws AuthenticationException, AuthorisationException, ServiceException {
		PSFUriPart psf;
		return new Page<HyperlinkOutVO>(WebUtil.getServiceLocator().getHyperlinkService().getHyperlinks(auth, hyperlinkModule, id, null, psf = new PSFUriPart(uriInfo)), psf);
	}



	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}/journal")
	public Page<JournalEntryOutVO> getJournal(@PathParam("id") Long id, @Context UriInfo uriInfo)
			throws AuthenticationException, AuthorisationException, ServiceException {
		PSFUriPart psf;
		return new Page<JournalEntryOutVO>(WebUtil.getServiceLocator().getJournalService().getJournal(auth, journalModule, id, psf = new PSFUriPart(uriInfo)), psf);
	}

	@Override
	protected String getRootEntityIdMethodParamName() {
		return ROOT_ENTITY_ID_METHOD_PARAM_NAME;
	}

	@Override
	protected Object getService() {
		return WebUtil.getServiceLocator().getTrialService();
	}

	@Override
	protected Class getServiceInterface() {
		return SERVICE_INTERFACE;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}")
	public TrialOutVO getTrial(@PathParam("id") Long id) throws AuthenticationException, AuthorisationException, ServiceException {
		return WebUtil.getServiceLocator().getTrialService().getTrial(auth, id);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Page<TrialOutVO> getTrialList(@Context UriInfo uriInfo)
			throws AuthenticationException, AuthorisationException, ServiceException {
		PSFUriPart psf;
		return new Page<TrialOutVO>(WebUtil.getServiceLocator().getTrialService().getTrialList(auth, null, null, psf = new PSFUriPart(uriInfo)), psf);
	}

	// @GET
	// @Produces({ MediaType.APPLICATION_JSON })
	// @Path("signup/{id}")
	// public Page<TrialOutVO> getTrialListSignup(@PathParam("id") Long id, @Context UriInfo uriInfo)
	// throws AuthenticationException, AuthorisationException, ServiceException {
	// PSFUriPart psf;
	// return new Page<TrialOutVO>(WebUtil.getServiceLocator().getTrialService().getSignupTrialList(auth, id, psf = new PSFUriPart(uriInfo)), psf);
	// }

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("signup")
	public Page<TrialOutVO> getTrialListSignup(@QueryParam("department_id") Long departmentId, @Context UriInfo uriInfo)
			throws AuthenticationException, AuthorisationException, ServiceException {
		PSFUriPart psf = new PSFUriPart(uriInfo, "department_id");
		return new Page<TrialOutVO>(WebUtil.getServiceLocator().getTrialService().getSignupTrialList(auth, departmentId, psf), psf);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}/list/{resource}")
	public Page list(@PathParam("id") Long id, @PathParam("resource") String resource, @Context UriInfo uriInfo) throws Throwable {
		return list(auth, id, resource, uriInfo);
	}

	// @GET
	// @Produces({ MediaType.APPLICATION_JSON })
	// @Path("test")
	// public Map<String,CriteriaOutVO> test(@Context UriInfo uriInfo)
	// throws AuthenticationException, AuthorisationException, ServiceException {
	// return new HashMap<String,CriteriaOutVO>();
	// }
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("list")
	public TrialListIndex listIndex() throws Exception {
		// new TrialListIndex(getListIndexNode(ResourceUtils.getMethodPath(TrialResource.class, "list")
		// .replaceFirst("/\\{resource\\}", ""), // "listIndex"),
		// SERVICE_INTERFACE, GET_LIST_METHOD_NAME_TRANSFORMER,
		// getArgsUriPart(SERVICE_INTERFACE, "", new AuthenticationVO(), ROOT_ENTITY_ID_METHOD_PARAM_NAME, GET_LIST_METHOD_NAME_TRANSFORMER, 0l, new PSFUriPart())));
		return LIST_INDEX;
	}

	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public TrialOutVO updateTrial(TrialInVO in) throws AuthenticationException, AuthorisationException, ServiceException {
		return WebUtil.getServiceLocator().getTrialService().updateTrial(auth, in);
	}
}
