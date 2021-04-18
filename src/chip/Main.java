package chip;

public class Main extends Thread {
	
	private Chip chip8;
	private Frame frame;
	
	//SUBSTITUIR STRINGS PELO DIRETÓRIO ONDE OS ARQUIVOS SE ENCONTRAM EM SEU COMPUTADOR!
	public static String audioString = "Path to beep.wav";
	private String romString = "Path to pong2.c8";
	
	public Main() {
		chip8 = new Chip();
		chip8.init();
		chip8.carregar(romString);
		frame = new Frame(chip8);
	}
	
	public void run() {
		while(true) {
			chip8.setBuffer(frame.getBuffer());
			chip8.executar();
			if(chip8.getAtualizarDisp()) {
				frame.repaint();
				chip8.desativasAtualizarDisp();
			}
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				//Unthrown exception
			}
		}
	}
	
	public static void main(String[] args) {
		Main main = new Main();
		main.start();
	}

}