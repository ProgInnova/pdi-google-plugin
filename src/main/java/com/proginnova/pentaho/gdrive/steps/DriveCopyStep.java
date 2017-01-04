package com.proginnova.pentaho.gdrive.steps;

import java.util.LinkedList;
import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
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
				connection = new GoogleConnection(meta.getServiceKeyFile(), meta.getServiceEmail(), meta.getImpersonateUser(), scopes);
			}else{
				connection = new GoogleConnection(meta.getServiceKeyFile(), meta.getServiceEmail(), scopes);
			}
			driveService = connection.getDrive();
			copyFile = DriveFileManagement.getFile(driveService, meta.getDriveFileToCopy(), false);
			folderDump = DriveFileManagement.getFile(driveService, DriveFileMimeTypes.FOLDER_MIME_TYPE, meta.getDriveFolderToDump());
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		return driveService != null && copyFile != null && folderDump != null;
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
			data.fieldFileTitleIndex = meta.getTitleSelectedIndex();
			
			if(meta.isCheckedInputAccess()){
				data.inputChecked = true;
				data.inputEmailAccount = meta.getInputEmailAccount();
				if(meta.isInputCheckedNotifyEmail() && meta.isInputCheckedNotifyEmail()){
					data.inputCheckedNotifyByEmail = data.inputCheckedCustomMessage = true;
					data.inputCustomMessage = meta.getInputCustomMessage();
				}else if(meta.isInputCheckedNotifyEmail()){
					data.inputCheckedNotifyByEmail = true;
				}
			}
			
			if(meta.isCheckedFieldAccess()){
				data.inputChecked = true;
				data.fieldEmailAccountIndex = meta.getFieldSelectedIndex();
				if(meta.isFieldCheckedNotifyEmail() && meta.isFieldCheckedNotifyEmail()){
					data.fieldCheckedNotifyByEmail = data.fieldCheckedCustomMessage = true;
					data.fieldCustomMessage = meta.getFieldCustomMessage();
				}else if(meta.isFieldCheckedNotifyEmail()){
					data.inputCheckedNotifyByEmail = true;
				}
			}
			
			if(meta.isCheckedAnyAccess()){
				data.anyAccessChecked = meta.isCheckedAnyAccess();
				if(meta.getAnyoneRole().equals(DriveRolePermission.owner)){
					throw new KettleException(BaseMessages.getString(PKG, "DriveCopyStep.Exception.AnyoneOwnerNotAllowed"));
				}
				data.anyAccessDriveRole = DriveRolePermission.valueOf(meta.getAnyoneRole());
			}
			
		}
		
		String copiedFileName = "";
		String inputEmail = "";
		String fieldEmail = "";
		
		try {
			File copiedFile = DriveFileManagement.copyFiles(driveService, copyFile, folderDump, copiedFileName);
			Object[] outputRow = RowDataUtil.createResizedCopy(row, data.outputRowMeta.size());
			int newColumnIndex = outputRow.length;
			if(data.anyAccessChecked){
				DriveFileManagement.addAnyoneAccess(driveService, copiedFile, data.anyAccessDriveRole);
			}
			
			if(data.inputChecked){
				if(data.inputCheckedNotifyByEmail && data.inputCheckedNotifyByEmail){
					DriveFileManagement.addUserPermission(driveService, copiedFile, inputEmail, data.inputDriveRole, data.inputCustomMessage);
				}else{
					DriveFileManagement.addUserPermission(driveService, copiedFile, inputEmail, data.inputDriveRole, data.inputCheckedNotifyByEmail);
				}
			}
			
			if(data.fieldChecked){
				if(data.fieldCheckedNotifyByEmail && data.fieldCheckedNotifyByEmail){
					DriveFileManagement.addUserPermission(driveService, copiedFile, fieldEmail, data.fieldDriveRole, data.fieldCustomMessage);
				}else{
					DriveFileManagement.addUserPermission(driveService, copiedFile, fieldEmail, data.fieldDriveRole, data.fieldCheckedNotifyByEmail);
				}
			}
			outputRow[newColumnIndex] = copiedFile.getId();
			putRow(data.outputRowMeta, row);
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
	

}
