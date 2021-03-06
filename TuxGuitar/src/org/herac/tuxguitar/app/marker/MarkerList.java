package org.herac.tuxguitar.app.marker;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionLock;
import org.herac.tuxguitar.app.editors.TGUpdateEvent;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.helper.SyncThread;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.undo.undoables.custom.UndoableChangeMarker;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGSong;

public class MarkerList implements TGEventListener {
	
	private static MarkerList instance;
	
	protected Shell dialog;
	private Table table;
	private List markers;
	
	private Composite compositeTable;
	private TableColumn measureColumn;
	private TableColumn titleColumn;
	
	private Composite compositeButtons;
	private Button buttonAdd;
	private Button buttonEdit;
	private Button buttonDelete;
	private Button buttonGo;
	private Button buttonClose;
	
	public static MarkerList instance(){
		if(instance == null){
			instance = new MarkerList();
		}
		return instance;
	}
	
	private MarkerList() {
		super();
	}
	
	public void show() {
		this.dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(), SWT.DIALOG_TRIM);
		this.dialog.setLayout(new GridLayout(2,false));
		this.dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		// ----------------------------------------------------------------------
		this.compositeTable = new Composite(this.dialog, SWT.NONE);
		this.compositeTable.setLayout(new GridLayout());
		this.compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		this.table = new Table(this.compositeTable, SWT.BORDER | SWT.FULL_SELECTION);
		this.table.setLayoutData(new GridData(250,200));
		this.table.setHeaderVisible(true);
		this.table.addListener (SWT.MouseDoubleClick, new Listener() {
			public void handleEvent (Event event) {
				new MarkerNavigator().goToSelectedMarker(getSelectedMarker());
				TuxGuitar.getInstance().updateCache(true);
			}
		});
		this.measureColumn = new TableColumn(this.table, SWT.LEFT);
		this.measureColumn.setWidth(70);
		
		this.titleColumn = new TableColumn(this.table, SWT.LEFT);
		this.titleColumn.setWidth(180);
		
		this.loadTableItems(false);
		
		// ------------------BUTTONS--------------------------
		this.compositeButtons = new Composite(this.dialog, SWT.NONE);
		this.compositeButtons.setLayout(new GridLayout(1,false));
		this.compositeButtons.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.buttonAdd = new Button(this.compositeButtons, SWT.PUSH);
		this.buttonAdd.setLayoutData(makeGridData(SWT.FILL, SWT.TOP,false));
		this.buttonAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(!TGActionLock.isLocked() && !TuxGuitar.getInstance().isLocked()){
					TGActionLock.lock();
					Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
					TGMarker marker = TuxGuitar.getInstance().getSongManager().getFactory().newMarker();
					marker.setMeasure(caret.getMeasure().getNumber());
					if(new MarkerEditor(marker,MarkerEditor.STATUS_NEW).open(MarkerList.this.dialog)){
						TuxGuitar.getInstance().updateCache(true);
						loadTableItems(true);
					}
					TGActionLock.unlock();
				}
			}
		});
		
		this.buttonEdit = new Button(this.compositeButtons, SWT.PUSH);
		this.buttonEdit.setLayoutData(makeGridData(SWT.FILL, SWT.TOP,false));
		this.buttonEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if(!TGActionLock.isLocked() && !TuxGuitar.getInstance().isLocked()){
					TGActionLock.lock();
					TGMarker marker = getSelectedMarker();
					if(marker != null){
						if(new MarkerEditor(marker,MarkerEditor.STATUS_EDIT).open(MarkerList.this.dialog)){
							TuxGuitar.getInstance().updateCache(true);
							loadTableItems(true);
						}
					}
					TGActionLock.unlock();
				}
			}
		});
		
		this.buttonDelete = new Button(this.compositeButtons, SWT.PUSH);
		this.buttonDelete.setLayoutData(makeGridData(SWT.FILL, SWT.TOP,false));
		this.buttonDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if(!TGActionLock.isLocked() && !TuxGuitar.getInstance().isLocked()){
					TGActionLock.lock();
					TGMarker marker = getSelectedMarker();
					TGSong song = TuxGuitar.getInstance().getDocumentManager().getSong();
					
					// comienza el undoable
					UndoableChangeMarker undoable = UndoableChangeMarker.startUndo(marker);
					
					TuxGuitar.getInstance().getSongManager().removeMarker(song, marker);
					
					// termia el undoable
					TuxGuitar.getInstance().getUndoableManager().addEdit(undoable.endUndo(null));
					TuxGuitar.getInstance().getFileHistory().setUnsavedFile();
					TuxGuitar.getInstance().updateCache(true);
					loadTableItems(true);
					TGActionLock.unlock();
				}
			}
		});
		
		this.buttonGo = new Button(this.compositeButtons, SWT.PUSH);
		this.buttonGo.setLayoutData(makeGridData(SWT.FILL, SWT.BOTTOM,true));
		this.buttonGo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if(!TGActionLock.isLocked() && !TuxGuitar.getInstance().isLocked()){
					TGActionLock.lock();
					new MarkerNavigator().goToSelectedMarker(getSelectedMarker());
					TuxGuitar.getInstance().updateCache(true);
					TGActionLock.unlock();
				}
			}
		});
		
		this.buttonClose = new Button(this.compositeButtons, SWT.PUSH);
		this.buttonClose.setLayoutData(makeGridData(SWT.FILL, SWT.BOTTOM,false));
		this.buttonClose.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				MarkerList.this.dialog.dispose();
			}
		});
		
		this.loadIcons();
		this.loadProperties(false);
		
		this.addListeners();
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				removeListeners();
			}
		});
		this.dialog.setDefaultButton( this.buttonGo );
		
		DialogUtils.openDialog(this.dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	public void addListeners(){
		TuxGuitar.getInstance().getIconManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
	}
	
	public void removeListeners(){
		TuxGuitar.getInstance().getIconManager().removeLoader(this);
		TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
		TuxGuitar.getInstance().getEditorManager().removeUpdateListener(this);
	}
	
	public void dispose(){
		if(!isDisposed()){
			this.dialog.dispose();
		}
	}
	
	public void update(){
		this.update(false);
	}
	
	public void update(final boolean keepSelection){
		if(!isDisposed()){
			new SyncThread(new Runnable() {
				public void run() {
					if(!isDisposed()){
						loadTableItems(keepSelection);
					}
				}
			}).start();
		}
	}
	
	private GridData makeGridData(int horizontalAlignment,int verticalAlignment,boolean grabExcessVerticalSpace){
		GridData data = new GridData();
		data.horizontalAlignment = horizontalAlignment;
		data.verticalAlignment = verticalAlignment;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = grabExcessVerticalSpace;
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		
		return data;
	}
	
	protected void loadTableItems(boolean keepSelection){
		int itemSelected = (keepSelection ? this.table.getSelectionIndex() : -1 );
		
		TGSong song = TuxGuitar.getInstance().getDocumentManager().getSong();
		
		this.table.removeAll();
		this.markers = TuxGuitar.getInstance().getSongManager().getMarkers(song);
		
		Iterator it = this.markers.iterator();
		while (it.hasNext()) {
			TGMarker marker = (TGMarker) it.next();
			
			TableItem item = new TableItem(this.table, SWT.NONE);
			item.setText(new String[] { Integer.toString(marker.getMeasure()),marker.getTitle() });
		}
		
		if(itemSelected >= 0 && itemSelected < this.markers.size()){
			this.table.select(itemSelected);
		}
	}
	
	protected TGMarker getSelectedMarker(){
		int itemSelected = this.table.getSelectionIndex();
		if(itemSelected >= 0 && itemSelected < this.markers.size()){
			return (TGMarker)this.markers.get(itemSelected);
		}
		return null;
	}
	
	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed());
	}
	
	public void loadIcons() {
		if(!isDisposed()){
			this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		}
	}
	
	public void loadProperties() {
		this.loadProperties(true);
	}
	
	public void loadProperties(boolean layout) {
		if(!isDisposed()){
			this.dialog.setText(TuxGuitar.getProperty("marker.list"));
			this.measureColumn.setText(TuxGuitar.getProperty("measure"));
			this.titleColumn.setText(TuxGuitar.getProperty("title"));
			this.buttonAdd.setText(TuxGuitar.getProperty("add"));
			this.buttonEdit.setText(TuxGuitar.getProperty("edit"));
			this.buttonDelete.setText(TuxGuitar.getProperty("remove"));
			this.buttonGo.setText(TuxGuitar.getProperty("go"));
			this.buttonClose.setText(TuxGuitar.getProperty("close"));
			
			if(layout){
				this.table.layout();
				this.compositeTable.layout();
				this.compositeButtons.layout();
				this.dialog.pack(true);
			}
		}
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getProperty(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type ==  TGUpdateEvent.SONG_LOADED ){
			this.update();
		}
	}
	
	public void processEvent(TGEvent event) {
		if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIcons();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadProperties();
		}
		else if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
	}
}
