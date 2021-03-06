/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.items.tool;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessor;
import org.herac.tuxguitar.app.action.impl.insert.InsertChordAction;
import org.herac.tuxguitar.app.action.impl.note.ChangeTiedNoteAction;
import org.herac.tuxguitar.app.items.ToolItems;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BeatToolItems  extends ToolItems{
	public static final String NAME = "beat.items";
	
	protected ToolBar toolBar;
	private ToolItem tiedNote;
	private ChordMenuItem chordItems;
	
	public BeatToolItems(){
		super(NAME);
	}
	
	public void showItems(ToolBar toolBar){
		this.toolBar = toolBar;
		
		this.tiedNote = new ToolItem(toolBar, SWT.CHECK);
		this.tiedNote.addSelectionListener(new TGActionProcessor(ChangeTiedNoteAction.NAME));
		
		this.chordItems = new ChordMenuItem();
		this.chordItems.addItems();
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		TGNote note = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getSelectedNote();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		this.tiedNote.setEnabled(!running);
		this.tiedNote.setSelection(note != null && note.isTiedNote());
		this.chordItems.setEnabled(!running);
		this.chordItems.update();
	}
	
	public void loadProperties(){
		this.tiedNote.setToolTipText(TuxGuitar.getProperty("note.tiednote"));
		this.chordItems.setToolTipText(TuxGuitar.getProperty("insert.chord"));
	}
	
	public void loadIcons(){
		this.tiedNote.setImage(TuxGuitar.getInstance().getIconManager().getNoteTied());
		this.chordItems.setImage(TuxGuitar.getInstance().getIconManager().getChord());
	}
	
	private class ChordMenuItem extends SelectionAdapter {
		private long lastEdit;
		private ToolItem item;
		private Menu subMenu;
		private MenuItem[] subMenuItems;
		
		public ChordMenuItem() {
			this.item = new ToolItem(BeatToolItems.this.toolBar, SWT.DROP_DOWN);
			this.item.addSelectionListener(this);
			this.subMenu = new Menu(this.item.getParent().getShell());
		}
		
		public void setToolTipText(String text){
			this.item.setToolTipText(text);
		}
		
		public void setEnabled(boolean enabled){
			this.item.setEnabled(enabled);
		}
		
		public void setImage(Image image){
			this.item.setImage(image);
		}
		
		public void addItems() {
			this.disposeItems();
			this.subMenuItems = new MenuItem[TuxGuitar.getInstance().getCustomChordManager().countChords()];
			for(int i = 0;i < this.subMenuItems.length; i++){
				TGChord chord = TuxGuitar.getInstance().getCustomChordManager().getChord(i);
				Map actionData = new HashMap();
				actionData.put(InsertChordAction.PROPERTY_CHORD, chord);
				
				this.subMenuItems[i] = new MenuItem(this.subMenu, SWT.PUSH);
				this.subMenuItems[i].setData(actionData);
				this.subMenuItems[i].setText(chord.getName());
				this.subMenuItems[i].addSelectionListener(new TGActionProcessor(InsertChordAction.NAME));
			}
		}
		
		public void disposeItems() {
			if(this.subMenuItems != null){
				for(int i = 0;i < this.subMenuItems.length; i++){
					this.subMenuItems[i].dispose();
				}
			}
		}
		
		public void widgetSelected(SelectionEvent event) {
			if (event.detail == SWT.ARROW && this.subMenuItems != null && this.subMenuItems.length > 0) {
				ToolItem item = (ToolItem) event.widget;
				Rectangle rect = item.getBounds();
				Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
				this.subMenu.setLocation(pt.x, pt.y + rect.height);
				this.subMenu.setVisible(true);
			}else{
				TGSynchronizer.instance().executeLater(new TGSynchronizer.TGRunnable() {
					public void run() throws TGException {
						TuxGuitar.getInstance().getActionManager().execute(InsertChordAction.NAME);
					}
				});
			}
		}
		
		public void update(){
			if(this.lastEdit != TuxGuitar.getInstance().getCustomChordManager().getLastEdit()){
				this.addItems();
				this.lastEdit = TuxGuitar.getInstance().getCustomChordManager().getLastEdit();
			}
		}
	}
}
