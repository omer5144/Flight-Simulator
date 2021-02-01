package server_side;

public class MatrixPath {

	private int i,j;
	
	public int GetI()
	{
		return this.i;
	}
	public int GetJ()
	{
		return this.j;
	}
	public MatrixPath(int i, int j)
	{
		this.i=i;
		this.j=j;
	}
	public boolean equals(Object mp)
	{ 
		MatrixPath newmp = (MatrixPath)mp;
		return (newmp.i == this.i && newmp.j == this.j);
	}
	
	@Override
	public String toString()
	{
		return i+", "+j;
	}
	
	public int hashCode()
	{
		return (this.toString().hashCode());
	}
	
}
