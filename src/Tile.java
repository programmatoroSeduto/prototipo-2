
public class Tile 
{
	//posizione cella
	public int x, y;
	//� una cella donatrice?
	public boolean canSpread = false;
	//valore attuale e media locale
	public float actualValue=0, localAverage=0;
	//il valore di localAverage � valido?
	public boolean localAverageFlag = false;
	//richieste dalle celle adiacenti
	public float request[] = {0, 0, 0, 0};
	
	public Tile(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
}
