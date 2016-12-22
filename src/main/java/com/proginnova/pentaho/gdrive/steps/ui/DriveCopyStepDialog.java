package com.proginnova.pentaho.gdrive.steps.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
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

public class DriveCopyStepDialog extends BasicStepDialog {
	
	private static Class<?> PKG = DriveCopyStepDialog.class;
	private DriveCopyStepMeta meta;
	
	private Display display;
	

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
		
		FormLayout shellLayout = new FormLayout();
		shellLayout.marginWidth = Const.FORM_MARGIN;
		shellLayout.marginHeight = Const.FORM_MARGIN;
		shell.setLayout(shellLayout);
		int margin = Const.MARGIN;
		int middle = props.getMiddlePct();
		
		// Step Name
		
		wlStepname = new Label(shell, SWT.RIGHT);
        wlStepname.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.stepName.label"));
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
        
        Label lblTitleByField = new Label(shell, SWT.RIGHT);
        lblTitleByField.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.titleFieldInput.label"));
        lblTitleByField.setLayoutData(getBaseFormData(wStepname, margin, middle, true));
        props.setLook(lblTitleByField);
        
        CCombo titleFieldSelect = new CCombo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(titleFieldSelect);
        titleFieldSelect.setLayoutData(getBaseFormData(wStepname, margin, middle, false));
        props.setLook(titleFieldSelect);
        
        // Output field
        
        Label lblOutputField = new Label(shell, SWT.RIGHT);
        lblOutputField.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.outputFieldInput.label"));
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
        googleConfigComposite.setLayout( getFormLayout(3, 3) );
        
        // Service Account Config Group
        
        Group serviceAccountGroup = new Group(googleConfigComposite, SWT.SHADOW_NONE);
        serviceAccountGroup.setText( BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.label") );
        props.setLook(serviceAccountGroup);
        serviceAccountGroup.setLayout( getFormLayout(10, 10) );
        
        Label lblsvAccountEmail = new Label(serviceAccountGroup, SWT.RIGHT);
        lblsvAccountEmail.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.emailLabel") );
        lblsvAccountEmail.setLayoutData( getBaseFormData(0, 2 * margin, 0, 0, middle, -margin) );
        props.setLook(lblsvAccountEmail);
        
        TextVar txtSvAccountEmail = new TextVar(transMeta, serviceAccountGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        txtSvAccountEmail.setLayoutData( getBaseFormData(0, 2 * margin, middle, 0, 100, 0) );
        props.setLook(txtSvAccountEmail);
        
        Label lblSvAccountKeyfile = new Label(serviceAccountGroup, SWT.RIGHT);
        lblSvAccountKeyfile.setText( BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.keyFileLabel") );
        props.setLook(lblSvAccountKeyfile);
        lblSvAccountKeyfile.setLayoutData(getBaseFormData(txtSvAccountEmail, margin, 0, 0, middle, -margin));
        
        Button btnSvKeyFileBrowse = createButton(serviceAccountGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.dialog.browse"), null);
        btnSvKeyFileBrowse.setToolTipText(BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.serviceAccountGroup.keyFileBrowseTooltip"));
        props.setLook(btnSvKeyFileBrowse);
        FormData fdBtnSvKeyFileBrowse = new FormData();
        fdBtnSvKeyFileBrowse.right = new FormAttachment(100, -margin);
        fdBtnSvKeyFileBrowse.top = new FormAttachment(txtSvAccountEmail, margin);
        btnSvKeyFileBrowse.setLayoutData(fdBtnSvKeyFileBrowse);
        
        TextVar txtSvKeyFile = new TextVar(transMeta, serviceAccountGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(txtSvKeyFile);
        FormData fdTxtSvKeyFile = new FormData();
        fdTxtSvKeyFile.left = new FormAttachment(lblSvAccountKeyfile, margin);
        fdTxtSvKeyFile.top = new FormAttachment(txtSvAccountEmail, margin);
        fdTxtSvKeyFile.right = new FormAttachment(btnSvKeyFileBrowse, -margin);
        txtSvKeyFile.setLayoutData(getBaseFormData(txtSvAccountEmail, margin, lblSvAccountKeyfile, margin, btnSvKeyFileBrowse, -margin));
        
        // File Config Group
        
        Group fileConfigGroup = new Group(googleConfigComposite, SWT.SHADOW_NONE);
        fileConfigGroup.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.fileConfigGroup.label"));
        fileConfigGroup.setLayout(getFormLayout(10, 10));
        props.setLook(fileConfigGroup);
        
        Label lblFileCopy = new Label(fileConfigGroup, SWT.RIGHT);
        lblFileCopy.setText(BaseMessages.getString(PKG, "DriveCopyStepDialog.googleConfigTab.fileConfigGroup.fileCopyLabel"));
        lblFileCopy.setLayoutData(getBaseFormData(txtSvKeyFile, margin, 0, 0, middle, -margin));
        props.setLook(lblFileCopy);
        
        Button btnFileCopyBrowse = createButton(fileConfigGroup, BaseMessages.getString(PKG, "DriveCopyStepDialog.dialog.browse"), null);
        props.setLook(btnFileCopyBrowse);
        FormData fdBtnFileCopyBrowse = new FormData();
        fdBtnFileCopyBrowse.right = new FormAttachment(100, -margin);
        fdBtnFileCopyBrowse.top = new FormAttachment(txtSvKeyFile, margin);
        btnFileCopyBrowse.setLayoutData(fdBtnFileCopyBrowse);
        
        Text txtFileCopy = new Text(fileConfigGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
        txtFileCopy.setLayoutData(getBaseFormData(txtSvKeyFile, margin, lblFileCopy, margin, btnFileCopyBrowse, -margin));
        props.setLook(lblFileCopy);
        
        // Final settings for Google Config Tab
        
        Control lastElement = txtFileCopy;
        
        FormData fdFolderTabs = getBaseFormData(txtOutputField, margin, 0, 0, 100, 0);
        fdFolderTabs.bottom = new FormAttachment(100, -50);
        folderTabs.setLayoutData(fdFolderTabs);
        
        serviceAccountGroup.setLayoutData( getBaseFormData(txtOutputField, margin, 0, margin, 100, -margin) );
        fileConfigGroup.setLayoutData( getBaseFormData(lastElement, margin, 0, margin, 100, -margin) );
        
        
        googleConfigComposite.layout();
        googleConfigTab.setControl(googleConfigComposite);
		
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
