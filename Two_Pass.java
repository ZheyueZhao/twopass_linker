package Lab1;
import java.util.*;
public class Two_Pass {
	
	
	
	
	
	public static void main(String[] args)
	{
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter your input, press 'enter' to enter");
		int modules = scanner.nextInt();
		List<Module> moduleList = new ArrayList<Module>();
		for (int i=0;i<modules;i++) {
			Module newMod = new Module();
			moduleList.add(newMod);
			
		}
		List<String>dup = new ArrayList<String>();
				/*
				 * Uses Scanner to take user input
				 * scanner.nextint is wrapped in either try catch or .hasnextint to avoid crushes
				 * Arbitary limit of 8 in order to prevent errors
				 * Created different classes for definition and usage and have them stored in an arraylist in their corresponding modules
				 * 
				 * 
				 * */
				for(int i = 0; i < modules; i++){
					int definition_length;
					try {
						definition_length = scanner.nextInt();
					}
					catch(InputMismatchException exception) {
						System.out.println("Error: Amount of definition must be a number, you may re-enter from the ealiest mistake");
						i=i-1;
						scanner.nextLine();
						continue;
					}
					for(int j = 0; j < definition_length; j++){
						
						String def = scanner.next();
						if(def.length()>8) {
							System.out.println("symbol sizes must be less than or equal to 8, exiting");
							System.exit(0);
						}
						
						while(scanner.hasNextInt()==false) {
							System.out.println("definition location must be a number, exiting");
							System.exit(0);
						}
						int def_position = scanner.nextInt();
						//creats an object for every definition and add it to a list
						definition newDef = new definition(def_position,def);
						newDef.module = i;
						moduleList.get(i).definitionList.add(newDef);
						dup.add(def);
						
						
					}
					
					int use_length = scanner.nextInt();
							
					for(int j=0;j<use_length;j++){
						String use = scanner.next();
						if(use.length()>8) {
							System.out.println("symbol sizes must be less than or equal to 8, exiting");
							System.exit(0);
						}
						if(scanner.hasNextInt()==false) {
							System.out.println("use location must be a number, exiting");
							System.exit(0);
						}
						int use_position = scanner.nextInt();
						//creats an object for every use and add it to a list
						use newUse = new use(use_position,use);
						moduleList.get(i).useList.add(newUse);
						
					}
					
					int program_lenth = scanner.nextInt();
					for(int j=0;j<program_lenth;j++){
						if(scanner.hasNextInt()==false) {
							System.out.println("program address location must be a number, exiting");
							System.exit(0);
						}
						int program_ID = scanner.nextInt();
						if(Integer.toString(program_ID).length()!=5) {
							System.out.println("Addresses should be 5 digits long,exiting ");
							System.exit(0);
						}
						moduleList.get(i).addressList.add(program_ID);
						
					}
					
					
				}
				
				Map <String, String> symbol_table = new HashMap<String, String>();
				Map <String, Boolean> truth_table = new HashMap<String, Boolean>();
				int ori_base = 0;
				/*Beginning of pass 1, calculates the length of the address list in order to determine base address
				 * Goes through all the addresses for definition list in order to resolve external symbols
				 * added in error checking wherever makes sense
				 * 
				 * */
				for(int i=0;i<modules;i++) {
					if(i>=1) {
						ori_base +=  moduleList.get(i-1).addressList.size();
					}
					moduleList.get(i).baseaddress = ori_base;
					for(int j=0;j<moduleList.get(i).definitionList.size();j++) {
						
						if(moduleList.get(i).definitionList.get(j).location >=moduleList.get(i).addressList.size()) {
							//if definition address exceeds size of module, we give it a relative address 0, error will be added later
							moduleList.get(i).definitionList.get(j).oldadd = String.format("%03d", moduleList.get(i).definitionList.get(j).location);
							moduleList.get(i).definitionList.get(j).location = 0;
							
						}
						//use base address to calculte the absolute address of each definition
						moduleList.get(i).definitionList.get(j).Abs_Add = String.format("%03d",
								moduleList.get(i).definitionList.get(j).location + moduleList.get(i).baseaddress);
						// if the definintion is already in symboltable then it is multiply defined
						// we add an error
						if(symbol_table.containsKey(moduleList.get(i).definitionList.get(j).name)) { 
							symbol_table.replace(moduleList.get(i).definitionList.get(j).name, symbol_table.get(moduleList.get(i).definitionList.get(j).name)+" Error: This variable is multiply defined, first value honored");
							continue;
						}
						symbol_table.put(moduleList.get(i).definitionList.get(j).name, moduleList.get(i).definitionList.get(j).Abs_Add);
						truth_table.put(moduleList.get(i).definitionList.get(j).name, false);
						
						if(moduleList.get(i).definitionList.get(j).oldadd != "") {
							// add error if definition address exceeds the size of the module
							symbol_table.replace(moduleList.get(i).definitionList.get(j).name, symbol_table.get(moduleList.get(i).definitionList.get(j).name) + " Error: definition address exceeds the size of the module, 0(relative) used");
							
						}
					}
					
				}
				/*
				 * Uses the address of definitions to resolve external symbols
				 * Added in some error checking wherever makes sense
				 * 
				 * 
				 * */
	
				
				for(int i=0;i<modules;i++) {
					moduleList.get(i).padd();
					for(int j=0;j<moduleList.get(i).useList.size();j++) {
						
						int location = moduleList.get(i).useList.get(j).location;
						
						do {
							if(symbol_table.containsKey(moduleList.get(i).useList.get(j).name)==false) {
								symbol_table.put(moduleList.get(i).useList.get(j).name, "000 Error: "+moduleList.get(i).useList.get(j).name+" is not defined; zero used");
								
							}
							if(moduleList.get(i).ResultList.get(location).equals("")) {
								/*
								 * If there is currently nothing in the resultList, that means it wasn't resolved yet
								 * thus we know resolve it
								 * */
							moduleList.get(i).ResultList.set(location,moduleList.get(i).addressList.get(location)/10000 + 
								  symbol_table.get(moduleList.get(i).useList.get(j).name) );
							}else {
								/*
								 * If there is already a value in it
								 * Then we know this place was used by more than one symbol, we add an extra error message
								 * */
								moduleList.get(i).ResultList.set(location,moduleList.get(i).addressList.get(location)/10000 + 
										  symbol_table.get(moduleList.get(i).useList.get(j).name) + " Error: More than one symbol used, last symbol honoroed");
								
							}
							if(moduleList.get(i).addressList.get(location)%10 == 1) {
								/*
								 * If the last digit is 1, that means an immediate address appeared on use list
								 * We add a error message
								 * 
								 * */
								
								moduleList.get(i).ResultList.set(location,moduleList.get(i).ResultList.get(location)+" Error: Immediate address appeared on use list, treated as external address");
								
							}
							
							truth_table.replace(moduleList.get(i).useList.get(j).name, true);	
							
							location = moduleList.get(i).addressList.get(location);
							location = Integer.parseInt(Integer.toString(location).substring(1))/10;
							
						}while(location != 777);
						
					}
				}
				//Prints out the symbol table
				System.out.println("Symbol Table");
				for( String key: symbol_table.keySet()) {
					if(symbol_table.get(key).contains("is not defined")==false) {
					System.out.println(key+"="+symbol_table.get(key));
					}
					
					
				}
				System.out.println();
				System.out.println("Memory Map");
				int warden = 0;
				/*Beginning of Pass two, uses base addresses and symbol table to calculate rest of the addresses
				 * added and removed some error messages wherever makes sense
				 * 
				 * 
				 * 
				 * */
				for(int i=0;i<modules;i++) {
					for(int j=0; j< moduleList.get(i).ResultList.size();j++) {
						if(moduleList.get(i).ResultList.get(j)=="") {
							int length = Integer.toString(moduleList.get(i).addressList.get(j)).length()-1;
							/*If the last digit is 3, that means it is a relative address
							 * We use base address to calculate its value
							 * */
							if(Integer.toString(moduleList.get(i).addressList.get(j)).charAt(length) == '3') {
								
								moduleList.get(i).ResultList.set(j,  Integer.toString(moduleList.get(i).addressList.get(j)/10 + moduleList.get(i).baseaddress).substring(0,length)) ;
							}
							//If last digit is 4, an error occured, we add error message and treat it as immediate
							if(Integer.toString(moduleList.get(i).addressList.get(j)).charAt(length) == '4') {
								moduleList.get(i).ResultList.set(j,  Integer.toString(moduleList.get(i).addressList.get(j)).substring(0,length) + " Error: external address not on a use list, trated as immediate address") ;
							}
							//Immediate address
							if(Integer.toString(moduleList.get(i).addressList.get(j)).charAt(length) == '1') {
								moduleList.get(i).ResultList.set(j,  Integer.toString(moduleList.get(i).addressList.get(j)).substring(0,length)) ;
							}
							//absolte address, we check if it is more than max word, if it is, add error message
							if(Integer.toString(moduleList.get(i).addressList.get(j)).charAt(length) == '2') {
								int tester = moduleList.get(i).addressList.get(j); tester = tester/10;
								if(tester%1000 <200) {
									moduleList.get(i).ResultList.set(j,  Integer.toString(moduleList.get(i).addressList.get(j)).substring(0,length)) ;
								}else {
									moduleList.get(i).ResultList.set(j,  Integer.toString(moduleList.get(i).addressList.get(j)).substring(0,1)+"199"+" Error: absolute address exceed size of machine, used largest legal val") ;
								}
							}
						}
						//Omit some errors that was previously displayed to the user already
						if(moduleList.get(i).ResultList.get(j).contains("definition address exceeds the size of the module")){
							String argument = "Error: definition address exceeds the size of the module, 0(relative) used";
							moduleList.get(i).ResultList.set(j,moduleList.get(i).ResultList.get(j).replace(argument, ""));
						}
						if(moduleList.get(i).ResultList.get(j).contains("first value honored")) {
							String argument = "Error: This variable is multiply defined, first value honored";
							System.out.println(warden+" : "+moduleList.get(i).ResultList.get(j).replace("Error: This variable is multiply defined, first value honored", ""));
						}else {
						System.out.println(warden+" : "+moduleList.get(i).ResultList.get(j));
						}
						warden++;
					}
				}

				
				int counter = -1;
				for( String key: truth_table.keySet()) {
					if(truth_table.get(key)==false) {
						for(int i=0; i < modules;i++) {
							for(int j=0;j<moduleList.get(i).definitionList.size();j++) {
								if(moduleList.get(i).definitionList.get(j).name.equals(key)) {
									counter = i;
								}
							}
						}
						System.out.println();
						System.out.println("Error: "+key+" was defined in module "+counter+" but never used");
					}
					
				}
			
			
				
	}
}
