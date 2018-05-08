package com.proginnova.pentaho.gdrive.steps.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.common.BareBonesBrowserLaunch;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.dialog.EnterSelectionDialog;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.StyledTextComp;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.proginnova.gdrive.impl.DriveFileManagement;
import com.proginnova.gdrive.impl.DriveFileMimeTypes;
import com.proginnova.gdrive.impl.DriveRolePermission;
import com.proginnova.gdrive.impl.GoogleConnection;
import com.proginnova.pentaho.gdrive.steps.meta.DriveCopyStepMeta;
import com.proginnova.pentaho.ui.BasicStepDialog;

public class DriveCopyStepDialog extends BasicStepDialog {

	private static Class<?> PKG = DriveCopyStepDialog.class;
	private DriveCopyStepMeta meta;

	private Display display;
	private String REFERENCE_GSUITE_DOMAIN_INFO = "https://developers.google.com/admin-sdk/directory/v1/guides/delegation";
	private Collection<String> driveScopes;

	private CCombo titleFieldSelect;

	private TextVar txtSvAccountEmail, txtSvKeyFile;
	private Label lblTestConectionResult;
	private Button btnTestConnection;

	private CCombo comboCustomMessageField;

	private Text txtFileCopy, txtFolderDump;

	private TextVar txtImpersonateUser, txtAccountInput;

	private Text txtOutputField;

	private StyledTextComp txtCustomMessage;

	private CCombo comboRoleInput, comboAccountField, comboRoleField, comboAnyPermissionRole;

	private Button ckInputPermission, ckNotifyEmailInput, ckCustomMessageInput, ckPermissionField, ckNotifyEmailField,
			ckCustomMessageField, ckAnyPermission;

	public DriveCopyStepDialog(Shell parent, Object baseStepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) baseStepMeta, transMeta, stepname);
		// TODO Auto-generated constructor stub
		this.meta = (DriveCopyStepMeta) baseStepMeta;

		driveScopes = new ArrayList<>();
		driveScopes.add(DriveScopes.DRIVE);
		driveScopes.add(DriveScopes.DRIVE_FILE);
	}

	@Override
	public String open() {
		// TODO Auto-generated method stub
		setDialogView();
		// setSize();
		// shell.pack();
		changed = meta.hasChanged();
		BaseStepDialog.setSize(shell);
		getData();
		meta.setChanged(changed);
		setModifyListener();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return stepname;
	}
	
	public void getData(){
		getData(meta, true);
	}
	
	public void getData(DriveCopyStepMeta driveCopyMeta, boolean copyStepname){
		if(copyStepname){
			wStepname.setText(stepname);
		}
		titleFieldSelect.setText(Const.NVL(driveCopyMeta.getTitleFieldSelected(), ""));
		
		
		txtOutputField.setText(driveCopyMeta.getOutputField());
		
		txtSvAccountEmail.setText(Const.NVL(driveCopyMeta.getServiceEmail(), ""));
		txtSvKeyFile.setText(Const.NVL(driveCopyMeta.getServiceKeyFile(), ""));
		
		txtFileCopy.setText(Const.NVL(driveCopyMeta.getDriveFileToCopy(), ""));
		txtFolderDump.setText(Const.NVL(driveCopyMeta.getDriveFolderToDump(), ""));
		
		txtImpersonateUser.setText(Const.NVL(driveCopyMeta.getImpersonateUser(), ""));
		
		ckInputPermission.setSelection(driveCopyMeta.isCheckedInputAccess());
		
		txtAccountInput.setEnabled(driveCopyMeta.isCheckedInputAccess());
		txtAccountInput.setText(driveCopyMeta.getInputEmailAccount());
		comboRoleInput.setEnabled(driveCopyMeta.isCheckedInputAccess());
		comboRoleInput.setText(driveCopyMeta.getInputRole());
		
		ckNotifyEmailInput.setEnabled(driveCopyMeta.isCheckedInputAccess());
		ckNotifyEmailInput.setSelection(driveCopyMeta.isInputCheckedNotifyEmail() && driveCopyMeta.isCheckedInputAccess());
		
		ckCustomMessageInput.setEnabled(driveCopyMeta.isInputCheckedNotifyEmail());
		ckCustomMessageInput.setSelection(driveCopyMeta.isInputCheckedCustomEmail() && driveCopyMeta.isInputCheckedNotifyEmail());
		
		txtCustomMessage.setEnabled(ckCustomMessageInput.getSelection());
		txtCustomMessage.setText(driveCopyMeta.getInputCustomMessage());
		
		ckPermissionField.setSelection(driveCopyMeta.isCheckedFieldAccess());
		
		comboAccountField.setEnabled(driveCopyMeta.isCheckedFieldAccess());
		comboAccountField.setText(Const.NVL(driveCopyMeta.getFieldAccount(), ""));
		
		comboRoleField.setEnabled(driveCopyMeta.isCheckedFieldAccess());
		comboRoleField.setText(Const.NVL(driveCopyMeta.getFieldRole(), ""));
		
		ckNotifyEmailField.setEnabled(driveCopyMeta.isCheckedFieldAccess());
		ckNotifyEmailField.setSelection(driveCopyMeta.isFieldCheckedNotifyEmail() && driveCopyMeta.isCheckedFieldAccess());
		
		ckCustomMessageField.setEnabled(driveCopyMeta.isFieldCheckedNotifyEmail());
		ckCustomMessageField.setSelection(driveCopyMeta.isFieldCheckedCustomEmail() && driveCopyMeta.isFieldCheckedNotifyEmail());
		
		comboCustomMessageField.setEnabled(ckCustomMessageField.getSelection());
		comboCustomMessageField.setText(driveCopyMeta.getFieldCustomMessage());
		
		ckAnyPermission.setSelection(driveCopyMeta.isCheckedAnyAccess());
		comboAnyPermissionRole.setEnabled(driveCopyMeta.isCheckedAnyAccess());
		comboAnyPermissionRole.setText(driveCopyMeta.getAnyoneRole());
		
		wStepname.selectAll();
		wStepname.setFocus();
	}

	private void setDialogView() {
		Shell parent = this.getParent();
		display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.CENTER | SWT.MIN | SWT.MAX);
		shell.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.dialog.label"));
		props.setLook(shell);
		setShellImage(shell, meta);
		changed = meta.hasChanged();

		shell.setLayout(getFormLayout(Const.FORM_MARGIN));
		int margin = Const.MARGIN;
		int marginLabel = (Const.isLinux())? margin + 6 : margin;
		int marginComboLabel = (Const.isLinux())? margin + 8 : margin;
		int marginCkLabel = (Const.isLinux())? margin + 5 : margin;
		int middle = props.getMiddlePct();

		int btnTopMargin = (Const.isOSX()) ? 0 : margin;

		// Step Name

		wlStepname = createLabel(shell, BaseMessages.getString(PKG, "DriveCopyStepDialog.stepName.label"));
		fdlStepname = getBaseFormData(null, marginLabel, middle, true);
		wlStepname.setLayoutData(fdlStepname);

		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		// wStepname.addModifyListener(modifiedListener);
		fdStepname = getBaseFormData(null, margin, middle, false);
		wStepname.setLayoutData(fdStepname);

		// Title by field

		Label lblTitleByField = createLabel(shell,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.titleFieldInput.label"));
		lblTitleByField.setLayoutData(getBaseFormData(wStepname, marginComboLabel, middle, true));

		titleFieldSelect = createFieldsCombo(shell);
		titleFieldSelect.setLayoutData(getBaseFormData(wStepname, margin, middle, false));
		titleFieldSelect.addFocusListener(loadComboOptions());

		// Output field

		Label lblOutputField = createLabel(shell,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.outputFieldInput.label"));
		lblOutputField.setLayoutData(getBaseFormData(titleFieldSelect, marginLabel, middle, true));

		txtOutputField = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		txtOutputField.setLayoutData(getBaseFormData(titleFieldSelect, margin, middle, false));
		props.setLook(txtOutputField);

		// Tabs

		CTabFolder folderTabs = new CTabFolder(shell, SWT.BORDER);
		props.setLook(folderTabs, Props.WIDGET_STYLE_TAB);

		CTabItem googleConfigTab = new CTabItem(folderTabs, SWT.NONE);
		googleConfigTab.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.label"));

		Composite googleConfigComposite = new Composite(folderTabs, SWT.NONE);
		props.setLook(googleConfigComposite);
		googleConfigComposite.setLayout(getFormLayout(3));

		// Service Account Config Group

		Group serviceAccountGroup = createGroup(googleConfigComposite,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.label"));
		serviceAccountGroup.setLayout(getFormLayout(10));

		Label lblsvAccountEmail = createLabel(serviceAccountGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.emailLabel"));
		lblsvAccountEmail.setLayoutData(getBaseFormData(0, ( (Const.isLinux() || Const.isWindows())? marginLabel + 5 : marginLabel) , 0, 0, middle, -margin));
		props.setLook(lblsvAccountEmail);

		txtSvAccountEmail = new TextVar(transMeta, serviceAccountGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		txtSvAccountEmail.setLayoutData(getBaseFormData(0, 2 * margin, middle, 0, 100, 0));
		props.setLook(txtSvAccountEmail);
		txtSvAccountEmail.addModifyListener(enableTestConnectionButton());

		Label lblSvAccountKeyfile = createLabel(serviceAccountGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.keyFileLabel"));
		props.setLook(lblSvAccountKeyfile);
		lblSvAccountKeyfile.setLayoutData(getBaseFormData(txtSvAccountEmail, marginLabel, 0, 0, middle, -margin));

		txtSvKeyFile = new TextVar(transMeta, serviceAccountGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(txtSvKeyFile);
		txtSvKeyFile
				.setLayoutData(getBaseFormData(txtSvAccountEmail, margin, lblSvAccountKeyfile, margin, 90, -margin));
		txtSvKeyFile.addModifyListener(enableTestConnectionButton());

		Button btnSvKeyFileBrowse = createButton(serviceAccountGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.dialog.browse"), keyFileOpenDialog());
		btnSvKeyFileBrowse.setToolTipText(BaseMessages.getString(PKG,
				"DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.keyFileBrowseTooltip"));
		btnSvKeyFileBrowse
				.setLayoutData(getBaseFormDataLeftDirection(txtSvAccountEmail, btnTopMargin, txtSvKeyFile, margin));

		btnTestConnection = createButton(serviceAccountGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.testConnection"),
				testConnection());
		btnTestConnection
				.setLayoutData(getBaseFormDataLeftDirection(btnSvKeyFileBrowse, btnTopMargin, middle, -margin));

		lblTestConectionResult = new Label(serviceAccountGroup, SWT.LEFT);
		// lblTestConectionResult.setText("TESTING");
		props.setLook(lblTestConectionResult);
		lblTestConectionResult
				.setLayoutData(getBaseFormData(btnSvKeyFileBrowse, margin * 2, btnTestConnection, margin, 100, 0));

		// Google File Config Group

		Group fileConfigGroup = createGroup(googleConfigComposite,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.fileConfigGroup.label"));
		fileConfigGroup.setLayout(getFormLayout(10));

		Label lblFileCopy = createLabel(fileConfigGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.fileConfigGroup.fileCopyLabel"));
		lblFileCopy.setLayoutData(getBaseFormData(0, marginLabel, 0, 0, middle, -margin));

		Button btnFileCopyBrowse = createButton(fileConfigGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.dialog.browse2"), chooseFileToCopy());
		btnFileCopyBrowse.setLayoutData(getBaseFormDataRightDirection(0, btnTopMargin, 100, 0));

		txtFileCopy = new Text(fileConfigGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(txtFileCopy);
		txtFileCopy.setLayoutData(getBaseFormData(0, margin, lblFileCopy, margin, btnFileCopyBrowse, -margin));

		Label lblFolderCopy = createLabel(fileConfigGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.fileConfigGroup.folderDumpLabel"));
		lblFolderCopy.setLayoutData(getBaseFormData(txtFileCopy, marginLabel, 0, 0, middle, -margin));

		Button btnFolderCopyBrowse = createButton(fileConfigGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.dialog.browse3"), chooseFolderToDump());
		props.setLook(btnFolderCopyBrowse);
		btnFolderCopyBrowse.setLayoutData(getBaseFormDataRightDirection(txtFileCopy, btnTopMargin, 100, 0));

		txtFolderDump = new Text(fileConfigGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(txtFolderDump);
		txtFolderDump.setLayoutData(
				getBaseFormData(txtFileCopy, margin, lblFolderCopy, margin, btnFolderCopyBrowse, -margin));

		Label lblFileInfo = new Label(fileConfigGroup, SWT.BOLD | SWT.CENTER);
		lblFileInfo.setText(
				BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.fileConfigGroup.fileInfoLabel"));
		props.setLook(lblFileInfo);
		lblFileInfo.setLayoutData(getBaseFormData(txtFolderDump, marginLabel, 0, 0, 100, 0));

		// Final settings for Google Config Tab

		FormData fdFolderTabs = getBaseFormData(txtOutputField, margin, 0, 0, 100, 0);
		fdFolderTabs.bottom = new FormAttachment(100, -50);
		folderTabs.setLayoutData(fdFolderTabs);

		// Impersonate Config Group

		Group impersonateGroup = createGroup(googleConfigComposite,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.impersonateUserGroup.text"));
		impersonateGroup.setLayout(getFormLayout(10));

		Label lblImpersonateUser = createLabel(impersonateGroup, BaseMessages.getString(PKG,
				"DriveCopyStepDialog.googleConfigTab.impersonateUserGroup.impersonateUserLabel"));
		lblImpersonateUser.setLayoutData(getBaseFormData(0, marginLabel, 0, 0, middle, -margin));

		txtImpersonateUser = new TextVar(transMeta, impersonateGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(txtImpersonateUser);
		txtImpersonateUser.setLayoutData(getBaseFormData(0, margin, lblImpersonateUser, margin, 100, 0));

		Link lblImpersonateUserInfo = new Link(impersonateGroup, SWT.BOLD | SWT.CENTER);
		lblImpersonateUserInfo
				.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.impersonateUserGroup.info"));
		props.setLook(lblImpersonateUserInfo);
		lblImpersonateUserInfo.setLayoutData(getBaseFormData(txtImpersonateUser, margin, 0, 0, 100, 0));
		lblImpersonateUserInfo.addSelectionListener(getLinkSelected());

		// Permissions Tab

		CTabItem permissionTab = new CTabItem(folderTabs, SWT.NONE);
		permissionTab.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.label"));

		Composite permissionComposite = new Composite(folderTabs, SWT.NONE);
		props.setLook(permissionComposite);
		permissionComposite.setLayout(getFormLayout(3));

		// Input Permission Group

		Group inputPermissionGroup = createGroup(permissionComposite,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.InputPermission.groupLabel"));
		inputPermissionGroup.setLayout(getFormLayout(10));

		Label lblCkPermissionInput = createLabel(inputPermissionGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.InputPermission.cklabel"));
		lblCkPermissionInput.setLayoutData(getBaseFormData(0, marginCkLabel, 0, 0, middle, -margin));

		ckInputPermission = createCheckbox(inputPermissionGroup);
		ckInputPermission.setLayoutData(getBaseFormData(0, margin, lblCkPermissionInput, margin, 100, 0));

		Label lblAccountInput = createLabel(inputPermissionGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.InputPermission.accountInput.label"));
		lblAccountInput.setLayoutData(getBaseFormData(ckInputPermission, marginComboLabel, 0, 0, middle, -margin));

		txtAccountInput = new TextVar(transMeta, inputPermissionGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(txtAccountInput);
		txtAccountInput.setLayoutData(getBaseFormData(ckInputPermission, margin, lblAccountInput, margin,
				middle + (100 - middle) / 2, margin));

		Label lblRoleInput = createLabel(inputPermissionGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.roleCombo.label"));
		lblRoleInput.setLayoutData(
				getBaseFormData(ckInputPermission, marginComboLabel, 0, 0, middle + 2 * (100 - middle) / 3, -margin));

		comboRoleInput = createRoleCombo(inputPermissionGroup);
		comboRoleInput.setLayoutData(getBaseFormData(ckInputPermission, margin, lblRoleInput, margin, 100, margin));

		Label lblCkNotifyEmailInput = createLabel(inputPermissionGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.ckNotifyEmail.label"));
		lblCkNotifyEmailInput.setLayoutData(getBaseFormData(txtAccountInput, marginCkLabel, 0, 0, middle, -margin));

		ckNotifyEmailInput = createCheckbox(inputPermissionGroup);
		ckNotifyEmailInput
				.setLayoutData(getBaseFormData(txtAccountInput, margin, lblCkNotifyEmailInput, margin, 100, 0));

		ckInputPermission.addSelectionListener(toogleCheckBox(txtAccountInput, comboRoleInput, ckNotifyEmailInput));

		Label lblCkCustomMessageInput = createLabel(inputPermissionGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.ckCustomMessage"));
		lblCkCustomMessageInput.setLayoutData(getBaseFormData(ckNotifyEmailInput, marginCkLabel, 0, 0, middle, -margin));

		ckCustomMessageInput = createCheckbox(inputPermissionGroup);
		ckCustomMessageInput
				.setLayoutData(getBaseFormData(ckNotifyEmailInput, margin, lblCkCustomMessageInput, margin, 100, 0));

		ckNotifyEmailInput.addSelectionListener(toogleCheckBox(ckCustomMessageInput));

		Label lblTxtCustomMessageInput = createLabel(inputPermissionGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.ckCustomMessage.label"));
		lblTxtCustomMessageInput.setLayoutData(getBaseFormData(ckCustomMessageInput, margin, 0, 0, middle, -margin));

		txtCustomMessage = new StyledTextComp(transMeta, inputPermissionGroup,
				SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL, "");
		props.setLook(txtCustomMessage, Props.WIDGET_STYLE_FIXED);
		FormData fdTxtCustomMessage = getBaseFormData(ckCustomMessageInput, margin, lblTxtCustomMessageInput, margin,
				100, 0);
		fdTxtCustomMessage.bottom = new FormAttachment(100, -margin);
		txtCustomMessage.setLayoutData(fdTxtCustomMessage);

		ckCustomMessageInput.addSelectionListener(toogleCheckBox(txtCustomMessage));
		ckInputPermission.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {

				if (!ckInputPermission.getSelection() && ckCustomMessageInput.isEnabled()) {
					ckNotifyEmailInput.setSelection(false);
					ckCustomMessageInput.setSelection(false);
					ckCustomMessageInput.setEnabled(false);
					txtCustomMessage.setEnabled(false);
				}
			}
		});

		// Field Permission Group

		Group fieldPermissionGroup = createGroup(permissionComposite,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.fieldPermission.groupLabel"));
		fieldPermissionGroup.setLayout(getFormLayout(10));

		Label lblCkPermissionField = createLabel(fieldPermissionGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.fieldPermission.label"));
		lblCkPermissionField.setLayoutData(getBaseFormData(0, marginCkLabel, 0, 0, middle, -margin));

		ckPermissionField = createCheckbox(fieldPermissionGroup);
		ckPermissionField.setLayoutData(getBaseFormData(0, margin, lblCkPermissionField, margin, 100, 0));

		Label lblPermissionFieldCombo = createLabel(fieldPermissionGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.fieldPermission.fieldCombo"));
		lblPermissionFieldCombo.setLayoutData(getBaseFormData(ckPermissionField, marginComboLabel, 0, 0, middle, -margin));

		comboAccountField = createFieldsCombo(fieldPermissionGroup);
		comboAccountField.setLayoutData(getBaseFormData(ckPermissionField, margin, lblPermissionFieldCombo, margin,
				middle + (100 - margin) / 3, margin));
		comboAccountField.addFocusListener(loadComboOptions());

		Label lblComboRoleField = createLabel(fieldPermissionGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.roleCombo.label"));
		lblComboRoleField.setLayoutData(
				getBaseFormData(ckPermissionField, marginComboLabel, 0, 0, middle + 2 * (100 - middle) / 3, -margin));

		comboRoleField = createRoleCombo(fieldPermissionGroup);
		comboRoleField
				.setLayoutData(getBaseFormData(ckPermissionField, margin, lblComboRoleField, margin, 100, margin));

		Label lblCkNotifyEmailField = createLabel(fieldPermissionGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.ckNotifyEmail.label"));
		lblCkNotifyEmailField.setLayoutData(getBaseFormData(comboRoleField, marginCkLabel, 0, 0, middle, -margin));

		ckNotifyEmailField = createCheckbox(fieldPermissionGroup);
		ckNotifyEmailField
				.setLayoutData(getBaseFormData(comboRoleField, margin, lblCkNotifyEmailField, margin, 100, 0));

		ckPermissionField.addSelectionListener(toogleCheckBox(comboAccountField, comboRoleField, ckNotifyEmailField));

		Label lblCkCustomMessageField = createLabel(fieldPermissionGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.ckCustomMessage"));
		lblCkCustomMessageField.setLayoutData(getBaseFormData(ckNotifyEmailField, marginCkLabel, 0, 0, middle, -margin));

		ckCustomMessageField = createCheckbox(fieldPermissionGroup);
		ckCustomMessageField
				.setLayoutData(getBaseFormData(ckNotifyEmailField, margin, lblCkCustomMessageField, margin, 60, 0));

		ckNotifyEmailField.addSelectionListener(toogleCheckBox(ckCustomMessageField));

		Label lblComboCustomMessageField = createLabel(fieldPermissionGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.ckCustomMessage.label"));
		lblComboCustomMessageField.setLayoutData(
				getBaseFormData(ckNotifyEmailField, marginComboLabel, 0, 0, middle + 2 * (100 - middle) / 3, -margin));

		comboCustomMessageField = createFieldsCombo(fieldPermissionGroup);
		comboCustomMessageField.setLayoutData(
				getBaseFormData(ckNotifyEmailField, margin, lblComboCustomMessageField, margin, 100, margin));
		comboCustomMessageField.addFocusListener(loadComboOptions());

		ckCustomMessageField.addSelectionListener(toogleCheckBox(comboCustomMessageField));

		ckPermissionField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {

				if (!ckPermissionField.getSelection() && ckCustomMessageField.isEnabled()) {
					ckNotifyEmailField.setSelection(false);
					ckCustomMessageField.setEnabled(false);
					ckCustomMessageField.setSelection(false);
					comboCustomMessageField.setEnabled(false);
				}
			}
		});

		// Any Permission Group

		Group anyPermissionGroup = createGroup(permissionComposite,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.anyPermission.groupLabel"));
		anyPermissionGroup.setLayout(getFormLayout(10));

		Label lblCkAnyPermission = createLabel(anyPermissionGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.anyPermission.label"));
		lblCkAnyPermission.setLayoutData(getBaseFormData(0, marginCkLabel, 0, 0, middle, -margin));

		ckAnyPermission = createCheckbox(anyPermissionGroup);
		ckAnyPermission.setLayoutData(getBaseFormData(0, margin, lblCkAnyPermission, margin, 60, 0));

		Label lblComboAnyPermissionRole = createLabel(anyPermissionGroup,
				BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.roleCombo.label"));
		lblComboAnyPermissionRole
				.setLayoutData(getBaseFormData(0, marginComboLabel, 0, 0, middle + 2 * (100 - middle) / 3, -margin));

		comboAnyPermissionRole = createRoleCombo(anyPermissionGroup);
		comboAnyPermissionRole
				.setLayoutData(getBaseFormData(0, margin, lblComboAnyPermissionRole, margin, 100, margin));

		ckAnyPermission.addSelectionListener(toogleCheckBox(comboAnyPermissionRole));

		// Groups Form Data Config
		googleConfigComposite.layout();
		googleConfigTab.setControl(googleConfigComposite);

		permissionComposite.layout();
		permissionTab.setControl(permissionComposite);

		serviceAccountGroup.setLayoutData(getBaseFormData(txtOutputField, margin, 0, margin, 100, -margin));
		fileConfigGroup.setLayoutData(getBaseFormData(serviceAccountGroup, margin, 0, margin, 100, -margin));
		impersonateGroup.setLayoutData(getBaseFormData(fileConfigGroup, margin, 0, margin, 100, -margin));

		FormData fdInputPermissionGroup = getBaseFormData(txtOutputField, margin, 0, margin, 100, -margin);
		fdInputPermissionGroup.bottom = new FormAttachment(60, 0);
		inputPermissionGroup.setLayoutData(fdInputPermissionGroup);
		fieldPermissionGroup.setLayoutData(getBaseFormData(inputPermissionGroup, margin, 0, margin, 100, -margin));
		anyPermissionGroup.setLayoutData(getBaseFormData(fieldPermissionGroup, margin, 0, margin, 100, -margin));

		// Ok and Cancel buttons

		SelectionListener btnListener = getOkCancelListener();
		Button btnOk = createButton(shell, BaseMessages.getString(PKG, "DriveCopyStepDialog.ok.label"), btnListener);
		Button btnCancel = createButton(shell, BaseMessages.getString(PKG, "DriveCopyStepDialog.cancel.label"),
				btnListener);

		setButtonPositions(new Button[] { btnOk, btnCancel }, margin, folderTabs);
		folderTabs.setSelection(0);
	}

	private void setModifyListener(){
		ModifyListener mdListener = getMetaModifyListener();
		wStepname.addModifyListener(mdListener);
		titleFieldSelect.addModifyListener(mdListener);
		txtOutputField.addModifyListener(mdListener);
		
		txtSvAccountEmail.addModifyListener(mdListener);
		txtSvKeyFile.addModifyListener(mdListener);
		
		txtImpersonateUser.addModifyListener(mdListener);
		
		txtAccountInput.addModifyListener(mdListener);
		comboRoleInput.addModifyListener(mdListener);
		txtCustomMessage.addModifyListener(mdListener);
		
		comboAccountField.addModifyListener(mdListener);
		comboRoleField.addModifyListener(mdListener);
		comboCustomMessageField.addModifyListener(mdListener);
		
		comboAnyPermissionRole.addModifyListener(mdListener);
	}
	
	@Override
	protected Button createHelpButton(Shell shell, StepMeta stepMeta, PluginInterface plugin) {
		return null;
	}

	private ModifyListener enableTestConnectionButton() {
		return new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent evt) {
				btnTestConnection.setEnabled(!txtSvAccountEmail.getText().isEmpty()
						&& !txtSvAccountEmail.getText().startsWith("$") && !txtSvKeyFile.getText().isEmpty());
			}
		};
	}

	private SelectionAdapter toogleCheckBox(Control... controls) {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				if (evt.widget instanceof Button) {
					Button btnCk = (Button) evt.widget;
					for (Control ctrlComponent : controls) {
						ctrlComponent.setEnabled(btnCk.getSelection());
					}
					meta.setChanged();
				}
			}
		};
	}

	private CCombo createRoleCombo(Composite composite) {
		CCombo roleCombo = new CCombo(composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		roleCombo.setEditable(false);
		props.setLook(roleCombo);
		roleCombo.add("");
		roleCombo.add(DriveRolePermission.reader.toString().toUpperCase());
		roleCombo.add(DriveRolePermission.commenter.toString().toUpperCase());
		roleCombo.add(DriveRolePermission.writer.toString().toUpperCase());
		return roleCombo;
	}

	private SelectionAdapter testConnection() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				try {
					GoogleConnection con = new GoogleConnection(txtSvKeyFile.getText(), txtSvAccountEmail.getText(),
							driveScopes);
					if (con.isConnected() && con.getDrive() != null) {
						lblTestConectionResult.setText(BaseMessages.getString(PKG,
								"DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.success"));
					} else {
						lblTestConectionResult.setText(BaseMessages.getString(PKG,
								"DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.fail"));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					new ErrorDialog(shell, BaseMessages.getString(PKG, "DriveCopyStepDialog.error.testConnection"),
							e.getMessage(), e);
					lblTestConectionResult.setText(BaseMessages.getString(PKG,
							"DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.fail"));
				}
			}
		};
	}

	private SelectionAdapter keyFileOpenDialog() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
				fileDialog.setFilterExtensions(new String[] { "*.p12;*.json", "*.p12", "*.json" });
				fileDialog.setFilterNames(
						new String[] { "Google p12 or json keyfiles", "Google p12 keyfiles", "Google json keyfiles" });
				String fileDir = fileDialog.open();
				if (fileDir != null) {
					txtSvKeyFile.setText(fileDir);
				}
			}
		};
	}
	
	private SelectionListener chooseFileToCopy() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				try {
					GoogleConnection con = new GoogleConnection(txtSvKeyFile.getText(), txtSvAccountEmail.getText(),
							driveScopes);
					if (!con.isConnected()) {
						throw new Exception("No connected");
					}
					Drive service = con.getDrive();
					List<File> sharedFiles = DriveFileManagement.getSharedFiles(service);
					HashMap<String, File> files = new HashMap<>();
					String ownerText = BaseMessages.getString(PKG, "DriveCopyStepDialog.dialog.owner");
					String selectedFile = null, fileNameFormat;
					for (File file : sharedFiles) {
						fileNameFormat = String.format("%s (%s: %s)", file.getName(), ownerText,
								file.getOwners().get(0).getEmailAddress());
						files.put(fileNameFormat, file);
						if (file.getId().equals(txtFileCopy.getText())) {
							selectedFile = fileNameFormat;
						}
					}
					String[] titles = files.keySet().toArray(new String[files.size()]);
					EnterSelectionDialog esd = new EnterSelectionDialog(shell, titles,
							BaseMessages.getString(PKG, "DriveCopyStepDialog.fileCopyDialog.title"),
							BaseMessages.getString(PKG, "DriveCopyStepDialog.fileCopyDialog.message"));
					if (selectedFile != null && !selectedFile.isEmpty()) {
						esd.setSelectedNrs(new int[] { Arrays.binarySearch(titles, selectedFile) });
					}
					String selectedOption = esd.open();
					if (selectedOption != null) {
						File fileSelected = files.get(selectedOption);
						txtFileCopy.setText(fileSelected.getId());
					}
				} catch (Exception err) {
					new ErrorDialog(shell, BaseMessages.getString(PKG, "DriveCopyStepDialog.error.fileToCopy.title"),
							err.getMessage(), err);
				}
			}
		};
	}

	private SelectionListener chooseFolderToDump() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				try {
					GoogleConnection con = new GoogleConnection(txtSvKeyFile.getText(), txtSvAccountEmail.getText(),
							driveScopes);
					if (!con.isConnected()) {
						throw new Exception("No connected");
					}
					Drive service = con.getDrive();
					List<File> sharedFiles = DriveFileManagement.getSharedFiles(service,
							DriveFileMimeTypes.FOLDER_MIME_TYPE);
					HashMap<String, File> files = new HashMap<>();
					String ownerText = BaseMessages.getString(PKG, "DriveCopyStepDialog.dialog.owner");
					String selectedFile = null, fileNameFormat;
					for (File file : sharedFiles) {
						fileNameFormat = String.format("%s (%s: %s)", file.getName(), ownerText,
								file.getOwners().get(0).getEmailAddress());
						files.put(fileNameFormat, file);
						if (file.getId().equals(txtFolderDump.getText())) {
							selectedFile = fileNameFormat;
						}
					}
					String[] titles = files.keySet().toArray(new String[files.size()]);
					EnterSelectionDialog esd = new EnterSelectionDialog(shell, titles,
							BaseMessages.getString(PKG, "DriveCopyStepDialog.folderDumpDialog.title"),
							BaseMessages.getString(PKG, "DriveCopyStepDialog.folderDumpDialog.message"));
					if (selectedFile != null && !selectedFile.isEmpty()) {
						esd.setSelectedNrs(new int[] { Arrays.binarySearch(titles, selectedFile) });
					}
					String selectedOption = esd.open();
					if (selectedOption != null) {
						File fileSelected = files.get(selectedOption);
						txtFolderDump.setText(fileSelected.getId());
					}
				} catch (Exception err) {
					new ErrorDialog(shell, BaseMessages.getString(PKG, "DriveCopyStepDialog.error.fileToCopy.title"),
							err.getMessage(), err);
				}
			}
		};
	}

	@Override
	protected ModifyListener getMetaModifyListener() {
		return new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				meta.setChanged();
			}
		};
	}

	@Override
	protected SelectionListener getOkCancelListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				if (evt.widget instanceof Button) {
					Button btnSource = (Button) evt.widget;
					if (btnSource.getText().equals(BaseMessages.getString(PKG, "DriveCopyStepDialog.ok.label"))) {
						okAction();
					} else {
						cancelAction();
					}
				}
			}
		};
	}

	private SelectionAdapter getLinkSelected() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evtSelection) {
				BareBonesBrowserLaunch.openURL(REFERENCE_GSUITE_DOMAIN_INFO);
			}
		};
	}

	@Override
	protected ShellAdapter getShellAdapter() {
		return new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent arg0) {
				// TODO Auto-generated method stub
				cancelAction();
			}
		};
	}

	private FocusAdapter loadComboOptions() {
		return new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent evt) {
				if (evt.widget instanceof CCombo) {
					Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
					shell.setCursor(busy);
					CCombo combo = (CCombo) evt.widget;
					try {
						RowMetaInterface r = transMeta.getPrevStepFields(stepname);
						String fieldValue = combo.getText();

						if (r != null && r.getFieldNames().length > 0) {
							combo.setItems(r.getFieldNames());
						} else {
							throw new KettleStepException( BaseMessages.getString(PKG, "DriveCopyStepDialog.error.fieldCombo.required"));
						}

						if (fieldValue != null && !fieldValue.isEmpty() ) {
							combo.setText(fieldValue);
						}
					} catch (KettleStepException e) {
						// TODO Auto-generated catch block
						new ErrorDialog(shell,
								BaseMessages.getString(PKG, "DriveCopyStepDialog.error.fieldCombo.title"),
								e.getMessage(), e);
						shell.setFocus();
					}
					shell.setCursor(null);
					busy.dispose();
				}

			}
		};
	}

	private void setInfo(DriveCopyStepMeta driveCopyMeta){
		driveCopyMeta.setTitleFieldSelected(titleFieldSelect.getText());
		driveCopyMeta.setOutputField(txtOutputField.getText());
		
		driveCopyMeta.setServiceEmail(txtSvAccountEmail.getText());
		driveCopyMeta.setServiceKeyFile(txtSvKeyFile.getText());
		
		driveCopyMeta.setDriveFileToCopy(txtFileCopy.getText());
		driveCopyMeta.setDriveFolderToDump(txtFolderDump.getText());
		
		driveCopyMeta.setImpersonateUser(txtImpersonateUser.getText());
		
		driveCopyMeta.setCheckedInputAccess(ckInputPermission.getSelection());
		driveCopyMeta.setInputEmailAccount(txtAccountInput.getText());
		driveCopyMeta.setInputRole(comboRoleInput.getText());
		driveCopyMeta.setInputCheckedNotifyEmail(ckNotifyEmailInput.getSelection());
		driveCopyMeta.setInputCheckedCustomEmail(ckCustomMessageInput.getSelection());
		driveCopyMeta.setInputCustomMessage(txtCustomMessage.getText());
		
		driveCopyMeta.setCheckedFieldAccess(ckPermissionField.getSelection());
		driveCopyMeta.setFieldAccount(comboAccountField.getText());
		driveCopyMeta.setFieldRole(comboRoleField.getText());
		driveCopyMeta.setFieldCheckedNotifyEmail(ckNotifyEmailField.getSelection());
		driveCopyMeta.setFieldCheckedCustomEmail(ckCustomMessageField.getSelection());
		driveCopyMeta.setFieldCustomMessage(comboCustomMessageField.getText());
		
		driveCopyMeta.setCheckedAnyAccess(ckAnyPermission.getSelection());
		driveCopyMeta.setAnyoneRole(comboAnyPermissionRole.getText());
		
		driveCopyMeta.setChanged();
	}
	
	private void okAction() {
		if(Utils.isEmpty( wStepname.getText() ) && Utils.isEmpty(txtOutputField.getText())){
			return;
		}
		setInfo(meta);
		stepname = wStepname.getText();
		dispose();
	}

	private void cancelAction() {
		stepname = null;
		meta.setChanged(changed);
		dispose();
	}

}
