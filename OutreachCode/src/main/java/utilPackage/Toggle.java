package utilPackage;

public class Toggle {
	boolean toggle = false, pInput = false;
	
	public Toggle(boolean init) {
		toggle = init;
	}
	
	public boolean toggleVar(boolean input){
		if(input && !pInput){
			toggle = !toggle;
		}
		pInput = input;
		return toggle;
	}
	
	public boolean getVal(){
		return toggle;
	}
	
//	public static void main(String[] args) {
//		boolean input = false;
//		Toggle tggl = new Toggle(input);
//		for(int i = 0; i < 20; i++){
//			if(i > 20)
//				input = false;
//			else if(i > 15)
//				input = true;
//			else if(i > 10)
//				input = false;
//			else if(i > 5)
//				input = true;
//			else 
//				input = false;
//			System.out.println("Input: "+input+"\tToggle: "+tggl.toggleVar(input));
//		}
//	}
}