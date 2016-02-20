import java.io.File;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class Main {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		File folder = new File(FolderName.tc5);
		File[] files = folder.listFiles();
		//while(File f : files)
		//{
		for(int i=0;i<files.length;i++)
		{
			File f = files[i];
			if(f.isFile())
			{
				String fileName = f.getName();
				String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
				String classString = "";
				if(ext.equals("java"))
				{
					CompilationUnit cu = JavaParser.parse(f);
					
					classString = cu.accept(new MyVisitor(), null);
					System.out.print(classString);
				}
				
			}
		}
		//}
	}

}
