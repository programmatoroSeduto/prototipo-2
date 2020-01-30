
public class ValueMap 
{
	private Resource r;
	private Tile map[][];
	private int n=1, m=1;
	public float globalAvg = 0;
	
	private static int UP = 0;
	private static int RIGHT = 1;
	private static int DOWN = 2;
	private static int LEFT = 3;
	
	private static int dir[][] = { {0, 1}, {1, 0}, {0, -1}, {-1, 0} };
	private static int dirX = 0;
	private static int dirY = 1;
	private static String nameOfDir[] = { "UP", "RIGHT", "DOWN", "LEFT"};
	
	public ValueMap(int n, int m, float k)
	{
		map = new Tile[n][m];
		for(int i=0; i<n; i++)
		{
			for(int j=0; j<m; j++)
			{
				map[i][j] = new Tile(i, j);
			}
		}
		this.n = n;
		this.m = m;
		r = new Resource(k);
	}
	
	public Tile getNearTile(int x, int y, int direction)
	{
		x += dir[direction][dirX];
		y += dir[direction][dirY];
		
		if((x>=0 && x<n) && (y>=0 && y<m))
		{
			Main.debugprint("NEAR CELL posizione: (" + x + ", " + y + ") direzione:" + nameOfDir[direction] + " - OK");
			return map[x][y];
		}
		else
		{
			Main.debugprint("NEAR CELL posizione: (" + x + ", " + y + ") direzione:" + nameOfDir[direction] + " - NON ESISTE");
			return null;
		}
	}
	
	public void updateStep1()
	{
		Main.debugprint("AGGIORNAMENTO fase 1");
		for(int x=0; x<n; x++)
		{
			for(int y=0; y<m; y++)
			{
				Main.debugprint("posizione: (" + x + ", " + y + ")");
				
				Tile t = map[x][y];
				
				//media locale
				float tot = t.actualValue;
				int n = 5;
				for(int i=0; i<4; i++)
				{
					Tile tadj = getNearTile(x, y, i);
					if(tadj == null)
					{
						n--;
					}
					else
					{
						tot += tadj.actualValue;
					}
				}
				
				t.localAverage = tot / n;
				t.localAverageFlag = true;
				
				Main.debugprint("MEDIA LOCALE: " + t.localAverage);
				
				//verifica
				if(t.localAverage < t.actualValue)
				{
					//cella donatrice
					t.canSpread = true;
					Main.debugprint("CAN SPREAD");
				}
				else if(t.localAverage > t.actualValue)
				{
					Main.debugprint("CELLA RICHIEDE RISORSA");
					
					//cella richiedente
					//quanto mi serve?
					float toRequire = r.k() * (t.localAverage - t.actualValue);
					
					Main.debugprint("risorsa da richiedere: " + toRequire);
					
					//cerco le celle donatrici e faccio le richieste
					tot -= t.actualValue;
					for(int i=0; i<4; i++)
					{
						Tile tadj = getNearTile(x, y, i);
						if(tadj == null) continue;
						if(tadj.actualValue < t.localAverage)
						{
							tot -= tadj.actualValue;
						}
					}
					for(int i=0; i<4; i++)
					{
						Tile tadj = getNearTile(x, y, i);
						if(tadj == null) continue;
						if(tadj.actualValue >= t.actualValue)
						{
							//posso richiedere! evvai!
							int newDir = (i+2)%4;
							tadj.request[newDir] += (tadj.actualValue/tot) * toRequire;
							Main.debugprint("richiesta finale: (" + i + ", " + tadj.request[newDir] + ")");
						}
					}
				}
			}
		}
	}
	
	public void updateStep2()
	{
		Main.debugprint("AGGIORNAMENTO fase 2");
		
		for(int x=0; x<n; x++)
		{
			for(int y=0; y<m; y++)
			{
				Main.debugprint("posizione: (" + x + ", " + y + ")");
				
				Tile t = map[x][y];
				if(t.canSpread)
				{
					float spreadable = r.k() * (-t.localAverage + t.actualValue);
					Main.debugprint("donabile : " + spreadable);
					
					//verificare di non aver sforato il limite di richieste
					float totReq = t.request[0] + t.request[1] + t.request[2] + t.request[3];
					if(spreadable < totReq)
					{
						Main.debugprint("FACCIO LA CRESTA");
						float cresta = totReq - spreadable;
						for(int i=0; i<4; i++)
						{
							if(t.request[i] > 0)
							{
								Main.debugprint("CORREZIONE direzione " + i + " valore " + t.request[i]);
								t.request[i] -= ((totReq - t.request[i])/totReq)*cresta;
							}
						}
					}
					
					//concedo le richieste
					float delta = 0;
					for(int i=0; i<4; i++)
					{
						Tile tadj = getNearTile(x, y, i);
						if(tadj == null) continue;
						tadj.actualValue += t.request[i];
						delta += t.request[i];
						Main.debugprint("SPREAD direzione " + i + " valore " + t.request[i]);
						t.request[i] = 0;
					}
					t.actualValue -= delta;
				}
				
				t.canSpread = false;
				t.localAverage = 0;
				t.localAverageFlag = false;
			}
		}
	}
	
	public void setValue(int x, int y, float value)
	{
		map[x][y].actualValue += value;
		globalAvg += value/(n*m);
	}
	
	public void print()
	{
		/*
		for(int y=m-1; y>=0; y--)
		{
			for(int x=0; x<n; x++)
			{
			*/
		for(int y=m-1; y>=0; y--)
		{
			for(int x=0; x<n; x++)
			{
				if(Main.freePrecision)
					System.out.print(map[x][y].actualValue + "\t");
				else
					System.out.print(Math.ceil(map[x][y].actualValue) + "\t");
			}
			System.out.println();
		}
	}
	
	public String toString()
	{
		String toReturn = "";
		for(int y=m-1; y>=0; y--)
		{
			for(int x=0; x<n; x++)
			{
				toReturn += map[x][y].actualValue + "\t";
			}
			toReturn += "\n";
		}
		return toReturn;
	}
	
	public float sumAll()
	{
		float toReturn = 0;
		for(int x=0; x<n; x++)
		{
			for(int y=m-1; y>=0; y--)
			{
				toReturn += map[x][y].actualValue;
			}
		}
		return toReturn;
	}
	
	public void V2_updateStep1()
	{
		Main.debugprint("AGGIORNAMENTO fase 1");
		for(int x=0; x<n; x++)
		{
			for(int y=0; y<m; y++)
			{
				Main.debugprint("posizione: (" + x + ", " + y + ")");
				
				Tile t = map[x][y];
				
				//media locale
				float tot = t.actualValue;
				int n = 5;
				for(int i=0; i<4; i++)
				{
					Tile tadj = getNearTile(x, y, i);
					if(tadj == null)
					{
						n--;
					}
					else
					{
						tot += tadj.actualValue;
					}
				}
				
				t.localAverage = tot / n;
				t.localAverageFlag = true;
				
				Main.debugprint("MEDIA LOCALE: " + t.localAverage);
				
				//verifica
				if(globalAvg < t.actualValue)
				{
					//cella donatrice
					t.canSpread = true;
					Main.debugprint("CAN SPREAD");
				}
				else if((globalAvg > t.actualValue) || ((globalAvg == t.actualValue) && (t.localAverage > globalAvg)))
				{
					Main.debugprint("CELLA RICHIEDE RISORSA");
					
					//cella richiedente
					//quanto mi serve?
					float toRequire = 0;
					if(globalAvg > t.actualValue)
					{
						toRequire = r.k() * (globalAvg - t.actualValue);
					}
					else
					{
						toRequire = r.k() * (t.localAverage - t.actualValue);
					}
					
					Main.debugprint("risorsa da richiedere: " + toRequire);
					
					//cerco le celle donatrici e faccio le richieste
					tot -= t.actualValue;
					Main.debugprint("tot=" + tot);
					for(int i=0; i<4; i++)
					{
						Tile tadj = getNearTile(x, y, i);
						//Main.debugprint("cella " + tadj);
						if(tadj == null) continue;
						if(tadj.actualValue <= globalAvg)
						{
							tot -= tadj.actualValue;
							//Main.debugprint("nuovo tot=" + tot);
						}
					}
					if(tot == 0) continue;
					for(int i=0; i<4; i++)
					{
						Tile tadj = getNearTile(x, y, i);
						if(tadj == null) continue;
						if(tadj.actualValue > t.actualValue)
						{
							//posso richiedere! evvai!
							int newDir = (i+2)%4;
							tadj.request[newDir] += (tadj.actualValue/tot) * toRequire;
							Main.debugprint("richiesta finale: (" + i + ", " + tadj.request[newDir] + ")");
						}
					}
				}
			}
		}
	}
	
	public void V2_updateStep2()
	{
		Main.debugprint("AGGIORNAMENTO fase 2");
		
		for(int x=0; x<n; x++)
		{
			for(int y=0; y<m; y++)
			{
				Main.debugprint("posizione: (" + x + ", " + y + ")");
				
				Tile t = map[x][y];
				if(t.canSpread)
				{
					float spreadable = r.k() * (-globalAvg + t.actualValue);
					Main.debugprint("donabile : " + spreadable);
					
					//verificare di non aver sforato il limite di richieste
					float totReq = t.request[0] + t.request[1] + t.request[2] + t.request[3];
					if(spreadable < totReq)
					{
						Main.debugprint("FACCIO LA CRESTA");
						float cresta = totReq - spreadable;
						for(int i=0; i<4; i++)
						{
							if(t.request[i] > 0)
							{
								Main.debugprint("CORREZIONE direzione " + i + " valore " + t.request[i]);
								t.request[i] -= ((totReq - t.request[i])/totReq)*cresta;
							}
						}
					}
					
					//concedo le richieste
					float delta = 0;
					for(int i=0; i<4; i++)
					{
						Tile tadj = getNearTile(x, y, i);
						if(tadj == null) continue;
						tadj.actualValue += t.request[i];
						delta += t.request[i];
						Main.debugprint("SPREAD direzione " + i + " valore " + t.request[i]);
						t.request[i] = 0;
					}
					t.actualValue -= delta;
				}
				
				t.canSpread = false;
				t.localAverage = 0;
				t.localAverageFlag = false;
			}
		}
	}
	
	private int minOf(float a[])
	{
		int minIdx = 0;
		float minVal = a[0];
		for(int i=0; i<a.length; i++)
		{
			if(a[i] == -1) continue;
			if(a[i] < minVal)
			{
				minIdx = i;
				minVal = a[i];
			}
		}
		
		a[minIdx] = -1;
		return minIdx;
	}
	
	public void V3_updateStep1()
	{
		Main.debugprint("UPDATE FASE 1");
		//per ogni cella della matrice...
		for(int x=0; x<n; x++)
		{
			for(int y=0; y<m; y++)
			{
				Tile t = map[x][y];
				Main.debugprint("posizione: (" + x + ", " + y + ") valore=" + t.actualValue + " media=" + globalAvg);
				
				//confronto il valore attuale con la media globale
				if(t.actualValue > globalAvg)
				{
					Main.debugprint("CAN SPREAD");
					//sono una cella che può donare risorsa
					t.canSpread = true;
					
					//quanto posso donare?
					float toSpread = r.k() * ( t.actualValue - globalAvg );
					Main.debugprint("toSpread=" + toSpread);
					
					//e quanta risorsa c'è attorno?
					float tot = 0;
					int nadj = 0;
					for(int i=0; i<4; i++)
					{
						Tile tadj = getNearTile(x, y, i);
						if(tadj == null) continue;
						tot += tadj.actualValue;
						nadj++;
					}
					
					Main.debugprint(/*"quanta risorsa attorno? tot=" + tot + */" nadj=" + nadj);
					
					//assegno il valore di spread a tutte le celle attorno
					for(int i=0; i<4; i++)
					{
						Tile tadj = getNearTile(x, y, i);
						if(tadj == null) continue;
						//t.request[i] += ((tot - tadj.actualValue)/tot)*toSpread;
						/*
						if(tot == 0)
						{
							t.request[i] += toSpread / nadj;
						}
						else
						{
							t.request[i] += ((tot - tadj.actualValue)/tot)*(toSpread/nadj);
						}
						*/
						//t.request[i] += toSpread / nadj;
						//t.request[i] += toSpread / Math.pow(2, i);
						if(tot == 0)
							t.request[i] = toSpread/nadj;
						else
							t.request[i] = toSpread/nadj + ((tot/nadj - tadj.actualValue)/tot) * toSpread/nadj;
						Main.debugprint("request in direzione " + nameOfDir[i] + " = " + t.request[i]);
					}
					
				}
				/*
				else if(t.actualValue < globalAvg)
				{
					//allora ci sarà qualche cella che mi concederà della risorsa
					//non faccio nulla
				}
				else //uguaglianza
				{
					//allora potrei essere in equilibrio
					//oppure potrebbe esserci qualche cell che vorrebbe fornirmi risorsa
					//in ogni caso, non faccio nulla
				}
				*/
			}
		}
	}
	
	public void V3_updateStep2()
	{
		Main.debugprint("UPDATE FASE 2");
		//per ogni cella della mappa...
		for(int x=0; x<n; x++)
		{
			for(int y=0; y<m; y++)
			{
				Main.debugprint("posizione: (" + x + ", " + y + ")");
				Tile t = map[x][y];
				
				//verifico se si tratta di una cella donatrice
				if(t.canSpread)
				{
					//eseguo lo spread, come programmato al passo 1
					for(int i=0; i<4; i++)
					{
						Tile tadj = getNearTile(x, y, i);
						if(tadj == null) continue;
						tadj.actualValue += t.request[i];
						t.actualValue -= t.request[i];
						t.request[i] = 0;
					}
					
					//pulizia della cella prima di passare alla prossima
					t.canSpread = false;
				}
			}
		}
	}
	
}
