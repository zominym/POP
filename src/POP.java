import java.io.IOException;



public class POP {
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			POPUI.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in POPUI :");
			e.printStackTrace();
		}
	}
}
