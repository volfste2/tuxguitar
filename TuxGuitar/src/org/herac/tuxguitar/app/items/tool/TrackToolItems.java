/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.items.tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessor;
import org.herac.tuxguitar.app.action.impl.track.AddTrackAction;
import org.herac.tuxguitar.app.action.impl.track.RemoveTrackAction;
import org.herac.tuxguitar.app.items.ToolItems;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TrackToolItems extends ToolItems{
	public static final String NAME = "track.items";
	
	private ToolItem add;
	private ToolItem remove;
	
	public TrackToolItems(){
		super(NAME);
	}
	
	public void showItems(ToolBar toolBar){
		this.add = new ToolItem(toolBar, SWT.PUSH);
		this.add.addSelectionListener(new TGActionProcessor(AddTrackAction.NAME));
		
		this.remove = new ToolItem(toolBar, SWT.PUSH);
		this.remove.addSelectionListener(new TGActionProcessor(RemoveTrackAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		this.add.setToolTipText(TuxGuitar.getProperty("track.add"));
		this.remove.setToolTipText(TuxGuitar.getProperty("track.remove"));
	}
	
	public void loadIcons(){
		this.add.setImage(TuxGuitar.getInstance().getIconManager().getTrackAdd());
		this.remove.setImage(TuxGuitar.getInstance().getIconManager().getTrackRemove());
	}
	
	public void update(){
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		this.add.setEnabled(!running);
		this.remove.setEnabled(!running);
	}
}
