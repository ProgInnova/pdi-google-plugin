package com.proginnova.pentaho.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
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
			formData.right = new FormAttachment(middle, -5);
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
	
	protected FormData getBaseFormData(int numeratorTop, int topOffset, Control leftLastControl, int leftOffset, int numeratorRight, int rightOffset){
		FormData formData = new FormData();
		formData.top = new FormAttachment(numeratorTop, topOffset);
		formData.left = new FormAttachment(leftLastControl, leftOffset);
		formData.right = new FormAttachment(numeratorRight, rightOffset);
		return formData;
	}
	
	protected FormData getBaseFormData(int numeratorTop, int topOffset, Control leftLastControl, int leftOffset, Control rightLastControl, int rightOffset){
		FormData formData = new FormData();
		formData.top = new FormAttachment(numeratorTop, topOffset);
		formData.left = new FormAttachment(leftLastControl, leftOffset);
		formData.right = new FormAttachment(rightLastControl, rightOffset);
		return formData;
	}
	
	protected FormData getBaseFormData(int numeratorTop, int topOffset, int numeratorLeft, int leftOffset, Control rightLastControl, int rightOffset){
		FormData formData = new FormData();
		formData.top = new FormAttachment(numeratorTop, topOffset);
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
	
	protected FormData getBaseFormDataLeftDirection(int numeratorTop, int topOffset, int numeratorLeft, int leftOffset){
		FormData formData = new FormData();
		formData.top = new FormAttachment(numeratorTop, topOffset);
		formData.left = new FormAttachment(numeratorLeft, leftOffset);
		return formData;
	}
	
	protected FormData getBaseFormDataLeftDirection(Control topLastControl, int topOffset, int numeratorLeft, int leftOffset){
		FormData formData = new FormData();
		formData.top = new FormAttachment(topLastControl, topOffset);
		formData.left = new FormAttachment(numeratorLeft, leftOffset);
		return formData;
	}
	
	protected FormData getBaseFormDataLeftDirection(Control topLastControl, int topOffset, Control leftLastControl, int leftOffset){
		FormData formData = new FormData();
		formData.top = new FormAttachment(topLastControl, topOffset);
		formData.left = new FormAttachment(leftLastControl, leftOffset);
		return formData;
	}
	
	protected FormData getBaseFormDataRightDirection(int numeratorTop, int topOffset, int numeratorRight, int rightOffset){
		FormData formData = new FormData();
		formData.top = new FormAttachment(numeratorTop, topOffset);
		formData.right = new FormAttachment(numeratorRight, rightOffset);
		return formData;
	}
	
	protected FormData getBaseFormDataRightDirection(Control topLastControl, int topOffset, int numeratorRight, int rightOffset){
		FormData formData = new FormData();
		formData.top = new FormAttachment(topLastControl, topOffset);
		formData.right = new FormAttachment(numeratorRight, rightOffset);
		return formData;
	}
	
	protected FormData getBaseFormDataRightDirection(Control topLastControl, int topOffset, Control rightLastControl, int rightOffset){
		FormData formData = new FormData();
		formData.top = new FormAttachment(topLastControl, topOffset);
		formData.right = new FormAttachment(rightLastControl, rightOffset);
		return formData;
	}
	
	protected FormLayout getFormLayout(int symetricmargin){
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = symetricmargin;
        formLayout.marginHeight = symetricmargin;
        return formLayout;
	}
	
	protected FormLayout getFormLayout(int marginWidth, int marginHeight){
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = marginWidth;
        formLayout.marginHeight = marginHeight;
        return formLayout;
	}
	
	protected Button createButton(Composite composite, String label, SelectionListener listener){
		Button btn = new Button(composite, SWT.PUSH | SWT.CENTER);
		//btn.addListener(arg0, arg1);(listener);
		if(listener != null){
			btn.addSelectionListener(listener);
		}
		btn.setText(label);
		props.setLook(btn);
		return btn;
	}
	
	protected Label createLabel(Composite composite, String labelTxt){
		Label label = new Label(composite, SWT.RIGHT);
		label.setText(labelTxt);
		props.setLook(label);
		return label;
	}
	
	protected Group createGroup(Composite composite, String label){
		Group group = new Group(composite, SWT.SHADOW_NONE);
		group.setText(label);
		props.setLook(group);
		return group;
	}
	
	protected CCombo createFieldsCombo(Composite composite){
		CCombo ccombo = new CCombo(composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(ccombo);
        return ccombo;
	}
	
	protected Button createCheckbox(Composite composite){
		Button btn = new Button(composite, SWT.CHECK);
		props.setLook(btn);
		return btn;
	}
	
	protected Button createCheckbox(Composite composite, SelectionListener listener){
		Button btn = new Button(composite, SWT.CHECK);
		props.setLook(btn);
		if(listener != null){
			btn.addSelectionListener(listener);
		}
		return btn;
	}
	
	protected abstract ShellAdapter getShellAdapter();
	protected abstract ModifyListener getMetaModifyListener();
	protected abstract SelectionListener getOkCancelListener();

}
