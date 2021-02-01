package server_side;

public interface CacheManager<Sol, Pro>
{
	public boolean IsExistSolution(Pro problem);
	public String GetSolution(Pro problem);
	public void Save(Sol solution, Pro problem);
}
