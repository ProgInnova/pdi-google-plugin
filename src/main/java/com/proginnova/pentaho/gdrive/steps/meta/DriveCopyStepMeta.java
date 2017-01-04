package com.proginnova.pentaho.gdrive.steps.meta;

import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
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
	
	private static final Class<?> PKG = DriveCopyStepData.class;

	private String serviceEmail, serviceKeyFile;
	private String driveFileToCopy, driveFolderToDump;
	private String impersonateUser;
	
	
	private boolean inputCheckedNotifyEmail, inputCheckedCustomEmail, fieldCheckedNotifyEmail, fieldCheckedCustomEmail, checkedAnyAccess, checkedInputAccess, checkedFieldAccess;
	private int fieldSelectedIndex, titleSelectedIndex;
	private String inputCustomMessage, fieldCustomMessage, inputEmailAccount;
	private String inputRole, fieldRole, anyoneRole;
	
	private String outputField;
	
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
	public StepDataInterface getStepData() {
		// TODO Auto-generated method stub
		return new DriveCopyStepData();
	}

	@Override
	public void setDefault() {
		// TODO Auto-generated method stub
		fieldSelectedIndex = -1;
		titleSelectedIndex = -1;
		inputCheckedNotifyEmail = false;
		inputCheckedCustomEmail = false;
		fieldCheckedNotifyEmail = false;
		fieldCheckedCustomEmail = false;
		checkedAnyAccess = false;
		checkedInputAccess = false;
		checkedFieldAccess = false;
		outputField = "fileId";
		serviceEmail = "";
	}
	
	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info, VariableSpace space, Repository repository,
			IMetaStore metaStore) {
		// TODO Auto-generated method stub
		CheckResult cr = null;
		if(input != null && input.length > 0){
			cr = new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.ReceivingRows.OK"), stepMeta);
		}else{
			cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "DriveCopyStep.CheckResult.ReceivingRows.ERROR"), stepMeta);
		}
		remarks.add(cr);
		super.check(remarks, transMeta, stepMeta, prev, input, output, info, space, repository, metaStore);
		
	}
	
	@Override
	public String getXML() throws KettleException {
		StringBuilder builder = new StringBuilder();
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
		
		builder.append("    ").append(XMLHandler.addTagValue("checkedFieldAccess", checkedFieldAccess));
		builder.append("    ").append(XMLHandler.addTagValue("fieldSelectedIndex", fieldSelectedIndex));
		builder.append("    ").append(XMLHandler.addTagValue("fieldCheckedNotifyEmail", fieldCheckedNotifyEmail));
		builder.append("    ").append(XMLHandler.addTagValue("fieldCheckedCustomEmail", fieldCheckedCustomEmail));
		builder.append("    ").append(XMLHandler.addTagValue("fieldCustomMessage", fieldCustomMessage));
		builder.append("    ").append(XMLHandler.addTagValue("fieldRole", fieldRole));
		
		builder.append("    ").append(XMLHandler.addTagValue("checkedAnyAccess", checkedAnyAccess));
		builder.append("    ").append(XMLHandler.addTagValue("anyoneRole", anyoneRole));
		
		builder.append("    ").append(XMLHandler.addTagValue("titleSelectedIndex", titleSelectedIndex));
		builder.append("    ").append(XMLHandler.addTagValue("outputField", outputField));
		return builder.toString();
	}
	
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
			rep.saveStepAttribute( id_transformation, id_step, "checkedFieldAccess", checkedFieldAccess );
			rep.saveStepAttribute( id_transformation, id_step, "fieldSelectedIndex", fieldSelectedIndex );
			rep.saveStepAttribute( id_transformation, id_step, "fieldCheckedNotifyEmail", fieldCheckedNotifyEmail );
			rep.saveStepAttribute( id_transformation, id_step, "fieldCheckedCustomEmail", fieldCheckedCustomEmail );
			rep.saveStepAttribute( id_transformation, id_step, "fieldCustomMessage", fieldCustomMessage );
			rep.saveStepAttribute( id_transformation, id_step, "fieldRole", fieldRole );
			rep.saveStepAttribute( id_transformation, id_step, "checkedAnyAccess", checkedAnyAccess );
			rep.saveStepAttribute( id_transformation, id_step, "anyoneRole", anyoneRole );
			rep.saveStepAttribute( id_transformation, id_step, "titleSelectedIndex", titleSelectedIndex );
			
			rep.saveStepAttribute(id_transformation, id_step, "outputField", outputField);
		}catch(Exception ex){
			throw new KettleException(BaseMessages.getString(PKG, "DriveCopyStepMeta.Exception.UnableToSaveStepInfoFromRepository", ex));
		}
	}
	
	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases) throws KettleXMLException {
		// TODO Auto-generated method stub
		readData(stepnode);
	}
	
	@Override
	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases)
			throws KettleException {
		try{
			serviceEmail = rep.getStepAttributeString(id_step, "serviceEmail");
			serviceKeyFile = rep.getStepAttributeString(id_step, "serviceKeyFile");
			impersonateUser = rep.getStepAttributeString(id_step, "impersonateUser");
			driveFileToCopy = rep.getStepAttributeString(id_step, "driveFileToCopy");
			driveFolderToDump = rep.getStepAttributeString(id_step, "driveFolderToDump");
			checkedInputAccess = rep.getStepAttributeBoolean(id_step, "checkedInputAccess");
			inputCheckedNotifyEmail = rep.getStepAttributeBoolean(id_step, "inputCheckedNotifyEmail");
			inputCheckedCustomEmail = rep.getStepAttributeBoolean(id_step, "inputCheckedCustomEmail");
			checkedInputAccess = rep.getStepAttributeBoolean(id_step, "checkedInputAccess");
			inputCustomMessage = rep.getStepAttributeString(id_step, "inputCustomMessage");
			inputRole = rep.getStepAttributeString(id_step, "inputRole");
			checkedFieldAccess = rep.getStepAttributeBoolean(id_step, "checkedFieldAccess");
			fieldSelectedIndex = (int) rep.getStepAttributeInteger(id_step, "fieldSelectedIndex");
			fieldCheckedNotifyEmail = rep.getStepAttributeBoolean(id_step, "fieldCheckedNotifyEmail");
			fieldCheckedCustomEmail = rep.getStepAttributeBoolean(id_step, "fieldCheckedCustomEmail");
			fieldCustomMessage = rep.getStepAttributeString(id_step, "fieldCustomMessage");
			fieldRole = rep.getStepAttributeString(id_step, "fieldRole");
			checkedAnyAccess = rep.getStepAttributeBoolean(id_step, "checkedAnyAccess");
			anyoneRole = rep.getStepAttributeString(id_step, "anyoneRole");
			titleSelectedIndex = (int) rep.getStepAttributeInteger(id_step, "titleSelectedIndex");
			outputField = rep.getStepAttributeString(id_step, "outputField");
		}catch(Exception ex){
			throw new KettleException(BaseMessages.getString(PKG, "DriveCopyStepMeta.Exception.UnexpectedErrorReadingStepInfoFromRepository", ex));
		}
	}
	
	private void readData(Node stepNode) throws KettleXMLException{
		try{
			serviceEmail = XMLHandler.getTagValue(stepNode, "serviceEmail");
			serviceKeyFile = XMLHandler.getTagValue(stepNode, "serviceKeyFile");
			impersonateUser = XMLHandler.getTagValue(stepNode, "impersonateUser");
			driveFileToCopy = XMLHandler.getTagValue(stepNode, "driveFileToCopy");
			driveFolderToDump = XMLHandler.getTagValue(stepNode, "driveFolderToDump");
			
			checkedInputAccess = Boolean.parseBoolean(XMLHandler.getTagValue(stepNode, "checkedInputAccess"));
			inputCheckedNotifyEmail = Boolean.parseBoolean(XMLHandler.getTagValue(stepNode, "inputCheckedNotifyEmail"));
			inputCheckedCustomEmail = Boolean.parseBoolean(XMLHandler.getTagValue(stepNode, "inputCheckedCustomEmail"));
			checkedInputAccess = Boolean.parseBoolean(XMLHandler.getTagValue(stepNode, "checkedInputAccess"));
			inputCustomMessage = XMLHandler.getTagValue(stepNode, "inputCustomMessage");
			inputRole = XMLHandler.getTagValue(stepNode, "inputRole");
			
			checkedFieldAccess = Boolean.parseBoolean(XMLHandler.getTagValue(stepNode, "checkedFieldAccess"));
			fieldSelectedIndex = Integer.parseInt(XMLHandler.getTagValue(stepNode, "fieldSelectedIndex"));
			fieldCheckedNotifyEmail = Boolean.parseBoolean(XMLHandler.getTagValue(stepNode, "fieldCheckedNotifyEmail"));
			fieldCheckedCustomEmail = Boolean.parseBoolean(XMLHandler.getTagValue(stepNode, "fieldCheckedCustomEmail"));
			fieldCustomMessage = XMLHandler.getTagValue(stepNode, "fieldCustomMessage");
			fieldRole = XMLHandler.getTagValue(stepNode, "fieldRole");
			
			checkedAnyAccess = Boolean.parseBoolean(XMLHandler.getTagValue(stepNode, "checkedAnyAccess"));
			anyoneRole = XMLHandler.getTagValue(stepNode, "anyoneRole");
			titleSelectedIndex = Integer.parseInt(XMLHandler.getTagValue(stepNode, "titleSelectedIndex"));
			outputField = XMLHandler.getTagValue(stepNode, "outputField");
		}catch(Exception ex){
			throw new KettleXMLException(BaseMessages.getString(PKG, "DriveCopyStepMeta.Exception.UnableToLoadStepFromXML", ex));
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
		System.out.println("INPUTROWMETA SIZE IN GET FIELDS: " + inputRowMeta.size());
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

	public boolean isCheckedFieldAccess() {
		return checkedFieldAccess;
	}

	public void setCheckedFieldAccess(boolean checkedFieldAccess) {
		this.checkedFieldAccess = checkedFieldAccess;
	}

	public int getFieldSelectedIndex() {
		return fieldSelectedIndex;
	}

	public void setFieldSelectedIndex(int fieldSelectedIndex) {
		this.fieldSelectedIndex = fieldSelectedIndex;
	}

	public int getTitleSelectedIndex() {
		return titleSelectedIndex;
	}

	public void setTitleSelectedIndex(int titleSelectedIndex) {
		this.titleSelectedIndex = titleSelectedIndex;
	}

	public String getInputCustomMessage() {
		return inputCustomMessage;
	}

	public void setInputCustomMessage(String inputCustomMessage) {
		this.inputCustomMessage = inputCustomMessage;
	}

	public String getFieldCustomMessage() {
		return fieldCustomMessage;
	}

	public void setFieldCustomMessage(String fieldCustomMessage) {
		this.fieldCustomMessage = fieldCustomMessage;
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

	public String getFieldRole() {
		return fieldRole;
	}

	public void setFieldRole(String fieldRole) {
		this.fieldRole = fieldRole;
	}

	public String getAnyoneRole() {
		return anyoneRole;
	}

	public void setAnyoneRole(String anyoneRole) {
		this.anyoneRole = anyoneRole;
	}
	
	
}
