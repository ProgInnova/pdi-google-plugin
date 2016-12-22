package com.proginnova.pentaho.ui;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.ResultFile;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepData.StepExecutionStatus;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.RowListener;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepListener;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public abstract class BasicStepDialog extends BaseStepDialog implements StepDialogInterface {

	public BasicStepDialog(Shell parent, BaseStepMeta baseStepMeta, TransMeta transMeta, String stepname) {
		super(parent, baseStepMeta, transMeta, stepname);
		// TODO Auto-generated constructor stub
	}
	
	protected FormData getBaseFormData(Control topLastControl, int margin, int middle, boolean label){
		FormData formData = new FormData();
		if(topLastControl == null){
			formData.top = new FormAttachment( 0, margin );
		}else{
			formData.top = new FormAttachment( topLastControl, margin );
		}
		
		if(label){
			formData.left = new FormAttachment(0, 0);
			formData.right = new FormAttachment(middle, -margin);
		}else{
			formData.left = new FormAttachment(middle, 0);
			formData.right = new FormAttachment(98, 0);
		}
		return formData;
	}
	
	protected FormData getBaseFormData(Control topLastControl, int topOffset, int numeratorLeft, int leftOffset, int numeratorRight, int rightOffset){
		FormData formData = new FormData();
		formData.top = new FormAttachment(topLastControl, topOffset);
		formData.left = new FormAttachment(numeratorLeft, leftOffset);
		formData.right = new FormAttachment(numeratorRight, rightOffset);
		return formData;
	}
	
	protected FormData getBaseFormData(Control topLastControl, int topOffset, Control leftLastControl, int leftOffset, int numeratorRight, int rightOffset){
		FormData formData = new FormData();
		formData.top = new FormAttachment(topLastControl, topOffset);
		formData.left = new FormAttachment(leftLastControl, leftOffset);
		formData.right = new FormAttachment(numeratorRight, rightOffset);
		return formData;
	}
	
	protected FormData getBaseFormData(Control topLastControl, int topOffset, Control leftLastControl, int leftOffset, Control rightLastControl, int rightOffset){
		FormData formData = new FormData();
		formData.top = new FormAttachment(topLastControl, topOffset);
		formData.left = new FormAttachment(leftLastControl, leftOffset);
		formData.right = new FormAttachment(rightLastControl, rightOffset);
		return formData;
	}
	
	protected FormData getBaseFormData(Control topLastControl, int topOffset, int numeratorLeft, int leftOffset, Control rightLastControl, int rightOffset){
		FormData formData = new FormData();
		formData.top = new FormAttachment(topLastControl, topOffset);
		formData.left = new FormAttachment(numeratorLeft, leftOffset);
		formData.right = new FormAttachment(rightLastControl, rightOffset);
		return formData;
	}
	
	protected FormData getBaseFormData(int numeratorTop, int topOffset, int numeratorLeft, int leftOffset, int numeratorRight, int rightOffset){
		FormData formData = new FormData();
		formData.top = new FormAttachment(numeratorTop, topOffset);
		formData.left = new FormAttachment(numeratorLeft, leftOffset);
		formData.right = new FormAttachment(numeratorRight, rightOffset);
		return formData;
	}
	
	
	protected FormLayout getFormLayout(int marginWidth, int marginHeight){
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = marginWidth;
        formLayout.marginHeight = marginHeight;
        return formLayout;
	}
	
	protected Button createButton(Composite composite, String label, Listener listener){
		Button btn = new Button(composite, SWT.PUSH | SWT.CENTER);
		//btn.addListener(arg0, arg1);(listener);
		btn.setText(label);
		return btn;
	}
	
	protected abstract ShellAdapter getShellAdapter();
	protected abstract ModifyListener getMetaModifyListener();
	protected abstract Listener getOkCancelListener();

}
