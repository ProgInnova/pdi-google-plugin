package com.proginnova.pentaho.gdrive.steps.meta;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.proginnova.gdrive.impl.DriveFileManagement;
import com.proginnova.gdrive.impl.GoogleConnection;
import com.proginnova.pentaho.gdrive.steps.DriveCopyStep;
import com.proginnova.pentaho.gdrive.steps.data.DriveCopyStepData;
import com.proginnova.pentaho.gdrive.steps.ui.DriveCopyStepDialog;

@Step(
	id = "DriveCopyStep", 
	name = "DriveCopyStep.Name", 
	description = "DriveCopyStep.TooltipDesc", 
	image = "drivecopy.svg", 
	categoryDescription="DriveSteps.Category", 
	i18nPackageName = "com.proginnova.pentaho.gdrive.steps.meta", 
	documentationUrl = "DriveCopyStep.DocumentationURL", 
	casesUrl = "DriveCopyStep.CasesURL", 
	forumUrl = "DriveCopyStep.ForumURL"
)
public class DriveCopyStepMeta extends BaseStepMeta implements StepMetaInterface {
	
	private static final Class<?> PKG = DriveCopyStepMeta.class;

	private String titleFieldSelected, outputField;
	
	private String serviceEmail, serviceKeyFile;
	private String driveFileToCopy, driveFolderToDump;
	private String impersonateUser;
	
	
	private boolean inputCheckedNotifyEmail, inputCheckedCustomEmail;
	private String inputCustomMessage, inputEmailAccount, inputRole;
	
	private boolean fieldCheckedNotifyEmail, fieldCheckedCustomEmail, checkedFieldAccess;
	private String fieldCustomMessage, fieldRole, fieldAccount;
	
	private boolean checkedAnyAccess, checkedInputAccess;
	private String anyoneRole;
	
	public DriveCopyStepMeta() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	
	@Override
	public String getDialogClassName() {
		// TODO Auto-generated method stub
		return DriveCopyStepDialog.class.getName();
	}
	
	public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta, TransMeta transMeta, String name){
		return new DriveCopyStepDialog(shell, meta, transMeta, name);
	}
	
	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		// TODO Auto-generated method stub
		return new DriveCopyStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		DriveCopyStepMeta cpy = (DriveCopyStepMeta) super.clone();
		cpy.setTitleFieldSelected(getTitleFieldSelected());
		cpy.setOutputField(this.getOutputField());
		cpy.setServiceEmail(this.getServiceEmail());
		cpy.setServiceKeyFile(serviceKeyFile);
		cpy.setDriveFileToCopy(driveFileToCopy);
		cpy.setDriveFolderToDump(driveFolderToDump);
		cpy.setImpersonateUser(impersonateUser);
		cpy.setCheckedInputAccess(checkedInputAccess);
		cpy.setInputEmailAccount(inputEmailAccount);
		cpy.setInputRole(inputRole);
		cpy.setInputCheckedNotifyEmail(inputCheckedNotifyEmail);
		cpy.setInputCheckedCustomEmail(inputCheckedCustomEmail);
		cpy.setInputCustomMessage(inputCustomMessage);
		cpy.setCheckedFieldAccess(checkedFieldAccess);
		cpy.setFieldAccount(fieldAccount);
		cpy.setFieldRole(fieldRole);
		cpy.setFieldCheckedNotifyEmail(fieldCheckedNotifyEmail);
		cpy.setFieldCheckedCustomEmail(fieldCheckedCustomEmail);
		cpy.setFieldCustomMessage(fieldCustomMessage);
		cpy.setAnyoneRole(anyoneRole);
		cpy.setTitleFieldSelected(titleFieldSelected);
		
		return cpy;
	}

	@Override
	public StepDataInterface getStepData() {
		// TODO Auto-generated method stub
		return new DriveCopyStepData();
	}

	@Override
	public void setDefault() {
		// TODO Auto-generated method stub
		titleFieldSelected = "";
		
		driveFileToCopy = ""; 
		driveFolderToDump  = "";
		
		serviceEmail = ""; 
		serviceKeyFile = "";
		fieldAccount = "";
		fieldCustomMessage = "";
		fieldRole = "";
		fieldAccount = "";
		serviceEmail = "";
		inputCustomMessage = "";
		inputEmailAccount = "";
		inputRole = "";
		anyoneRole = "";
		
		inputCheckedNotifyEmail = false;
		inputCheckedCustomEmail = false;
		fieldCheckedNotifyEmail = false;
		fieldCheckedCustomEmail = false;
		checkedAnyAccess = false;
		checkedInputAccess = false;
		checkedFieldAccess = false;
		outputField = "fileId";
	}
	
	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info, VariableSpace space, Repository repository,
			IMetaStore metaStore) {
		// TODO Auto-generated method stub
		boolean success = true;
		if(input != null && input.length > 0){
			remarks.add(new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.ReceivingRows.OK"), stepMeta));
		}else{
			remarks.add(new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.ReceivingRows.ERROR"), stepMeta));
		}
		
		if(!serviceEmail.isEmpty() && !serviceKeyFile.isEmpty()){
			try{
				Collection<String> scopes = new LinkedList<>();
				scopes.add(DriveScopes.DRIVE);
				GoogleConnection con = new GoogleConnection(serviceKeyFile, serviceEmail, scopes);
				if(con.isConnected()){
					remarks.add(new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.ServiceConfigured.OK"), stepMeta));
				}else{
					remarks.add(new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.ServiceConfigured.ERROR"), stepMeta));
					success = false;
				}
				
				if(!driveFileToCopy.isEmpty()){
					remarks.add(new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.FileCopySetted.OK"), stepMeta));
				}else{
					remarks.add(new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.FileCopySetted.ERROR"), stepMeta));
					success = false;
				}
				
				if(!driveFolderToDump.isEmpty()){
					remarks.add(new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.FolderDumpSetted.OK"), stepMeta));
				}else{
					remarks.add(new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.FolderDumpSetted.ERROR"), stepMeta));
					success = false;
				}
				
				if(con.isConnected() && !driveFileToCopy.isEmpty() && !driveFolderToDump.isEmpty()){
					Drive service = con.getDrive();
					if(service.files().get(driveFileToCopy).setFields("id").execute() != null){
						remarks.add(new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.FileCopyExists.OK"), stepMeta));
					}else{
						remarks.add(new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.FileCopyExists.ERROR"), stepMeta));
						success = false;
					}
					
					if(service.files().get(driveFolderToDump).setFields("id").execute() != null){
						remarks.add(new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.FolderDumpExists.OK"), stepMeta));
					}else{
						remarks.add(new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.FolderDumpExists.ERROR"), stepMeta));
						success = false;
					}
				}
				
				if(success){
					remarks.add(new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.StepConfigured.OK"), stepMeta));
				}else{
					remarks.add(new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.StepConfigured.ERROR"), stepMeta));
				}
			}catch(Exception ex){
				remarks.add(new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.StepConfigured.ERROR"), stepMeta));
			}
		}else{
			remarks.add(new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.ServiceNotConfigured"), stepMeta));
		}
		super.check(remarks, transMeta, stepMeta, prev, input, output, info, space, repository, metaStore);
		
	}
	
	@Override
	public String getXML() throws KettleException {
		StringBuilder builder = new StringBuilder();
		try{
			builder.append("    ").append(XMLHandler.addTagValue("titleFieldSelected", titleFieldSelected));
			
			builder.append("    ").append(XMLHandler.addTagValue("serviceEmail", serviceEmail));
			builder.append("    ").append(XMLHandler.addTagValue("serviceKeyFile", serviceKeyFile));
			builder.append("    ").append(XMLHandler.addTagValue("impersonateUser", impersonateUser));
			builder.append("    ").append(XMLHandler.addTagValue("driveFileToCopy", driveFileToCopy));
			builder.append("    ").append(XMLHandler.addTagValue("driveFolderToDump", driveFolderToDump));
			
			builder.append("    ").append(XMLHandler.addTagValue("checkedInputAccess", checkedInputAccess));
			builder.append("    ").append(XMLHandler.addTagValue("inputCheckedNotifyEmail", inputCheckedNotifyEmail));
			builder.append("    ").append(XMLHandler.addTagValue("inputCheckedCustomEmail", inputCheckedCustomEmail));
			builder.append("    ").append(XMLHandler.addTagValue("checkedInputAccess", checkedInputAccess));
			builder.append("    ").append(XMLHandler.addTagValue("inputCustomMessage", inputCustomMessage));
			builder.append("    ").append(XMLHandler.addTagValue("inputRole", inputRole));
			builder.append("    ").append(XMLHandler.addTagValue("inputEmailAccount", inputEmailAccount));
			
			builder.append("    ").append(XMLHandler.addTagValue("checkedFieldAccess", checkedFieldAccess));
			builder.append("    ").append(XMLHandler.addTagValue("fieldAccount", fieldAccount));
			builder.append("    ").append(XMLHandler.addTagValue("fieldCheckedNotifyEmail", fieldCheckedNotifyEmail));
			builder.append("    ").append(XMLHandler.addTagValue("fieldCheckedCustomEmail", fieldCheckedCustomEmail));
			builder.append("    ").append(XMLHandler.addTagValue("fieldCustomMessage", fieldCustomMessage));
			builder.append("    ").append(XMLHandler.addTagValue("fieldRole", fieldRole));
			
			builder.append("    ").append(XMLHandler.addTagValue("checkedAnyAccess", checkedAnyAccess));
			builder.append("    ").append(XMLHandler.addTagValue("anyoneRole", anyoneRole));
			
			builder.append("    ").append(XMLHandler.addTagValue("outputField", outputField));
		}catch(Exception ex){
			throw new KettleValueException("Unable to write step to XML", ex);
		}
		return builder.toString();
	}
	
	//fieldCustomMessage
	
	@Override
	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step)
			throws KettleException {
		try{
			rep.saveStepAttribute( id_transformation, id_step, "serviceEmail", serviceEmail );
			rep.saveStepAttribute( id_transformation, id_step, "serviceKeyFile", serviceKeyFile );
			rep.saveStepAttribute( id_transformation, id_step, "impersonateUser", impersonateUser );
			
			rep.saveStepAttribute( id_transformation, id_step, "driveFileToCopy", driveFileToCopy );
			rep.saveStepAttribute( id_transformation, id_step, "driveFolderToDump", driveFolderToDump );
			
			rep.saveStepAttribute( id_transformation, id_step, "checkedInputAccess", checkedInputAccess );
			rep.saveStepAttribute( id_transformation, id_step, "inputCheckedNotifyEmail", inputCheckedNotifyEmail );
			rep.saveStepAttribute( id_transformation, id_step, "inputCheckedCustomEmail", inputCheckedCustomEmail );
			rep.saveStepAttribute( id_transformation, id_step, "checkedInputAccess", checkedInputAccess );
			rep.saveStepAttribute( id_transformation, id_step, "inputCustomMessage", inputCustomMessage );
			rep.saveStepAttribute( id_transformation, id_step, "inputRole", inputRole );
			rep.saveStepAttribute(id_transformation, id_step, "inputEmailAccount", inputEmailAccount);
			
			rep.saveStepAttribute( id_transformation, id_step, "checkedFieldAccess", checkedFieldAccess );
			rep.saveStepAttribute( id_transformation, id_step, "fieldCheckedNotifyEmail", fieldCheckedNotifyEmail );
			rep.saveStepAttribute( id_transformation, id_step, "fieldCheckedCustomEmail", fieldCheckedCustomEmail );
			rep.saveStepAttribute( id_transformation, id_step, "fieldCustomMessage", fieldCustomMessage );
			rep.saveStepAttribute( id_transformation, id_step, "fieldRole", fieldRole );
			
			rep.saveStepAttribute( id_transformation, id_step, "checkedAnyAccess", checkedAnyAccess );
			rep.saveStepAttribute( id_transformation, id_step, "anyoneRole", anyoneRole );
			
			rep.saveStepAttribute(id_transformation, id_step, "outputField", outputField);
			rep.saveStepAttribute( id_transformation, id_step, "titleFieldSelected", titleFieldSelected );
			rep.saveStepAttribute(id_transformation, id_step, "fieldAccount", fieldAccount);
		}catch(Exception ex){
			throw new KettleException(BaseMessages.getString(PKG, "DriveCopyStepMeta.Exception.UnableToSaveStepInfoFromRepository", ex));
		}
	}
	
	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {
		// TODO Auto-generated method stub
		
		String def = "";
		try{
			this.serviceEmail = Const.NVL(XMLHandler.getTagValue(stepnode, "serviceEmail"), def);
			this.serviceKeyFile = Const.NVL(XMLHandler.getTagValue(stepnode, "serviceKeyFile"), def);
			this.impersonateUser = Const.NVL(XMLHandler.getTagValue(stepnode, "impersonateUser"), def);
			this.driveFileToCopy = Const.NVL(XMLHandler.getTagValue(stepnode, "driveFileToCopy"), def);
			this.driveFolderToDump = Const.NVL(XMLHandler.getTagValue(stepnode, "driveFolderToDump"), def);
			
			this.checkedInputAccess = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "checkedInputAccess"));
			this.inputEmailAccount = Const.NVL(XMLHandler.getTagValue(stepnode, "inputEmailAccount"), def);
			
			this.inputCheckedNotifyEmail = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "inputCheckedNotifyEmail"));
			
			this.inputCheckedCustomEmail = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "inputCheckedCustomEmail"));
			this.checkedInputAccess = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "checkedInputAccess"));
			this.inputCustomMessage = Const.NVL(XMLHandler.getTagValue(stepnode, "inputCustomMessage"), def);
			this.inputRole = Const.NVL(XMLHandler.getTagValue(stepnode, "inputRole"), def);
			
			this.checkedFieldAccess = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "checkedFieldAccess"));
			this.fieldAccount = Const.NVL(XMLHandler.getTagValue(stepnode, "fieldAccount"), def);
			this.fieldCheckedNotifyEmail = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "fieldCheckedNotifyEmail"));
			this.fieldCheckedCustomEmail = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "fieldCheckedCustomEmail"));
			this.fieldCustomMessage = Const.NVL(XMLHandler.getTagValue(stepnode, "fieldCustomMessage"), def);
			this.fieldRole = Const.NVL(XMLHandler.getTagValue(stepnode, "fieldRole"), def);
			
			this.checkedAnyAccess = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "checkedAnyAccess"));
			this.anyoneRole = Const.NVL(XMLHandler.getTagValue(stepnode, "anyoneRole"), def);
			this.outputField = Const.NVL(XMLHandler.getTagValue(stepnode, "outputField"), def);
			this.titleFieldSelected = Const.NVL(XMLHandler.getTagValue(stepnode, "titleFieldSelected"), def);
		}catch(Exception ex){
			throw new KettleXMLException(BaseMessages.getString(PKG, "DriveCopyStepMeta.Exception.UnableToLoadStepFromXML", ex));
		}
	}
	
	@Override
	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases)
			throws KettleException {
		try{
			this.serviceEmail = rep.getStepAttributeString(id_step, "serviceEmail");
			this.serviceKeyFile = rep.getStepAttributeString(id_step, "serviceKeyFile");
			this.impersonateUser = rep.getStepAttributeString(id_step, "impersonateUser");
			this.driveFileToCopy = rep.getStepAttributeString(id_step, "driveFileToCopy");
			this.driveFolderToDump = rep.getStepAttributeString(id_step, "driveFolderToDump");
			
			this.checkedInputAccess = rep.getStepAttributeBoolean(id_step, "checkedInputAccess");
			this.inputCheckedNotifyEmail = rep.getStepAttributeBoolean(id_step, "inputCheckedNotifyEmail");
			this.inputCheckedCustomEmail = rep.getStepAttributeBoolean(id_step, "inputCheckedCustomEmail");
			this.checkedInputAccess = rep.getStepAttributeBoolean(id_step, "checkedInputAccess");
			this.inputCustomMessage = rep.getStepAttributeString(id_step, "inputCustomMessage");
			this.inputRole = rep.getStepAttributeString(id_step, "inputRole");
			this.inputEmailAccount = rep.getStepAttributeString(id_step, "inputEmailAccount");
			
			this.checkedFieldAccess = rep.getStepAttributeBoolean(id_step, "checkedFieldAccess");
			this.fieldAccount = rep.getStepAttributeString(id_step, "fieldAccount");
			this.fieldCheckedNotifyEmail = rep.getStepAttributeBoolean(id_step, "fieldCheckedNotifyEmail");
			this.fieldCheckedCustomEmail = rep.getStepAttributeBoolean(id_step, "fieldCheckedCustomEmail");
			this.fieldCustomMessage = rep.getStepAttributeString(id_step, "fieldCustomMessage");
			this.fieldRole = rep.getStepAttributeString(id_step, "fieldRole");
			
			this.checkedAnyAccess = rep.getStepAttributeBoolean(id_step, "checkedAnyAccess");
			this.anyoneRole = rep.getStepAttributeString(id_step, "anyoneRole");
			
			this.outputField = rep.getStepAttributeString(id_step, "outputField");
			this.titleFieldSelected = rep.getStepAttributeString(id_step, "titleFieldSelected");
		}catch(Exception ex){
			throw new KettleException(BaseMessages.getString(PKG, "DriveCopyStepMeta.Exception.UnexpectedErrorReadingStepInfoFromRepository", ex));
		}
	}
	
	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {
		// TODO Auto-generated method stub
		
		ValueMetaInterface vmi = new ValueMetaString(outputField);
		
		vmi.setTrimType(ValueMetaInterface.TRIM_TYPE_BOTH);
		
		vmi.setOrigin(name);
		
		inputRowMeta.addValueMeta(vmi);
		
	}

	public String getTitleFieldSelected() {
		return titleFieldSelected;
	}

	public void setTitleFieldSelected(String titleFieldSelected) {
		this.titleFieldSelected = titleFieldSelected;
	}

	public String getOutputField() {
		return outputField;
	}

	public void setOutputField(String outputField) {
		this.outputField = outputField;
	}

	public String getServiceEmail() {
		return serviceEmail;
	}

	public void setServiceEmail(String serviceEmail) {
		this.serviceEmail = serviceEmail;
	}

	public String getServiceKeyFile() {
		return serviceKeyFile;
	}

	public void setServiceKeyFile(String serviceKeyFile) {
		this.serviceKeyFile = serviceKeyFile;
	}

	public String getDriveFileToCopy() {
		return driveFileToCopy;
	}

	public void setDriveFileToCopy(String driveFileToCopy) {
		this.driveFileToCopy = driveFileToCopy;
	}

	public String getDriveFolderToDump() {
		return driveFolderToDump;
	}

	public void setDriveFolderToDump(String driveFolderToDump) {
		this.driveFolderToDump = driveFolderToDump;
	}

	public String getImpersonateUser() {
		return impersonateUser;
	}

	public void setImpersonateUser(String impersonateUser) {
		this.impersonateUser = impersonateUser;
	}

	public boolean isInputCheckedNotifyEmail() {
		return inputCheckedNotifyEmail;
	}

	public void setInputCheckedNotifyEmail(boolean inputCheckedNotifyEmail) {
		this.inputCheckedNotifyEmail = inputCheckedNotifyEmail;
	}

	public boolean isInputCheckedCustomEmail() {
		return inputCheckedCustomEmail;
	}

	public void setInputCheckedCustomEmail(boolean inputCheckedCustomEmail) {
		this.inputCheckedCustomEmail = inputCheckedCustomEmail;
	}

	public String getInputCustomMessage() {
		return inputCustomMessage;
	}

	public void setInputCustomMessage(String inputCustomMessage) {
		this.inputCustomMessage = inputCustomMessage;
	}

	public String getInputEmailAccount() {
		return inputEmailAccount;
	}

	public void setInputEmailAccount(String inputEmailAccount) {
		this.inputEmailAccount = inputEmailAccount;
	}

	public String getInputRole() {
		return inputRole;
	}

	public void setInputRole(String inputRole) {
		this.inputRole = inputRole;
	}

	public boolean isFieldCheckedNotifyEmail() {
		return fieldCheckedNotifyEmail;
	}

	public void setFieldCheckedNotifyEmail(boolean fieldCheckedNotifyEmail) {
		this.fieldCheckedNotifyEmail = fieldCheckedNotifyEmail;
	}

	public boolean isFieldCheckedCustomEmail() {
		return fieldCheckedCustomEmail;
	}

	public void setFieldCheckedCustomEmail(boolean fieldCheckedCustomEmail) {
		this.fieldCheckedCustomEmail = fieldCheckedCustomEmail;
	}

	public boolean isCheckedFieldAccess() {
		return checkedFieldAccess;
	}

	public void setCheckedFieldAccess(boolean checkedFieldAccess) {
		this.checkedFieldAccess = checkedFieldAccess;
	}

	public String getFieldCustomMessage() {
		return fieldCustomMessage;
	}

	public void setFieldCustomMessage(String fieldCustomMessage) {
		this.fieldCustomMessage = fieldCustomMessage;
	}

	public String getFieldRole() {
		return fieldRole;
	}

	public void setFieldRole(String fieldRole) {
		this.fieldRole = fieldRole;
	}

	public String getFieldAccount() {
		return fieldAccount;
	}

	public void setFieldAccount(String fieldAccount) {
		this.fieldAccount = fieldAccount;
	}

	public boolean isCheckedAnyAccess() {
		return checkedAnyAccess;
	}

	public void setCheckedAnyAccess(boolean checkedAnyAccess) {
		this.checkedAnyAccess = checkedAnyAccess;
	}

	public boolean isCheckedInputAccess() {
		return checkedInputAccess;
	}

	public void setCheckedInputAccess(boolean checkedInputAccess) {
		this.checkedInputAccess = checkedInputAccess;
	}

	public String getAnyoneRole() {
		return anyoneRole;
	}

	public void setAnyoneRole(String anyoneRole) {
		this.anyoneRole = anyoneRole;
	}

	@Override
	public String toString() {
		return "DriveCopyStepMeta [titleFieldSelected=" + titleFieldSelected + ", outputField=" + outputField
				+ ", serviceEmail=" + serviceEmail + ", serviceKeyFile=" + serviceKeyFile + ", driveFileToCopy="
				+ driveFileToCopy + ", driveFolderToDump=" + driveFolderToDump + ", impersonateUser=" + impersonateUser
				+ ", inputCheckedNotifyEmail=" + inputCheckedNotifyEmail + ", inputCheckedCustomEmail="
				+ inputCheckedCustomEmail + ", inputCustomMessage=" + inputCustomMessage + ", inputEmailAccount="
				+ inputEmailAccount + ", inputRole=" + inputRole + ", fieldCheckedNotifyEmail="
				+ fieldCheckedNotifyEmail + ", fieldCheckedCustomEmail=" + fieldCheckedCustomEmail
				+ ", checkedFieldAccess=" + checkedFieldAccess + ", fieldCustomMessage=" + fieldCustomMessage
				+ ", fieldRole=" + fieldRole + ", fieldAccount=" + fieldAccount + ", checkedAnyAccess="
				+ checkedAnyAccess + ", checkedInputAccess=" + checkedInputAccess + ", anyoneRole=" + anyoneRole + "]";
	}
	
	
}