import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
/**
 * 
 * @author Barış Ege Sevgili
 * 
 * 
 */
public class Organiser {
	Stack myStack;    //     stack for arranging the order of expressions during the infix to postfix operations
	String input; // expression line to be changed to postfix
	String output = ""; // postfix form of the expression

	int parentCtr = 0;    //counter for parenthesis        
	int lineCtr = 0;    //counter for lines        

	Organiser(String in, int line) {    // organizer constructor
		lineCtr= line; // Stores the current line for error handling
		input = in; // takes given line from main which is read from input file
		myStack = new Stack(); 
	}
	public String changer() {     // method for changing infix expression to postfix
		//checking if there is any operation at the beginning
		for(int i=0; i<=input.length()-1; i++){
			while(input.charAt(i) == ' '){
				i++;
				if(i == input.length()){
					i--;
					break;
				}
			}
			if(input.charAt(i) == '+' || input.charAt(i) == '*' || input.charAt(i) == ','){ // //gives error if an expression starts with operator 
				System.out.println("ERROR line = " + lineCtr);
				System.exit(1);
			}else{
				break;
			}
		}
		//checking if there is any operation at the end
		for(int i=input.length()-1; i>=0; i--){
			while(input.charAt(i) == ' '){
				i--;
				if(i == -1){
					i++;
					break;
				}
			}
			if(input.charAt(i) == '+' || input.charAt(i) == '*' || input.charAt(i) == ','){ //gives error if an expression ends with operator 
				System.out.println("ERROR line = " + lineCtr);
				System.exit(1);
			}else{
				break;
			}
		}

		int powC = 0; // Counter for powers and comas

		boolean varSeen = false; // checking whether any two operations comes consecutively

		for (int j = 0; j < input.length(); j++) { // traverses expression
			char ch = input.charAt(j); // each character of the expression

			boolean spaceCheck = false; //Space checker for variables 

			switch (ch) { //cases for the taken character 

			// for each char handles proper operations
			// +,),*,+,^,( error handling for these operations 
			case '(': 
				myStack.push(ch);
				parentCtr++;
				if(varSeen){
					System.out.println("ERROR line = " + lineCtr);    // can not come after a variable without any operator        
					System.exit(1);
				}
				break;
			case ')': 
				if(!varSeen){    
					System.out.println("ERROR line = " + lineCtr);    //can not follow any operator        
					System.exit(1);            
				}								
				prnths(ch); 
				parentCtr--;            
				if(parentCtr < 0){              
					System.out.println("ERROR line = " + lineCtr); // handles cases like ())( and such            
					System.exit(1);            
				} // *

				break;
			case '+': 
				if(!varSeen){            
					System.out.println("ERROR line = " + lineCtr);        // operations can not follow or be followed by another operation    
					System.exit(1);            
				}

				opr(ch, 1);
				varSeen = false;
				break; 
			case '*': 

				if(!varSeen){    
					System.out.println("ERROR line = " + lineCtr);    //     operations can not follow or be followed by another operation    
					System.exit(1);            
				}

				opr(ch, 2); 
				varSeen = false;
				break;
			case ',':
				if(!varSeen){            
					System.out.println("ERROR line = " + lineCtr);        // operations can not follow or be followed by another operation    
					System.exit(1);            
				}
				powC--; 

				prnths(')');
				opr('^', 3); 
				myStack.push('(');
				varSeen = false;
				break;
			case 'p':
				if(input.substring(j).indexOf(')') >= 6){
					if(input.substring(j).length() > 3 && input.substring(j,j+4).equals("pow(")){  //recognizes power and handles some errors about power operation            
						j+=2;
						powC++;
						break;            
					}
				}else {
					if(input.substring(j).length() > 3 && input.substring(j,j+4).equals("pow(")){             
						System.out.println("ERROR line = " + lineCtr); // handles misinterpreted power operation 
						System.exit(1);
					}
				}

			default:
				if(ch == ' '){ // ignores spaces
					break;
				}
				if((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >='0' && ch <= '9')){ //    checks whether the variable ensures the constraints                            
					output = output + "." + ch ; //modifies output and "." identifies that a variable coming
					varSeen = true;
				}else{
					System.out.println("ERROR line = " + lineCtr); // variable consists a character which is prohibited
					System.exit(1);
				}


				if(j<input.length()-1) { 
					for (int i = j+1; i < input.length(); i++) { // for getting  whole variable name
						char ch2 = input.charAt(i);

						if(ch2!='+'&&ch2!='*'&&ch2!='('&&ch2!=')'&&ch2!=',') { //finds variable
							if(ch2 == ' '){ // ignores spaces
								spaceCheck = true;

							}else{
								if(spaceCheck){ //error handling for if there is a space between variables 
									System.out.println("ERROR line = " + lineCtr);
									System.exit(1);
								}

								if((ch2 >= 'a' && ch2 <= 'z') || (ch2 >= 'A' && ch2 <= 'Z') || (ch2 >='0' && ch2 <= '9')){ //    checks whether the variable ensures the constraints                                
									output = output + ch2 ;
									j++;
								}else{
									System.out.println("ERROR line = " + lineCtr);
									System.exit(1);
								}
							}
						}else {
							i=input.length();
						}
					}
				}

				break;
			}
		}
		while (!myStack.isEmpty()) { // at the ends empties the stack of operations and adds them to the output one by one
			output = output +","+ myStack.pop();
		}

		if(parentCtr != 0){
			System.out.println("ERROR line = " + lineCtr); // checks whether the number of "("s and ")"s are equal  
			System.exit(1);
		}
		if(powC != 0){ //checks whether the number of comas and powers are equal 
			System.out.println("ERROR line = " + lineCtr);
			System.exit(1);
		}
		return output; //returns postfix expression
	}
	public void opr(char opThis, int opr1) {  // checks for precedence and arranges operations accordingly
		while (!myStack.isEmpty()) {
			char opAtTop = (char)myStack.pop();
			if (opAtTop == '(') {
				myStack.push(opAtTop);
				break;
			} else {
				int opr2;
				if (opAtTop == '+')
					opr2 = 1; // the greater number has precedence
				else
					opr2 = 2;
				if (opr2 < opr1) { 
					myStack.push(opAtTop);
					break;
				} 
				else output = output +","+ opAtTop ;
			}
		}
		myStack.push(opThis); 
	}
	public void prnths(char ch) {  // arranges how to put parenthesis 
		while (!myStack.isEmpty()) {
			char chx = (char)myStack.pop();
			if (chx == '(') 
				break; 
			else output = output +","+ chx ; 
		}
	}
	public static void main(String[] args) throws IOException,FileNotFoundException{

		Map<String, String> m1 = new HashMap<String, String>(); // stores variables

		String DecVar="jmp start\nvarM0 dw 0h\nvarM1 dw 0h\nvarM2 dw 0h\nvarC1 " // assembly code to initialize variables which are needed for mul and pow ops
				+ "dw 0h\npowC dw 0h\npowH dw 0h\npowL dw 0h\n";
		String DecCalc="start:\n"; // assembly code after start label

		int powCount=0; // counts pow operations
		int printCount=0; // counts print operations
		int varCount=0;


		File inFile = null;
		if (1 < args.length) {
			inFile = new File(args[0]);
		} else {
			System.out.println("Invalid arguments count:" + args.length); // checks if there is any file 
			System.exit(1);
		}
		//        File file = new File("C:\\SD4U\\workspace\\cmpe230_project1\\src\\cmpe230_project1\\input.txt");
		Scanner sc = new Scanner(inFile);


		String currLine=""; // current line

		int lineCounter=0; // counts line 
		while (sc.hasNextLine()) {
			boolean endLine = false; // checks whether the line has ended
			boolean spaceChecker = false;

			String varName=""; // name of variable
			lineCounter++; // holds the number of current line
			currLine=sc.nextLine(); // takes the next line from input file
			while(currLine.equals("")){ // if line is empty, passes
				if(sc.hasNextLine()){
					lineCounter++;
					currLine=sc.nextLine();
				}else{
					endLine = true;
					break;
				}
			}

			int p = 0;
			while(!endLine && currLine.charAt(p) == ' '){ //ignores spaces
				p++;
				while(currLine.substring(p).equals("")){ // if line is empty, passes
					if(sc.hasNextLine()){
						lineCounter++;
						currLine=sc.nextLine();
						p =0 ;
					}else{
						endLine = true; // there is no line left
						break;
					}
				}

				if(endLine){ 
					break;
				}

			}

			if(!endLine){ // if there is no line left jump to the end

				if(!((currLine.charAt(p)>='a'&&currLine.charAt(p)<='z')||(currLine.charAt(p)>='A'&&currLine.charAt(p)<='Z')) && currLine.indexOf("=") != -1) {
					System.out.println("ERROR line = " + lineCounter);  // checks whether the variable starts with abc or not            
					System.exit(1);
				}

				if(currLine.indexOf('=') != -1){ // checks for existence of equal sign
					if(currLine.substring(p,currLine.indexOf('=')).indexOf(' ') != -1){   // handles spaces
						int m = p+1;
						while(currLine.charAt(m) == ' '){
							m++;
							spaceChecker = true;
						}	
						if(spaceChecker && currLine.charAt(m) != '='){ // if there is a space between two declared variable before "=" gives error
							System.out.println("ERROR line = " + lineCounter);
							System.exit(1);
						}

						varName = currLine.substring(p,p+currLine.substring(p).indexOf(' ')); // gets variable name to be assigned
					}else{
						varName = currLine.substring(p,currLine.indexOf('=')); // gets variable name to be assigned
					}
				}else{ 
					varName = currLine; //if there is no "=" takes the line as whole
				}
				if(!m1.containsKey(varName)) { // checks whether the variable created before

					m1.put(varName, "var"+varCount);

					DecVar+=m1.get(varName)+"_H dw 0h\n"+m1.get(varName)+"_L dw 0h\n"; // if not , initializes as high and low 16 bit vars

					varCount++;
				}

				

				Organiser toPost = null;
				if(currLine.indexOf('=') != -1) {
					if(currLine.substring(currLine.indexOf('=')).length() == 0){ // checks whether there is any expression after the "="        
						System.out.println("ERROR line = " + lineCounter);    
						System.exit(1);
					}
					// takes the expression after "=", if "=" exists
					toPost = new Organiser(currLine.substring(currLine.indexOf('=')+1),lineCounter);					
				}else{
					//takes line as it is
					toPost = new Organiser(currLine,lineCounter);   
				}
				String expr=toPost.changer(); //turns it into postfix form

				for(int k=0; k<expr.length(); k++) { // traverse post fix expression

					if(expr.charAt(k)=='.') { // identifier for variable

						int j=k+1;
						String vr="";
						while(j<expr.length()&&expr.charAt(j)!='.' && expr.charAt(j)!=',') {
							vr+=expr.charAt(j);
							j++;
							k++;
						}

						if(((vr.charAt(0)>='a'&&vr.charAt(0)<='z')||(vr.charAt(0)>='A'&&vr.charAt(0)<='Z'))){ // checks whether it is variable
							if(!m1.containsKey(vr)) { 

								DecCalc+= "push 0h\npush 0h\n"; // takes as 0 if the variable has not declared before

							}else {

								DecCalc+= "push "+m1.get(vr)+"_H\npush " +m1.get(vr)+"_L\n"; // takes the variable which is declared before

							}
						}else {  // cuts number to two parts : high, low
							String high="";
							String low="";

							if(vr.length()<=4) { // if there is no high parts
								low+=vr +"h";
								high+="0h";
							}else if(vr.length()==5&&vr.charAt(0)=='0'){ // if the number is 16 bits and starts with either a,b,c,d,e,f
								low+=vr +"h";
								high+="0h";
							}else { // if the number is bigger than 16 bits
								low+="0"+vr.substring(vr.length()-4) +"h";
								high+=vr.substring(0, vr.length()-4) +"h";
							}
							DecCalc+= "push "+high+"\npush "+low +"\n";
						}
					}else if(expr.charAt(k)==',') { // identifier for operators
						if(expr.charAt(k+1)=='*') { //assembly code for mul operation
							DecCalc+="pop ax\nmov varM0, ax\npop bx\npop cx\npop dx\nmov varM1,dx\nmul cx\nmov varC1,"
									+ "dx\nmov varM2,ax\nmov ax,bx\nmul cx\nadd varC1,ax\nmov ax,varM0\nmul varM1\nadd ax,"
									+ "varC1\npush ax\npush varM2\n";

						}else if(expr.charAt(k+1)=='^') {
							//assembly code for pow operation
							DecCalc += " pop ax\npop bx\nmov powC,ax\npop cx\npop dx\ncmp powC,0h\nje zerocase"+powCount+"\n" + 
									" mov powH,dx\nmov powL,cx\npush dx\npush cx\ncmp powC,1h\n" + 
									" je ctr"+powCount+"\ndec powC\npow"+powCount+":\npop ax\n" + 
									" mov varM0,ax\npop bx\nmul powL\nmov varC1,dx\nmov varM1,ax\n" + 
									" mov ax, powL\nmul bx\nadd varC1,ax\nmov ax, varM0\nmul powH\n" + 
									" add varC1,ax\npush varC1\npush varM1\nctr"+powCount+":\n" + 
									" dec powC\njnz pow"+powCount+"\njmp go"+powCount+" \r\nzerocase"+powCount+":\n" + 
									" mov powH,0h\nmov powL,1h\npush powH\npush powL\ngo"+powCount+":\n";
							powCount++;
						}else if(expr.charAt(k+1)=='+') {
							//assembly code for add operation
							DecCalc+="pop ax\npop bx\npop cx\npop dx\nadd ax,cx\nadc bx,dx\npush bx\npush ax\n";

						}
					}

				}

				//assigning result to variable
				DecCalc+="pop ax\npop bx\nmov "+m1.get(varName)+"_H,bx\nmov "+m1.get(varName)+"_L,ax\npush bx\npush ax\n";

				if(currLine.indexOf('=') == -1) { // line does not contain "=" so we are expected to print variables value
					//assembly code for printing
					DecCalc+=" mov bx,"+m1.get(varName)+"_H\nmov cx,4h\n mov ah,2h\nloop"+printCount+":\n" + 
							" mov dx,0fh\n rol bx,4h\n and dx,bx\n cmp dl,0ah\n jae hexdigit"+printCount+"\n" + 
							" add dl,'0'\n jmp output"+printCount+"\nhexdigit"+printCount+":\n add dl,'A'\n" + 
							" sub dl,0ah\noutput"+printCount+":\n int 21h\n dec cx\n jnz loop"+printCount+"\n";
					printCount++;
					DecCalc+=" mov bx,"+m1.get(varName)+"_L\n mov cx,4h\n mov ah,2h\nloop"+printCount+":\n" + 
							" mov dx,0fh\n rol bx,4h\n and dx,bx\n cmp dl,0ah\n jae hexdigit"+printCount+"\n add dl,'0'\n" + 
							" jmp output"+printCount+"\nhexdigit"+printCount+":\n add dl,'A'\n sub dl,0ah\n" + 
							"output"+printCount+":\n int 21h\n dec cx\n jnz loop"+printCount+"\n" + 
							"MOV dl, 10\nMOV ah, 02h\nINT 21h\nMOV dl, 13\nMOV ah, 02h\nINT 21h \n";

					printCount++;

				}

			}
		}
		//assembly code for exiting program
		DecCalc += "mov ah,4ch\nint 21h"; //to do

		System.out.println("\n");

		//assembly code
		String Assembly= DecVar+"\n"+DecCalc;

		//        System.out.println(Assembly);

		PrintStream ofs = new PrintStream(args[1]);

		ofs.println(Assembly);

	}
}
