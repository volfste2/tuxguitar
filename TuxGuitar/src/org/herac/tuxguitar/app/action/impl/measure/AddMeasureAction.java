/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.measure;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.helper.SyncThread;
import org.herac.tuxguitar.app.undo.undoables.UndoableJoined;
import org.herac.tuxguitar.app.undo.undoables.measure.UndoableAddMeasure;
import org.herac.tuxguitar.app.util.DialogUtils;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddMeasureAction extends TGActionBase{
	
	public static final String NAME = "action.measure.add";
	
	public AddMeasureAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		showDialog();
	}
	
	public void showDialog() {
		if (getEditor().getTablature().getCaret().getMeasure() != null) {
			final Shell dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			dialog.setLayout(new GridLayout());
			dialog.setText(TuxGuitar.getProperty("measure.add"));
			
			//-----------------COUNT------------------------
			Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			group.setLayout(new GridLayout(2,false));
			group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			group.setText(TuxGuitar.getProperty("measure.add"));
			
			Label countLabel = new Label(group, SWT.NULL);
			countLabel.setText(TuxGuitar.getProperty("measure.add.count"));
			
			final Spinner countSpinner = new Spinner(group, SWT.BORDER);
			countSpinner.setLayoutData(getSpinnerData());
			countSpinner.setMinimum( 1 );
			countSpinner.setMaximum( 100 );
			countSpinner.setSelection( 1 );
			
			//----------------------------------------------------------------------
			Group options = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			options.setLayout(new GridLayout());
			options.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			options.setText(TuxGuitar.getProperty("options"));
			
			final Button beforePosition = new Button(options,SWT.RADIO);
			beforePosition.setText(TuxGuitar.getProperty("measure.add-before-current-position"));
			
			final Button afterPosition = new Button(options,SWT.RADIO);
			afterPosition.setText(TuxGuitar.getProperty("measure.add-after-current-position"));
			
			final Button atEnd = new Button(options,SWT.RADIO);
			atEnd.setText(TuxGuitar.getProperty("measure.add-at-end"));
			atEnd.setSelection(true);
			//------------------BUTTONS--------------------------
			Composite buttons = new Composite(dialog, SWT.NONE);
			buttons.setLayout(new GridLayout(2,false));
			buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
			
			final Button buttonOK = new Button(buttons, SWT.PUSH);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setLayoutData(getButtonData());
			buttonOK.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					int number = 0;
					int count = countSpinner.getSelection();
					if(beforePosition.getSelection()){
						number = (getEditor().getTablature().getCaret().getMeasure().getNumber());
					}else if(afterPosition.getSelection()){
						number = (getEditor().getTablature().getCaret().getMeasure().getNumber() + 1);
					}else if(atEnd.getSelection()){
						number = (getEditor().getTablature().getSong().countMeasureHeaders() + 1);
					}
					addMeasure(number, count);
					dialog.dispose();
				}
			});
			
			Button buttonCancel = new Button(buttons, SWT.PUSH);
			buttonCancel.setText(TuxGuitar.getProperty("cancel"));
			buttonCancel.setLayoutData(getButtonData());
			buttonCancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					dialog.dispose();
				}
			});
			
			dialog.setDefaultButton( buttonOK );
			
			DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		}
	}
	
	private GridData getSpinnerData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 150;
		return data;
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	private void addMeasure( final int number , final int count ){
		if(count > 0 && number > 0 && number <=  (getEditor().getTablature().getSong().countMeasureHeaders() + 1)){
			new Thread(new Runnable() {
				public void run() {
					new SyncThread(new Runnable() {
						public void run() {
							UndoableJoined undoable = new UndoableJoined();
							for( int i = 0 ; i < count ; i ++ ){
								//comienza el undoable
								UndoableAddMeasure mUndoable = UndoableAddMeasure.startUndo( ( number + i ) );
								
								getSongManager().addNewMeasure(getEditor().getTablature().getSong(), ( number + i ) );
								
								//termia el undoable
								undoable.addUndoableEdit(mUndoable.endUndo());
								
							}
							updateSong();
							
							int trackNumber = getEditor().getTablature().getCaret().getTrack().getNumber();
							int stringNumber = getEditor().getTablature().getCaret().getStringNumber();
							long start = getSongManager().getMeasureHeader(getEditor().getTablature().getSong(), number).getStart();
							getEditor().getTablature().getCaret().update(trackNumber,start,stringNumber);
							
							//termia el undoable
							addUndoableEdit( undoable.endUndo() );
							
							TuxGuitar.getInstance().getFileHistory().setUnsavedFile();
							TuxGuitar.getInstance().updateCache(true);
						}
					}).start();
				}
			}).start();
		}
	}
}
