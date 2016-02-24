import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;

/* variable name check is pending */


public class Utils {
	
	public static String getTypeForUMLString(String typeName){
		if(typeName!=null){
			typeName = typeName.replace("<", "\uFF1C");
			typeName = typeName.replace(">", "\uFF1E");
		}
		return typeName;
	}
	
	public static String constructHasARelationShipClass(String className, String type){
		String res = "";
		res = res +"[";
		res = res + className;
		res = res + "]";
		res = res + Lines.HAS_ARROW;
		res = res + "[";
		res = res + type;
		res = res + "]";
		res = res +"\n";
		return res;
	}
	public static String constructHasARelationShipInterface(String className, String interfaceVar){
		String res = "";
		res = res +"[";
		res = res + className;
		res = res + "]";
		res = res + Lines.HAS_ARROW;
		res = res + interfaceVar;
		return res;
	}
	
	public static String constructUsesARelationShipClass(String className, String type){
		String res = "";
		res = res +"[";
		res = res + className;
		res = res + "]";
		res = res + Lines.USES_ARROW;
		res = res + "[";
		res = res + type;
		res = res + "]";
		res = res +"\n";
		return res;
	}
	public static String constructUsesARelationShipInterface(String className, String interfaceVar){
		String res = "";
		res = res +"[";
		res = res + className;
		res = res + "]";
		res = res + Lines.USES_ARROW;
		res = res + interfaceVar;
		return res;
	}
	/*public static String constructHasARelationShipInterface(String className, String type){
		
	}*/
	public static List<String> getInterfaces(String classString)
	{
		if(classString==null)
			return null;
		String[] classes = classString.split("\n");
		List<String> interfaceList = new ArrayList<String>();
		for(String klass: classes)
		{
			//String compString = klass.substring(1,1+ Constants.interfaceBegin.length());
			if(isInterface(klass))
			{
				interfaceList.add(klass+"\n");
			}
		}
		return interfaceList;
	}
	
	public static List<String> getClasses(String classString)
	{
		if(classString==null)
			return null;
		String[] classes = classString.split("\n");
		List<String> classList = new ArrayList<String>();
		for(String klass: classes)
		{
			//String compString = klass.substring(1,1+ Constants.interfaceBegin.length());
			if(!isInterface(klass))
			{
				classList.add(klass+"\n");
			}
		}
		return classList;
	}
	public static boolean isInterface(String klass)
	{
		boolean isInterface = false;
		if(1+ Constants.interfaceBegin.length() <= klass.length())
		{
			String compString = klass.substring(1,1+ Constants.interfaceBegin.length());
			if(compString!=null && compString.equals(Constants.interfaceBegin))
			{
				isInterface = true;
			}
		}
		return isInterface;
	}
	
	public static boolean hasInterface(String parentInterfaceName ,String interfaceVar)
	{
		boolean hasInterface = false;
		int interfaceVarLength = Constants.interfaceBegin.length();
		int parentInterfaceNameLength = parentInterfaceName.length();
		if(interfaceVarLength+1+parentInterfaceNameLength <= interfaceVar.length())
		{
			String interfaceNameFromUML = interfaceVar.substring(interfaceVarLength+1,interfaceVarLength+1+parentInterfaceNameLength);
			
			if(interfaceNameFromUML!=null && interfaceNameFromUML.equals(parentInterfaceName)) 
			{
				hasInterface = true;
			}
		}
		return hasInterface;
	}
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

	public static String getModifierString(int modifier)
	{
		String res = "";
		switch(modifier)
		{
		case 1:
		{
			res = "+"; break;
		}
		case 2:
		{
			res = "-"; break;
		}
		default: {break;}
		}
		return res;
	}
	
	public static String getClassName(String classString)
	{
		if(classString == null)
			return "";
		String res= "";
		if(!isInterface(classString))
		{
			if(classString.contains("|"))
			{
				int startIndex = 1;
				int lastIndex = classString.indexOf("|");
				res = classString.substring(startIndex,lastIndex);
			}
			else
			{
				int startIndex = 1;
				int lastIndex = classString.lastIndexOf("]");
				res = classString.substring(startIndex,lastIndex);
			}
		}
		return res;
	}
	public static String getInterfaceName(String classString)
	{
		if(classString == null)
			return "";
		String res= "";
		if(isInterface(classString))
		{
			if(classString.contains("|"))
			{
				int startIndex = Constants.interfaceBegin.length()+1;
				int lastIndex = classString.indexOf("|");
				res = classString.substring(startIndex,lastIndex);
			}
			else
			{
				int startIndex = Constants.interfaceBegin.length()+1;
				int lastIndex = classString.lastIndexOf("]")-1;
				res = classString.substring(startIndex,lastIndex);
			}
		}
		return res;
	}
	
	public static Set<String> getTypeParamsFromType (String type)
	{
		Set<String> list = new HashSet<String>();
		if(hasTypeParams(type)){
			int firstIndex = type.indexOf("\uFF1C");
			int lastIndex = type.lastIndexOf("\uFF1E")+1;
			
			list = getAllParams(type.substring(firstIndex,lastIndex));
		}
		return list;
	}

		
	private static Set<String> getAllParams(String S)
	{
		S.replaceAll("\\s", "");
		int len = S.length();
		Set<String> parameters = new HashSet<String>();
		int prevIndex = 1;
		for(int i=1;i<len ;i++){
			if(S.charAt(i) == '\uFF1C' || S.charAt(i) == '\uFF1E' || S.charAt(i) == ','){
				String parameter = S.substring(prevIndex,i);
				if(!(parameter == null || parameter.equals("\uFF1C") || parameter.equals("\uFF1E")
						|| parameter.equals(",") || parameter.equals("")))
					parameters.add(parameter);
				prevIndex = i+1;
			}
		}
		return parameters;
	}
	
	/*
	private static List<String> getAllParams(String S)
	{
		int len = S.length();
		Stack<Integer> stack  = new Stack<Integer>();
		List<String> parameters = new LinkedList<String>();
		for(int i=0;i<len ;i++){
			if(S.charAt(i) == '<'){
				stack.push(i);
			}
			else if(S.charAt(i) == '>'){
				int beginIndex = stack.pop();
				int endIndex = i;
				String x = S.substring(beginIndex,endIndex);
				if(x.contains(","))
				{
					String[] paramsSepComma = x.split(",");
					for(String paramSepComma:paramsSepComma){
						parameters.add(paramSepComma);
					}
				}
				else
					parameters.add(x);
				
				if(!stack.isEmpty())
					break;
			}
		}
		return parameters;
		
	}
	*/
	
	public static boolean hasTypeParams(String type){
		boolean hasTypeParams = false;
		if(type.contains("\uFF1C") && type.contains("\uFF1E")){
			hasTypeParams = true;
		}
		return hasTypeParams;
	}
	
}
