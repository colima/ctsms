package org.phoenixctms.ctsms.executable.xls;


import java.io.File;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.WritableWorkbook;

import org.phoenixctms.ctsms.util.JobOutput;
import org.phoenixctms.ctsms.vo.AuthenticationVO;
import org.springframework.beans.factory.annotation.Autowired;

public class XlsExporter {

	@Autowired
	protected SelectionSetValueRowWriter selectionSetValueRowWriter;
	@Autowired
	protected InputFieldRowWriter inputFieldRowWriter;
	@Autowired
	protected EcrfFieldRowWriter ecrfFieldRowWriter;
	@Autowired
	protected EcrfRowWriter ecrfRowWriter;
	private JobOutput jobOutput;

	public XlsExporter() {
	}

	public long exportEcrfs(String fileName, AuthenticationVO auth, Long trialId) throws Throwable {
		XlsExporterContext context =new XlsExporterContext(this, fileName);
		setContext(ecrfRowWriter, context);
		setContext(ecrfFieldRowWriter, context);
		setContext(inputFieldRowWriter, context);
		setContext(selectionSetValueRowWriter, context);
		context.setEntityId(ecrfRowWriter,trialId);
		context.setAuth(auth);

		return printRows(context, ecrfRowWriter);
	}

	public long exportInputField(String fileName, AuthenticationVO auth, Long inputFieldId) throws Throwable {
		XlsExporterContext context = new XlsExporterContext(this, fileName);
		setContext(inputFieldRowWriter, context);
		setContext(selectionSetValueRowWriter, context);
		context.setEntityId(inputFieldRowWriter, inputFieldId);
		context.setAuth(auth);
		return printRows(context, inputFieldRowWriter);
	}

	public EcrfFieldRowWriter getEcrfFieldRowWriter() {
		return ecrfFieldRowWriter;
	}

	public InputFieldRowWriter getInputFieldRowWriter() {

		return inputFieldRowWriter;
	}


	public SelectionSetValueRowWriter getSelectionSetValueRowWriter() {
		return selectionSetValueRowWriter;
	}

	private long printRows(XlsExporterContext context, RowWriter writer) throws Throwable {
		jobOutput.println("writing to file " + context.getFileName());

		try {
			WritableWorkbook workbook;
			WorkbookSettings workbookSettings = writer.getWorkbookSettings();
			if (workbookSettings != null) {
				workbook = Workbook.createWorkbook(new File(context.getFileName()), workbookSettings);
			} else {
				workbook = Workbook.createWorkbook(new File(context.getFileName()));
			}
			context.setWorkbook(workbook);
			writer.init();
			writer.printRows();
			workbook.write();
			workbook.close();
		} catch (Exception e) {
			throw new IllegalArgumentException("row " + (writer.getLineCount() + 1) + ": error writing row", e);
		}
		jobOutput.println(writer.getLineCount() + " rows exported");
		return writer.getLineCount();
	}

	private void setContext(RowWriter writer, XlsExporterContext context) { // , String sheetName) {
		writer.setContext(context);
		writer.setJobOutput(jobOutput);
	}

	public void setJobOutput(JobOutput jobOutput) {
		this.jobOutput = jobOutput;
	}
}
