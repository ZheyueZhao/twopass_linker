package Lab1;
import java.util.*;
public class Module {
	public List<definition> definitionList = new ArrayList<definition>();
	public List<use> useList = new ArrayList<use>();
	public List<Integer> addressList = new ArrayList<Integer>();
	public List<String> ResultList = new ArrayList<String>();
	public int baseaddress = 0;
	
	
	public void padd() {
		
		for(int i=0;i<addressList.size();i++) {
			ResultList.add("");
		}
	}
	
	public void check_exceeds() {
		for(int i=0;i<definitionList.size();i++) {
			if(definitionList.get(i).location > addressList.size()-1) {
				
				definitionList.get(i).exceeds = true;
				definitionList.get(i).oldadd = String.format("%03d" , definitionList.get(i).location);
				definitionList.get(i).location = 0;
			}
		}
		
		
	}
}



