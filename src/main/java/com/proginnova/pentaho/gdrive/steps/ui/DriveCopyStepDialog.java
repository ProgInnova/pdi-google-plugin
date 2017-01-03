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
		
		// Step Name
		
		wlStepname = createLabel(shell, BaseMessages.getString(PKG, "DriveCopyStepDialog.stepName.label"));
        props.setLook(wlStepname);
        fdlStepname = getBaseFormData(null, margin, middle, true);
        wlStepname.setLayoutData(fdlStepname);
        
        wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wStepname.setText(stepname);
        props.setLook(wStepname);
        //wStepname.addModifyListener(modifiedListener);
        fdStepname = getBaseFormData(null, margin, middle, false);
        wStepname.setLayoutData(fdStepname);
        
        // Title by field
        
        Label lblTitleByField = createLabel(shell, BaseMessages.getString(PKG, "DriveCopyStepDialog.titleFieldInput.label"));
        lblTitleByField.setLayoutData(getBaseFormData(wStepname, margin, middle, true));
        props.setLook(lblTitleByField);
        
        CCombo titleFieldSelect = new CCombo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(titleFieldSelect);
        titleFieldSelect.setLayoutData(getBaseFormData(wStepname, margin, middle, false));
        
        // Output field
        
        Label lblOutputField = createLabel(shell, BaseMessages.getString(PKG, "DriveCopyStepDialog.outputFieldInput.label"));
        lblOutputField.setLayoutData(getBaseFormData(titleFieldSelect, margin, middle, true));
        props.setLook(lblOutputField);
        
        Text txtOutputField = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        txtOutputField.setLayoutData(getBaseFormData(titleFieldSelect, margin, middle, false));
        props.setLook(txtOutputField);
        
        // Tabs
        
        CTabFolder folderTabs = new CTabFolder(shell, SWT.BORDER);
        props.setLook(folderTabs, Props.WIDGET_STYLE_TAB);
        //folderTabs.setSimple(true);
        
        
        CTabItem googleConfigTab = new CTabItem(folderTabs, SWT.NONE);
        googleConfigTab.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.label"));
        
        
        Composite googleConfigComposite = new Composite(folderTabs, SWT.NONE);
        props.setLook(googleConfigComposite);
        googleConfigComposite.setLayout( getFormLayout(3) );
        
        // Service Account Config Group
        
        Group serviceAccountGroup = new Group(googleConfigComposite, SWT.SHADOW_NONE);
        serviceAccountGroup.setText( BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.label") );
        props.setLook(serviceAccountGroup);
        serviceAccountGroup.setLayout( getFormLayout(10) );
        
        Label lblsvAccountEmail = createLabel(serviceAccountGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.emailLabel"));
        lblsvAccountEmail.setLayoutData( getBaseFormData(0, 2 * margin, 0, 0, middle, -margin) );
        props.setLook(lblsvAccountEmail);
        
        TextVar txtSvAccountEmail = new TextVar(transMeta, serviceAccountGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        txtSvAccountEmail.setLayoutData( getBaseFormData(0, 2 * margin, middle, 0, 100, 0) );
        props.setLook(txtSvAccountEmail);
        
        Label lblSvAccountKeyfile = createLabel(serviceAccountGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.keyFileLabel"));
        props.setLook(lblSvAccountKeyfile);
        lblSvAccountKeyfile.setLayoutData(getBaseFormData(txtSvAccountEmail, margin, 0, 0, middle, -margin));
        
        
        
        TextVar txtSvKeyFile = new TextVar(transMeta, serviceAccountGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(txtSvKeyFile);
        txtSvKeyFile.setLayoutData(getBaseFormData(txtSvAccountEmail, margin, lblSvAccountKeyfile, margin, 90, -margin));
        
        Button btnSvKeyFileBrowse = createButton(serviceAccountGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.dialog.browse"), null);
        btnSvKeyFileBrowse.setToolTipText( BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.keyFileBrowseTooltip") );
        props.setLook(btnSvKeyFileBrowse);
        btnSvKeyFileBrowse.setLayoutData(getBaseFormDataLeftDirection(txtSvAccountEmail, 0, txtSvKeyFile, margin));
        
        Button btnTestConnection = createButton(serviceAccountGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.testConnection"), null);
        btnTestConnection.setLayoutData(getBaseFormDataLeftDirection(btnSvKeyFileBrowse, margin, middle, -margin));
        
        
        // File Config Group
        
        Group fileConfigGroup = new Group(googleConfigComposite, SWT.SHADOW_NONE);
        fileConfigGroup.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.fileConfigGroup.label"));
        fileConfigGroup.setLayout(getFormLayout(10));
        props.setLook(fileConfigGroup);
        
        Label lblFileCopy = createLabel(fileConfigGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.fileConfigGroup.fileCopyLabel"));
        props.setLook(lblFileCopy);
        lblFileCopy.setLayoutData(getBaseFormData(0, margin, 0, 0, middle, -margin));
        
        Button btnFileCopyBrowse = createButton(fileConfigGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.dialog.browse2"), null);
        props.setLook(btnFileCopyBrowse);
        btnFileCopyBrowse.setLayoutData(getBaseFormDataRightDirection(0, 0, 100, 0));
        
        Text txtFileCopy = new Text(fileConfigGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
        props.setLook(txtFileCopy);
        txtFileCopy.setLayoutData(getBaseFormData(0, margin, lblFileCopy, margin, btnFileCopyBrowse, -margin));
        
        
        Label lblFolderCopy = createLabel(fileConfigGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.fileConfigGroup.folderDumpLabel"));
        props.setLook(lblFolderCopy);
        lblFolderCopy.setLayoutData(getBaseFormData(txtFileCopy, margin, 0, 0, middle, -margin));
        
        Button btnFolderCopyBrowse = createButton(fileConfigGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.dialog.browse3"), null);
        props.setLook(btnFolderCopyBrowse);
        btnFolderCopyBrowse.setLayoutData(getBaseFormDataRightDirection(txtFileCopy, 0, 100, 0));
        
        Text txtFolderDump = new Text(fileConfigGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
        props.setLook(txtFolderDump);
        txtFolderDump.setLayoutData(getBaseFormData(txtFileCopy, margin, lblFolderCopy, margin, btnFolderCopyBrowse, -margin));
        
        Label lblFileInfo = new Label(fileConfigGroup, SWT.BOLD | SWT.CENTER);
        lblFileInfo.setText( BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.fileConfigGroup.fileInfoLabel") );
        props.setLook(lblFileInfo);
        lblFileInfo.setLayoutData( getBaseFormData(txtFolderDump, margin, 0, 0, 100, 0) );
        
        // Final settings for Google Config Tab
        
        FormData fdFolderTabs = getBaseFormData(txtOutputField, margin, 0, 0, 100, 0);
        fdFolderTabs.bottom = new FormAttachment(100, -50);
        folderTabs.setLayoutData(fdFolderTabs);
        
        // Impersonate Config Group
        
        Group impersonateGroup = new Group(googleConfigComposite, SWT.SHADOW_OUT);
        impersonateGroup.setText( BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.impersonateUserGroup.text"));
        impersonateGroup.setLayout( getFormLayout(10) );
        props.setLook(impersonateGroup);
        
        Label lblImpersonateUser = createLabel(impersonateGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.impersonateUserGroup.impersonateUserLabel") );
        lblImpersonateUser.setLayoutData(getBaseFormData(0, margin, 0, 0, middle, -margin));
        props.setLook(lblImpersonateUser);
        
        TextVar txtImpersonateUser = new TextVar(transMeta, impersonateGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(txtImpersonateUser);
        txtImpersonateUser.setLayoutData( getBaseFormData(0, margin, lblImpersonateUser, margin, 100, 0) );
        
        Link lblImpersonateUserInfo = new Link(impersonateGroup, SWT.BOLD | SWT.CENTER);
        lblImpersonateUserInfo.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.impersonateUserGroup.info"));
        props.setLook(lblImpersonateUserInfo);
        lblImpersonateUserInfo.setLayoutData( getBaseFormData(txtImpersonateUser, margin, 0, 0, 100, 0) );
        lblImpersonateUserInfo.addSelectionListener(getLinkSelected());
        
        
        // Permissions Tab
        
        CTabItem permissionTab = new CTabItem(folderTabs, SWT.NONE);
        permissionTab.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.label"));
        
        Composite permissionComposite = new Composite(folderTabs, SWT.NONE);
        props.setLook(permissionComposite);
        permissionComposite.setLayout( getFormLayout(3) );
        
        // Input Permission Group
        
        Group inputPermissionGroup = new Group(permissionComposite, SWT.SHADOW_OUT);
        props.setLook(inputPermissionGroup);
        inputPermissionGroup.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.InputPermission.groupLabel"));
        inputPermissionGroup.setLayout(getFormLayout(10));
        
        Label lblCkPermission = createLabel(inputPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.InputPermission.cklabel"));
        lblCkPermission.setLayoutData(getBaseFormData(0, margin, 0, 0, middle, -margin));
        props.setLook(lblCkPermission);
        
        Button ckInputPermission = createCheckbox(inputPermissionGroup, null);
        ckInputPermission.setLayoutData( getBaseFormData(0, margin, lblCkPermission, margin, 100, 0) );
        props.setLook(ckInputPermission);
        
        Label lblAccountInput = createLabel(inputPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.InputPermission.accountInput.label"));
        props.setLook(lblAccountInput);
        lblAccountInput.setLayoutData( getBaseFormData(ckInputPermission, margin, 0, 0, middle, -margin));
        
        TextVar txtAccountInput = new TextVar(transMeta, inputPermissionGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(txtAccountInput);
        txtAccountInput.setLayoutData( getBaseFormData(ckInputPermission, margin, lblAccountInput, margin, 100, margin));
        
        Label lblRoleInput = createLabel(inputPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.roleCombo.label"));
        lblRoleInput.setLayoutData( getBaseFormData(txtAccountInput, margin, 0, 0, middle, -margin));
        props.setLook(lblRoleInput);
        
        CCombo comboRoleInput = new CCombo(inputPermissionGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(comboRoleInput);
        comboRoleInput.setLayoutData( getBaseFormData(txtAccountInput, margin, lblRoleInput, margin, 100, margin));
        
        
        Label lblCkNotifyEmailInput = createLabel(inputPermissionGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.permissionsTab.ckNotifyEmail.label"));
        props.setLook(lblCkNotifyEmailInput);
        lblCkNotifyEmailInput.setLayoutData( getBaseFormData(comboRoleInput, margin, 0, 0, middle, -margin));
        
        Button ckNotifyEmailInput = createCheckbox(inputPermissionGroup, null);
        props.setLook(ckNotifyEmailInput);
        ckNotifyEmailInput.setLayoutData( getBaseFormData(comboRoleInput, margin, lblCkNotifyEmailInput, margin, 100, 0));
        
        
        
        
        
        // Groups Form Data Config
        googleConfigComposite.layout();
        googleConfigTab.setControl(googleConfigComposite);
        
        permissionComposite.layout();
        permissionTab.setControl(permissionComposite);
        
        
        serviceAccountGroup.setLayoutData( getBaseFormData(txtOutputField, margin, 0, margin, 100, -margin) );
        fileConfigGroup.setLayoutData( getBaseFormData(serviceAccountGroup, margin, 0, margin, 100, -margin) );
        impersonateGroup.setLayoutData( getBaseFormData(fileConfigGroup, margin, 0, margin, 100, -margin) );
        
        
        inputPermissionGroup.setLayoutData( getBaseFormData(txtOutputField, margin, 0, margin, 100, -margin));
		
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
