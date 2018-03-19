package com.proginnova.pentaho.gdrive.steps;

import java.util.LinkedList;
import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.oauth2.Oauth2Scopes;
import com.proginnova.gdrive.impl.DriveFileManagement;
import com.proginnova.gdrive.impl.DriveFileMimeTypes;
import com.proginnova.gdrive.impl.DriveRolePermission;
import com.proginnova.gdrive.impl.GoogleConnection;
import com.proginnova.pentaho.gdrive.steps.data.DriveCopyStepData;
import com.proginnova.pentaho.gdrive.steps.meta.DriveCopyStepMeta;

public class DriveCopyStep extends BaseStep implements StepInterface {
	
	private Class<?> PKG = DriveCopyStep.class;

	private GoogleConnection connection;
	private Drive driveService;
	private File copyFile, folderDump;
	
	public DriveCopyStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		DriveCopyStepMeta meta = (DriveCopyStepMeta) smi;
		DriveCopyStepData data = (DriveCopyStepData) sdi;
		if(!super.init(meta, data)){
			return false;
		}
		List<String> scopes = new LinkedList<>();
		scopes.add(DriveScopes.DRIVE);
		try{
			if(meta.getImpersonateUser() != null && !meta.getImpersonateUser().isEmpty()){
				scopes.add(Oauth2Scopes.USERINFO_EMAIL);
				scopes.add(Oauth2Scopes.USERINFO_PROFILE);
				connection = new GoogleConnection(this.environmentSubstitute(meta.getServiceKeyFile()),  this.environmentSubstitute(meta.getServiceEmail()), this.environmentSubstitute(meta.getImpersonateUser()), scopes);
			}else{
				connection = new GoogleConnection(this.environmentSubstitute(meta.getServiceKeyFile()), this.environmentSubstitute(meta.getServiceEmail()), scopes);
			}
			driveService = connection.getDrive();
			copyFile = DriveFileManagement.getFile(driveService, meta.getDriveFileToCopy(), true);
			folderDump = DriveFileManagement.getFile(driveService, meta.getDriveFolderToDump(), true);
			if(connection.isImpersonate() && !this.environmentSubstitute(meta.getImpersonateUser()).equals(folderDump.getOwners().get(0).getEmailAddress())){
				throw new Exception("Impersonate it's not the owner of the folder");
			}
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		return driveService != null && copyFile != null && folderDump != null && folderDump.getMimeType().equals(DriveFileMimeTypes.FOLDER_MIME_TYPE);
	}
	
	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		DriveCopyStepData data = (DriveCopyStepData) sdi;
		
		Object[] row = getRow();
		
		if(row == null){
			setOutputDone();
			return false;
		}
		
		if(first){
			DriveCopyStepMeta meta = (DriveCopyStepMeta) smi;
			first = false;
			data.inputRowMeta = getInputRowMeta().clone();
			data.numberOfPrevRows = getInputRowMeta().size();
			data.outputRowMeta = getInputRowMeta().clone();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this, repository, metaStore);
			data.fieldFileTitleIndex = data.outputRowMeta.indexOfValue(meta.getTitleFieldSelected());
			data.newColumnIndex = data.outputRowMeta.indexOfValue(meta.getOutputField());
			if(data.newColumnIndex < 0){
				throw new KettleStepException("Not valid field");
			}
			if(meta.isCheckedInputAccess()){
				data.inputChecked = true;
				data.inputEmailAccount = this.environmentSubstitute(meta.getInputEmailAccount());
				if(meta.isInputCheckedNotifyEmail() && meta.isInputCheckedNotifyEmail()){
					data.inputCheckedNotifyByEmail = data.inputCheckedCustomMessage = true;
					data.inputCustomMessage = this.environmentSubstitute(meta.getInputCustomMessage());
				}else if(meta.isInputCheckedNotifyEmail()){
					data.inputCheckedNotifyByEmail = true;
				}
				data.inputDriveRole = DriveRolePermission.valueOf(meta.getInputRole().toLowerCase());
			}
			
			if(meta.isCheckedFieldAccess()){
				data.fieldChecked = true;
				data.fieldEmailAccountIndex = data.outputRowMeta.indexOfValue(meta.getFieldAccount());
				if(data.fieldEmailAccountIndex < 0){
					throw new KettleStepException("Required field");
				}
				if(meta.isFieldCheckedNotifyEmail() && meta.isFieldCheckedNotifyEmail()){
					data.fieldCheckedNotifyByEmail = data.fieldCheckedCustomMessage = true;
					data.fieldCustomMessageIndex = data.outputRowMeta.indexOfValue(meta.getFieldCustomMessage());
				}else if(meta.isFieldCheckedNotifyEmail()){
					data.inputCheckedNotifyByEmail = true;
				}
				data.fieldDriveRole = DriveRolePermission.valueOf(meta.getFieldRole().toLowerCase());
			}
			
			if(meta.isCheckedAnyAccess()){
				data.anyAccessChecked = meta.isCheckedAnyAccess();
				if(meta.getAnyoneRole().toLowerCase().equals(DriveRolePermission.owner)){
					throw new KettleException(BaseMessages.getString(PKG, "DriveCopyStep.Exception.AnyoneOwnerNotAllowed"));
				}
				data.anyAccessDriveRole = DriveRolePermission.valueOf(meta.getAnyoneRole().toLowerCase());
			}
			
		}

		try {
			Object[] outputRow = RowDataUtil.createResizedCopy(row, data.outputRowMeta.size());
			File copiedFile = DriveFileManagement.copyFiles(driveService, copyFile, folderDump, (String) outputRow[data.fieldFileTitleIndex]);;
			if(log.isDebug()){
				log.logDebug("COPIED FILEID: " + copiedFile.getId());
			}
			if(data.anyAccessChecked){
				DriveFileManagement.addAnyoneAccess(driveService, copiedFile, data.anyAccessDriveRole);
			}
			
			if(data.inputChecked){
				if(data.inputCheckedNotifyByEmail && data.inputCheckedNotifyByEmail){
					DriveFileManagement.addUserPermission(driveService, copiedFile, data.inputEmailAccount, data.inputDriveRole, data.inputCustomMessage);
				}else{
					DriveFileManagement.addUserPermission(driveService, copiedFile, data.inputEmailAccount, data.inputDriveRole, data.inputCheckedNotifyByEmail);
				}
			}
			
			if(data.fieldChecked){
				if(log.isDebug()){
					log.logDebug("Adding sharing with: " + (String)outputRow[data.fieldEmailAccountIndex]);
				}
				
				
				if(data.fieldCheckedNotifyByEmail && data.fieldCheckedNotifyByEmail){
					DriveFileManagement.addUserPermission(driveService, copiedFile, (String)outputRow[data.fieldEmailAccountIndex], data.fieldDriveRole, (String)outputRow[data.fieldCustomMessageIndex]);
				}else{
					DriveFileManagement.addUserPermission(driveService, copiedFile, (String)outputRow[data.fieldEmailAccountIndex], data.fieldDriveRole, data.fieldCheckedNotifyByEmail);
				}
			}
			outputRow[data.newColumnIndex] = copiedFile.getId();
			putRow(data.outputRowMeta, outputRow);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logError(BaseMessages.getString(PKG, "DriveCopyStep.Log.StepCanNotContinueForErrors", e.getMessage()));
			logError(Const.getStackTracker(e));
			stopAll();
			setOutputDone();
			throw new KettleException(e.getMessage());
		}
		
		return true;
	}
	
	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		// TODO Auto-generated method stub
		driveService = null;
		connection = null;
		copyFile = null;
		folderDump = null;
		super.dispose(smi, sdi);
	}
	
	@Override
	public String environmentSubstitute(String aString) {
		// TODO Auto-generated method stub
		return (aString.startsWith("$"))? super.environmentSubstitute(aString):aString;
	}
	

}
