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
				(n.isInterface()?"<<interface>>;"+res: res);
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
		if(!n.isInterface())
			res = res +"|";
		if(!fields.isEmpty())
		{
			for(FieldDeclaration field : fields)
			{
				//boolean getterFlag=false,setterFlag=false;
				int modifier = field.getModifiers();			
				String x = "";
				
				if(modifier==1)
				{
					x =  field.accept(this, arg);
					if(x!=null)
					{	res = res + x; } 
				}
				else if(modifier == 2)
				{

						x = x + field.accept(this, arg);
						if(x!=null)
						{	res = res + x;  }
					
				}
				
			}
		}
		if(!n.isInterface())
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
		if(fieldModifier == 1){
			res = res + " +";
		}
		else if(fieldModifier == 2){
			res = res + " -";
		}
		
		List<VariableDeclarator> variables = n.getVariables();
		Type type= n.getType();
		if(variables!=null)
		{
			for(VariableDeclarator variable : variables)
			{
				res = res+variable.toString()+":"+type.toString()+";";
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
		if(methodModifier == 1)
		{ res = res +"+ ";}
		else if(methodModifier == 2)
		{ res = res +"+ ";}
		String methodName = n.getName()+" ";
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
				{ res = res + " , ";}
			}
		}
		res = res + ")";
		res = res + " :";
		res = res + n.getType().toString();
		res = res + ";";
		return res;

	}
	public String visit(final ConstructorDeclaration n, final Object arg ){
		String res="";
		int constructorModifier = n.getModifiers();
		if(constructorModifier == 1)
		{ res = res +"+ ";}
		else if(constructorModifier == 2)
		{ res = res +"+ ";}
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
				{
					res = res + x; paramCount++;}
				if(paramCount != parameters.size())
				{ res = res + " , ";}
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
			if(varName!= null && varType!=null)
			{
				res = res + varName + " : " + varType;
			}
		}
		return res;
	}

	 
}