import java.util.List;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;

/* variable name check is pending */


public class Utils {
	public static boolean isGetter(FieldDeclaration field, MethodDeclaration method)
	
	{
		boolean getter = false;
		int modifier = method.getModifiers();/*==1*/
		List<Parameter> parameters = method.getParameters(); /*isEmpty or null*/
		Type methodType = method.getType();
		Type fieldType = field.getType();
		if(modifier == 1 &&( parameters== null || parameters.isEmpty()) 
				&& methodType.toString().equals(fieldType.toString()) 
				)
		{
			getter = true;
		}
		return getter;
	}
	
	public static boolean isSetter(FieldDeclaration field, MethodDeclaration method)
	
	{
		boolean setter = false;
		int modifier = method.getModifiers();/*==1*/
		List<Parameter> parameters = method.getParameters(); /*isEmpty or null*/
		Type methodType = method.getType();
		Type fieldType = field.getType();
		
		
		if(modifier == 1 &&( parameters!= null && parameters.size() == 1 &&  parameters.get(0).getType().toString().equals(fieldType.toString())) 
				&& methodType.toString().toLowerCase().equals("void") 
				)
		{
			setter = true;
		}
		return setter;
	}
	
}
