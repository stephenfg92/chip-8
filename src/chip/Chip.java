package chip;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;


public class Chip {
	
	private char[] memoria;
	private char[] V;
	private char I;
	private char contador;
	private char[] pilha;
	private int ponteiroPilha;
	private int temp_atraso;
	private int temp_som;
	private byte[] teclas;
	private byte[] display;
	private boolean atualizarDisp;
	
	public void init() {
		
		memoria = new char[4096];
		V = new char[16];
		I = 0x0;
		contador = 0x200;
		pilha = new char[16];
		ponteiroPilha = 0;
		temp_atraso = 0;
		temp_som = 0;
		teclas = new byte[16];
		display = new byte[64 * 32];
		
		atualizarDisp = false;
		carregarFontes();

	}

	public void executar() {
		
		char instrucao = (char)((memoria[contador] << 8) | memoria[contador + 1]);
		System.out.print(Integer.toHexString(instrucao).toUpperCase() + ": ");
	
		switch(instrucao & 0xF000) {
		
		case 0x0000:
			switch(instrucao & 0x00FF) {
			case 0x00E0:
				for(int i = 0; i < display.length; i++) {
					display[i] = 0;
				}
				contador += 2;
				atualizarDisp = true;
				break;
				
			case 0x00EE:
				ponteiroPilha--;
				contador = (char)(pilha[ponteiroPilha] + 2);
				System.out.println("Retornando para " + Integer.toHexString(contador).toUpperCase());
				break;
				
			default:
				System.err.println("Instrução não implementada!");
				System.exit(0);
				break;
			}
			break;
		
		case 0x1000: {
			int nnn = instrucao & 0x0FFF;
			contador = (char)nnn;
			System.out.println("Saltando para o endereço " + Integer.toHexString(contador).toUpperCase());
			break;
		}
			
		case 0x2000:
			pilha[ponteiroPilha] = contador;
			ponteiroPilha++;
			contador = (char)(instrucao & 0x0FFF);
			System.out.println("Chamando subrotina no endereço " + Integer.toHexString(contador).toUpperCase() + " from " + Integer.toHexString(pilha[ponteiroPilha - 1]).toUpperCase());
			break;
			
		case 0x3000: {
			int x = (instrucao & 0x0F00) >> 8;
			int nn = (instrucao & 0x00FF);
			if(V[x] == nn) {
				contador += 4;
				System.out.println("Executando instrução próxima a seguinte, pois (V[" + x +"] == " + nn + ")");
			} else {
				contador += 2;
				System.out.println("Executando instrução seguinte, pois (V[" + x +"] =/= " + nn + ")");
			}
			break;
		}
		
		case 0x4000: {
			int x = (instrucao & 0x0F00) >> 8;
			int nn = instrucao & 0x00FF;
			if(V[x] != nn) {
				System.out.println("Execuntando instrução próxima a seguinte, pois V[" + x + "] = " + (int)V[x] + " != " + nn);
				contador += 4;
			} else {
				System.out.println("Executando instrução seguinte, pois V[" + x + "] = " + (int)V[x] + " == " + nn);
				contador += 2;
			}
			break;
		}
		
		case 0x5000: {
			int x = (instrucao & 0x0F00) >> 8;
			int y = (instrucao & 0x00F0) >> 4;
			if(V[x] == V[y]) {
				System.out.println("Execuntando instrução próxima a seguinte, pois V[" + x + "] == V[" + y + "]");
				contador += 4;
			} else {
				System.out.println("Executando instrução seguinte, pois V[" + x + "] =/= V[" + y + "]");
				contador += 2;
			}
			break;
		}
			
		case 0x6000: {
			int x = (instrucao & 0x0F00) >> 8;
			V[x] = (char)(instrucao & 0x00FF);
			contador += 2;
			System.out.println("V[" + x + "] passa a ser igual a " + (int)V[x]);
			break;
		}
			
		case 0x7000: {
			int x = (instrucao & 0x0F00) >> 8;
			int nn = (instrucao & 0x00FF);
			V[x] = (char)((V[x] + nn) & 0xFF);
			contador += 2;
			System.out.println("Somando " + nn + " a V["+ x + "] = " + (int)V[x]);
			break;
		}
		
		case 0x8000:
			
			switch(instrucao & 0x000F) {
			
			case 0x0000: {
				int x = (instrucao & 0x0F00) >> 8;
				int y = (instrucao & 0x00F0) >> 4;
				System.out.println("V[" + x + "] passa a ser igual a " + (int)V[y]);
				V[x] = V[y];
				contador += 2;
				break;
			}
			
			case 0x0001: {
				int x = (instrucao & 0x0F00) >> 8;
				int y = (instrucao & 0x00F0) >> 4;
				System.out.println("V[" + x + "] = V[" + x + "] OR V[" + y + "]");
				V[x] = (char)((V[x] | V[y]) & 0xFF);
				contador += 2;
				break;
			}
				
			case 0x0002: {
				int x = (instrucao & 0x0F00) >> 8;
				int y = (instrucao & 0x00F0) >> 4;
				System.out.println("V[" + x + "] passa a ser V[" + x + "] = " + (int)V[x] + " AND V[" + y + "] = " + (int)V[y] + " = " + (int)(V[x] & V[y]));
				V[x] = (char)(V[x] & V[y]);
				contador += 2;
				break;
			}
			
			case 0x0003: {
				int x = (instrucao & 0x0F00) >> 8;
				int y = (instrucao & 0x00F0) >> 4;
				System.out.println("V[" + x + "] passa a ser V[" + x + "] XOR V[" + y + "]");
				V[x] = (char)((V[x] ^ V[y]) & 0xFF);
				contador += 2;
				break;
			}
				
			case 0x0004: {
				int x = (instrucao & 0x0F00) >> 8;
				int y = (instrucao & 0x00F0) >> 4;
				System.out.print("Somando V[" + x + "] (" + (int)V[x]  + ") a V[" + y + "] (" + (int)V[y]  + ") = " + ((V[x] + V[y]) & 0xFF) + ", ");
				if(V[y] > 0xFF - V[x]) {
					V[0xF] = 1;
					System.out.println("Empréstimo aritmético");
				} else {
					V[0xF] = 0;
					System.out.println("Soma normal");
				}
				V[x] = (char)((V[x] + V[y]) & 0xFF);
				contador += 2;
				break;
			}
			
			case 0x0005: {
				int x = (instrucao & 0x0F00) >> 8;
				int y = (instrucao & 0x00F0) >> 4;
				System.out.print("V[" + x + "] = " + (int)V[x] + " V[" + y + "] = " + (int)V[y] + ", ");
				if(V[x] > V[y]) {
					V[0xF] = 1;
					System.out.println("Empréstimo aritmético");
				} else {
					V[0xF] = 0;
					System.out.println("Subtração normal");
				}
				V[x] = (char)((V[x] - V[y]) & 0xFF);
				contador += 2;
				break;
			}
			
			case 0x0006: {
				int x = (instrucao & 0x0F00) >> 8;
				V[0xF] = (char)(V[x] & 0x1);
				V[x] = (char)(V[x] >> 1);
				contador += 2;
				System.out.println("Desloca V[ " + x + "] >> 1 and VF passa a ser o bit menos significante de VX");
				break;
			}
			
			case 0x0007: { 
				int x = (instrucao & 0x0F00) >> 8;
				int y = (instrucao & 0x00F0) >> 4;
				
				if(V[x] > V[y])
					V[0xF] = 0;
				else
					V[0xF] = 1;
				
				V[x] = (char)((V[y] - V[x]) & 0xFF);
				System.out.println("V[" + x + "] = V[" + y + "] - V[" + x + "], realiza empréstimo aritmético se necessário");
				
				contador += 2;
				break;
			}
			
			case 0x000E: {
				int x = (instrucao & 0x0F00) >> 8;
				V[0xF] = (char)(V[x] & 0x80);
				V[x] = (char)(V[x] << 1);
				contador += 2;
				System.out.println("Descola V[ " + x + "] << 1 and VF passa a ser bit mais significante de VX");
				break;
			}
			
				default:
					System.err.println("Instrução não implementanda!");
					System.exit(0);
					break;
			}
				
			break;
			
		case 0x9000: {
			int x = (instrucao & 0x0F00) >> 8;
			int y = (instrucao & 0x00F0) >> 4;
			if(V[x] != V[y]) {
				System.out.println("Pulando para a próxima insturção V[" + x + "] != V[" + y + "]");
				contador += 4;
			} else {
				System.out.println("Executando instruções normalmente V[" + x + "] == V[" + y + "]");
				contador += 2;
			}
			break;
		}
			
		case 0xA000:
			I = (char)(instrucao & 0x0FFF);
			contador += 2;
			System.out.println("I passa a ser " + Integer.toHexString(I).toUpperCase());
			break;
			
		case 0xB000: {
			int nnn = instrucao & 0x0FFF;
			int extra = V[0] & 0xFF;
			contador = (char)(nnn + extra);
			break;
		}
			
		case 0xC000: { 
			int x = (instrucao & 0x0F00) >> 8;
			int nn = (instrucao & 0x00FF);
			int randomNumber = new Random().nextInt(255) & nn;
			System.out.println("V[" + x + "] passa a ser o número " + randomNumber + " , gerado aleatoriamente.");
			V[x] = (char)randomNumber;
			contador += 2;
			break;
		}
			
		case 0xD000: {
			int x = V[(instrucao & 0x0F00) >> 8];
			int y = V[(instrucao & 0x00F0) >> 4];
			int altura = instrucao & 0x000F;
			
			V[0xF] = 0;
			
			for(int _y = 0; _y < altura; _y++) {
				int linha = memoria[I + _y];
				for(int _x = 0; _x < 8; _x++) {
					int pixel = linha & (0x80 >> _x);
					if(pixel != 0) {
						int totalX = x + _x;
						int totalY = y + _y;
						
						totalX = totalX % 64;
						totalY = totalY % 32;
						
						int index = (totalY * 64) + totalX;
						
						if(display[index] == 1)
							V[0xF] = 1;
						
						display[index] ^= 1;
					}
				}
			}
			contador += 2;
			atualizarDisp = true;
			System.out.println("Desenhando no registro V[" + ((instrucao & 0x0F00) >> 8) + "] = " + x + ", V[" + ((instrucao & 0x00F0) >> 4) + "] = " + y);
			break;
		}
		
		case 0xE000: {
			switch (instrucao & 0x00FF) {
			case 0x009E: { 
				int x = (instrucao & 0x0F00) >> 8;
				int key = V[x];
				if(teclas[key] == 1) {
					contador += 4;
				} else {
					contador += 2;
				}
				System.out.println("Pulando para a próxima instrução se V[" + x + "] = " + ((int)V[x])+ " for pressionada");
				break;
			}
				
			case 0x00A1: {
				int x = (instrucao & 0x0F00) >> 8;
				int key = V[x];
				if(teclas[key] == 0) {
					contador += 4;
				} else {
					contador += 2;
				}
				System.out.println("Pula a próxima instrução se V[" + x + "] = " + (int)V[x] + " não houver sido pressionada.");
				break;
			}
				
				default:
					System.err.println("Instrução não implementada!");
					System.exit(0);
					return;
			}
			break;
		}
		
		case 0xF000:
			
			switch(instrucao & 0x00FF) {
			
			case 0x0007: {
				int x = (instrucao & 0x0F00) >> 8;
				V[x] = (char)temp_atraso;
				contador += 2;
				System.out.println("V[" + x + "] passa a ser " + temp_atraso);
				break;
			}
			
			case 0x000A: {
				int x = (instrucao & 0x0F00) >> 8;
				for(int i = 0; i < teclas.length; i++) {
					if(teclas[i] == 1) {
						V[x] = (char)i;
						contador += 2;
						break;
					}
				}
				System.out.println("Gravando tecla pressionada em V[" + x + "]");
				break;
			}
			
			case 0x0015: { 
				int x = (instrucao & 0x0F00) >> 8;
				temp_atraso = V[x];
				contador += 2;
				System.out.println("temp_atraso passa a ser V[" + x + "] = " + (int)V[x]);
				break;
			}
			
			case 0x0018: { 
				int x = (instrucao & 0x0F00) >> 8;
				temp_som = V[x];
				contador += 2;
				break;
			}
			
			case 0x001E: {
				int x = (instrucao & 0x0F00) >> 8;
				I = (char)(I + V[x]);
				System.out.println("Somando V[" + x + "] = " + (int)V[x] + " a I");
				contador += 2;
				break;
			}
			
			case 0x0029: {
				int x = (instrucao & 0x0F00) >> 8;
				int character = V[x];
				I = (char)(0x050 + (character * 5));
				System.out.println("I passa a ser o caractere V[" + x + "] = " + (int)V[x] + " descolado para 0x" + Integer.toHexString(I).toUpperCase());
				contador += 2;
				break;
			}
			
			case 0x0033: {
				int x = (instrucao & 0x0F00) >> 8;
				int value = V[x];
				int hundreds = (value - (value % 100)) / 100;
				value -= hundreds * 100;
				int tens = (value - (value % 10))/ 10;
				value -= tens * 10;
				memoria[I] = (char)hundreds;
				memoria[I + 1] = (char)tens;
				memoria[I + 2] = (char)value;
				System.out.println("Gravando número decimal V[" + x + "] = " + (int)(V[(instrucao & 0x0F00) >> 8]) + " na seguinte notação: { " + hundreds+ ", " + tens + ", " + value + "}");
				contador += 2;
				break;
			}
			
			case 0x0055: {
				int x = (instrucao & 0x0F00) >> 8;
				for(int i = 0; i <= x; i++) { 
					memoria[I + i] = V[i];
				}
				System.out.println("memoria[" + Integer.toHexString(I & 0xFFFF).toUpperCase() + " + n] = V[0] passa a ser V[x]");
				contador += 2;
				break;
			}
			
			case 0x0065: {
				int x = (instrucao & 0x0F00) >> 8;
				for(int i = 0; i <= x; i++) { 
					V[i] = memoria[I + i];
				}
				System.out.println("Registros de V[0] a V[" + x + "] passam a conter os valoress de memoria[0x" + Integer.toHexString(I & 0xFFFF).toUpperCase() + "]");
				I = (char)(I + x + 1);
				contador += 2;
				break;
			}
			
			default:
				System.err.println("Instrução não implementada!");
				System.exit(0);
			}
			break;
		
			default:
				System.err.println("Instrução não implementada!");
				System.exit(0);
		}
		
		if(temp_som > 0) {
			temp_som--;
			Audio.playSound(Main.audioString);
			System.out.println("audio");
		}
		if(temp_atraso > 0)
			temp_atraso--;
}

	
	public byte[] getDisplay() {
		
		return display;
		
	}
	
	public boolean getAtualizarDisp() {
		return atualizarDisp;
	}

	public void desativasAtualizarDisp() {
		atualizarDisp = false;		
	}
	
	public void carregarFontes() {
		
		int i = 0;
		while(i < Fontset.fontes.length) {
			memoria[0x50 + i] = Fontset.fontes[i];
			i++;
		}
		
	}
	
	public void setBuffer(int[] buffer) {
		for(int i = 0; i < teclas.length; i++) {
			teclas[i] = (byte)buffer[i];
		}
	}

	public void carregar(String arquivo) {
		
		DataInputStream input = null;
		
		try {
			input = new DataInputStream(new FileInputStream(new File(arquivo)));
			
			int posicaoAtual = 0;
			while(input.available() > 0) { 
				memoria[0x200 + posicaoAtual] = (char)(input.readByte() & 0xFF);
				posicaoAtual++;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);			
		} finally {
			if (input != null) {
				try {input.close();} catch (IOException ex) {}
			}
		}
		
	}

}
