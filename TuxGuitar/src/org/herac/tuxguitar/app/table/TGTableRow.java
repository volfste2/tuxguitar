package org.herac.tuxguitar.app.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.app.action.TGActionProcessor;
import org.herac.tuxguitar.app.action.impl.track.ChangeTrackMuteAction;
import org.herac.tuxguitar.app.action.impl.track.ChangeTrackSoloAction;

public class TGTableRow {
	private TGTable table;
	private Composite row;
	private CLabel number;
	private Button soloCheckbox;
	private Button muteCheckbox;
	private CLabel name;
	private CLabel instrument;
	private Composite painter;
	private MouseListener mouseListenerLabel;
	private MouseListener mouseListenerCanvas;
	private PaintListener paintListenerCanvas;
	
	public TGTableRow(TGTable table){
		this.table = table;
		this.init();
	}
	
	public void init(){
		MouseListener mouseListenerLabel = new MouseListenerLabel();
		MouseListener mouseListenerCanvas = new MouseListenerCanvas();
		PaintListener paintListenerCanvas = new PaintListenerCanvas();
		
		this.row = new Composite(this.table.getRowControl(),SWT.NONE );
		this.row.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		
		this.number = new CLabel(this.row,SWT.LEFT);
		this.number.addMouseListener(mouseListenerLabel);
		this.table.addRowItem(this.table.getColumnNumber(),this.number,true);
		
		this.soloCheckbox = new Button(this.row,SWT.CHECK);
		this.soloCheckbox.addMouseListener(mouseListenerLabel);
		this.soloCheckbox.addSelectionListener(new TGActionProcessor(ChangeTrackSoloAction.NAME));
		this.table.addRowItem(this.table.getColumnSolo(),this.soloCheckbox,true);

		this.muteCheckbox = new Button(this.row,SWT.CHECK);
		this.muteCheckbox.addMouseListener(mouseListenerLabel);
		this.muteCheckbox.addSelectionListener(new TGActionProcessor(ChangeTrackMuteAction.NAME));
		this.table.addRowItem(this.table.getColumnMute(),this.muteCheckbox,true);

		this.name = new CLabel(this.row,SWT.LEFT);
		this.name.addMouseListener(mouseListenerLabel);
		this.table.addRowItem(this.table.getColumnName(),this.name,true);
		
		this.instrument = new CLabel(this.row,SWT.LEFT);
		this.instrument.addMouseListener(mouseListenerLabel);
		this.table.addRowItem(this.table.getColumnInstrument(),this.instrument,true);
		
		this.painter = new Composite(this.row,SWT.DOUBLE_BUFFERED);
		this.painter.addMouseListener(mouseListenerCanvas);
		this.painter.addPaintListener(paintListenerCanvas);
		this.table.addRowItem(this.table.getColumnCanvas(),this.painter,false);
		
		this.row.pack();
	}
	
	public void setBackground(Color background){
		this.number.setBackground(background);
		this.soloCheckbox.setBackground(background);
		this.muteCheckbox.setBackground(background);
		this.name.setBackground(background);
		this.instrument.setBackground(background);
	}
	
	public void setForeground(Color foreground){
		this.number.setForeground(foreground);
		this.soloCheckbox.setForeground(foreground);
		this.muteCheckbox.setForeground(foreground);
		this.name.setForeground(foreground);
		this.instrument.setForeground(foreground);
	}
	
	public void dispose(){
		this.row.dispose();
	}
	
	public Composite getPainter() {
		return this.painter;
	}
	
	public CLabel getInstrument() {
		return this.instrument;
	}
	
	public CLabel getName() {
		return this.name;
	}
	
	public CLabel getNumber() {
		return this.number;
	}

	public Button getSoloCheckbox() {
		return soloCheckbox;
	}

	public Button getMuteCheckbox() {
		return muteCheckbox;
	}

	public MouseListener getMouseListenerLabel() {
		return this.mouseListenerLabel;
	}
	
	public void setMouseListenerLabel(MouseListener mouseListenerLabel) {
		this.mouseListenerLabel = mouseListenerLabel;
	}
	
	public MouseListener getMouseListenerCanvas() {
		return this.mouseListenerCanvas;
	}
	
	public void setMouseListenerCanvas(MouseListener mouseListenerCanvas) {
		this.mouseListenerCanvas = mouseListenerCanvas;
	}
	
	public PaintListener getPaintListenerCanvas() {
		return this.paintListenerCanvas;
	}
	
	public void setPaintListenerCanvas(PaintListener paintListenerCanvas) {
		this.paintListenerCanvas = paintListenerCanvas;
	}
	
	private class MouseListenerLabel implements MouseListener{
		
		public MouseListenerLabel(){
			super();
		}
		
		public void mouseDoubleClick(MouseEvent e) {
			if(getMouseListenerLabel() != null){
				getMouseListenerLabel().mouseDoubleClick(e);
			}
		}
		
		public void mouseDown(MouseEvent e) {
			if(getMouseListenerLabel() != null){
				getMouseListenerLabel().mouseDown(e);
			}
		}
		
		public void mouseUp(MouseEvent e) {
			if(getMouseListenerLabel() != null){
				getMouseListenerLabel().mouseUp(e);
			}
		}
	}
	
	private class MouseListenerCanvas implements MouseListener{
		
		public MouseListenerCanvas(){
			super();
		}
		
		public void mouseDoubleClick(MouseEvent e) {
			if(getMouseListenerCanvas() != null){
				getMouseListenerCanvas().mouseDoubleClick(e);
			}
		}
		
		public void mouseDown(MouseEvent e) {
			if(getMouseListenerCanvas() != null){
				getMouseListenerCanvas().mouseDown(e);
			}
		}
		
		public void mouseUp(MouseEvent e) {
			if(getMouseListenerCanvas() != null){
				getMouseListenerCanvas().mouseUp(e);
			}
		}
	}
	
	private class PaintListenerCanvas implements PaintListener{
		
		public PaintListenerCanvas(){
			super();
		}
		
		public void paintControl(PaintEvent e) {
			if(getPaintListenerCanvas() != null){
				getPaintListenerCanvas().paintControl(e);
			}
		}
	}
}
