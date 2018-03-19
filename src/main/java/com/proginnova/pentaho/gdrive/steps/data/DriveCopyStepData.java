package com.proginnova.pentaho.gdrive.steps.data;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import com.proginnova.gdrive.impl.DriveRolePermission;

public class DriveCopyStepData extends BaseStepData implements StepDataInterface {

	public RowMetaInterface inputRowMeta, outputRowMeta;
	
	public boolean inputCheckedNotifyByEmail, inputCheckedCustomMessage, inputChecked;
	public String inputEmailAccount, inputCustomMessage;
	public DriveRolePermission inputDriveRole;
	
	public boolean fieldCheckedNotifyByEmail, fieldCheckedCustomMessage, fieldChecked;
	public DriveRolePermission fieldDriveRole;
	public int fieldEmailAccountIndex, fieldCustomMessageIndex;
	
	public boolean anyAccessChecked;
	public DriveRolePermission anyAccessDriveRole;
	
	public int fieldFileTitleIndex;
	
	public int numberOfPrevRows;
	
	public int newColumnIndex;
	
	public DriveCopyStepData(){
		super();
		anyAccessChecked = false;
		fieldCheckedCustomMessage = false;
		fieldCheckedNotifyByEmail = false;
		inputChecked = false;
		
		inputCheckedCustomMessage = false;
		inputCheckedNotifyByEmail = false;
		fieldChecked = false;
		
		inputDriveRole = null;
		fieldDriveRole = null;
		anyAccessDriveRole = null;
		
		numberOfPrevRows = 0;
		fieldFileTitleIndex = -1;
		fieldCustomMessageIndex = -1;
		newColumnIndex = -1;
	}
	
	
	
	
}
