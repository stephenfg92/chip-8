package chip;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class Frame extends JFrame implements KeyListener {
	
	private static final long serialVersionUID = 1L;
	private Panel panel;
	private int[] buffer;
	private int[] teclas;

	public Frame(Chip c) {
		setPreferredSize(new Dimension(640, 320));
		pack();
		setPreferredSize(new Dimension(640 + getInsets().left + getInsets().right, 320 + getInsets().top + getInsets().bottom));
		panel = new Panel(c);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Chip 8 Emulator");
		pack();
		setVisible(true);
		addKeyListener(this);
		
		teclas = new int[256];
		buffer = new int[16];
		preencher();
	}
	
	private void preencher() {
		for(int i = 0; i < teclas.length; i++) {
			teclas[i] = -1;
		}
		teclas['1'] = 1;
		teclas['2'] = 2;
		teclas['3'] = 3;		
		teclas['Q'] = 4;
		teclas['W'] = 5;
		teclas['E'] = 6;
		teclas['A'] = 7;
		teclas['S'] = 8;
		teclas['D'] = 9;
		teclas['Z'] = 0xA;
		teclas['X'] = 0;
		teclas['C'] = 0xB;
		teclas['4'] = 0xC;
		teclas['R'] = 0xD;
		teclas['F'] = 0xE;
		teclas['V'] = 0xF;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(teclas[e.getKeyCode()] != -1) {
			buffer[teclas[e.getKeyCode()]] = 1;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(teclas[e.getKeyCode()] != -1) {
			buffer[teclas[e.getKeyCode()]] = 0;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {	
	}
	
	public int[] getBuffer() {
		return buffer;
	}
	
}