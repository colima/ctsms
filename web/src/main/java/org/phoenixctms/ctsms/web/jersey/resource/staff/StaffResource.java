package org.phoenixctms.ctsms.web.jersey.resource.staff;

import io.swagger.annotations.Api;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import org.phoenixctms.ctsms.service.staff.StaffService;
import org.phoenixctms.ctsms.util.CommonUtil;
import org.phoenixctms.ctsms.util.MethodTransfilter;
import org.phoenixctms.ctsms.vo.AuthenticationVO;
import org.phoenixctms.ctsms.vo.FileOutVO;
import org.phoenixctms.ctsms.vo.FilePDFVO;
import org.phoenixctms.ctsms.vo.HyperlinkOutVO;
import org.phoenixctms.ctsms.vo.JournalEntryOutVO;
import org.phoenixctms.ctsms.vo.StaffImageInVO;
import org.phoenixctms.ctsms.vo.StaffImageOutVO;
import org.phoenixctms.ctsms.vo.StaffInVO;
import org.phoenixctms.ctsms.vo.StaffOutVO;
import org.phoenixctms.ctsms.web.jersey.index.StaffListIndex;
import org.phoenixctms.ctsms.web.jersey.resource.PSFUriPart;
import org.phoenixctms.ctsms.web.jersey.resource.Page;
import org.phoenixctms.ctsms.web.jersey.resource.ResourceUtils;
import org.phoenixctms.ctsms.web.jersey.resource.ServiceResourceBase;
import org.phoenixctms.ctsms.web.util.DefaultSettings;
import org.phoenixctms.ctsms.web.util.SettingCodes;
import org.phoenixctms.ctsms.web.util.Settings;
import org.phoenixctms.ctsms.web.util.Settings.Bundle;
import org.phoenixctms.ctsms.web.util.WebUtil;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Api
@Path("/staff")
public class StaffResource extends ServiceResourceBase {

	@Context
	AuthenticationVO auth;
	// private final static DBModule dbModule = DBModule.STAFF_DB;
	private final static FileModule fileModule = FileModule.STAFF_DOCUMENT;
	private final static JournalModule journalModule = JournalModule.STAFF_JOURNAL;
	private final static HyperlinkModule hyperlinkModule = HyperlinkModule.STAFF_HYPERLINK;
	private final static Class SERVICE_INTERFACE = StaffService.class;
	private final static String ROOT_ENTITY_ID_METHOD_PARAM_NAME = "staffId";
	private static final MethodTransfilter GET_LIST_METHOD_NAME_TRANSFORMER = getGetListMethodNameTransformer(ROOT_ENTITY_ID_METHOD_PARAM_NAME, StaffOutVO.class);
	public final static StaffListIndex LIST_INDEX = new StaffListIndex(getListIndexNode(ResourceUtils.getMethodPath(StaffResource.class, "list")
			.replaceFirst("/\\{resource\\}", ""), // "listIndex"),
			SERVICE_INTERFACE, GET_LIST_METHOD_NAME_TRANSFORMER,
			getArgsUriPart(SERVICE_INTERFACE, "", new AuthenticationVO(), ROOT_ENTITY_ID_METHOD_PARAM_NAME, GET_LIST_METHOD_NAME_TRANSFORMER, 0l, new PSFUriPart())));
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public StaffOutVO addStaff(StaffInVO in) throws AuthenticationException, AuthorisationException, ServiceException {
		return WebUtil.getServiceLocator().getStaffService()
				.addStaff(auth, in, Settings.getIntNullable(SettingCodes.API_GRAPH_MAX_STAFF_INSTANCES, Bundle.SETTINGS, DefaultSettings.API_GRAPH_MAX_STAFF_INSTANCES),
						Settings.getIntNullable(SettingCodes.API_GRAPH_MAX_STAFF_PARENT_DEPTH, Bundle.SETTINGS, DefaultSettings.API_GRAPH_MAX_STAFF_PARENT_DEPTH));
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
	public StaffOutVO deleteStaff(@PathParam("id") Long id) throws AuthenticationException, AuthorisationException, ServiceException {
		return WebUtil
				.getServiceLocator()
				.getStaffService()
				.deleteStaff(auth, id, Settings.getBoolean(SettingCodes.STAFF_DEFERRED_DELETE, Bundle.SETTINGS, DefaultSettings.STAFF_DEFERRED_DELETE),
						false,
						Settings.getIntNullable(SettingCodes.API_GRAPH_MAX_STAFF_INSTANCES, Bundle.SETTINGS, DefaultSettings.API_GRAPH_MAX_STAFF_INSTANCES),
						Settings.getIntNullable(SettingCodes.API_GRAPH_MAX_STAFF_PARENT_DEPTH, Bundle.SETTINGS, DefaultSettings.API_GRAPH_MAX_STAFF_PARENT_DEPTH));
	}

	@Override
	protected AuthenticationVO getAuth() {
		return auth;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}/children")
	public Collection<StaffOutVO> getChildren(@PathParam("id") Long id) throws AuthenticationException, AuthorisationException, ServiceException {
		return WebUtil.getServiceLocator().getStaffService()
				.getStaff(auth, id, Settings.getIntNullable(SettingCodes.API_GRAPH_MAX_STAFF_INSTANCES, Bundle.SETTINGS, DefaultSettings.API_GRAPH_MAX_STAFF_INSTANCES),
						Settings.getIntNullable(SettingCodes.API_GRAPH_MAX_STAFF_PARENT_DEPTH, Bundle.SETTINGS, DefaultSettings.API_GRAPH_MAX_STAFF_PARENT_DEPTH)).getChildren();
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
		return WebUtil.getServiceLocator().getStaffService();
	}

	@Override
	protected Class getServiceInterface() {
		return SERVICE_INTERFACE;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}")
	public StaffOutVO getStaff(@PathParam("id") Long id) throws AuthenticationException, AuthorisationException, ServiceException {
		return WebUtil.getServiceLocator().getStaffService()
				.getStaff(auth, id, Settings.getIntNullable(SettingCodes.API_GRAPH_MAX_STAFF_INSTANCES, Bundle.SETTINGS, DefaultSettings.API_GRAPH_MAX_STAFF_INSTANCES),
						Settings.getIntNullable(SettingCodes.API_GRAPH_MAX_STAFF_PARENT_DEPTH, Bundle.SETTINGS, DefaultSettings.API_GRAPH_MAX_STAFF_PARENT_DEPTH));
	}

	@GET
	@Path("{id}/image")
	public Response getStaffImage(@PathParam("id") Long id) throws AuthenticationException, AuthorisationException, ServiceException {
		StaffImageOutVO out = WebUtil.getServiceLocator().getStaffService().getStaffImage(auth, id);
		ResponseBuilder response = javax.ws.rs.core.Response.ok(new ByteArrayInputStream(out.getDatas()), out.getContentType().getMimeType());
		response.header(HttpHeaders.CONTENT_LENGTH, out.getFileSize());
		return response.build();
	}

	// @HEAD
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}/image/head")
	public StaffImageOutVO getStaffImageHead(@PathParam("id") Long id) throws AuthenticationException, AuthorisationException, ServiceException {
		StaffImageOutVO out = WebUtil.getServiceLocator().getStaffService().getStaffImage(auth, id);
		out.setDatas(null);
		return out;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Page<StaffOutVO> getStaffList(@Context UriInfo uriInfo)
			throws AuthenticationException, AuthorisationException, ServiceException {
		PSFUriPart psf;
		return new Page<StaffOutVO>(WebUtil.getServiceLocator().getStaffService()
				.getStaffList(auth, null, null, ResourceUtils.LIST_GRAPH_MAX_STAFF_INSTANCES, psf = new PSFUriPart(uriInfo)), psf);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}/list/{resource}")
	public Page list(@PathParam("id") Long id, @PathParam("resource") String resource, @Context UriInfo uriInfo) throws Throwable {
		return list(auth, id, resource, uriInfo);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("list")
	public StaffListIndex listIndex() throws Exception {
		return LIST_INDEX;
	}

	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_JSON })
	public StaffImageOutVO setStaffImage(@FormDataParam("json") StaffImageInVO in,
			@FormDataParam("data") FormDataBodyPart content,
			@FormDataParam("data") FormDataContentDisposition contentDisposition,
			@FormDataParam("data") final InputStream input) throws Exception {
		in.setDatas(CommonUtil.inputStreamToByteArray(input));
		in.setMimeType(content.getMediaType().toString());
		in.setFileName(contentDisposition.getFileName());
		return WebUtil.getServiceLocator().getStaffService().setStaffImage(auth, in);
	}

	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public StaffOutVO updateStaff(StaffInVO in) throws AuthenticationException, AuthorisationException, ServiceException {
		return WebUtil.getServiceLocator().getStaffService()
				.updateStaff(auth, in, Settings.getIntNullable(SettingCodes.API_GRAPH_MAX_STAFF_INSTANCES, Bundle.SETTINGS, DefaultSettings.API_GRAPH_MAX_STAFF_INSTANCES),
						Settings.getIntNullable(SettingCodes.API_GRAPH_MAX_STAFF_PARENT_DEPTH, Bundle.SETTINGS, DefaultSettings.API_GRAPH_MAX_STAFF_PARENT_DEPTH));
	}
	// @OPTIONS
	// public javax.ws.rs.core.Response davOptionsModuleRoot() {
	// return super.davOptions();
	// }
	// @PROPFIND
	// @Consumes({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// @Produces({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// public javax.ws.rs.core.Response davPropFindModuleRoot(@Context UriInfo uriInfo,@DefaultValue(DEPTH_INFINITY) @HeaderParam(DEPTH) String depth,
	// @Context Providers providers) throws AuthenticationException, AuthorisationException, ServiceException {
	// return super.davPropFind(null,uriInfo, depth, providers);
	// }
	// @OPTIONS
	// @Path("{id}")
	// public javax.ws.rs.core.Response davOptionsEntityRoot() {
	// return super.davOptions();
	// }
	// @PROPFIND
	// @Consumes({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// @Produces({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// @Path("{id}")
	// public javax.ws.rs.core.Response davPropFindEntityRoot(@Context UriInfo uriInfo,@DefaultValue(DEPTH_INFINITY) @HeaderParam(DEPTH) String depth,
	// @Context Providers providers) throws AuthenticationException, AuthorisationException, ServiceException {
	// return super.davPropFind(null,uriInfo, depth, providers);
	// }
	// @OPTIONS
	// @Path("{id}/files")
	// public javax.ws.rs.core.Response davOptionsFilesRoot() {
	// return super.davOptions();
	// }
	// @PROPFIND
	// @Consumes({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// @Produces({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// @Path("{id}/files")
	// public javax.ws.rs.core.Response davPropFindEntityFilesRoot(@Context UriInfo uriInfo,@DefaultValue(DEPTH_INFINITY) @HeaderParam(DEPTH) String depth,
	// @Context Providers providers) throws AuthenticationException, AuthorisationException, ServiceException {
	// return super.davPropFind(null,uriInfo, depth, providers);
	// }
	//
	//
	//
	// @OPTIONS
	// @Path("{id}/files/dav")
	// public javax.ws.rs.core.Response davOptions() {
	// return super.davOptions();
	// }
	//
	// @PROPFIND
	// @Consumes({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// @Produces({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// @Path("{id}/files/dav")
	// public javax.ws.rs.core.Response davPropFind(@PathParam("id") Long id,@Context UriInfo uriInfo,@DefaultValue(DEPTH_INFINITY) @HeaderParam(DEPTH) String depth,
	// @Context Providers providers) throws AuthenticationException, AuthorisationException, ServiceException {
	// return super.davPropFind(id,uriInfo, depth, providers);
	// }
	//
	// @PROPFIND
	// @Consumes({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// @Produces({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// @Path("{id}/files/dav/{fileName}")
	// public javax.ws.rs.core.Response davPropFind(@PathParam("id") Long id,@PathParam("fileName") String fileName, @Context UriInfo uriInfo,
	// @Context Providers providers) throws AuthenticationException, AuthorisationException, ServiceException {
	// return super.davPropFind(id,fileName, uriInfo, providers);
	// }
	//
	// @PROPPATCH
	// @Consumes({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// @Produces({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// @Path("{id}/files/dav/{fileName}")
	// public javax.ws.rs.core.Response davPropPatch(InputStream body, @Context UriInfo uriInfo, @Context Providers providers, @Context HttpHeaders httpHeaders) throws IOException
	// {
	// return super.davPropPatch(body,uriInfo,providers,httpHeaders);
	// }
	//
	// @GET
	// @Path("{id}/files/dav/{fileName}")
	// public javax.ws.rs.core.Response get(@PathParam("id") Long id,@PathParam("fileName") String fileName) throws AuthenticationException, AuthorisationException,
	// ServiceException {
	// return super.davGet(id, fileName);
	// }
	//
	// @HEAD
	// @Path("{id}/files/dav/{fileName}")
	// public javax.ws.rs.core.Response davHead(@PathParam("id") Long id,@PathParam("fileName") String fileName) throws AuthenticationException, AuthorisationException,
	// ServiceException {
	// return super.davHead(id,fileName);
	// }
	//
	// @PUT
	// @Path("{id}/files/dav/{fileName}")
	// protected javax.ws.rs.core.Response davPut(InputStream input, @PathParam("id") Long id, @PathParam("fileName") String fileName,
	// @HeaderParam(CONTENT_LENGTH) long contentLength, @Context HttpHeaders httpHeaders)
	// throws AuthenticationException, AuthorisationException, ServiceException {
	// return super.davPut(input, id, fileName, contentLength, httpHeaders);
	// }
	//
	// @DELETE
	// @Path("{id}/files/dav/{fileName}")
	// public javax.ws.rs.core.Response davDelete(@PathParam("id") Long id,@PathParam("fileName") String fileName) throws AuthenticationException, AuthorisationException,
	// ServiceException {
	// return super.davDelete(id, fileName);
	// }
	//
	// @LOCK
	// @Consumes({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// @Path("{id}/files/dav/{fileName}")
	// public Prop davLock(LockInfo lockInfo, @Context UriInfo uriInfo,
	// @HeaderParam(DEPTH) @DefaultValue(DEPTH_INFINITY) String depth, @HeaderParam(TIMEOUT) TimeOut timeout) throws URISyntaxException, IOException {
	// return super.davLock(lockInfo,uriInfo,depth,timeout);
	// }
	//
	// //@Consumes({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// //@Produces({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// @UNLOCK
	// @Consumes({ MediaType.APPLICATION_XML , MediaType.TEXT_XML })
	// @Path("{id}/files/dav/{fileName}")
	// public javax.ws.rs.core.Response davUnlock() {
	// return davUnlock();
	// }
	//
	//
	// @Override
	// protected FileModule getFileModule() {
	// return fileModule;
	// }
	//
	//
	// @Override
	// protected AuthenticationVO getAuth() {
	// return auth;
	// }
}
