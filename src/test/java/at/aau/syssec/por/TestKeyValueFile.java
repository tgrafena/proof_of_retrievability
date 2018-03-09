package at.aau.syssec.por;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

public class TestKeyValueFile {

	@Test
	public final void testGetArrayValue() {
		KeyValueFile file = null;
		try {
			file = new KeyValueFile("~/.por/por.conf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String key = "test";
		String emptyKey = "empty";
		String [] values = {"test1", "test2", "test3"};
		String [] empty = new String[0];
		
		
		//test full array:
		file.setArrayValue(key, values);
		String [] returnValues = file.getArrayValue(key);
		
		if(returnValues.length != values.length){
			fail("Failure_Test_KeyValueFile_GetArrayValue: length missmatch between origin value-array and returnValue-array.");
		}
		
		for(int i = 0; i < returnValues.length; i++){
			if(!values[i].equals(returnValues[i])){
				fail("Failure_Test_KeyValueFile_GetArrayValue: value missmatch between origin value-array and returnValue-array.");
			}
		}
		
		//test empty array:
		
		file.setArrayValue(emptyKey, empty);
		returnValues = file.getArrayValue(emptyKey);
		
		if(returnValues.length != empty.length){
			fail("Failure_Test_KeyValueFile_GetArrayValue: length missmatch between empty value-array and returnValue-array.");
		}
		
		
		
	}

	@Test
	public final void testSetArrayValue() {
		KeyValueFile file = null;
		try {
			file = new KeyValueFile("~/.por/por.conf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String key = "test";
		String emptyKey = "empty";
		String [] values = {"test1", "test2", "test3"};
		String [] empty = new String[0];
		
		
		//test full array:
		file.setArrayValue(key, values);
		String [] returnValues = file.getArrayValue(key);
		
		if(returnValues.length != values.length){
			fail("Failure_Test_KeyValueFile_SetArrayValue: length missmatch between origin value-array and returnValue-array.");
		}
		
		for(int i = 0; i < returnValues.length; i++){
			if(!values[i].equals(returnValues[i])){
				fail("Failure_Test_KeyValueFile_SetArrayValue: value missmatch between origin value-array and returnValue-array.");
			}
		}
		
		//test empty array:
		
		file.setArrayValue(emptyKey, empty);
		returnValues = file.getArrayValue(emptyKey);
		
		if(returnValues.length != empty.length){
			fail("Failure_Test_KeyValueFile_SetArrayValue: length missmatch between empty value-array and returnValue-array.");
		}
		
	}

}
