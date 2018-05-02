package cz.koscak.jan.mockservergui.core;

import javax.swing.JFrame;

import cz.koscak.jan.mockservergui.gui.GUI;

public class Main {

	public static void main(String[] args) {

		JFrame frame = new JFrame("Mock REST Server - Monitoring Tool");
		
		GUI.createGUIElements(frame);

		//frame.setSize(1000, 727);
		frame.setSize(990, 717);
		frame.setLayout(null);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}