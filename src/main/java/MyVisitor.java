import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

public class MyVisitor extends GenericVisitorAdapter<String,Object> {

	@Override
	public String visit(final CompilationUnit n, final Object arg) {

		List<TypeDeclaration> typeDeclarations = n.getTypes();
		String classString = "";
		if (typeDeclarations != null) {
			classString += "[";
			for (final TypeDeclaration typeDeclaration :typeDeclarations) 
			{
				String x = typeDeclaration.accept(this, arg);
				x = Utils.getTypeForUMLString(x);
				if(x!=null)
					classString += x;
				classString+=("]\n");
			}
		}
		
		return classString;
	}

	@Override
	public String visit(final ClassOrInterfaceDeclaration n, final Object arg) {
		String res = n.getName();
		res = 
				(n.isInterface()?Constants.interfaceBegin+res: res);
				//(n.isInterface()?"<<"+res+">>": res);
		List<BodyDeclaration> members = n.getMembers();
		List<MethodDeclaration> methods = new LinkedList<MethodDeclaration>();
		List<FieldDeclaration> fields = new LinkedList<FieldDeclaration>();
		List<ConstructorDeclaration> constructors = new LinkedList<ConstructorDeclaration>();
		if (members != null) {
			for (final BodyDeclaration member : members) {	
				if(member instanceof FieldDeclaration)
				{
					fields.add((FieldDeclaration)member);
				}
				else if(member instanceof MethodDeclaration)
				{
					methods.add((MethodDeclaration)member);
				}		
				else if(member instanceof ConstructorDeclaration)
				{
					constructors.add((ConstructorDeclaration)member);
				}
			}
		}
		//if(!n.isInterface())
			res = res +"|";
		if(!fields.isEmpty())
		{
			for(FieldDeclaration field : fields)
			{
				//boolean getterFlag=false,setterFlag=false;
				int modifier = field.getModifiers();			
				String x = "";
				
				if(modifier==1 || modifier == 2)
				{
					x +=  field.accept(this, arg);
					if(x!=null)
					{	res = res + x; } 
				}
			}
		}
		//if(!n.isInterface())
			res = res + "|";
		if(!constructors.isEmpty())
		{
			for(ConstructorDeclaration constructor : constructors)
			{
				String x = constructor.accept(this, arg);
				if(x != null)
				{	res = res+x; }
			}
		}
		if(!methods.isEmpty())
		{			
			for(MethodDeclaration method : methods)
			{
				
				String x = method.accept(this, arg);
				if(x != null)
				{	res = res+x; }
			}
		}
		return res;
	}

	@Override
	public String visit(final FieldDeclaration n, final Object arg) {
		String res = "";
		
		int fieldModifier = n.getModifiers();
		res = res +Utils.getModifierString(fieldModifier)+"";
		
		List<VariableDeclarator> variables = n.getVariables();
		Type type= n.getType();
		String typeName = type.toString();
		if(typeName!=null && typeName.contains("[]"))
		{
			typeName = typeName.replace("[]","\uFF3B\uFF3D");
		}
		if(variables!=null)
		{
			for(VariableDeclarator variable : variables)
			{
				res = res+variable.getId().toString()+":"+typeName+";";
			}
		}
		//n.getChildrenNodes().

		//super.visit(n, arg);	
		return res;
	}
	
	@Override
	public String visit(final MethodDeclaration n, final Object arg) {
		String res="";
		int methodModifier = n.getModifiers();
		res= res+ Utils.getModifierString(methodModifier)+ "";
		String methodName = n.getName()+"";
		if(methodName!=null)
		{res = res + methodName;}
		List<Parameter> parameters =  n.getParameters();
		int paramCount = 0;
		res = res + "(";
		if(parameters!= null && !parameters.isEmpty()){
			for(Parameter parameter : parameters){
				String x =  parameter.accept(this, arg);
				if(x!=null)
					
				{ 
					res = res + x; paramCount++;}
				if(paramCount != parameters.size())
				{ res = res + ",";}
			}
		}
		res = res + ")";
		res = res + ":";
		res = res + n.getType().toString();
		res = res + ";";
		return res;
	}
	public String visit(final ConstructorDeclaration n, final Object arg ){
		String res="";
		int constructorModifier = n.getModifiers();
		res= res+ Utils.getModifierString(constructorModifier)+ "";
		String methodName = n.getName()+" ";
		if(methodName!=null)
		{res = res + methodName;}
		List<Parameter> parameters =  n.getParameters();
		int paramCount = 0;
		res = res +"(";
		if(parameters!= null && !parameters.isEmpty()){
			for(Parameter parameter : parameters){
				String x =  parameter.accept(this, arg);
				if(x!=null)	
				{					res = res + x; paramCount++;}
				if(paramCount != parameters.size())
				{ res = res + ",";}
			}
		}
		res = res+")";
		res = res + ";";
		return res;
	}

	@Override
	public String visit(final Parameter n, final Object arg){
		String res = "";
		if(n!=null && n.getId()!=null && n.getType()!=null)
		{		
			String varName = n.getId().getName();
			String varType = n.getType().toString();
			/*support for yUML array sq brackets */
			if(varType.contains("[]")){
				varType = varType.replace("[]", "\uFF3B\uFF3D");
			}
			if(varName!= null && varType!=null)
			{
				res = res + varName + ":" + varType;
			}
		}
		return res;
	}	 
}