package interperter_side;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Scope {
	private Scope innerScope;
	private ConcurrentHashMap<String, Value> vars;
	private ConcurrentHashMap<String, List<String>> binded;
	
	public Scope() {
		innerScope = null;
		vars = new ConcurrentHashMap<String, Value>();
		binded = new ConcurrentHashMap<String, List<String>>();
	}
	
	public boolean isExistInnerScope()
	{
		return innerScope!=null;
	}
	
	public List<String> getVriablesThatAreBinded(String bind)
	{
		if(binded.containsKey(bind))
		{
			return binded.get(bind);
		}
		return new ArrayList<String>();
	}
	
	public List<String> getBindedToVariable(String variable)
	{
		ArrayList<String> arr = new ArrayList<String>();
		for(String bind : binded.keySet())
		{
			if(binded.get(bind).contains(variable))
			{
				arr.add(bind);
			}
			
		}
		return arr;
	}
	
	public boolean isExistVar(String name)
	{
		boolean found = false;
		
		if(innerScope != null)
		{
			found = innerScope.isExistVar(name);
		}
		
		if(found)
			return found;
		
		return vars.containsKey(name);
	}
	
	public Scope getInnerScope()
	{
		return innerScope;
	}
 	
	public void createScope() {
		if(innerScope == null)
			innerScope = new Scope();
		else
			innerScope.createScope();
	}
	
	public void deleteScope() {
		if(innerScope.innerScope == null)
			innerScope = null;
		else
			innerScope.deleteScope();
	}
	
	public void addVariable(String name)
	{
		if(innerScope!=null)
		{
			innerScope.addVariable(name);
			return;
		}
		
		vars.put(name, new Value());
	}
	
	public boolean setVariable(String name, double value)
	{
		boolean success = false;
		
		if(innerScope!=null)
			success = innerScope.setVariable(name, value);
		
		if(success)
			return true;
		
		if(vars.containsKey(name))
		{
			vars.get(name).setValue(value);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean bindVariable(String name ,String bind)
	{
		boolean success = false;
		
		if(innerScope!=null)
			success = innerScope.bindVariable(name, bind);
		
		if(success)
			return true;
		
		if(vars.containsKey(name))
		{
			List<String> list;
			
			if(binded.containsKey(bind))
			{
				list = binded.get(bind);
			}
			else
			{
				list = new ArrayList<String>();
			}
			list.add(name);
			binded.put(bind, list);
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public Value getVariable(String name)
	{
		Value value = null;
		
		if(innerScope!=null)
			value = innerScope.getVariable(name);
		
		if(value != null)
			return value;
		
		if(vars.containsKey(name))
		{
			return vars.get(name);
		}
		else
		{
			return null;
		}
	}
}
