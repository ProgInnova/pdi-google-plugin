package com.proginnova.pentaho.gdrive.steps.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.common.BareBonesBrowserLaunch;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.ui.core.widget.LabelText;
import org.pentaho.di.ui.core.widget.StyledTextComp;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import com.proginnova.pentaho.gdrive.steps.meta.DriveCopyStepMeta;
import com.proginnova.pentaho.ui.BasicStepDialog;
import com.sun.javafx.PlatformUtil;

import jdk.nashorn.internal.ir.ForNode;

public class DriveCopyStepDialog extends BasicStepDialog {
	
	private static Class<?> PKG = DriveCopyStepDialog.class;
	private DriveCopyStepMeta meta;
	
	private Display display;
	private String REFERENCE_GSUITE_DOMAIN_INFO = "https://developers.google.com/admin-sdk/directory/v1/guides/delegation";
	

	public DriveCopyStepDialog(Shell parent, Object baseStepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) baseStepMeta, transMeta, stepname);
		// TODO Auto-generated constructor stub
		meta = (DriveCopyStepMeta) baseStepMeta;
	}
	
	

	@Override
	public String open() {
		// TODO Auto-generated method stub
		setDialogView();
		//setSize();
		//shell.pack();
		BaseStepDialog.setSize(shell);
		shell.open();
		while(!shell.isDisposed()){
			if(!display.readAndDispatch()){
				display.sleep();
			}
		}
		return stepname;
	}
	
	private void setDialogView(){
		Shell parent = this.getParent();
		display = parent.getDisplay();
		ModifyListener modifylistener = getMetaModifyListener();
		
		shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.CENTER | SWT.MIN | SWT.MAX );
		shell.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.dialog.label"));
		props.setLook(shell);
		setShellImage(shell, meta);
		changed = meta.hasChanged();
		
		shell.setLayout( getFormLayout(Const.FORM_MARGIN) );
		int margin = Const.MARGIN;
		int middle = props.getMiddlePct();
		
		int btnTopMargin = (System.getProperty("os.name").toLowerCase().contains("mac"))? 0:margin;
		
		// Step Name
		
		wlStepname = createLabel(shell, BaseMessages.getString(PKG, "DriveCopyStepDialog.stepName.label") );
        fdlStepname = getBaseFormData(null, margin, middle, true);
        wlStepname.setLayoutData(fdlStepname);
        
        wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wStepname.setText(stepname);
        props.setLook(wStepname);
        //wStepname.addModifyListener(modifiedListener);
        fdStepname = getBaseFormData(null, margin, middle, false);
        wStepname.setLayoutData(fdStepname);
        
        // Title by field
        
        Label lblTitleByField = createLabel(shell, BaseMessages.getString(PKG, "DriveCopyStepDialog.titleFieldInput.label") );
        lblTitleByField.setLayoutData(getBaseFormData(wStepname, margin, middle, true));
        
        CCombo titleFieldSelect = createFieldsCombo(shell);
        titleFieldSelect.setLayoutData( getBaseFormData(wStepname, margin, middle, false) );
        
        // Output field
        
        Label lblOutputField = createLabel(shell, BaseMessages.getString(PKG, "DriveCopyStepDialog.outputFieldInput.label") );
        lblOutputField.setLayoutData( getBaseFormData(titleFieldSelect, margin, middle, true) );
        
        Text txtOutputField = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        txtOutputField.setLayoutData( getBaseFormData(titleFieldSelect, margin, middle, false) );
        props.setLook(txtOutputField);
        
        // Tabs
        
        CTabFolder folderTabs = new CTabFolder(shell, SWT.BORDER);
        props.setLook(folderTabs, Props.WIDGET_STYLE_TAB);
        
        CTabItem googleConfigTab = new CTabItem(folderTabs, SWT.NONE);
        googleConfigTab.setText( BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.label") );
        
        
        Composite googleConfigComposite = new Composite(folderTabs, SWT.NONE);
        props.setLook(googleConfigComposite);
        googleConfigComposite.setLayout( getFormLayout(3) );
        
        // Service Account Config Group
        
        Group serviceAccountGroup = createGroup(googleConfigComposite, BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.label") );
        serviceAccountGroup.setLayout( getFormLayout(10) );
        
        Label lblsvAccountEmail = createLabel(serviceAccountGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.emailLabel") );
        lblsvAccountEmail.setLayoutData( getBaseFormData(0, 2 * margin, 0, 0, middle, -margin) );
        props.setLook(lblsvAccountEmail);
        
        TextVar txtSvAccountEmail = new TextVar(transMeta, serviceAccountGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        txtSvAccountEmail.setLayoutData( getBaseFormData(0, 2 * margin, middle, 0, 100, 0) );
        props.setLook(txtSvAccountEmail);
        
        Label lblSvAccountKeyfile = createLabel(serviceAccountGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.keyFileLabel"));
        props.setLook(lblSvAccountKeyfile);
        lblSvAccountKeyfile.setLayoutData( getBaseFormData(txtSvAccountEmail, margin, 0, 0, middle, -margin) );
        
        TextVar txtSvKeyFile = new TextVar(transMeta, serviceAccountGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(txtSvKeyFile);
        txtSvKeyFile.setLayoutData( getBaseFormData(txtSvAccountEmail, margin, lblSvAccountKeyfile, margin, 90, -margin) );
        
        Button btnSvKeyFileBrowse = createButton(serviceAccountGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.dialog.browse"), null);
        btnSvKeyFileBrowse.setToolTipText( BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.keyFileBrowseTooltip") );
        btnSvKeyFileBrowse.setLayoutData( getBaseFormDataLeftDirection(txtSvAccountEmail, btnTopMargin, txtSvKeyFile, margin) );
        
        Button btnTestConnection = createButton(serviceAccountGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.testConnection"), null);
        btnTestConnection.setLayoutData( getBaseFormDataLeftDirection(btnSvKeyFileBrowse, margin, middle, -margin) );
        
        
        // File Config Group
        
        Group fileConfigGroup = createGroup(googleConfigComposite, BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.fileConfigGroup.label"));
        fileConfigGroup.setLayout(getFormLayout(10));
        
        Label lblFileCopy = createLabel(fileConfigGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.fileConfigGroup.fileCopyLabel"));
        lblFileCopy.setLayoutData(getBaseFormData(0, margin, 0, 0, middle, -margin));
        
        Button btnFileCopyBrowse = createButton(fileConfigGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.dialog.browse2"), null);
        btnFileCopyBrowse.setLayoutData(getBaseFormDataRightDirection(0, btnTopMargin, 100, 0));
        
        Text txtFileCopy = new Text(fileConfigGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
        props.setLook(txtFileCopy);
        txtFileCopy.setLayoutData(getBaseFormData(0, margin, lblFileCopy, margin, btnFileCopyBrowse, -margin));
        
        
        Label lblFolderCopy = createLabel(fileConfigGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.fileConfigGroup.folderDumpLabel"));
        lblFolderCopy.setLayoutData( getBaseFormData(txtFileCopy, margin, 0, 0, middle, -margin) );
        
        Button btnFolderCopyBrowse = createButton(fileConfigGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.dialog.browse3"), null);
        props.setLook(btnFolderCopyBrowse);
        btnFolderCopyBrowse.setLayoutData( getBaseFormDataRightDirection(txtFileCopy, btnTopMargin, 100, 0) );
        
        Text txtFolderDump = new Text(fileConfigGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
        props.setLook(txtFolderDump);
        txtFolderDump.setLayoutData( getBaseFormData(txtFileCopy, margin, lblFolderCopy, margin, btnFolderCopyBrowse, -margin) );
        
        Label lblFileInfo = new Label(fileConfigGroup, SWT.BOLD | SWT.CENTER);
        lblFileInfo.setText( BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.fileConfigGroup.fileInfoLabel") );
        props.setLook(lblFileInfo);
        lblFileInfo.setLayoutData( getBaseFormData(txtFolderDump, margin, 0, 0, 100, 0) );
        
        // Final settings for Google Config Tab
        
        FormData fdFolderTabs = getBaseFormData(txtOutputField, margin, 0, 0, 100, 0);
        fdFolderTabs.bottom = new FormAttachment(100, -50);
        folderTabs.setLayoutData(fdFolderTabs);
        
        // Impersonate Config Group
        
        Group impersonateGroup = createGroup(googleConfigComposite, BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.impersonateUserGroup.text"));
        impersonateGroup.setLayout( getFormLayout(10) );
        
        Label lblImpersonateUser = createLabel(impersonateGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.impersonateUserGroup.impersonateUserLabel") );
        lblImpersonateUser.setLayoutData( getBaseFormData(0, margin, 0, 0, middle, -margin) );
        
        TextVar txtImpersonateUser = new TextVar(transMeta, impersonateGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(txtImpersonateUser);
        txtImpersonateUser.setLayoutData( getBaseFormData(0, margin, lblImpersonateUser, margin, 100, 0) );
        
        Link lblImpersonateUserInfo = new Link(impersonateGroup, SWT.BOLD | SWT.CENTER);
        lblImpersonateUserInfo.setText( BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.impersonateUserGroup.info") );
        props.setLook(lblImpersonateUserInfo);
        lblImpersonateUserInfo.setLayoutData( getBaseFormData(txtImpersonateUser, margin, 0, 0, 100, 0) );
        lblImpersonateUserInfo.addSelectionListener( getLinkSelected() );
        
        
        // Permissions Tab
        
        CTabItem permissionTab = new CTabItem(folderTabs, SWT.NONE);
        permissionTab.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.label"));
        
        Composite permissionComposite = new Composite(folderTabs, SWT.NONE);
        props.setLook(permissionComposite);
        permissionComposite.setLayout( getFormLayout(3) );
        
        // Input Permission Group
        
        Group inputPermissionGroup = createGroup(permissionComposite, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.InputPermission.groupLabel"));
        inputPermissionGroup.setLayout( getFormLayout(10) );
        
        Label lblCkPermissionInput = createLabel(inputPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.InputPermission.cklabel"));
        lblCkPermissionInput.setLayoutData( getBaseFormData(0, margin, 0, 0, middle, -margin) );
        
        Button ckInputPermission = createCheckbox(inputPermissionGroup, null);
        ckInputPermission.setLayoutData( getBaseFormData(0, margin, lblCkPermissionInput, margin, 100, 0) );
        
        Label lblAccountInput = createLabel(inputPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.InputPermission.accountInput.label"));
        lblAccountInput.setLayoutData( getBaseFormData(ckInputPermission, margin, 0, 0, middle, -margin));
        
        TextVar txtAccountInput = new TextVar(transMeta, inputPermissionGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(txtAccountInput);
        txtAccountInput.setLayoutData( getBaseFormData(ckInputPermission, margin, lblAccountInput, margin, middle + (100 - middle) / 2, margin));
        
        Label lblRoleInput = createLabel(inputPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.roleCombo.label"));
        lblRoleInput.setLayoutData( getBaseFormData(ckInputPermission, margin, 0, 0, middle + 2 * (100 - middle) / 3, -margin) );
        
        CCombo comboRoleInput = createRoleCombo(inputPermissionGroup);
        comboRoleInput.setLayoutData( getBaseFormData(ckInputPermission, margin, lblRoleInput, margin, 100, margin));
        
        Label lblCkNotifyEmailInput = createLabel(inputPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.ckNotifyEmail.label"));
        lblCkNotifyEmailInput.setLayoutData( getBaseFormData(txtAccountInput, margin, 0, 0, middle, -margin));
        
        Button ckNotifyEmailInput = createCheckbox(inputPermissionGroup, null);
        ckNotifyEmailInput.setLayoutData( getBaseFormData(txtAccountInput, margin, lblCkNotifyEmailInput, margin, 100, 0));
        
        Label lblCkCustomMessageInput = createLabel(inputPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.ckCustomMessage"));
        lblCkCustomMessageInput.setLayoutData( getBaseFormData(ckNotifyEmailInput, margin, 0, 0, middle, -margin));
        
        Button ckCustomMessageInput = createCheckbox(inputPermissionGroup, null);
        ckCustomMessageInput.setLayoutData( getBaseFormData(ckNotifyEmailInput, margin, lblCkCustomMessageInput, margin, 100, 0));
        
        Label lblTxtCustomMessageInput = createLabel(inputPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.ckCustomMessage.label"));
        lblTxtCustomMessageInput.setLayoutData(getBaseFormData(ckCustomMessageInput, margin, 0, 0, middle, -margin));
        
        StyledTextComp txtCustomMessage = new StyledTextComp(transMeta, inputPermissionGroup, SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL, "");
        props.setLook(txtCustomMessage, Props.WIDGET_STYLE_FIXED);
        FormData fdTxtCustomMessage = getBaseFormData(ckCustomMessageInput, margin, lblTxtCustomMessageInput, margin, 100, 0);
        fdTxtCustomMessage.bottom = new FormAttachment(100, -margin);
        txtCustomMessage.setLayoutData(fdTxtCustomMessage);
        
        // Field Permission Group
        
        Group fieldPermissionGroup = createGroup(permissionComposite, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.fieldPermission.groupLabel"));
        fieldPermissionGroup.setLayout( getFormLayout(10) );
        
        Label lblCkPermissionField = createLabel(fieldPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.fieldPermission.label"));
        lblCkPermissionField.setLayoutData( getBaseFormData(0, margin, 0, 0, middle, -margin) );
        
        Button ckPermissionField = createCheckbox(fieldPermissionGroup, null);
        ckPermissionField.setLayoutData( getBaseFormData(0, margin, lblCkPermissionField, margin, 100, 0) );

        Label lblPermissionFieldCombo = createLabel(fieldPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.fieldPermission.fieldCombo"));
        lblPermissionFieldCombo.setLayoutData( getBaseFormData(ckPermissionField, margin, 0, 0, middle, -margin) );
        
        CCombo comboAccountField = createFieldsCombo(fieldPermissionGroup);
        comboAccountField.setLayoutData( getBaseFormData(ckPermissionField, margin, lblPermissionFieldCombo, margin, middle + (100 - margin) / 3, margin));
        
        Label lblComboRoleField = createLabel(fieldPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.roleCombo.label"));
        lblComboRoleField.setLayoutData( getBaseFormData(ckPermissionField, margin, 0, 0, middle + 2 * (100 -middle) / 3, -margin) );
        
        CCombo comboRoleField = createRoleCombo(fieldPermissionGroup);
        comboRoleField.setLayoutData( getBaseFormData(ckPermissionField, margin, lblComboRoleField, margin, 100, margin));
        
        Label lblCkNotifyEmailField = createLabel(fieldPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.ckNotifyEmail.label"));
        lblCkNotifyEmailField.setLayoutData( getBaseFormData(comboRoleField, margin, 0, 0, middle, -margin) );
        
        Button ckNotifyEmailField = createCheckbox(fieldPermissionGroup, null);
        ckNotifyEmailField.setLayoutData( getBaseFormData(comboRoleField, margin, lblCkNotifyEmailField, margin, 100, 0) );
        
        Label lblCkCustomMessageField = createLabel(fieldPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.ckCustomMessage"));
        lblCkCustomMessageField.setLayoutData( getBaseFormData(ckNotifyEmailField, margin, 0, 0, middle, -margin) );
        
        Button ckCustomMessageField = createCheckbox(fieldPermissionGroup, null);
        ckCustomMessageField.setLayoutData( getBaseFormData(ckNotifyEmailField, margin, lblCkCustomMessageField, margin, 60, 0) );
        
        Label lblComboCustomMessageField = createLabel(fieldPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.ckCustomMessage.label"));
        lblComboCustomMessageField.setLayoutData( getBaseFormData(ckNotifyEmailField, margin, 0, 0, middle + 2 * (100 - middle) / 3, -margin) );
        
        CCombo comboCustomMessageField = createFieldsCombo(fieldPermissionGroup);
        comboCustomMessageField.setLayoutData( getBaseFormData(ckNotifyEmailField, margin, lblComboCustomMessageField, margin, 100, margin) );
        
        // Any Permission Group
        
        Group anyPermissionGroup = createGroup(permissionComposite, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.anyPermission.groupLabel"));
        anyPermissionGroup.setLayout( getFormLayout(10) );
        
        Label lblCkAnyPermission = createLabel(anyPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.anyPermission.label"));
        lblCkAnyPermission.setLayoutData( getBaseFormData(0, margin, 0, 0, middle, -margin) );
        
        Button ckAnyPermission = createCheckbox(anyPermissionGroup, null);
        ckAnyPermission.setLayoutData( getBaseFormData(0, margin, lblCkAnyPermission, margin, 60, 0) );
        
        Label lblComboAnyPermissionRole = createLabel(anyPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.roleCombo.label"));
        lblComboAnyPermissionRole.setLayoutData( getBaseFormData(0, margin, 0, 0, middle + 2 * (100 - middle) / 3, -margin) );
        
        CCombo comboAnyPermissionRole = createRoleCombo(anyPermissionGroup);
        comboAnyPermissionRole.setLayoutData( getBaseFormData(0, margin, lblComboAnyPermissionRole, margin, 100, margin) );
        
        
        
        // Groups Form Data Config
        googleConfigComposite.layout();
        googleConfigTab.setControl(googleConfigComposite);
        
        permissionComposite.layout();
        permissionTab.setControl(permissionComposite);
        
        
        serviceAccountGroup.setLayoutData( getBaseFormData(txtOutputField, margin, 0, margin, 100, -margin) );
        fileConfigGroup.setLayoutData( getBaseFormData(serviceAccountGroup, margin, 0, margin, 100, -margin) );
        impersonateGroup.setLayoutData( getBaseFormData(fileConfigGroup, margin, 0, margin, 100, -margin) );
        
        FormData fdInputPermissionGroup = getBaseFormData(txtOutputField, margin, 0, margin, 100, -margin);
        fdInputPermissionGroup.bottom = new FormAttachment(60, 0);
        inputPermissionGroup.setLayoutData( fdInputPermissionGroup );
        fieldPermissionGroup.setLayoutData( getBaseFormData(inputPermissionGroup, margin, 0, margin, 100, -margin));
        anyPermissionGroup.setLayoutData( getBaseFormData(fieldPermissionGroup, margin, 0, margin, 100, -margin) );
		
        // Ok and Cancel buttons
		
		Listener btnListener = getOkCancelListener();
		Button btnOk = createButton(shell, BaseMessages.getString(PKG, "DriveCopyStepDialog.ok.label"), btnListener);
		Button btnCancel = createButton(shell, BaseMessages.getString(PKG, "DriveCopyStepDialog.cancel.label"), btnListener);
		
		setButtonPositions(new Button[]{btnOk, btnCancel}, margin, folderTabs);
		folderTabs.setSelection(0);
	}
	
	
	@Override
	protected Button createHelpButton(Shell shell, StepMeta stepMeta, PluginInterface plugin){
		return null;
	}
	
	private CCombo createRoleCombo(Composite composite){
		CCombo roleCombo = new CCombo(composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(roleCombo);
        return roleCombo;
	}
	
	
	private void setDataFromMeta(){
		
	}
	
	
	private Button createCheckbox(Composite composite, Listener listener){
		Button btn = new Button(composite, SWT.CHECK);
		return btn;
	}
	
	@Override
	protected ModifyListener getMetaModifyListener(){
		return new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				meta.setChanged();
			}
		};
	}
	
	@Override
	protected Listener  getOkCancelListener(){
		return new Listener() {
			
			@Override
			public void handleEvent(Event evt) {
				if(evt.widget instanceof Button){
					Button btnSource = (Button) evt.widget;
					if(btnSource.getText().equals(BaseMessages.getString(PKG, "DriveCopyStepDialog.ok.label"))){
						okAction();
					}else{
						cancelAction();
					}
				}
			}
		};
	}
	
	private SelectionAdapter getLinkSelected(){
		return new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent evtSelection) {
				BareBonesBrowserLaunch.openURL(REFERENCE_GSUITE_DOMAIN_INFO);
			}
		};
	}
	
	@Override
	protected ShellAdapter getShellAdapter(){
		return new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent arg0) {
				// TODO Auto-generated method stub
				cancelAction();
			}
		};
	}
	
	private void okAction(){
		
	}
	
	private void cancelAction(){
		
	}

}
