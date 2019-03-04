package uet.nam;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import edu.gatech.mcpad.utils.ClusteringConfiguration;
import edu.gatech.mcpad.utils.DirectoryHandler;

public class JoinDimension {
	private int nu;
	private DirectoryHandler directoryHandler;
	private void writeToFile(BufferedWriter out, ArrayList<Double> content)
	{
		try {
			out.write("+1\t" + convertDoubleListToString(content) + "\r\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String convertDoubleListToString(ArrayList<Double> a)
	{
		ArrayList<String> list = new ArrayList<>();
		int index = 0;
		for(Double item : a)
		{
			index++;
			list.add(String.valueOf(index) + ":" + String.valueOf(item));
		}
		return String.join("\t", list);
	}
	
	private void printNam(Object content)
	{
		System.out.println(content);
	}
	
	private Double convertSVMItem2Double(String s, int start, int end)
	{
		Double result = 0.0;
		String item = s.substring(start, end);
		try
		{
			
			result = Double.parseDouble(item.substring(item.indexOf(':') + 1));
		}
		catch(NumberFormatException e)
		{
			
			System.out.println(s);
			System.out.println(item);
			System.out.println(item.substring(item.indexOf(':') + 1));
		}
		return result;
	}
	
	private ArrayList<Double> convertToDouble(String s)
	{
		ArrayList<Double> result = new ArrayList<>();
		int index = 0, next = 0;
		
		while (true)
		{
			next = s.indexOf('\t',index);
			if(next == -1)
				break;
			//System.out.println("next = ");
			//System.out.println(next);
			result.add(convertSVMItem2Double(s, index, next));
			index = next + 1;
		}
		//result.add(Double.parseDouble(s.substring(index)));
		return result;
	}
	
	private ArrayList<Double> addTwoDoubleArray(ArrayList<Double> a, ArrayList<Double> b)
	{
		Double sum = 0.0;
		ArrayList<Double> result = new ArrayList<>();
		for(int i = 0; i< a.size(); i++)
		{
			result.add(WEIGHT_A * a.get(i) + WEIGHT_B * b.get(i));
			sum+=result.get(i);
		}
		for(int i = 0; i< result.size(); i++)
		{
			result.set(i, result.get(i) / sum);
		}
		
		return result;
	}
	
	private ArrayList<Double> genConcatString(String[] listFileContent, int[] start, int[] end)
	{
		
		
		String file = listFileContent[0];
		ArrayList<Double> result = convertToDouble(file.substring(start[0], end[0]));
		for(int i = 1;i < nu; i++)
		{
			//printNam("i = ");
			//System.out.println(i);
			try
			{
				result = addTwoDoubleArray(result, convertToDouble(listFileContent[i].substring(start[i], end[i])));
			}
			catch(StringIndexOutOfBoundsException e)
			{
				e.printStackTrace();
				System.out.printf("Start = %d and end = %d", start[i], end[i]);
			}
		}
		return result;
	}
	
	private ArrayList<Double> genConcatString(String[] listFileContent, int[] start)
	{
		
		int[] end = new int[nu];
		for (int i = 0; i < nu; i++)
			end[i] = listFileContent[i].length() - 2;
		String file = listFileContent[0];
		ArrayList<Double> result = convertToDouble(file.substring(start[0], end[0]));
		for(int i = 1;i < nu; i++)
		{
			result = addTwoDoubleArray(result, convertToDouble(listFileContent[i].substring(start[i], end[i])));
		}
		return result;
	}
	
	private boolean checkNotFound(int[] next)
	{
		for(int i = 0; i < nu; i++)
		if (next[i] == NOT_FOUND_STRING)return true;
		return false;
	}
	
	private int[] intialStart()
	{
		int[] start = new int[nu];
		for(int i=0;i < start.length;i++) start[i] = START_POSITION;
		return start;
	}
	private int[] findNext(int[] start, String[] listFileContent)
	{
		int[] next = new int[nu];
		for(int i = 0; i < nu; i++) next[i] = listFileContent[i].indexOf(SIGN, start[i]);
		return next;
	}
	
	private int[] findCopyPos(int[] next)
	{
		int[] copyPos = new int[nu];
		for(int i = 0; i < nu; i++) copyPos[i] = next[i] - NEXT_BACK_TO_DATA;
		return copyPos;
	}
	
	private int[] updateStart(int[] next) 
	{
		int[] start = new int[nu];
		for(int i = 0; i < nu; i++) start[i] = next[i] + NEXT_TO_DATA;
		return start;
	}
	
	private void joinFiles(String[] listFileContent, String filename0)
	{
		//sua start, next, copyPos position
		//while: next1->next
		//concatstring = file1.copy
		//for concat from 1 - nu ---> new function
		System.out.println("co vao day");
		int[] start = new int[nu], next = new int[nu], copyPos = new int[nu];
		start = intialStart();
		ArrayList<Double> sumArray;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename0));
			while (true)//while  next1 = find (file1,+1)
			{
				next = findNext(start, listFileContent);//find next
				if (checkNotFound(next)) break;
				copyPos = findCopyPos(next);//find copyPos
				
				
				
				/*printNam("Day la next1: ");System.out.println(next1);
				printNam("next2: "); System.out.println(next2);
				printNam("start1: "); System.out.println(start1);
				printNam("start2: "); System.out.println(start2);
				printNam("file: "); System.out.println(index);*/
				//concatString = file1.substring(start1, copyPos1).concat(file2.substring(start2, copyPos2));
				
				sumArray = genConcatString(listFileContent, start, copyPos);
				
				writeToFile(out, sumArray);//write to 0.data.
				//update start
				start = updateStart(next);
				
				
			}
			
			/*sumArray = genConcatString(listFileContent, start);
				
			writeToFile(out, sumArray);*/
				
			out.close();
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	private String readFile(String path) throws IOException
	{
		byte[] encoded =  Files.readAllBytes(Paths.get(path));
		return new String(encoded);
	}
	
	private String getFileName(File[] trainingSetFileName, int i)
	{
		return directoryHandler.getTrainingsetDir().getAbsoluteFile() + File.separator
				+ trainingSetFileName[i].getName().toString();
	}
	
	private String getFileContent(File[] trainingSetFileName, int i)
	{
		
		String filename = getFileName(trainingSetFileName, i), result = "";
		try {
			/*FileInputStream inputFile = new FileInputStream(filename);
			ObjectInputStream input = new ObjectInputStream(inputFile);
			String result = (String)input.readObject();*/
			result = readFile(filename);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return result;
	}
	private void writeToDataSet(String content,String file)
	{
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(content);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void join(String rootDirectory)
	{
		directoryHandler = new DirectoryHandler(rootDirectory);
		ClusteringConfiguration clusConfig = new ClusteringConfiguration(directoryHandler
												.getClusterConfigFile().getAbsolutePath());
		
		nu = clusConfig.nu.length ;//int nu = ?
		
		File[] trainingSetFileName = directoryHandler.getTrainingsetDir().listFiles();
		String file1 = getFileContent(trainingSetFileName, 0), file2 = "",result = "",
				filename0 = getFileName(trainingSetFileName, 0);
		String[] listFileContent = new String[11];
		for(int i = 0;i < nu;i++)//for 1 - > nu
		{
			System.out.println("i=" + i);
			listFileContent[i] = getFileContent(trainingSetFileName, i);//get name to list
			
		}
		joinFiles(listFileContent,filename0);
		deleteResidualFile();
	}
	
	private void deleteResidualFile()
	{
		//xac dinh thu muc
		//list file
		File[] trainingSetFileName = directoryHandler.getTrainingsetDir().listFiles();
		try
		{
			for (int i = 1; i < nu; i++) trainingSetFileName[i].delete();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private final char SIGN = '+';
	private final int START_POSITION = 3;
	private final int NEXT_BACK_TO_DATA = 2;
	private final int NEXT_TO_DATA = 3;
	private final int NOT_FOUND_STRING = -1;
	private final int END_FILE = -1;
	private final int WEIGHT_A = 1;
	private final int WEIGHT_B = 1;

}
