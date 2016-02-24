import java.io.BufferedOutputStream;
import java.io.File;
//import java.util.List;
import java.io.FileOutputStream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class Main {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		File folder = new File(FolderName.tc5);
		File[] files = folder.listFiles();

		String classString = "";
		String relString = "";
		for(File f :files)
		{	
			if(f.isFile())
			{
				String fileName = f.getName();
				String ext = fileName.substring(fileName.lastIndexOf('.') + 1);				
				if(ext.equals("java"))
				{
					String x = "";
					CompilationUnit cu = JavaParser.parse(f);					
					x = cu.accept(new MyVisitor(), null);
					if(x!=null)
						classString+=x;
				}	
			}
	}
		for(int i=0;i<files.length;i++)
		{
			File f = files[i];
			
			if(f.isFile())
			{
				String fileName = f.getName();
				String ext = fileName.substring(fileName.lastIndexOf('.') + 1);				
				if(ext.equals("java"))
				{
					String x = "";
					CompilationUnit cu = JavaParser.parse(f);					
					
					x = cu.accept(new RelVisitor(), classString);
					if(x!=null)
						relString+=x;
				}	
			}
		}
		String resultYuml = classString + relString;
		byte[] utf8Bytes = resultYuml.getBytes("UTF8");
		BufferedOutputStream b = new BufferedOutputStream(new FileOutputStream(new File("C:\\Users\\Bala\\Desktop\\202 - SSE\\UML Parser Project\\yUMLOutput.yuml")));
		b.write(utf8Bytes); b.close();
		//System.out.println(classString);
		//System.out.println(relString);
		System.out.println(resultYuml);
	}

}
