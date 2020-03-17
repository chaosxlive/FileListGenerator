package temp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
	private static int layer = 0;
	private static BufferedWriter writer;
	
	public static void main(String[] args) throws IOException {
		String mainPath;
		Main.writer = new BufferedWriter(new FileWriter("output.txt"));
		
		Scanner input = new Scanner(System.in);
		System.out.println("Please input the directory path:");
		System.out.print(">>>");
		mainPath = input.nextLine();
		Main thisProg = new Main();
		thisProg.loop(mainPath);
		
		input.close();
		Main.writer.close();
	}
	
	private void loop(String path) throws IOException {
		File file = new File(path);
		String[] paths = file.list();
		for(String p : paths) {
			for(int i = 0; i < Main.layer; i++) {
				System.out.print("│");
				Main.writer.write("│");
			}
			System.out.print("├");
			Main.writer.write("├");
			File tester = new File(path + "\\" + p);
			if(tester.isDirectory()) {
				Main.layer++;
				String[] pathName = path.split("\\\\");
				System.out.println(pathName[pathName.length - 1]);
				Main.writer.write(pathName[pathName.length - 1] + "\n");
				loop(path + "\\" +p);
				Main.layer--;
			}
			else {
				System.out.println(p);
				Main.writer.write(p + "\n");
			}
		}
		for(int i = 0; i < Main.layer; i++) {
			System.out.print("│");
			Main.writer.write("│");
		}
		System.out.print("\n");
		Main.writer.write("\n");
	}
}
